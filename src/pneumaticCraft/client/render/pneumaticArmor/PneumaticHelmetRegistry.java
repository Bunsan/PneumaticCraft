package pneumaticCraft.client.render.pneumaticArmor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import pneumaticCraft.api.client.pneumaticHelmet.IBlockTrackEntry;
import pneumaticCraft.api.client.pneumaticHelmet.IEntityTrackEntry;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableEntity;
import pneumaticCraft.api.client.pneumaticHelmet.IPneumaticHelmetRegistry;
import pneumaticCraft.api.client.pneumaticHelmet.IUpgradeRenderHandler;
import pneumaticCraft.client.render.pneumaticArmor.blockTracker.BlockTrackEntryList;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler.HackingEntityProperties;
import pneumaticCraft.lib.Log;

public class PneumaticHelmetRegistry implements IPneumaticHelmetRegistry{

    private static final PneumaticHelmetRegistry INSTANCE = new PneumaticHelmetRegistry();
    public final List<Class<? extends IEntityTrackEntry>> entityTrackEntries = new ArrayList<Class<? extends IEntityTrackEntry>>();
    public final Map<Class<? extends Entity>, Class<? extends IHackableEntity>> hackableEntities = new HashMap<Class<? extends Entity>, Class<? extends IHackableEntity>>();
    public final Map<Block, Class<? extends IHackableBlock>> hackableBlocks = new HashMap<Block, Class<? extends IHackableBlock>>();
    public final Map<String, Class<? extends IHackableEntity>> stringToEntityHackables = new HashMap<String, Class<? extends IHackableEntity>>();
    public final Map<String, Class<? extends IHackableBlock>> stringToBlockHackables = new HashMap<String, Class<? extends IHackableBlock>>();

    public static PneumaticHelmetRegistry getInstance(){
        return INSTANCE;
    }

    @Override
    public void registerEntityTrackEntry(Class<? extends IEntityTrackEntry> entry){
        if(entry == null) throw new NullPointerException("Can't register null!");
        entityTrackEntries.add(entry);
    }

    @Override
    public void addHackable(Class<? extends Entity> entityClazz, Class<? extends IHackableEntity> iHackable){
        if(entityClazz == null) throw new NullPointerException("Entity class is null!");
        if(iHackable == null) throw new NullPointerException("IHackableEntity is null!");
        if(Entity.class.isAssignableFrom(iHackable)) {
            Log.warning("Entities that implement IHackableEntity shouldn't be registered as hackable! Registering entity: " + entityClazz.getCanonicalName());
        } else {
            try {
                IHackableEntity hackableEntity = iHackable.newInstance();
                if(hackableEntity.getId() != null) stringToEntityHackables.put(hackableEntity.getId(), iHackable);
                hackableEntities.put(entityClazz, iHackable);
            } catch(InstantiationException e) {
                Log.error("Not able to register hackable entity: " + iHackable.getName() + ". Does the class have a parameterless constructor?");
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                Log.error("Not able to register hackable entity: " + iHackable.getName() + ". Is the class a public class?");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addHackable(Block block, Class<? extends IHackableBlock> iHackable){
        if(block == null) throw new NullPointerException("Block is null!");
        if(iHackable == null) throw new NullPointerException("IHackableBlock is null!");

        if(Block.class.isAssignableFrom(iHackable)) {
            Log.warning("Blocks that implement IHackableBlock shouldn't be registered as hackable! Registering block: " + block.getLocalizedName());
        } else {
            try {
                IHackableBlock hackableBlock = iHackable.newInstance();
                if(hackableBlock.getId() != null) stringToBlockHackables.put(hackableBlock.getId(), iHackable);
                hackableBlocks.put(block, iHackable);
            } catch(InstantiationException e) {
                Log.error("Not able to register hackable block: " + iHackable.getName() + ". Does the class have a parameterless constructor?");
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                Log.error("Not able to register hackable block: " + iHackable.getName() + ". Is the class a public class?");
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<IHackableEntity> getCurrentEntityHacks(Entity entity){
        HackingEntityProperties hackingProps = (HackingEntityProperties)entity.getExtendedProperties("PneumaticCraftHacking");
        if(hackingProps != null) {
            List<IHackableEntity> hackables = hackingProps.getCurrentHacks();
            if(hackables != null) return hackables;
        } else {
            Log.warning("Extended entity props HackingEntityProperties couldn't be found in the entity " + entity.getName());
        }
        return new ArrayList<IHackableEntity>();
    }

    @Override
    public void registerBlockTrackEntry(IBlockTrackEntry entry){
        if(entry == null) throw new NullPointerException("Block Track Entry can't be null!");
        BlockTrackEntryList.instance.trackList.add(entry);
    }

    @Override
    public void registerRenderHandler(IUpgradeRenderHandler renderHandler){
        if(renderHandler == null) throw new NullPointerException("Render handler can't be null!");
        UpgradeRenderHandlerList.instance().upgradeRenderers.add(renderHandler);
    }
}
