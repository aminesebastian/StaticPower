package theking530.staticpower.client.render.tileentitys.machines;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.RenderUtil;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;

public class TileEntityRenderCropSqueezer extends BaseMachineTESR<TileEntityCropSqueezer> {

	static float texel = 1/64F;

	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/crop_squeezer_front_on.png");
	private static final ResourceLocation frontOff = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/crop_squeezer_front_off.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : frontOff;
	}
	public void drawExtra(TileEntityCropSqueezer te, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {
		if(te.fluidTank.getFluid() != null || (te.getInputStack(0) != ItemStack.EMPTY && te.getFluidResult(te.getInputStack(0)) != null)) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if(te.fluidTank.getFluid() != null) {
				drawLiquidPour(te, te.fluidTank.getFluid());
			}else if(te.getInputStack(0) != null && te.getFluidResult(te.getInputStack(0)) != null){
				drawLiquidPour(te, te.getFluidResult(te.getInputStack(0)));
			}else{
				
			}
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	public static void drawLiquidPour(TileEntity tileentity, FluidStack fluidStack) {
		TileEntityCropSqueezer squeezer = (TileEntityCropSqueezer)tileentity;
		float progress = ((float)squeezer.processingTimer/(float)squeezer.processingTime)*0.5f;
		RenderUtil.drawFluidInWorld(fluidStack, fluidStack.amount, 28*texel, 1-16*texel-progress, 1.0001F, 8*texel, progress);
		RenderUtil.drawFluidInWorld(fluidStack, squeezer.fluidTank.getCapacity(), 16*texel, 14.0F*texel, 1.0001F, 32*texel, texel*13.0f);
	}
}
