package theking530.staticpower.blocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.ItemCustomRendererPassthroughModel;

public class StaticPowerItemBlockCustomRenderer extends StaticPowerItemBlock implements ICustomModelSupplier {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerItemBlockCustomRenderer.class);

	public StaticPowerItemBlockCustomRenderer(Block block,
			java.util.function.Supplier<java.util.concurrent.Callable<net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer>> renderer) {
		super(block, new Item.Properties().setISTER(renderer));
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new ItemCustomRendererPassthroughModel(existingModel);
	}
}
