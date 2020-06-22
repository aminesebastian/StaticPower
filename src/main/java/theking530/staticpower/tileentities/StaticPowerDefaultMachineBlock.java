package theking530.staticpower.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;

public abstract class StaticPowerDefaultMachineBlock extends StaticPowerTileEntityBlock implements ICustomModelSupplier {

	protected StaticPowerDefaultMachineBlock(String name) {
		super(name);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new DefaultMachineBakedModel(existingModel);
	}
}
