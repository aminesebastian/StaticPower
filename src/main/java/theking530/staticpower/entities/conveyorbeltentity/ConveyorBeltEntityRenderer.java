package theking530.staticpower.entities.conveyorbeltentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
public class ConveyorBeltEntityRenderer extends EntityRenderer<ItemEntity> {
	private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
	private final Random random = new Random();

	public ConveyorBeltEntityRenderer(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn) {
		super(renderManagerIn);
		this.itemRenderer = itemRendererIn;
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	public void render(ItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.push();
		ItemStack itemstack = entityIn.getItem();
		int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
		this.random.setSeed((long) i);
		IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entityIn.world, (LivingEntity) null);
		boolean flag = ibakedmodel.isGui3d();
		int j = this.getModelCount(itemstack);

		// Add a random y offset to deal with stacked items z-fighting.
		if (entityIn instanceof ConveyorBeltEntity) {
			matrixStackIn.translate(0, ((ConveyorBeltEntity) entityIn).getYRenderOffset(), 0);
		}

		// Normalize the motion vector.
		Vector3d normalizedDirection = entityIn.getMotion().normalize();

		// Calculate the rotation angle (and bias it a bit). Also, capture the sign of
		// the angle.
		double velocityFacingRotation = Math.toDegrees(Math.asin(normalizedDirection.getY()));
		double angleSign = Math.signum(velocityFacingRotation);
		if (velocityFacingRotation != 0) {
			velocityFacingRotation += angleSign * 20;
		}

		// Rotate to face the y velocity and bias a bit down.
		matrixStackIn.rotate(new Quaternion((float) (velocityFacingRotation * -normalizedDirection.getZ()), (float) 0, (float) (velocityFacingRotation * normalizedDirection.getX()), true));
		if (angleSign < 0) {
			matrixStackIn.translate(0.0, normalizedDirection.getY() * 0.2f, 0.0);
		} else {
			matrixStackIn.translate(0.0, normalizedDirection.getY() * -0.1f * angleSign, 0.0);
		}

		// Trasnform blocks differently than items.
		if (ibakedmodel.isGui3d()) {
			matrixStackIn.scale(1.25f, 1.25f, 1.25f);
			matrixStackIn.translate(0.0, -0.01, 0.0);
		} else {
			matrixStackIn.translate(0.0, ((5 - j) * 0.02) - 0.06, -0.1);
			matrixStackIn.scale(1f, 1.1f, 1f);
			matrixStackIn.rotate(new Quaternion(90, 0, 0, true));
		}

		float f3 = entityIn.getItemHover(partialTicks);
		matrixStackIn.rotate(Vector3f.YP.rotation(f3));
		if (!flag) {
			float f7 = -0.0F * (float) (j - 1) * 0.5F;
			float f8 = -0.0F * (float) (j - 1) * 0.5F;
			float f9 = -0.09375F * (float) (j - 1) * 0.5F;
			matrixStackIn.translate((double) f7, (double) f8, (double) f9);
		}

		for (int k = 0; k < j; ++k) {
			matrixStackIn.push();
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

			this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
			matrixStackIn.pop();
			if (!flag) {
				matrixStackIn.translate(0.0, 0.0, 0.03F);
			}
		}

		matrixStackIn.pop();
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
	@SuppressWarnings("deprecation")
	public ResourceLocation getEntityTexture(ItemEntity entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}

	/**
	 * @return If items should spread out when rendered in 3D
	 */
	public boolean shouldSpreadItems() {
		return true;
	}
}
