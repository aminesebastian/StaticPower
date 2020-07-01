package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.utilities.Color;
import theking530.common.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.item.ItemRoutingParcelClient;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.TileEntityBase;

@SuppressWarnings("deprecation")
public abstract class AbstractCableTileEntityRenderer<T extends TileEntityBase> extends StaticPowerTileEntitySpecialRenderer<T> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();
	protected static final Vector3D BLOCK_RENDER_SCALE = new Vector3D(0.3f, 0.3f, 0.3f);
	protected static final Vector3D ITEM_RENDER_SCALE = new Vector3D(0.25f, 0.25f, 0.25f);

	public AbstractCableTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	protected void renderItemRoutingParcel(ItemRoutingParcelClient packet, T te, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

		Direction dir = packet.getItemAnimationDirection();
		if (dir == null) {
			return;
		}
		float lerpValue = packet.getItemMoveLerp() + (partialTicks / 20.0f);
		matrixStack.push();

		// Determine which scale to use when drawing.
		if (packet.getContainedItem().getItem() instanceof BlockItem) {
			drawItemInWorld(te, packet.getContainedItem(), TransformType.FIXED, getItemParcelAnimationOffset(lerpValue, dir), BLOCK_RENDER_SCALE, partialTicks, matrixStack, buffer, 15728880, combinedOverlay);
		} else {
			drawItemInWorld(te, packet.getContainedItem(), TransformType.FIXED, getItemParcelAnimationOffset(lerpValue, dir), ITEM_RENDER_SCALE, partialTicks, matrixStack, buffer, 15728880, combinedOverlay);
		}

		matrixStack.pop();
	}

	protected Vector3D getItemParcelAnimationOffset(float lerpValue, Direction dir) {
		Vector3D baseOffset = new Vector3D(0.5f, 0.5f, 0.5f);
		Vector3D directionVector = new Vector3D(dir);
		directionVector.multiply(0.5f);
		directionVector.multiply(lerpValue);
		baseOffset.add(directionVector);
		return baseOffset;
	}

	protected void drawFluidCable(FluidStack fluid, float filledPercentage, float radius, MatrixStack matrixStack, AbstractCableProviderComponent cableComponent) {
		TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
		Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);

		boolean wasExtensionDrawn = false;
		for (Direction dir : Direction.values()) {
			if (cableComponent.getConnectionState(dir) != CableConnectionState.NONE) {
				wasExtensionDrawn = true;
				drawExtensions(dir, sprite, filledPercentage, radius, fluidColor, matrixStack);
			}
		}
		if (!wasExtensionDrawn) {
			drawFluidCore(sprite, filledPercentage, radius, fluidColor, matrixStack);
		}
	}

	protected void drawFluidCore(TextureAtlasSprite sprite, float filledAmount, float radius, Color fluidColor, MatrixStack matrixStack) {
		float diameter = radius * 2.0f - 0.01f;
		radius -= 0.005f;
		float minWidth = 0.22f * filledAmount;
		float minWidthOffset = (-0.1f * filledAmount) + 0.5f;
		float floorOffset = 0.06f - (filledAmount * 0.06f);
		CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius, minWidthOffset - floorOffset, 0.5f - radius), new Vector3f(diameter, minWidth, diameter), fluidColor, matrixStack, sprite);
	}

	protected void drawExtensions(Direction side, TextureAtlasSprite sprite, float filledAmount, float radius, Color fluidColor, MatrixStack matrixStack) {
		float diameter = (radius * 2.0f) - 0.01f;
		radius -= 0.005f;
		float yAxisOffset = radius * (1.0f - filledAmount);

		if (side == Direction.SOUTH) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius, 0.5f - radius, 0.39f), new Vector3f(diameter, diameter * filledAmount, 0.6f), fluidColor, matrixStack, sprite);
		} else if (side == Direction.NORTH) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius, 0.5f - radius, 0.01f), new Vector3f(diameter, diameter * filledAmount, 0.6f), fluidColor, matrixStack, sprite);
		} else if (side == Direction.DOWN) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius + yAxisOffset, 0.0f, 0.5f - radius + yAxisOffset), new Vector3f(diameter * filledAmount, 0.6f, diameter * filledAmount), fluidColor, matrixStack, sprite);
		} else if (side == Direction.UP) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius + yAxisOffset, 0.39f, 0.5f - radius + yAxisOffset), new Vector3f(diameter * filledAmount, 0.6f, diameter * filledAmount), fluidColor, matrixStack, sprite);
		} else if (side == Direction.WEST) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.01f, 0.5f - radius, 0.5f - radius), new Vector3f(0.6f, diameter * filledAmount, diameter), fluidColor, matrixStack, sprite);
		} else if (side == Direction.EAST) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.39f, 0.5f - radius, 0.5f - radius), new Vector3f(0.6f, diameter * filledAmount, diameter), fluidColor, matrixStack, sprite);
		}
	}
}
