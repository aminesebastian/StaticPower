package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidAttributes;
import theking530.api.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.api.utilities.Color;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.cables.ServerCable.CableConnectionState;
import theking530.staticpower.tileentities.cables.fluid.TileEntityFluidCable;

public class TileEntityRenderFluidCable extends StaticPowerTileEntitySpecialRenderer<TileEntityFluidCable> {
	private static final BlockModel FLUID_MODEL = new BlockModel();

	public TileEntityRenderFluidCable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityFluidCable tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (tileEntity.fluidCableComponent.getFilledPercentage() > 0 && !tileEntity.fluidCableComponent.getFluidInTank(0).isEmpty()) {
			FluidAttributes attributes = tileEntity.fluidCableComponent.getFluidInTank(0).getFluid().getAttributes();
			int encodedFluidColor = attributes.getColor(tileEntity.fluidCableComponent.getFluidInTank(0));
			TextureAtlasSprite sprite = GuiFluidBarUtilities.getStillFluidSprite(tileEntity.fluidCableComponent.getFluidInTank(0));
			Color fluidColor = Color.fromEncodedInteger(encodedFluidColor).fromEightBitToFloat();
			drawFluidCore(sprite, tileEntity.fluidCableComponent.getFilledPercentage(), fluidColor, matrixStack);
			for (Direction dir : Direction.values()) {
				if (tileEntity.fluidCableComponent.getConnectionState(dir) != CableConnectionState.NONE) {
					drawExtensions(dir, sprite, tileEntity.fluidCableComponent.getFilledPercentage(), fluidColor, matrixStack);
				}
			}
		}
	}

	protected void drawFluidCore(TextureAtlasSprite sprite, float filledAmount, Color fluidColor, MatrixStack matrixStack) {
		float minWidth = 0.22f * filledAmount;
		float minWidthOffset = (-0.1f * filledAmount) + 0.5f;
		FLUID_MODEL.drawPreviewCube(new Vector3f(minWidthOffset, minWidthOffset, minWidthOffset), new Vector3f(minWidth, minWidth, minWidth), fluidColor, matrixStack, sprite);
	}

	protected void drawExtensions(Direction side, TextureAtlasSprite sprite, float filledAmount, Color fluidColor, MatrixStack matrixStack) {
		float minWidth = 0.22f * filledAmount;
		float minWidthOffset = (-0.1f * filledAmount) + 0.5f;

		if (side == Direction.SOUTH) {
			FLUID_MODEL.drawPreviewCube(new Vector3f(minWidthOffset, minWidthOffset, 0.6f), new Vector3f(minWidth, minWidth, 0.4f), fluidColor, matrixStack, sprite);
		} else if (side == Direction.NORTH) {
			FLUID_MODEL.drawPreviewCube(new Vector3f(minWidthOffset, minWidthOffset, 0.0f), new Vector3f(minWidth, minWidth, 0.4f), fluidColor, matrixStack, sprite);
		} else if (side == Direction.DOWN) {
			FLUID_MODEL.drawPreviewCube(new Vector3f(minWidthOffset, 0.0f, minWidthOffset), new Vector3f(minWidth, 0.4f, minWidth), fluidColor, matrixStack, sprite);
		} else if (side == Direction.UP) {
			FLUID_MODEL.drawPreviewCube(new Vector3f(minWidthOffset, 0.6f, minWidthOffset), new Vector3f(minWidth, 0.4f, minWidth), fluidColor, matrixStack, sprite);
		} else if (side == Direction.WEST) {
			FLUID_MODEL.drawPreviewCube(new Vector3f(0.0f, minWidthOffset, minWidthOffset), new Vector3f(0.4f, minWidth, minWidth), fluidColor, matrixStack, sprite);
		} else if (side == Direction.EAST) {
			FLUID_MODEL.drawPreviewCube(new Vector3f(0.6f, minWidthOffset, minWidthOffset), new Vector3f(0.4f, minWidth, minWidth), fluidColor, matrixStack, sprite);
		}
	}
}
