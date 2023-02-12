package theking530.staticpower.blockentities.machines.pump;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PumpTubeCache implements Queue<BlockPos> {
	private final BlockPos position;
	private final Direction facingDirection;
	private final Queue<BlockPos> positionsWithSourceFluids;
	private final HashSet<BlockPos> positionUniqueSet;

	public PumpTubeCache(BlockPos position, Direction facingDirection) {
		this.position = position;
		this.facingDirection = facingDirection;
		this.positionsWithSourceFluids = new LinkedList<>();
		this.positionUniqueSet = new HashSet<>();
	}

	public BlockPos getPosition() {
		return position;
	}

	public Direction getFacingDirection() {
		return facingDirection;
	}

	@Override
	public BlockPos poll() {
		return positionsWithSourceFluids.poll();
	}

	@Override
	public BlockPos peek() {
		return positionsWithSourceFluids.peek();
	}

	@Override
	public int size() {
		return positionsWithSourceFluids.size();
	}

	@Override
	public boolean isEmpty() {
		return positionsWithSourceFluids.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return positionsWithSourceFluids.contains(o);
	}

	@Override
	public Iterator<BlockPos> iterator() {
		return positionsWithSourceFluids.iterator();
	}

	@Override
	public Object[] toArray() {
		return positionsWithSourceFluids.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return positionsWithSourceFluids.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return positionsWithSourceFluids.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return positionsWithSourceFluids.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends BlockPos> c) {
		return positionsWithSourceFluids.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return positionsWithSourceFluids.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return positionsWithSourceFluids.retainAll(c);
	}

	@Override
	public void clear() {
		positionsWithSourceFluids.clear();
	}

	@Override
	public boolean add(BlockPos e) {
		if (positionUniqueSet.add(e)) {
			positionsWithSourceFluids.add(e);
			return true;
		}
		return false;
	}

	@Override
	public boolean offer(BlockPos e) {
		return positionsWithSourceFluids.offer(e);
	}

	@Override
	public BlockPos remove() {
		return positionsWithSourceFluids.remove();
	}

	@Override
	public BlockPos element() {
		return positionsWithSourceFluids.element();
	}
}
