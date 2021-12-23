package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

/**
 * Pre-registers a {@link TileEntity} for initialization through the registry
 * event. Returns an instance of the {@link TileEntityType} for this
 * {@link TileEntity} and {@link Block} list combination. The registry name for
 * the tile entity type will be the registry name of the first provided block.
 */
public class BlockEntityTypeAllocator<T extends BlockEntity> {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	@OnlyIn(Dist.CLIENT)
	public Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> rendererFactory;

	public final Function<BlockEntityTypeAllocator<T>, T> factory;
	public final Block[] blocks;
	protected BlockEntityType<T> type;
	private boolean registered;

	public BlockEntityTypeAllocator(Function<BlockEntityTypeAllocator<T>, T> factory, Block... blocks) {
		this.factory = factory;
		this.blocks = blocks;
		this.registered = false;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean requiresTileEntitySpecialRenderer() {
		return rendererFactory != null;
	}

	@OnlyIn(Dist.CLIENT)
	public BlockEntityTypeAllocator<T> setTileEntitySpecialRenderer(Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> rendererFactory) {
		this.rendererFactory = rendererFactory;
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> getTileEntitySpecialRenderer() {
		return rendererFactory;
	}

	public BlockEntityType<T> getType() {
		return type;
	}

	public void register(RegistryEvent.Register<BlockEntityType<?>> event) {
		if (registered) {
			throw new RuntimeException("Attempted to register an already registered TileEntityTypeAllocator!");
		} else {
			TileEntityInitializer<T> initializer = new TileEntityInitializer<T>(this);
			type = BlockEntityType.Builder.of(initializer, blocks).build(null);
			type.setRegistryName(blocks[0].getRegistryName());
			event.getRegistry().register(type);
			initializer.setType(type);
			registered = true;
		}
	}

	public BlockEntity create(BlockPos pos, BlockState state) {
		return type.create(pos, state);
	}
}
