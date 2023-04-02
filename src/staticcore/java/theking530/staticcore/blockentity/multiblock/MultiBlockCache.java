package theking530.staticcore.blockentity.multiblock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.StaticCore;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticcore.world.WorldUtilities;

public class MultiBlockCache<T extends BlockEntity> implements Iterable<MultiBlockEntry<T>> {
	private final T controller;
	private final TriFunction<BlockPos, BlockState, BlockEntity, Boolean> filter;
	private final Function<Map<BlockPos, MultiBlockEntry<T>>, MultiBlockFormationStatus> wellFormedCheck;
	private final Map<BlockPos, MultiBlockEntry<T>> multiBlockEntries;
	private MultiBlockFormationStatus currentStatus;

	public MultiBlockCache(T controller, TriFunction<BlockPos, BlockState, BlockEntity, Boolean> filter,
			Function<Map<BlockPos, MultiBlockEntry<T>>, MultiBlockFormationStatus> wellFormedCheck) {
		this.filter = filter;
		this.controller = controller;
		this.wellFormedCheck = wellFormedCheck;
		multiBlockEntries = new HashMap<>();
		currentStatus = MultiBlockFormationStatus.INVALID;
	}

	public MultiBlockFormationStatus getStatus() {
		return currentStatus;
	}

	public T getController() {
		return controller;
	}

	@SuppressWarnings("unchecked")
	public MultiBlockFormationStatus update() {
		int initialBlockCount = multiBlockEntries.size();
		Level level = controller.getLevel();

		level.getProfiler().push(String.format("MultiBlockCache.%1$s.Traversing", controller.getClass().getName()));
		Map<BlockPos, MultiBlockEntry<T>> discovered = WorldUtilities.bfsTraverseWorld(level, controller.getBlockPos(), (pos, state, entity) -> {
			if (filter.apply(pos, state, entity)) {
				return new MultiBlockEntry<T>(this, state, pos);
			}
			return null;
		});
		level.getProfiler().pop();

		// Check to see which blocks were added and which were removed.
		List<MultiBlockEntry<T>> newBlocks = new LinkedList<>();
		for (Entry<BlockPos, MultiBlockEntry<T>> entry : discovered.entrySet()) {
			if (!multiBlockEntries.containsKey(entry.getKey())) {
				newBlocks.add(entry.getValue());

				BlockEntity entity = level.getBlockEntity(entry.getKey());
				if (entity instanceof IMultiBlockComponent) {
					IMultiBlockComponent<T> comp = (IMultiBlockComponent<T>) entity;
					comp.setToken(entry.getValue());
				}
			}
		}

		List<MultiBlockEntry<T>> removed = new LinkedList<>();
		for (Entry<BlockPos, MultiBlockEntry<T>> entry : multiBlockEntries.entrySet()) {
			if (!discovered.containsKey(entry.getKey())) {
				removed.add(entry.getValue());

				BlockEntity entity = level.getBlockEntity(entry.getKey());
				if (entity instanceof IMultiBlockComponent) {
					IMultiBlockComponent<T> comp = (IMultiBlockComponent<T>) entity;
					comp.clearToken();
				}
			}
		}

		// Now we can update the entry list with the newly discovered ones.
		multiBlockEntries.clear();
		multiBlockEntries.putAll(discovered);

		level.getProfiler().push(String.format("MultiBlockCache.%1$s.CheckingWellFormed", controller.getClass().getName()));
		MultiBlockFormationStatus status = wellFormedCheck.apply(multiBlockEntries);
		level.getProfiler().pop();

		if (status == MultiBlockFormationStatus.OK && initialBlockCount != multiBlockEntries.size()) {
			StaticCore.LOGGER.debug(String.format("Multiblock with controller at location: %1$s updated originally with: %2$d blocks, now contains: %3$d blocks!",
					controller.getBlockPos(), initialBlockCount, multiBlockEntries.size()));
		} else if (status != currentStatus) {
			StaticCore.LOGGER.debug(String.format("Multiblock with controller at location: %1$s failed to update with: %2$d blocks. Encountered status: %3$s.",
					controller.getBlockPos(), initialBlockCount, status.getUnlocalizedStatus()));
		}
		currentStatus = status;
		return status;
	}

	public int size() {
		return multiBlockEntries.size();
	}

	@Override
	public Iterator<MultiBlockEntry<T>> iterator() {
		return new MultiBlockCacheIterator();
	}

	class MultiBlockCacheIterator implements Iterator<MultiBlockEntry<T>> {
		private int index = 0;
		private List<MultiBlockEntry<T>> flatArray;

		public MultiBlockCacheIterator() {
			flatArray = multiBlockEntries.values().stream().toList();
		}

		public boolean hasNext() {
			return index < flatArray.size();
		}

		public MultiBlockEntry<T> next() {
			return flatArray.get(index++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Blocks cannot be manually removed from the cache!");
		}
	}
}
