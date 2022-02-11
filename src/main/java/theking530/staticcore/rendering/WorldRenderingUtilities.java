package theking530.staticcore.rendering;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.digistorenetwork.digistore.TileEntityDigistore;

public class WorldRenderingUtilities {
	public static void drawItemInWorld(TileEntityBase tileEntity, ItemStack item, TransformType transformType, Vector3D offset, Vector3D scale, float partialTicks, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		drawItemInWorld(tileEntity, item, transformType, offset, scale, Vector3D.ZERO, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
	}

	/**
	 * Renders the provided {@link ItemStack} in the world.
	 * 
	 * @param item            The {@link ItemStack} to render.
	 * @param offset          The offset to apply in world space.
	 * @param partialTicks    The partial ticks.
	 * @param matrixStack     The matrix stack to use for transformations.
	 * @param buffer          The buffer type.
	 * @param combinedLight   The combined light at the block this
	 *                        {@link TileEntity} is rendering at.
	 * @param combinedOverlay The combined overlay.
	 */
	public static void drawItemInWorld(TileEntityBase tileEntity, ItemStack item, TransformType transformType, Vector3D offset, Vector3D scale, Vector3D rotation, float partialTicks,
			PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
		matrixStack.mulPose(new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), true));
		BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(item, tileEntity.getLevel(), null, combinedLight); // TODO: Figure out if the last parameter here is correct.
		Minecraft.getInstance().getItemRenderer().render(item, transformType, false, matrixStack, buffer, combinedLight, combinedOverlay, itemModel);
		matrixStack.popPose();
	}

	public static void drawFlatItemInWorld(TileEntityDigistore tile, @Nonnull ItemStack itemStack, Vector3D offset, Vector2D scale, float partialTickTime, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		// Skip empty items.
		if (itemStack.isEmpty()) {
			return;
		}

		// Move and scale the item given the provided values.
		matrixStack.pushPose();
		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), 0.01f);

		// Get the baked model and check if it wants to render the item in 3d or 2d.
		BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, combinedLight); // TODO: Figure out if the last parameter here is correct.
		boolean render3D = itemModel.isGui3d();

		// Thank the lord for Storage Drawers
		// (https://github.com/jaquadro/StorageDrawers/blob/1.15/src/main/java/com/jaquadro/minecraft/storagedrawers/client/renderer/TileEntityDrawersRenderer.java).
		// Spent hours on the bug that's caused when you dont have this called before
		// and after.
		if (buffer instanceof MultiBufferSource.BufferSource) {
			((MultiBufferSource.BufferSource) buffer).endBatch();
		}

		// Set the correct lighting values.
		if (render3D) {
			Lighting.setupFor3DItems();
		} else {
			Lighting.setupForFlatItems();
		}

		// Invert the normals of the model on the Y-Axis.
		matrixStack.last().normal().load(Matrix3f.createScaleMatrix(1, -1, 1));

		// Render the item.
		Minecraft.getInstance().getItemRenderer().render(itemStack, ItemTransforms.TransformType.GUI, false, matrixStack, buffer, combinedLight, combinedOverlay, itemModel);

		// Finish the buffer again (thanks again to Storage Drawers).
		if (buffer instanceof MultiBufferSource.BufferSource) {
			((MultiBufferSource.BufferSource) buffer).endBatch();
		}

		// Pop the matrix we pushed.
		matrixStack.popPose();
	}

	/**
	 * Renders a string in the world.
	 * 
	 * @param text            The text to render.
	 * @param tileEntity
	 * @param color
	 * @param offset
	 * @param scale
	 * @param partialTicks
	 * @param matrixStack
	 * @param buffer
	 * @param combinedLight
	 * @param combinedOverlay
	 */
	public static void drawTextInWorld(BlockEntityRenderDispatcher renderDispatcher, String text, TileEntityBase tileEntity, Color color, Vector3D offset, float scale, float partialTicks,
			PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (text == null || text.isEmpty())
			return;

		int textWidth = renderDispatcher.font.width(text);

		matrixStack.pushPose();
		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180, true));
		matrixStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), 180, true));
		matrixStack.scale(scale, scale, scale);

		renderDispatcher.font.drawInBatch(text, -textWidth / 2f, 0.5f, color.encodeInInteger(), false, matrixStack.last().pose(), buffer, false, 0, 15728880); // 15728880

		matrixStack.popPose();
	}

	/**
	 * Draws a texture quad at the face of the block.
	 * 
	 * @param texture       The sprite to draw.
	 * @param matrixStack   The matrix stack to work with.
	 * @param buffer        The buffer to draw to.
	 * @param offset        The offset from the face of the block.
	 * @param scale         The scale.
	 * @param tint          The tint to apply.
	 * @param combinedLight The combined light level at the block.
	 */
	public static void drawTexturedQuadLit(ResourceLocation texture, PoseStack matrixStack, MultiBufferSource buffer, Vector3D offset, Vector3D scale, Vector4D uv, Color tint, int combinedLight) {
		matrixStack.pushPose();
		VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
		@SuppressWarnings("deprecation")
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);

		float uDelta = sprite.getU1() - sprite.getU0();
		float vDelta = sprite.getV1() - sprite.getV0();

		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
		builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.uv(sprite.getU0() + (uDelta) * uv.getZ(), sprite.getV0() + (vDelta) * uv.getY()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.uv(sprite.getU0() + (uDelta) * uv.getZ(), sprite.getV0() + (vDelta) * uv.getW()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.uv(sprite.getU0() + (uDelta) * uv.getX(), sprite.getV0() + (vDelta) * uv.getW()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.uv(sprite.getU0() + (uDelta) * uv.getX(), sprite.getV0() + (vDelta) * uv.getY()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		matrixStack.popPose();
	}

	public static void drawTexturedQuadUnlit(ResourceLocation texture, PoseStack matrixStack, MultiBufferSource buffer, Vector3D offset, Vector3D scale, Vector4D uv, Color tint) {
		drawTexturedQuadLit(texture, matrixStack, buffer, offset, scale, uv, tint, 15728880);
	}

	public static void drawRectangleLit(PoseStack matrixStack, MultiBufferSource buffer, Vector3D offset, Vector3D scale, Color tint, int combinedLight) {
		matrixStack.pushPose();
		VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
		builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv2(combinedLight).normal(1, 0, 0).endVertex();
		matrixStack.popPose();
	}

	public static void drawRectangleUnlit(PoseStack matrixStack, MultiBufferSource buffer, Vector3D offset, Vector3D scale, Color color) {
		drawRectangleLit(matrixStack, buffer, offset, scale, color, 15728880);
	}

	public static int getForwardFacingLightLevel(TileEntityBase tileEntity) {
		return LevelRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().relative(tileEntity.getFacingDirection()));
	}

	public static void drawFluidQuadLit(FluidStack fluid, PoseStack matrixStack, MultiBufferSource buffer, Vector3D offset, Vector3D scale, Vector4D uv, int lightlevel) {
		Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);
		TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(fluid);
		drawTexturedQuadLit(icon.getName(), matrixStack, buffer, offset, scale, uv, fluidColor, lightlevel);
	}

	public static void drawFluidQuadUnlit(FluidStack fluid, PoseStack matrixStack, MultiBufferSource buffer, Vector3D offset, Vector3D scale, Vector4D uv) {
		drawFluidQuadLit(fluid, matrixStack, buffer, offset, scale, uv, 15728880);
	}

	public static void drawLine(PoseStack matrixStack, MultiBufferSource buffer, Vector3D start, Vector3D end, float thickness, Color color) {
		// TODO: Find a workaround for this! GlStateManager._lineWidth(thickness);
		VertexConsumer builder = buffer.getBuffer(RenderType.lines());
		builder.vertex(matrixStack.last().pose(), start.getX(), start.getY(), start.getZ()).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).normal(1.0f, 0.0f, 0.0f)
				.endVertex();
		builder.vertex(matrixStack.last().pose(), end.getX(), end.getY(), end.getZ()).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).normal(1.0f, 0.0f, 0.0f).endVertex();
	}

	/**
	 * Rotates the last matrix on the provided stack to face the provided direction.
	 * 
	 * @param side
	 * @param matrixStack
	 */
	public static void rotateMatrixToFaceSide(Direction side, PoseStack matrixStack) {
		if (side == Direction.WEST) {
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -90, true));
			matrixStack.translate(0, 0, -1);
		} else if (side == Direction.NORTH) {
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180, true));
			matrixStack.translate(-1, 0, -1);
		} else if (side == Direction.EAST) {
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), 90, true));
			matrixStack.translate(-1, 0, 0);
		}
	}
}
