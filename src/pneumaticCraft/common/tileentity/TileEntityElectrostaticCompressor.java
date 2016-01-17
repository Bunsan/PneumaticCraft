package pneumaticCraft.common.tileentity;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.lib.PneumaticValues;

public class TileEntityElectrostaticCompressor extends TileEntityPneumaticBase implements IInventory, IRedstoneControl{

    private ItemStack[] inventory;

    private final int INVENTORY_SIZE = 4;

    private boolean lastRedstoneState;
    @GuiSynced
    public int redstoneMode = 0;
    public int ironBarsBeneath = 0;
    private int struckByLightningCooldown; //used by the redstone.

    public TileEntityElectrostaticCompressor(){
        super(PneumaticValues.DANGER_PRESSURE_ELECTROSTATIC_COMPRESSOR, PneumaticValues.MAX_PRESSURE_ELECTROSTATIC_COMPRESSOR, PneumaticValues.VOLUME_ELECTROSTATIC_COMPRESSOR, 0, 1, 2, 3);
        inventory = new ItemStack[INVENTORY_SIZE];
    }

    @Override
    public void update(){
        /*
         * Most of the Electrostatic Compressor's logic can be found in TickHandlerPneumaticCraft#handleElectrostaticGeneration().
         */
        if(worldObj.getTotalWorldTime() % 40 == 0) {
            for(ironBarsBeneath = 0; ironBarsBeneath < 128; ironBarsBeneath++) {
                if(worldObj.getBlockState(getPos().offset(EnumFacing.DOWN, ironBarsBeneath + 1)).getBlock() != Blocks.iron_bars) {
                    break;
                }
            }
        }
        super.update();
        if(!worldObj.isRemote) {
            if(lastRedstoneState != shouldEmitRedstone()) {
                lastRedstoneState = !lastRedstoneState;
                updateNeighbours();
            }
            struckByLightningCooldown--;
        }

    }

    @Override
    public boolean isConnectedTo(EnumFacing dir){
        return dir != EnumFacing.UP;
    }

    public boolean shouldEmitRedstone(){
        switch(redstoneMode){
            case 0:
                return false;
            case 1:
                return struckByLightningCooldown > 0;
        }
        return false;
    }

    public void onStruckByLightning(){
        struckByLightningCooldown = 10;
        if(getPressure() > PneumaticValues.DANGER_PRESSURE_ELECTROSTATIC_COMPRESSOR) {
            int maxRedirection = PneumaticValues.MAX_REDIRECTION_PER_IRON_BAR * ironBarsBeneath;
            int tooMuchAir = (int)((getPressure() - PneumaticValues.DANGER_PRESSURE_ELECTROSTATIC_COMPRESSOR) * getAirHandler(null).getVolume());
            addAir(-Math.min(maxRedirection, tooMuchAir));
        }
    }

    @Override
    public void handleGUIButtonPress(int buttonID, EntityPlayer player){
        if(buttonID == 0) {
            redstoneMode++;
            if(redstoneMode > 1) redstoneMode = 0;
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory(){

        return inventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot){

        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount){

        ItemStack itemStack = getStackInSlot(slot);
        if(itemStack != null) {
            if(itemStack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                itemStack = itemStack.splitStack(amount);
                if(itemStack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot){

        ItemStack itemStack = getStackInSlot(slot);
        if(itemStack != null) {
            setInventorySlotContents(slot, null);
        }
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack){

        inventory[slot] = itemStack;
        if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getName(){

        return Blockss.electrostaticCompressor.getUnlocalizedName();
    }

    @Override
    public int getInventoryStackLimit(){

        return 64;
    }

    @Override
    public void openInventory(EntityPlayer player){}

    @Override
    public void closeInventory(EntityPlayer player){}

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound){

        super.readFromNBT(nbtTagCompound);

        redstoneMode = nbtTagCompound.getInteger("redstoneMode");
        // Read in the ItemStacks in the inventory from NBT
        NBTTagList tagList = nbtTagCompound.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for(int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            byte slot = tagCompound.getByte("Slot");
            if(slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound){

        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("redstoneMode", redstoneMode);
        // Write the ItemStacks in the inventory to NBT
        NBTTagList tagList = new NBTTagList();
        for(int currentIndex = 0; currentIndex < inventory.length; ++currentIndex) {
            if(inventory[currentIndex] != null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte)currentIndex);
                inventory[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        nbtTagCompound.setTag("Items", tagList);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack){
        return canInsertUpgrade(i, itemstack);
    }

    @Override
    public boolean hasCustomName(){
        return false;
    }

    @Override
    public void clear(){
        Arrays.fill(inventory, null);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1){
        return isGuiUseableByPlayer(var1);
    }

    @Override
    public int getRedstoneMode(){
        return redstoneMode;
    }
}
