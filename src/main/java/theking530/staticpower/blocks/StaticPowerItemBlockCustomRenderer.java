package theking530.staticpower.blocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.ItemCustomRendererPassthroughModel;

public class StaticPowerItemBlockCustomRenderer extends StaticPowerItemBlock implements ICustomModelSupplier {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerItemBlockCustomRenderer.class);

	public StaticPowerItemBlockCustomRenderer(Block block,
			java.util.function.Supplier<java.util.concurrent.Callable<net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer>> renderer) {
		super(block, new Item.Properties().setISTER(renderer));
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new ItemCustomRendererPassthroughModel(existingModel);
	}
}
