package theking530.staticpower.tileentities;

import net.minecraft.client.renderer.model.IBakedModel;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.MachineBakedModel;

public abstract class StaticPowerDefaultMachineBlock extends StaticPowerTileEntityBlock implements ICustomModelSupplier {

	protected StaticPowerDefaultMachineBlock(String name) {
		super(name);
	}

	@Override
	public boolean hasModelOverride() {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(IBakedModel originalModel) {
		if (originalModel == null) {
			return null;
		}
		return new MachineBakedModel(originalModel);
	}
}
