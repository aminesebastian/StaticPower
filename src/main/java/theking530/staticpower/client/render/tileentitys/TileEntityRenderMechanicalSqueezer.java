package theking530.staticpower.client.render.tileentitys;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.model.ModelBlock;
import theking530.staticpower.machines.mechanicalsqueezer.TileEntityMechanicalSqueezer;
import theking530.staticpower.utils.RenderUtil;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityRenderMechanicalSqueezer extends TileEntitySpecialRenderer<TileEntity> {

	ResourceLocation side = new ResourceLocation(Reference.MODID, "textures/blocks/MachineSide.png");
	ResourceLocation sideIn = new ResourceLocation(Reference.MODID, "textures/blocks/MachineSideIn.png");
	ResourceLocation sideOut = new ResourceLocation(Reference.MODID, "textures/blocks/MachineSideOut.png");
	ResourceLocation front = new ResourceLocation(Reference.MODID, "textures/blocks/MechanicalSqueezerFrontOFF.png");
	ResourceLocation frontOn = new ResourceLocation(Reference.MODID, "textures/blocks/MechanicalSqueezerFrontON.png");

	static float texel = 1/16F;
	
	@Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		TileEntityMechanicalSqueezer squeezer = (TileEntityMechanicalSqueezer)te;
		EnumFacing facing = EnumFacing.getHorizontal(te.getBlockMetadata())	;
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
		checkAndRenderSides(te, 0);
		checkAndRenderSides(te, 1);
		checkAndRenderSides(te, 2);
		checkAndRenderSides(te, 3);
		checkAndRenderSides(te, 4);
		checkAndRenderSides(te, 5);
		if(squeezer.TANK.getFluid() != null || (squeezer.getInputStack(0) != null && squeezer.getFluidResult(squeezer.getInputStack(0)) != null)) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if(squeezer.TANK.getFluid() != null) {
				drawLiquidPour(te, squeezer.TANK.getFluid());
			}else if(squeezer.getInputStack(0) != null && squeezer.getFluidResult(squeezer.getInputStack(0)) != null){
				drawLiquidPour(te, squeezer.getFluidResult(squeezer.getInputStack(0)));
			}else{
				
			}
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(-x, -y, -z);	
		GL11.glColor3f(1.0F, 1.0F, 1.0F);	
		GL11.glPopMatrix();

	}
	
	public void checkAndRenderSides(TileEntity tileentity, int side) {
		TileEntityMechanicalSqueezer squeezer = (TileEntityMechanicalSqueezer)tileentity;		
		ModelBlock block = new ModelBlock();
		if(side != 3) {
			if(squeezer.getModeFromInt(side) == Mode.Regular) {
				this.bindTexture(this.side);
			}
			if(squeezer.getModeFromInt(side) == Mode.Input) {
				this.bindTexture(sideIn);
			}
			if(squeezer.getModeFromInt(side) == Mode.Output) {
				this.bindTexture(sideOut);
			}
			if(squeezer.getModeFromInt(side) == Mode.Disabled) {
				this.bindTexture(this.side);
			}
			block.drawBlock(side);
		}
		if(side == 3) {
			if(squeezer.isProcessing()) {
				this.bindTexture(frontOn);
				}else{
				this.bindTexture(front);
			}
			block.drawBlock(3);
		}
	}

	public static void drawLiquidPour(TileEntity tileentity, FluidStack fluidStack) {
		TileEntityMechanicalSqueezer squeezer = (TileEntityMechanicalSqueezer)tileentity;
		float height = squeezer.getFluidLevelScaled(1);	
		float progress = ((float)squeezer.PROCESSING_TIMER/(float)squeezer.PROCESSING_TIME)*0.5f;
		RenderUtil.drawFluidInWorld(fluidStack, fluidStack.amount, 7*texel, 1-4*texel-progress, 1.0001F, 2*texel, progress);
		RenderUtil.drawFluidInWorld(fluidStack, squeezer.TANK.getCapacity(), 4*texel, 3.5F*texel, 1.0001F, 8*texel, texel*2.5f);
	}
}