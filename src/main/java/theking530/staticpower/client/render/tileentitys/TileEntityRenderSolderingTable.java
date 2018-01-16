package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.model.ModelSolderingTable;
import theking530.staticpower.tileentity.solderingtable.TileEntitySolderingTable;

public class TileEntityRenderSolderingTable extends TileEntitySpecialRenderer<TileEntitySolderingTable> {
	
	private final ModelSolderingTable Table;
	ResourceLocation SolderingTableTexture = new ResourceLocation(Reference.MODID, "textures/models/tileentity/SolderingTable.png");
	
	static float texel = 1F/16F;
	
    public TileEntityRenderSolderingTable() {
    	Table = new ModelSolderingTable();
    }
	
	@Override
	 public void render(TileEntitySolderingTable tileentity, double x, double y, double z, float f, int destroyStage, float alpha) {				
		EnumFacing facing = tileentity.getFacingDirection();
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glTranslated(.5, -1.5, -.5);
		if(facing == EnumFacing.WEST) {
			GL11.glRotated(90, 0, 1, 0);
		}
		if(facing == EnumFacing.NORTH) {
			GL11.glRotated(180, 0, 1, 0);
		}
		if(facing == EnumFacing.EAST) {
			GL11.glRotated(-90, 0, 1, 0);
		}
		bindTexture(SolderingTableTexture);
		Table.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glTranslated(-x, -y, -z);	
		GL11.glColor3f(1.0F, 1.0F, 1.0F);	
		GL11.glPopMatrix();
	}
}
