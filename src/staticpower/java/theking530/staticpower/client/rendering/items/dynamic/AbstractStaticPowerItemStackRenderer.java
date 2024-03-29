package theking530.staticpower.client.rendering.items.dynamic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import theking530.staticcore.client.models.ItemCustomRendererPassthroughModel;

public abstract class AbstractStaticPowerItemStackRenderer extends BlockEntityWithoutLevelRenderer {
	protected static final float TEXEL = (1.0f / 16.0f);
	protected final BlockEntityRenderDispatcher dispatcher;

	public AbstractStaticPowerItemStackRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
		super(p_172550_, p_172551_);
		dispatcher = p_172550_;
	}

	@SuppressWarnings("deprecation")
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		// Get the base model.
		BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, dispatcher.level, null, 0);

		// If its a passthrough model, grab the original model.
		if (itemModel instanceof ItemCustomRendererPassthroughModel) {
			// Get the original model.
			ItemCustomRendererPassthroughModel passthroughModel = (ItemCustomRendererPassthroughModel) itemModel;
			BakedModel baseModel = passthroughModel.getBaseModel();

			// Push a new matrix and move to the center.
			matrixStack.pushPose();
			matrixStack.translate(0.5f, 0.5f, 0.5f);

			// Render the base item mode.
			renderItemModel(stack, transformType, true, matrixStack, buffer, Sheets.translucentCullBlockSheet(), combinedLight, combinedOverlay, baseModel);

			// Transform the matrix using the transform type transforms.
			baseModel.getTransforms().getTransform(transformType).apply(false, matrixStack);
			matrixStack.translate(-0.5f, -0.5f, -0.5f);

			// Render anything custom.
			render(stack, baseModel, transformType, matrixStack, buffer, combinedLight, combinedOverlay);

			// Pop the matrix.
			matrixStack.popPose();
		}
	}

	/**
	 * This method should be overridden to write any custom rendering.
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

			matrixStackIn.popPose();
		}
	}
}
