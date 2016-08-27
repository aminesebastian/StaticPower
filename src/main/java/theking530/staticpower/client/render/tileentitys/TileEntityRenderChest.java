package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.tileentity.chest.TileEntityBaseChest;

@SideOnly(Side.CLIENT)
public class TileEntityRenderChest extends TileEntitySpecialRenderer<TileEntity> {
	
    private ResourceLocation texture;
    private ModelChest model = new ModelChest();
    private Tier TIER;
    
    public TileEntityRenderChest(Tier tier) {
    	TIER = tier;	
    	switch(TIER) {
    	case STATIC:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/StaticChest.png");
    		break;
    	case ENERGIZED:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/EnergizedChest.png");
    		break;
    	case LUMUM:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/LumumChest.png");
    		break;
    	default:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/StaticChest.png");
    		break;
    	}
    }
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
    	TileEntityBaseChest staticChest = (TileEntityBaseChest)te;
        int i = staticChest.getBlockMetadata();	
        
    	switch(TIER) {
    	case STATIC:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/StaticChest.png");
    		break;
    	case ENERGIZED:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/EnergizedChest.png");
    		break;
    	case LUMUM:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/LumumChest.png");
    		break;
    	default:
    		texture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/StaticChest.png");
    		break;
    	}
        
        ModelChest modelchest;
        modelchest = this.model;
        this.bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		EnumFacing facing = EnumFacing.getHorizontal(te.getBlockMetadata());
		if(facing == EnumFacing.WEST) {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(0, 0, 0);
			}
		if(facing == EnumFacing.NORTH) {
			GL11.glRotated(180, 0, 1, 0);
			GL11.glTranslated(0, 0, 0);
			}
		if(facing == EnumFacing.EAST) {
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(0, 0, 0);
		}
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float f1 = staticChest.prevLidAngle + (staticChest.lidAngle - staticChest.prevLidAngle) * partialTicks;
        float f2;
        
        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;
        modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
        modelchest.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}