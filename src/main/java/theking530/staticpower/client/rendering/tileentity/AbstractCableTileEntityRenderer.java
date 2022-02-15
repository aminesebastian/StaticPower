package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.item.ItemRoutingParcelClient;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.TileEntityBase;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCableTileEntityRenderer<T extends TileEntityBase> extends StaticPowerTileEntitySpecialRenderer<T> {
	protected static final BlockModel CUBE_MODEL = new BlockModel();
	protected static final Vector3D BLOCK_RENDER_SCALE = new Vector3D(0.3f, 0.3f, 0.3f);
	protected static final Vector3D ITEM_RENDER_SCALE = new Vector3D(0.25f, 0.25f, 0.25f);

	public AbstractCableTileEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
		this.shouldPreRotateTowardsFacingDirection = false;
	}

	protected void renderItemRoutingParcel(ItemRoutingParcelClient packet, T te, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {

		// Get the travel direction and return early if one does not exist.
		Direction dir = packet.getItemAnimationDirection();
		if (dir == null) {
			return;
		}

		// Capture the lerp and rotation values.
		float lerpValue = packet.getItemMoveLerp() + (partialTicks / (packet.getCurrentMoveTime() / 2));
		float renderRotation = packet.getRenderRotation(partialTicks);

		// Get the baked model and check if it wants to render the item in 3d or 2d.
		BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(packet.getContainedItem(), null, null, combinedOverlay);
		boolean render3D = itemModel.isGui3d();

		// Determine which scale to use when drawing.
		if (render3D) {
			WorldRenderingUtilities.drawItemInWorld(te, packet.getContainedItem(), TransformType.FIXED, getItemParcelAnimationOffset(lerpValue, dir), BLOCK_RENDER_SCALE, getItemParcelRotation(renderRotation),
					partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		} else {
			WorldRenderingUtilities.drawItemInWorld(te, packet.getContainedItem(), TransformType.FIXED, getItemParcelAnimationOffset(lerpValue, dir), ITEM_RENDER_SCALE, getItemParcelRotation(renderRotation),
					partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
	}

	protected Vector3D getItemParcelAnimationOffset(float lerpValue, Direction dir) {
		Vector3D baseOffset = new Vector3D(0.5f, 0.5f, 0.5f);
		Vector3D directionVector = new Vector3D(dir);
		directionVector.multiply(0.5f);
		directionVector.multiply(lerpValue);
		baseOffset.add(directionVector);
		return baseOffset;
	}

	protected Vector3D getItemParcelRotation(float rotation) {
		return new Vector3D(0, rotation, 0);
	}

	protected void drawFluidCable(FluidStack fluid, float filledPercentage, float radius, PoseStack matrixStack, AbstractCableProviderComponent cableComponent) {
		TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
		Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);
		fluidColor.setW(1.0f); // Force the opacity  to 1.0f to have the texture control the opaicty.
		
		boolean wasExtensionDrawn = false;
		for (Direction dir : Direction.values()) {
			if (cableComponent.getConnectionState(dir) != CableConnectionState.NONE && !cableComponent.isSideDisabled(dir)) {
				wasExtensionDrawn = true;
				drawExtensions(dir, sprite, filledPercentage, radius, fluidColor, matrixStack);
			}
		}
		if (!wasExtensionDrawn) {
			drawFluidCore(sprite, filledPercentage, radius, fluidColor, matrixStack);
		}
	}

	protected void drawFluidCore(TextureAtlasSprite sprite, float filledAmount, float radius, Color fluidColor, PoseStack matrixStack) {
		float diameter = radius * 2.0f - 0.01f;
		radius -= 0.005f;
		float minWidth = 0.22f * filledAmount;
		float minWidthOffset = (-0.1f * filledAmount) + 0.5f;
		float floorOffset = 0.06f - (filledAmount * 0.06f);
		CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius, minWidthOffset - floorOffset, 0.5f - radius), new Vector3f(diameter, minWidth, diameter), fluidColor, matrixStack, sprite);
	}

	protected void drawExtensions(Direction side, TextureAtlasSprite sprite, float filledAmount, float radius, Color fluidColor, PoseStack matrixStack) {
		float diameter = (radius * 2.0f) - 0.01f;
		radius -= 0.005f;
		float yAxisOffset = radius * (1.0f - filledAmount);

		if (side == Direction.SOUTH) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius, 0.5f - radius, 0.39f), new Vector3f(diameter, diameter * filledAmount, 0.6f), fluidColor, matrixStack, sprite);
		} else if (side == Direction.NORTH) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius, 0.5f - radius, 0.01f), new Vector3f(diameter, diameter * filledAmount, 0.6f), fluidColor, matrixStack, sprite);
		} else if (side == Direction.DOWN) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius + yAxisOffset, 0.0f, 0.5f - radius + yAxisOffset), new Vector3f(diameter * filledAmount, 0.6f, diameter * filledAmount),
					fluidColor, matrixStack, sprite);
		} else if (side == Direction.UP) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.5f - radius + yAxisOffset, 0.39f, 0.5f - radius + yAxisOffset), new Vector3f(diameter * filledAmount, 0.6f, diameter * filledAmount),
					fluidColor, matrixStack, sprite);
		} else if (side == Direction.WEST) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.01f, 0.5f - radius, 0.5f - radius), new Vector3f(0.6f, diameter * filledAmount, diameter), fluidColor, matrixStack, sprite);
		} else if (side == Direction.EAST) {
			CUBE_MODEL.drawPreviewCube(new Vector3f(0.39f, 0.5f - radius, 0.5f - radius), new Vector3f(0.6f, diameter * filledAmount, diameter), fluidColor, matrixStack, sprite);
		}
	}
}
