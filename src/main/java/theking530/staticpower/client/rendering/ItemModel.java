package theking530.staticpower.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

public abstract class ItemModel implements BakedModel {

	@Override
	public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack pStack, boolean leftFlip) {
		return BakedModel.super.applyTransform(transformType, pStack, leftFlip);
	}
}
