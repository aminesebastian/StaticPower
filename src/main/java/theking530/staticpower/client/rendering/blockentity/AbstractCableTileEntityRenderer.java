package theking530.staticpower.client.rendering.blockentity;

import java.util.List;

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
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.cablenetwork.CableRenderingState;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.item.ItemRoutingParcelClient;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCableTileEntityRenderer<T extends BlockEntityBase> extends StaticPowerBlockEntitySpecialRenderer<T> {
	protected static final Vector3D BLOCK_RENDER_SCALE = new Vector3D(0.3f, 0.3f, 0.3f);
	protected static final Vector3D ITEM_RENDER_SCALE = new Vector3D(0.25f, 0.25f, 0.25f);

	public AbstractCableTileEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
		this.shouldPreRotateTowardsFacingDirection = false;
	}

	protected void renderItemRoutingParcel(ItemRoutingParcelClient packet, T te, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLight, int combinedOverlay) {

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
			WorldRenderingUtilities.drawItemInWorld(te, packet.getContainedItem(), TransformType.FIXED, getItemParcelAnimationOffset(lerpValue, dir), BLOCK_RENDER_SCALE,
					getItemParcelRotation(renderRotation), partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		} else {
			WorldRenderingUtilities.drawItemInWorld(te, packet.getContainedItem(), TransformType.FIXED, getItemParcelAnimationOffset(lerpValue, dir), ITEM_RENDER_SCALE,
					getItemParcelRotation(renderRotation), partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
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

	protected void drawFluidCable(BlockEntity entity, FluidStack fluid, float filledPercentage, float radius, PoseStack pose, AbstractCableProviderComponent cableComponent) {
		TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
		SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
		fluidColor.setAlpha(1.0f); // Force the opacity to 1.0f to have the texture control the opaicty.

		if (filledPercentage < 0.1f) {
			fluidColor.setAlpha(filledPercentage * 10);
		}

		ModelData data = entity.getModelData();
		CableRenderingState renderingState = data.get(AbstractCableProviderComponent.CABLE_RENDERING_STATE);
		boolean isStraightConnection = CableUtilities.isCableStraightConnection(entity.getBlockState(), renderingState);
		if (isStraightConnection) {
			Direction straightConnectionSide = CableUtilities.getStraightConnectionSide(entity.getBlockState(), renderingState);
			if (straightConnectionSide.getAxis() == Axis.Y) {
				drawVerticalStraightConnection(pose, sprite, filledPercentage, radius, fluidColor);
			} else {
				drawHorizontalStraightConnection(pose, straightConnectionSide.getAxis(), sprite, filledPercentage, radius, fluidColor);
			}

		} else {
			List<Direction> connectedSides = CableUtilities.getConnectedSides(entity.getBlockState());
			if (connectedSides.size() == 1) {
				drawExtension(pose, connectedSides.get(0), sprite, filledPercentage, radius, fluidColor);
			} else {
				// Check to see if all extensions are on the horizontal plane.
				boolean areAllExtensionsHorizontal = connectedSides.stream().map((dir) -> dir.getAxis()).filter((axis) -> axis == Axis.Y).count() == 0;
				if (areAllExtensionsHorizontal) {
					drawCore(pose, sprite, filledPercentage, radius, fluidColor);
					for (Direction dir : connectedSides) {
						drawExtension(pose, dir, sprite, filledPercentage, radius, fluidColor);
					}
				} else {

				}
			}
		}
	}

	protected void drawVerticalStraightConnection(PoseStack pose, TextureAtlasSprite fluidSprite, float fillPercentage, float radius, SDColor color) {
		radius -= 0.005f;
		float diameter = radius * 2.0f - 0.01f;
		BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0, 0.5f - radius), new Vector3f(diameter, fillPercentage, diameter), color, fluidSprite);
	}

	protected void drawHorizontalStraightConnection(PoseStack pose, Axis axis, TextureAtlasSprite fluidSprite, float fillPercentage, float radius, SDColor color) {
		radius -= 0.005f;
		float diameter = radius * 2.0f - 0.01f;

		if (axis == Axis.X) {
			BlockModel.drawCubeInWorld(pose, new Vector3f(0, 0.5f - radius, 0.5f - radius), new Vector3f(1, diameter * fillPercentage, diameter), color, fluidSprite);
		} else {
			BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.5f - radius, 0), new Vector3f(diameter, diameter * fillPercentage, 1), color, fluidSprite);
		}
	}

	protected void drawCore(PoseStack pose, TextureAtlasSprite fluidSprite, float fillPercentage, float radius, SDColor color) {
		radius -= 0.005f;
		float diameter = radius * 2.0f - 0.01f;

		// Draw the core
		BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.5f - radius, 0.5f - radius), new Vector3f(diameter, diameter * fillPercentage, diameter), color,
				fluidSprite);
	}

	protected void drawExtension(PoseStack pose, Direction side, TextureAtlasSprite sprite, float fillPercentage, float radius, SDColor fluidColor) {
		float diameter = (radius * 2.0f) - 0.01f;
		radius -= 0.005f;

		if (side.getAxis() == Axis.Y) {
			if (side == Direction.UP) {
				BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.5f - radius, 0.5f - radius), new Vector3f(diameter, 0.5f * fillPercentage + radius, diameter),
						fluidColor, sprite);
			} else {
				BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.0f, 0.5f - radius), new Vector3f(diameter, (0.5f + radius) * fillPercentage, diameter), fluidColor,
						sprite);
			}
		} else {
			if (side == Direction.EAST) {
				BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.5f - radius, 0.5f - radius), new Vector3f(0.5f + radius, diameter * fillPercentage, diameter),
						fluidColor, sprite);
			}

			if (side == Direction.WEST) {
				BlockModel.drawCubeInWorld(pose, new Vector3f(0.0f, 0.5f - radius, 0.5f - radius), new Vector3f(0.5f + radius, diameter * fillPercentage, diameter), fluidColor,
						sprite);
			}

			if (side == Direction.NORTH) {
				BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.5f - radius, 0.0f), new Vector3f(diameter, diameter * fillPercentage, 0.5f + radius), fluidColor,
						sprite);
			}

			if (side == Direction.SOUTH) {
				BlockModel.drawCubeInWorld(pose, new Vector3f(0.5f - radius, 0.5f - radius, 0.5f - radius), new Vector3f(diameter, diameter * fillPercentage, 0.5f + radius),
						fluidColor, sprite);
			}
		}
	}
}
