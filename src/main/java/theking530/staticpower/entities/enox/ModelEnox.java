package theking530.staticpower.entities.enox;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelEnox extends QuadrupedModel<EntityEnox> {
	public ModelEnox() {
		super(12, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
		this.head = new ModelPart(this, 0, 0);
		this.head.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, 0.0F);
		this.head.setPos(0.0F, 4.0F, -8.0F);
		this.head.texOffs(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		this.head.texOffs(22, 0).addBox(4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		this.body = new ModelPart(this, 18, 4);
		this.body.addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, 0.0F);
		this.body.setPos(0.0F, 5.0F, 2.0F);
		this.body.texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F);
		--this.leftFrontLeg.x;
		++this.rightFrontLeg.x;
		this.leftFrontLeg.z += 0.0F;
		this.rightFrontLeg.z += 0.0F;
		--this.leftHindLeg.x;
		++this.rightHindLeg.x;
		--this.leftHindLeg.z;
		--this.rightHindLeg.z;
	}

	public ModelPart getHead() {
		return this.head;
	}
}
