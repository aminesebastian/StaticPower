package theking530.staticpower.entities.smeep;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

@OnlyIn(Dist.CLIENT)
public class WoolLayerSmeep extends RenderLayer<EntitySmeep, ModelSmeep> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(StaticPower.MOD_ID, "textures/entity/smeep/smeep_fur.png");
	private final SmeepWoolModel smeepModel = new SmeepWoolModel();

	public WoolLayerSmeep(RenderLayerParent<EntitySmeep, ModelSmeep> rendererIn) {
		super(rendererIn);
	}

	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySmeep entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entitylivingbaseIn.isSheared() && !entitylivingbaseIn.isInvisible()) {
			coloredCutoutModelCopyLayerRender(this.getParentModel(), this.smeepModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks,
					netHeadYaw, headPitch, partialTicks, 1.0f, 1.0f, 1.0f);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class SmeepWoolModel extends QuadrupedModel<EntitySmeep> {
		private float headRotationAngleX;

		public SmeepWoolModel() {
			super(12, 0.0F, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
			this.head = new ModelPart(this, 0, 0);
			this.head.addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, 0.6F);
			this.head.setPos(0.0F, 6.0F, -8.0F);
			this.body = new ModelPart(this, 28, 8);
			this.body.addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, 1.75F);
			this.body.setPos(0.0F, 5.0F, 2.0F);
			this.leftFrontLeg = new ModelPart(this, 0, 16);
			this.leftFrontLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.leftFrontLeg.setPos(-3.0F, 12.0F, 7.0F);
			this.rightFrontLeg = new ModelPart(this, 0, 16);
			this.rightFrontLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.rightFrontLeg.setPos(3.0F, 12.0F, 7.0F);
			this.leftHindLeg = new ModelPart(this, 0, 16);
			this.leftHindLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.leftHindLeg.setPos(-3.0F, 12.0F, -5.0F);
			this.rightHindLeg = new ModelPart(this, 0, 16);
			this.rightHindLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
			this.rightHindLeg.setPos(3.0F, 12.0F, -5.0F);
		}

		public void prepareMobModel(EntitySmeep entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
			super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
			this.head.y = 6.0F + entityIn.getHeadEatPositionScale(partialTick) * 9.0F;
			this.headRotationAngleX = entityIn.getHeadEatAngleScale(partialTick);
		}

		/**
		 * Sets this entity's model rotation angles
		 */
		public void setupAnim(EntitySmeep entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			this.head.xRot = this.headRotationAngleX;
		}
	}

}
