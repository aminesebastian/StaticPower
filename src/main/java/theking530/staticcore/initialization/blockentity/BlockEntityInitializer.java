package theking530.staticcore.initialization.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.BlockEntityBase;

/**
 * Wrapper class used to help in the initialization of tile entites.
 * 
 * @author amine
 *
 * @param <T>
 */
public class BlockEntityInitializer<T extends BlockEntityBase> implements BlockEntityType.BlockEntitySupplier<T> {
	private BlockEntityTypeAllocator<T> allocator;

	public BlockEntityInitializer(BlockEntityTypeAllocator<T> allocator) {
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
