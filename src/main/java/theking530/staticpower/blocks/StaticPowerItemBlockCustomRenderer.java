package theking530.staticpower.blocks;

import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.ItemCustomRendererPassthroughModel;

public class StaticPowerItemBlockCustomRenderer extends StaticPowerItemBlock implements ICustomModelSupplier {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerItemBlockCustomRenderer.class);
	private final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> renderer;

	public StaticPowerItemBlockCustomRenderer(Block block, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> renderer) {
		super(block, new Item.Properties());
		this.renderer = renderer;
	}

	@Override
	public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
			}
		});
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
