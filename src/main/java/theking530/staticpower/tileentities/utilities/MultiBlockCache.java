package theking530.staticpower.tileentities.utilities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.utilities.TriFunction;

public class MultiBlockCache<T> implements Iterable<MultiBlockWrapper<T>> {
	@Nullable
	private final Class<T> validBlockEntity;
	private final List<MultiBlockWrapper<T>> multiBlockEntries;
	private final TriFunction<BlockPos, BlockState, BlockEntity, Boolean> filter;

	public MultiBlockCache(TriFunction<BlockPos, BlockState, BlockEntity, Boolean> filter, @Nullable Class<T> validBlockEntity) {
		this.filter = filter;
		this.validBlockEntity = validBlockEntity;
		multiBlockEntries = new LinkedList<>();
	}

	public void refresh(Level level, BlockPos center) {
		multiBlockEntries.clear();

		Set<BlockPos> visited = new HashSet<>();
		visited.add(center);

		Queue<BlockPos> toCheck = new LinkedList<>();
		for (Direction dir : Direction.values()) {
			toCheck.add(center.relative(dir));
		}

		while (!toCheck.isEmpty()) {
			BlockPos target = toCheck.remove();
			BlockState state = level.getBlockState(target);
			BlockEntity be = level.getBlockEntity(target);
			visited.add(target);

			if (filter.apply(target, state, be)) {
				if (validBlockEntity != null && validBlockEntity.isInstance(be)) {
					multiBlockEntries.add(new MultiBlockWrapper<>(target, validBlockEntity.cast(be)));
				} else {
					multiBlockEntries.add(new MultiBlockWrapper<>(target));
				}

				for (Direction dir : Direction.values()) {
					BlockPos newTarget = target.relative(dir);
					if (!visited.contains(newTarget)) {
						toCheck.add(newTarget);
					}
				}
			}
		}
	}

	public int size() {
		return multiBlockEntries.size();
	}

	@Override
	public Iterator<MultiBlockWrapper<T>> iterator() {
		return new MultiBlockCacheIterator();
	}

	class MultiBlockCacheIterator implements Iterator<MultiBlockWrapper<T>> {
		private int index = 0;

		public boolean hasNext() {
			return index < multiBlockEntries.size();
		}

		public MultiBlockWrapper<T> next() {
			return multiBlockEntries.get(index++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Blocks cannot be manually removed from the cache!");
		}
	}
}
