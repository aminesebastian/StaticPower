package theking530.staticcore.initialization.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.tileentities.TileEntityBase;

/**
 * Wrapper class used to help in the initialization of tile entites.
 * 
 * @author amine
 *
 * @param <T>
 */
public class TileEntityInitializer<T extends TileEntityBase> implements BlockEntityType.BlockEntitySupplier<T> {
	private BlockEntityTypeAllocator<T> allocator;

	public TileEntityInitializer(BlockEntityTypeAllocator<T> allocator) {
		this.allocator = allocator;
	}

	public void setType(BlockEntityType<T> type) {
		allocator.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T create(BlockPos p_155268_, BlockState p_155269_) {
		return (T) allocator.create(p_155268_, p_155269_);
	}
}
