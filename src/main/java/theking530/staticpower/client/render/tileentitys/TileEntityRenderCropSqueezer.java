package theking530.staticpower.client.render.tileentitys;

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

	static float texel = 1/16F;

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/CropSqueezerFrontOFF.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/CropSqueezerFrontOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}
	public void drawExtra(TileEntityCropSqueezer te, double translationX, double translationY, double translationZ, float f, int dest, float alpha) {
		if(te.TANK.getFluid() != null || (te.getInputStack(0) != ItemStack.EMPTY && te.getFluidResult(te.getInputStack(0)) != null)) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if(te.TANK.getFluid() != null) {
				drawLiquidPour(te, te.TANK.getFluid());
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
		RenderUtil.drawFluidInWorld(fluidStack, fluidStack.amount, 7*texel, 1-4*texel-progress, 1.0001F, 2*texel, progress);
		RenderUtil.drawFluidInWorld(fluidStack, squeezer.TANK.getCapacity(), 4*texel, 3.5F*texel, 1.0001F, 8*texel, texel*2.5f);
	}
}
