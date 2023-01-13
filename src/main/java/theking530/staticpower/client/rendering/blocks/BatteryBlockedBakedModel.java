package theking530.staticpower.client.rendering.blocks;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatteryBlockedBakedModel extends DefaultMachineBakedModel {
	public BatteryBlockedBakedModel(BakedModel baseModel) {
		super(baseModel, true);
	}
}
