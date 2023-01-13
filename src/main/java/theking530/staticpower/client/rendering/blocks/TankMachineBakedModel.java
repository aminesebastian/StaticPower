package theking530.staticpower.client.rendering.blocks;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TankMachineBakedModel extends DefaultMachineBakedModel {

	public TankMachineBakedModel(BakedModel baseModel) {
		super(baseModel, true);
	}
}
