package theking530.staticcore.block;

import java.util.function.Function;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.client.ICustomModelProvider;

public class StaticCoreItemBlockCustomModel extends StaticCoreItemBlock implements ICustomModelProvider {
	private final Function<BakedModel, BakedModel> modelSupplier;

	public StaticCoreItemBlockCustomModel(Block block, Function<BakedModel, BakedModel> modelSupplier) {
		super(block, new Item.Properties());
		this.modelSupplier = modelSupplier;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return modelSupplier.apply(existingModel);
	}
}
