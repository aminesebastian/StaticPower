package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.RenderUtil;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;

public class TileEntityRenderFluidInfuser extends BaseMachineTESR<TileEntityFluidInfuser> {

	static float texel = 1F/16F;

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FluidInfuserFront.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return front;
	}
	public void drawExtra(TileEntityFluidInfuser tileentity, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {
		if(tileentity.fluidTank.getFluid() != null) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			drawLiquidBar(tileentity, tileentity.fluidTank.getFluid());
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	public static void drawLiquidBar(TileEntity tileentity, FluidStack fluidStack) {
		TileEntityFluidInfuser infuser = (TileEntityFluidInfuser)tileentity;
		RenderUtil.drawFluidInWorld(fluidStack, infuser.fluidTank.getCapacity(), 4F*texel, 3.5F*texel, 1.0001F, 8F*texel, texel*9);
	}
}
