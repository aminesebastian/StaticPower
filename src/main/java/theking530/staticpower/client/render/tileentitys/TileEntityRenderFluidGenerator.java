package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.RenderUtil;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;

public class TileEntityRenderFluidGenerator extends BaseMachineTESR<TileEntityFluidGenerator> {

	static float texel = 1F/16F;
	static float width = 1F;
	float height = 1F;
	
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FluidGeneratorFrontOn.png");
	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FluidGeneratorFrontOff.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}

	@Override
	public void drawExtra(TileEntityFluidGenerator tileentity, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {
		if(tileentity.TANK.getFluid() != null) {	
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			drawLiquidBar(tileentity, tileentity.TANK.getFluid());
			GL11.glDisable(GL11.GL_BLEND);
		}
		drawEnergyBar(tileentity);
	}
	public static void drawLiquidBar(TileEntity tileentity, FluidStack fluidStack) {
		TileEntityFluidGenerator fGen = (TileEntityFluidGenerator)tileentity;
		RenderUtil.drawFluidInWorld(fluidStack, fGen.TANK.getCapacity(), 10.5F*texel, 2.5F*texel, 1.0005F, 3F*texel, 0.69f);
	}
	public static void drawEnergyBar(TileEntity tileentity) {
		TileEntityFluidGenerator fGen = (TileEntityFluidGenerator)tileentity;
		float height = ((float)(fGen.energyStorage.getEnergyStored()/(float)fGen.energyStorage.getMaxEnergyStored())) > 0 ? ((float)(fGen.energyStorage.getEnergyStored()/(float)fGen.energyStorage.getMaxEnergyStored())) : 0;	
		RenderUtil.drawPowerBarInWorld(height, 2.5F*texel, 2.5F*texel, 1.0005F, 3F*texel, .69F);
	}
}
