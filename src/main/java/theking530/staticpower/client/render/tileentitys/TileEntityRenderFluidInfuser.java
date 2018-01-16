package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.model.ModelBlock;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.utils.RenderUtil;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityRenderFluidInfuser extends TileEntitySpecialRenderer<TileEntityFluidInfuser> {

	ResourceLocation side = new ResourceLocation(Reference.MODID, "textures/blocks/MachineSide.png");
	ResourceLocation sideIn = new ResourceLocation(Reference.MODID, "textures/blocks/MachineSideIn.png");
	ResourceLocation sideOut = new ResourceLocation(Reference.MODID, "textures/blocks/MachineSideOut.png");
	ResourceLocation front = new ResourceLocation(Reference.MODID, "textures/blocks/FluidInfuserFront.png");
	
	static float texel = 1F/16F;
	
	@Override
	public void render(TileEntityFluidInfuser tileentity, double x, double y, double z, float f, int destroyStage, float alpha) {		
		EnumFacing facing = tileentity.getFacingDirection();
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		if(facing == EnumFacing.WEST) {
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(0, 0, -1);
			}
		if(facing == EnumFacing.NORTH) {
			GL11.glRotated(180, 0, 1, 0);
			GL11.glTranslated(-1, 0, -1);
			}
		if(facing == EnumFacing.EAST) {
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-1, 0, 0);
		}
		GL11.glDisable(GL11.GL_LIGHTING);		
		checkAndRenderSides(tileentity, 0);
		checkAndRenderSides(tileentity, 1);
		checkAndRenderSides(tileentity, 2);
		checkAndRenderSides(tileentity, 3);
		checkAndRenderSides(tileentity, 4);
		checkAndRenderSides(tileentity, 5);
		//renderInfusingItem(tileentity, x, y, z);
		if(tileentity.TANK.getFluid() != null) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			drawLiquidBar(tileentity, tileentity.TANK.getFluid());
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(-x, -y, -z);	
		GL11.glColor3f(1.0F, 1.0F, 1.0F);	
		GL11.glPopMatrix();
	}
	
	public void checkAndRenderSides(TileEntity tileentity, int side) {
		TileEntityFluidInfuser infuser = (TileEntityFluidInfuser)tileentity;	
		ModelBlock block = new ModelBlock();
		if(side != 3) {
			if(infuser.getModeFromInt(side) == Mode.Regular) {
				this.bindTexture(this.side);
			}
			if(infuser.getModeFromInt(side) == Mode.Input) {
				this.bindTexture(sideIn);
			}
			if(infuser.getModeFromInt(side) == Mode.Output) {
				this.bindTexture(sideOut);
			}
			if(infuser.getModeFromInt(side) == Mode.Disabled) {
				this.bindTexture(this.side);
			}
			block.drawBlock(side);
		}
			this.bindTexture(front);
			block.drawBlock(3);
	}
	
	public static void drawLiquidBar(TileEntity tileentity, FluidStack fluidStack) {
		TileEntityFluidInfuser infuser = (TileEntityFluidInfuser)tileentity;
		RenderUtil.drawFluidInWorld(fluidStack, infuser.TANK.getCapacity(), 4F*texel, 3.5F*texel, 1.0001F, 8F*texel, texel*9);
	}
}
