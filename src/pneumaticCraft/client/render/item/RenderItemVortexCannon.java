package pneumaticCraft.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.client.model.ModelVortexCannon;
import pneumaticCraft.lib.Textures;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemVortexCannon implements IItemRenderer{

    private final ModelVortexCannon model;

    public RenderItemVortexCannon(){
        model = new ModelVortexCannon();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type){

        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){

        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data){

        switch(type){
            case ENTITY: {
                render(0.0F, 0.0F, 1.0F, 0.5F);
                return;
            }
            case EQUIPPED: {
                GL11.glRotatef(-30F, 0, 1.0F, 0.0F);
                GL11.glRotatef(70F, 0, 0F, 1.0F);
                render(0.5F, -0.5F, 0.5F, 1.0F);
                return;
            }
            case EQUIPPED_FIRST_PERSON: {
                GL11.glRotatef(-140F, 0, 1.2F, 0.0F);
                render(0.0F, 0.5F, 1.7F, 1.0F);
                return;
            }
            case INVENTORY: {
                render(0.0F, 0.0F, 1.0F, 1.0F);
                return;
            }
            default:
                return;
        }
    }

    private void render(float x, float y, float z, float scale){

        GL11.glPushMatrix();
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glRotatef(-90F, 1F, 0, 0);
        // Scale, Translate, Rotate
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(-90F, 1F, 0, 0);

        // Bind texture
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(Textures.MODEL_VORTEX_CANNON);
        // Render
        model.renderModel(1F / 16F);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
