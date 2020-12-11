package theking530.staticpower.entities;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

@OnlyIn(Dist.CLIENT)
public class SmeepWoolLayer extends LayerRenderer<EntitySmeep, SmeepModel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(StaticPower.MOD_ID, "textures/entity/smeep/smeep_fur.png");
	private final SmeepWoolModel smeepModel = new SmeepWoolModel();

	public SmeepWoolLayer(IEntityRenderer<EntitySmeep, SmeepModel> rendererIn) {
		super(rendererIn);
	}

	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntitySmeep entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
			renderCopyCutoutModel(this.getEntityModel(), this.smeepModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks,
					netHeadYaw, headPitch, partialTicks, 1.0f, 1.0f, 1.0f);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class SmeepWoolModel extends QuadrupedModel<EntitySmeep> {
		private float headRotationAngleX;

		public SmeepWoolModel() {
			super(12, 0.0F, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
			this.headModel = new ModelRenderer(this, 0, 0);
			this.headModel.addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, 0.6F);
			this.headModel.setRotationPoint(0.0F, 6.0F, -8.0F);
			this.body = new ModelRenderer(this, 28, 8);
			this.body.addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, 1.75F);
			this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
			this.legBackRight = new ModelRenderer(this, 0, 16);
			this.legBackRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.legBackRight.setRotationPoint(-3.0F, 12.0F, 7.0F);
			this.legBackLeft = new ModelRenderer(this, 0, 16);
			this.legBackLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.legBackLeft.setRotationPoint(3.0F, 12.0F, 7.0F);
			this.legFrontRight = new ModelRenderer(this, 0, 16);
			this.legFrontRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.legFrontRight.setRotationPoint(-3.0F, 12.0F, -5.0F);
			this.legFrontLeft = new ModelRenderer(this, 0, 16);
			this.legFrontLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.legFrontLeft.setRotationPoint(3.0F, 12.0F, -5.0F);
		}

		public void setLivingAnimations(EntitySmeep entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
			super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
			this.headModel.rotationPointY = 6.0F + entityIn.getHeadRotationPointY(partialTick) * 9.0F;
			this.headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);
		}

		/**
		 * Sets this entity's model rotation angles
		 */
		public void setRotationAngles(EntitySmeep entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			this.headModel.rotateAngleX = this.headRotationAngleX;
		}
	}

}
