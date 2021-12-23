package theking530.staticpower.client.rendering.items.dynamic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import theking530.staticpower.client.rendering.items.ItemCustomRendererPassthroughModel;

public abstract class AbstractStaticPowerItemStackRenderer extends BlockEntityWithoutLevelRenderer {
	protected static final float TEXEL = (1.0f / 16.0f);

	@SuppressWarnings("deprecation")
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		// Get the base model.
		BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, null);

		// If its a passthrough model, grab the original model.
		if (itemModel instanceof ItemCustomRendererPassthroughModel) {
			// Get the original model.
			ItemCustomRendererPassthroughModel passthroughModel = (ItemCustomRendererPassthroughModel) itemModel;
			BakedModel baseModel = passthroughModel.getBaseModel();

			// Get the transforms for the requested transform type.
			Vector3f translation = baseModel.getTransforms().getTransform(transformType).translation;
			Vector3f scale = baseModel.getTransforms().getTransform(transformType).scale;
			Vector3f rotation = baseModel.getTransforms().getTransform(transformType).rotation;

			// Push a new matrix and move to the center.
			matrixStack.pushPose();
			matrixStack.translate(0.5f, 0.5f, 0.5f);

			// Render the base item mode.
			renderItemModel(stack, transformType, true, matrixStack, buffer, Sheets.translucentCullBlockSheet(), combinedLight, combinedOverlay, baseModel);

			// Transform the matrix using the transform type transforms.
			matrixStack.scale(scale.x(), scale.y(), scale.z());
			matrixStack.translate(translation.x(), translation.y(), translation.z());
			matrixStack.mulPose(new Quaternion(rotation.x(), rotation.y(), rotation.z(), true));
			if (transformType != ItemTransforms.TransformType.GUI) {
				matrixStack.translate(-0.5f, -0.5f, -0.5f);
			} else {
				matrixStack.translate(-0.5f, -0.5f, -0.5f);
			}

			// Render anything custom.
			render(stack, baseModel, transformType, matrixStack, buffer, combinedLight, combinedOverlay);

			// Pop the matrix.
			matrixStack.popPose();
		}
	}

	/**
	 * This method should be overriden to write any custom rendering.
	 * 
	 * @param stack
	 * @param defaultModel
	 * @param transformType
	 * @param matrixStack
	 * @param buffer
	 * @param combinedLight
	 * @param combinedOverlay
	 */
	public abstract void render(ItemStack stack, BakedModel defaultModel, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLight, int combinedOverlay);

	/**
	 * This method is exposed such that an implementer may need to change the
	 * default item rendering behaviour (the model defined in the model file).
	 * 
	 * @param stack
	 * @param defaultModel
	 * @param transformType
	 * @param matrixStack
	 * @param buffer
	 * @param combinedLight
	 * @param combinedOverlay
	 */
	public void renderBaseModel(ItemStack stack, BakedModel defaultModel, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLight, int combinedOverlay) {
		renderItemModel(stack, transformType, true, matrixStack, buffer, Sheets.translucentCullBlockSheet(), combinedLight, combinedOverlay, defaultModel);
	}

	/**
	 * Renders the provided item.
	 * 
	 * @param itemStackIn
	 * @param transformTypeIn
	 * @param leftHand
	 * @param matrixStackIn
	 * @param bufferIn
	 * @param renderType
	 * @param combinedLightIn
	 * @param combinedOverlayIn
	 * @param modelIn
	 */
	protected void renderItemModel(ItemStack itemStackIn, ItemTransforms.TransformType transformTypeIn, boolean leftHand, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			RenderType renderType, int combinedLightIn, int combinedOverlayIn, BakedModel modelIn) {
		if (!itemStackIn.isEmpty()) {
			matrixStackIn.pushPose();
			boolean flag = transformTypeIn == ItemTransforms.TransformType.GUI || transformTypeIn == ItemTransforms.TransformType.GROUND
					|| transformTypeIn == ItemTransforms.TransformType.FIXED;
			if (itemStackIn.getItem() == Items.TRIDENT && flag) {
				modelIn = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
			}

			modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, transformTypeIn, leftHand);
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			if (!modelIn.isCustomRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag)) {
				boolean flag1;
				if (transformTypeIn != ItemTransforms.TransformType.GUI && !transformTypeIn.firstPerson() && itemStackIn.getItem() instanceof BlockItem) {
					Block block = ((BlockItem) itemStackIn.getItem()).getBlock();
					flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
				} else {
					flag1 = true;
				}
				if (modelIn.isLayered()) {
					net.minecraftforge.client.ForgeHooksClient.drawItemLayered(Minecraft.getInstance().getItemRenderer(), modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn,
							combinedOverlayIn, flag1);
				} else {
					RenderType rendertype = renderType;
					VertexConsumer ivertexbuilder;
					if (itemStackIn.getItem() == Items.COMPASS && itemStackIn.hasFoil()) {
						matrixStackIn.pushPose();
						PoseStack.Pose matrixstack$entry = matrixStackIn.last();
						if (transformTypeIn == ItemTransforms.TransformType.GUI) {
							matrixstack$entry.pose().multiply(0.5F);
						} else if (transformTypeIn.firstPerson()) {
							matrixstack$entry.pose().multiply(0.75F);
						}

						if (flag1) {
							ivertexbuilder = ItemRenderer.getCompassFoilBufferDirect(bufferIn, rendertype, matrixstack$entry);
						} else {
							ivertexbuilder = ItemRenderer.getCompassFoilBuffer(bufferIn, rendertype, matrixstack$entry);
						}

						matrixStackIn.popPose();
					} else if (flag1) {
						ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, rendertype, true, itemStackIn.hasFoil());
					} else {
						ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, rendertype, true, itemStackIn.hasFoil());
					}

					Minecraft.getInstance().getItemRenderer().renderModelLists(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
				}
			} else {
				itemStackIn.getItem().getItemStackTileEntityRenderer().renderByItem(itemStackIn, transformTypeIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			}

			matrixStackIn.popPose();
		}
	}
}
