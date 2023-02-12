package theking530.staticpower.blocks;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.client.ICustomModelProvider;

public class StaticPowerItemBlockCustomModel extends StaticPowerItemBlock implements ICustomModelProvider {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerItemBlockCustomModel.class);
	private final Function<BakedModel, BakedModel> modelSupplier;

	public StaticPowerItemBlockCustomModel(Block block, Function<BakedModel, BakedModel> modelSupplier) {
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
