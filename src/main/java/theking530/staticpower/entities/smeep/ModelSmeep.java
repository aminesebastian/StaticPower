package theking530.staticpower.entities.smeep;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSmeep extends QuadrupedModel<EntitySmeep> {
	private float headRotationAngleX;

	public ModelSmeep() {
		super(12, 0.0F, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
		this.head = new ModelPart(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F, 0.0F);
		this.head.setPos(0.0F, 6.0F, -8.0F);
		this.body = new ModelPart(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, 0.0F);
		this.body.setPos(0.0F, 5.0F, 2.0F);
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
