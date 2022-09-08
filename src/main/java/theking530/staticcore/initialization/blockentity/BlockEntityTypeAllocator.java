package theking530.staticcore.initialization.blockentity;

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
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.client.rendering.blockentity.StaticPowerBlockEntitySpecialRenderer;

/**
 * Pre-registers a {@link TileEntity} for initialization through the registry
 * event. Returns an instance of the {@link TileEntityType} for this
 * {@link TileEntity} and {@link Block} list combination. The registry name for
 * the tile entity type will be the registry name of the first provided block.
 */
public class BlockEntityTypeAllocator<T extends BlockEntityBase> {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	@OnlyIn(Dist.CLIENT)
	public BlockEntityRendererProvider<T> rendererFactory;

	public final TriFunction<BlockEntityTypeAllocator<T>, BlockPos, BlockState, T> factory;
	public final RegistryObject<? extends Block> block;
	protected BlockEntityType<T> type;
	private boolean registered;

	public BlockEntityTypeAllocator(TriFunction<BlockEntityTypeAllocator<T>, BlockPos, BlockState, T> factory, RegistryObject<? extends Block> block) {
		this.factory = factory;
		this.block = block;
		this.registered = false;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean requiresBlockEntitySpecialRenderer() {
		return rendererFactory != null;
	}

	@OnlyIn(Dist.CLIENT)
	public BlockEntityTypeAllocator<T> setTileEntitySpecialRenderer(Function<BlockEntityRendererProvider.Context, StaticPowerBlockEntitySpecialRenderer<T>> supplier) {
		this.rendererFactory = (context) -> supplier.apply(context);
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public BlockEntityRendererProvider<T> getBlockEntitySpecialRenderer() {
		return rendererFactory;
	}

	public BlockEntityType<T> getType() {
		return type;
	}

	public void register(RegistryEvent.Register<BlockEntityType<?>> event) {
		if (registered) {
			throw new RuntimeException("Attempted to register an already registered TileEntityTypeAllocator!");
		} else {
			BlockEntityInitializer<T> initializer = new BlockEntityInitializer<T>(this);
			type = BlockEntityType.Builder.of(initializer, block.get()).build(null);
			type.setRegistryName(block.get().getRegistryName());
			event.getRegistry().register(type);
			initializer.setType(type);
			registered = true;
		}
	}

	public BlockEntity create(BlockPos pos, BlockState state) {
		return factory.apply(this, pos, state);
	}
}
