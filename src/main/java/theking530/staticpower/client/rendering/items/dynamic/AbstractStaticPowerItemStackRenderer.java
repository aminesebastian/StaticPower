package theking530.staticpower.client.rendering.items.dynamic;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import theking530.staticpower.client.rendering.items.ItemCustomRendererPassthroughModel;

public abstract class AbstractStaticPowerItemStackRenderer extends ItemStackTileEntityRenderer {
	protected static final float TEXEL = (1.0f / 16.0f);

	@SuppressWarnings("deprecation")
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// Get the base model.
		IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, Minecraft.getInstance().world, null);

		// If its a passthrough model, grab the original model.
		if (itemModel instanceof ItemCustomRendererPassthroughModel) {
			// Get the original model.
			ItemCustomRendererPassthroughModel passthroughModel = (ItemCustomRendererPassthroughModel) itemModel;
			IBakedModel baseModel = passthroughModel.getBaseModel();

			// Get the transforms for the requested transform type.
			Vector3f translation = baseModel.getItemCameraTransforms().getTransform(transformType).translation;
			Vector3f scale = baseModel.getItemCameraTransforms().getTransform(transformType).scale;
			Vector3f rotation = baseModel.getItemCameraTransforms().getTransform(transformType).rotation;

			// Push a new matrix and move to the center.
			matrixStack.push();
			matrixStack.translate(0.5f, 0.5f, 0.5f);

			// Render the base item mode.
			renderItemModel(stack, transformType, true, matrixStack, buffer, Atlases.getTranslucentCullBlockType(), combinedLight, combinedOverlay, baseModel);

			// Transform the matrix using the transform type transforms.
			matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
			matrixStack.translate(translation.getX(), translation.getY(), translation.getZ());
			matrixStack.rotate(new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), true));
			if (transformType != ItemCameraTransforms.TransformType.GUI) {
				matrixStack.translate(-0.5f, -0.5f, -0.5f);
			} else {
				matrixStack.translate(-0.5f, -0.5f, -0.5f);
			}

			// Render anything custom.
			render(stack, baseModel, transformType, matrixStack, buffer, combinedLight, combinedOverlay);

			// Pop the matrix.
			matrixStack.pop();
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
	public abstract void render(ItemStack stack, IBakedModel defaultModel, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer,
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
	public void renderBaseModel(ItemStack stack, IBakedModel defaultModel, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer,
			int combinedLight, int combinedOverlay) {
		renderItemModel(stack, transformType, true, matrixStack, buffer, Atlases.getTranslucentCullBlockType(), combinedLight, combinedOverlay, defaultModel);
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
	protected void renderItemModel(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			RenderType renderType, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn) {
		if (!itemStackIn.isEmpty()) {
			matrixStackIn.push();
			boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI || transformTypeIn == ItemCameraTransforms.TransformType.GROUND
					|| transformTypeIn == ItemCameraTransforms.TransformType.FIXED;
			if (itemStackIn.getItem() == Items.TRIDENT && flag) {
				modelIn = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
			}

			modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, transformTypeIn, leftHand);
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			if (!modelIn.isBuiltInRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag)) {
				boolean flag1;
				if (transformTypeIn != ItemCameraTransforms.TransformType.GUI && !transformTypeIn.isFirstPerson() && itemStackIn.getItem() instanceof BlockItem) {
					Block block = ((BlockItem) itemStackIn.getItem()).getBlock();
					flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
				} else {
					flag1 = true;
				}
				if (modelIn.isLayered()) {
					net.minecraftforge.client.ForgeHooksClient.drawItemLayered(Minecraft.getInstance().getItemRenderer(), modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn,
							combinedOverlayIn, flag1);
				} else {
					RenderType rendertype = renderType;
					IVertexBuilder ivertexbuilder;
					if (itemStackIn.getItem() == Items.COMPASS && itemStackIn.hasEffect()) {
						matrixStackIn.push();
						MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
						if (transformTypeIn == ItemCameraTransforms.TransformType.GUI) {
							matrixstack$entry.getMatrix().mul(0.5F);
						} else if (transformTypeIn.isFirstPerson()) {
							matrixstack$entry.getMatrix().mul(0.75F);
						}

						if (flag1) {
							ivertexbuilder = ItemRenderer.getDirectGlintVertexBuilder(bufferIn, rendertype, matrixstack$entry);
						} else {
							ivertexbuilder = ItemRenderer.getGlintVertexBuilder(bufferIn, rendertype, matrixstack$entry);
						}

						matrixStackIn.pop();
					} else if (flag1) {
						ivertexbuilder = ItemRenderer.getEntityGlintVertexBuilder(bufferIn, rendertype, true, itemStackIn.hasEffect());
					} else {
						ivertexbuilder = ItemRenderer.getBuffer(bufferIn, rendertype, true, itemStackIn.hasEffect());
					}

					Minecraft.getInstance().getItemRenderer().renderModel(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
				}
			} else {
				itemStackIn.getItem().getItemStackTileEntityRenderer().func_239207_a_(itemStackIn, transformTypeIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			}

			matrixStackIn.pop();
		}
	}
}
