package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticpower.client.rendering.tileentity.StaticPowerTileEntitySpecialRenderer;
import theking530.staticpower.tileentities.TileEntityBase;

/**
 * Pre-registers a {@link TileEntity} for initialization through the registry
 * event. Returns an instance of the {@link TileEntityType} for this
 * {@link TileEntity} and {@link Block} list combination. The registry name for
 * the tile entity type will be the registry name of the first provided block.
 */
public class BlockEntityTypeAllocator<T extends TileEntityBase> {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	@OnlyIn(Dist.CLIENT)
	public BlockEntityRendererProvider<T> rendererFactory;

	public final TriFunction<BlockEntityTypeAllocator<T>, BlockPos, BlockState, T> factory;
	public final Block[] blocks;
	protected BlockEntityType<T> type;
	private boolean registered;

	public BlockEntityTypeAllocator(TriFunction<BlockEntityTypeAllocator<T>, BlockPos, BlockState, T> factory,
			Block... blocks) {
		this.factory = factory;
		this.blocks = blocks;
		this.registered = false;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean requiresTileEntitySpecialRenderer() {
		return rendererFactory != null;
	}

	@OnlyIn(Dist.CLIENT)
	public BlockEntityTypeAllocator<T> setTileEntitySpecialRenderer(
			Function<BlockEntityRendererProvider.Context, StaticPowerTileEntitySpecialRenderer<T>> supplier) {
		this.rendererFactory = (context) -> supplier.apply(context);
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public BlockEntityRendererProvider<T> getTileEntitySpecialRenderer() {
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
		return factory.apply(this, pos, state);
	}
}
