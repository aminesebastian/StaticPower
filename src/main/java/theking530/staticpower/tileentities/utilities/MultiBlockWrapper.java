package theking530.staticpower.tileentities.utilities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockWrapper<T> {
	public final BlockPos position;
	@Nullable
	public final T entity;

	public MultiBlockWrapper(BlockPos position) {
		this(position, null);
	}

	public MultiBlockWrapper(BlockPos position, T entity) {
		this.position = position;
		this.entity = entity;
	}

	public BlockState getBlockState(BlockGetter level) {
		return level.getBlockState(position);
	}

	public boolean hasBlockEntity() {
		return entity != null;
	}
}