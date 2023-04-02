package theking530.staticcore.blockentity.multiblock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.StaticCore;
import theking530.staticcore.utilities.TriFunction;

public class MultiBlockCache<T extends BlockEntity> implements Iterable<MultiBlockToken<T>> {
	private final T controller;
	private final Map<BlockPos, MultiBlockToken<T>> multiBlockEntries;
	private final TriFunction<BlockPos, BlockState, BlockEntity, Boolean> filter;

	public MultiBlockCache(T controller, TriFunction<BlockPos, BlockState, BlockEntity, Boolean> filter) {
		this.filter = filter;
		this.controller = controller;
		multiBlockEntries = new HashMap<>();
	}

	public T getController() {
		return controller;
	}

	public void checkForChanges() {
		boolean changed = false;
		Level level = controller.getLevel();
		for (Entry<BlockPos, MultiBlockToken<T>> entry : multiBlockEntries.entrySet()) {
			if (!level.getBlockState(entry.getKey()).equals(entry.getValue().getBlockState())) {
				changed = true;
				break;
			}
		}

		if (changed) {
			StaticCore.LOGGER.debug(String.format("Multiblock with controller at location: %1$s changed, performing update!", controller.getBlockPos()));
			update();
		}
	}

	@SuppressWarnings("unchecked")
	public void update() {
		int initialBlockCount = multiBlockEntries.size();

		Level level = controller.getLevel();
		level.getProfiler().push(String.format("MultiBlockCache.%1$s", controller.getClass().getName()));

		Set<BlockPos> discovered = new HashSet<>();

		Set<BlockPos> visited = new HashSet<>();
		visited.add(controller.getBlockPos());

		Queue<BlockPos> toCheck = new LinkedList<>();
		for (Direction dir : Direction.values()) {
			toCheck.add(controller.getBlockPos().relative(dir));
		}

		while (!toCheck.isEmpty()) {
			BlockPos target = toCheck.remove();
			BlockState state = level.getBlockState(target);
			BlockEntity be = level.getBlockEntity(target);
			visited.add(target);

			if (filter.apply(target, state, be)) {
				discovered.add(target);
				for (Direction dir : Direction.values()) {
					BlockPos newTarget = target.relative(dir);
					if (!visited.contains(newTarget)) {
						toCheck.add(newTarget);
					}
				}
			}
		}

		// Handle the removals.
		Set<BlockPos> removed = new HashSet<>();
		for (BlockPos existingPos : multiBlockEntries.keySet()) {
			if (!discovered.contains(existingPos)) {
				removed.add(existingPos);
			}
		}
		for (BlockPos removedPos : removed) {
			if (level.getBlockEntity(removedPos) instanceof IMultiBlockComponent) {
				IMultiBlockComponent<T> comp = (IMultiBlockComponent<T>) level.getBlockEntity(removedPos);
				comp.clearToken();
			}
			multiBlockEntries.get(removedPos).invalidate();
			multiBlockEntries.remove(removedPos);
		}

		// Add new tokens.
		for (BlockPos newPos : discovered) {
			if (!multiBlockEntries.containsKey(newPos)) {
				MultiBlockToken<T> newToken = new MultiBlockToken<T>(this, level.getBlockState(newPos), newPos);
				if (level.getBlockEntity(newPos) instanceof IMultiBlockComponent) {
					IMultiBlockComponent<T> comp = (IMultiBlockComponent<T>) level.getBlockEntity(newPos);
					comp.setToken(newToken);
				}
				multiBlockEntries.put(newPos, newToken);
			}
		}

		if (initialBlockCount != multiBlockEntries.size()) {
			StaticCore.LOGGER.debug(String.format("Multiblock with controller at location: %1$s updated originally with: %2$d blocks, now contains: %3$d blocks!",
					controller.getBlockPos(), initialBlockCount, multiBlockEntries.size()));
		}
		level.getProfiler().pop();
	}

	public int size() {
		return multiBlockEntries.size();
	}

	@Override
	public Iterator<MultiBlockToken<T>> iterator() {
		return new MultiBlockCacheIterator();
	}

	class MultiBlockCacheIterator implements Iterator<MultiBlockToken<T>> {
		private int index = 0;
		private List<MultiBlockToken<T>> flatArray;

		public MultiBlockCacheIterator() {
			flatArray = multiBlockEntries.values().stream().toList();
		}

		public boolean hasNext() {
			return index < flatArray.size();
		}

		public MultiBlockToken<T> next() {
			return flatArray.get(index++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Blocks cannot be manually removed from the cache!");
		}
	}
}
