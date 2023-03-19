package theking530.staticpower.entities.anvilentity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer for conveyor belt entities. We keep the generic parameter as an item
 * entity because in rare cases, conveyor belt entities may convert back into
 * regular entities, and that causes vanilla minecraft to crash.
 * 
 * @author amine
 *
 */
@OnlyIn(Dist.CLIENT)
public class AnvilForgeEntityRenderer extends EntityRenderer<ItemEntity> {
	private final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer;
	private final Random random = new Random();

	public AnvilForgeEntityRenderer(Context context, net.minecraft.client.renderer.entity.ItemRenderer itemRendererIn) {
		super(context);
		this.itemRenderer = itemRendererIn;
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}

	public void render(ItemEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();
		ItemStack itemstack = entityIn.getItem();
		int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
		this.random.setSeed((long) i);
		BakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, entityIn.level, (LivingEntity) null, 0);
		boolean flag = ibakedmodel.isGui3d();
		int j = this.getModelCount(itemstack);

		// Transform blocks differently than items.
		if (entityIn instanceof AnvilForgeEntity) {
			AnvilForgeEntity forgeEntity = (AnvilForgeEntity) entityIn;
			if (forgeEntity.isOnAnvil()) {
				if (ibakedmodel.isGui3d()) {
					matrixStackIn.scale(1.25f, 1.25f, 1.25f);
					matrixStackIn.translate(0.0, -0.01, 0.0);
				} else {
					matrixStackIn.translate(0.0, ((5 - j) * 0.02) - 0.06, -0.1);
					matrixStackIn.scale(1f, 1.1f, 1f);
					matrixStackIn.mulPose(new Quaternion(90, 0, 0, true));
				}
			} else {
				float f1 = Mth.sin(((float) entityIn.getAge() + partialTicks) / 10.0F + entityIn.bobOffs) * 0.1F + 0.1F;
				float f2 = ibakedmodel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
				matrixStackIn.translate(0.0D, (double) (f1 + 0.25F * f2), 0.0D);

				float f3 = entityIn.getSpin(partialTicks);
				matrixStackIn.mulPose(Vector3f.YP.rotation(f3));
			}
		}

		if (!flag) {
			float f7 = -0.0F * (float) (j - 1) * 0.5F;
			float f8 = -0.0F * (float) (j - 1) * 0.5F;
			float f9 = -0.09375F * (float) (j - 1) * 0.5F;
			matrixStackIn.translate((double) f7, (double) f8, (double) f9);
		}

		for (int k = 0; k < j; ++k) {
			matrixStackIn.pushPose();
			if (k > 0) {
				if (flag) {
					float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrixStackIn.translate(shouldSpreadItems() ? f11 : 0, shouldSpreadItems() ? f13 : 0, shouldSpreadItems() ? f10 : 0);
				} else {
					float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					matrixStackIn.translate(shouldSpreadItems() ? f12 : 0, shouldSpreadItems() ? f14 : 0, 0.0D);
				}
			}
			this.itemRenderer.render(itemstack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
			matrixStackIn.popPose();
			if (!flag) {
				matrixStackIn.translate(0.0, 0.0, 0.03F);
			}
		}

		matrixStackIn.popPose();

		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	protected int getModelCount(ItemStack stack) {
		int i = 1;
		if (stack.getCount() > 48) {
			i = 5;
		} else if (stack.getCount() > 32) {
			i = 4;
		} else if (stack.getCount() > 16) {
			i = 3;
		} else if (stack.getCount() > 1) {
			i = 2;
		}

		return i;
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(ItemEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

	/**
	 * @return If items should spread out when rendered in 3D
	 */
	public boolean shouldSpreadItems() {
		return true;
	}
}
