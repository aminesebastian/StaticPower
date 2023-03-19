package theking530.staticpower.cables.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.cablenetwork.pathfinding.Path;
import theking530.staticcore.cablenetwork.pathfinding.Path.PathEntry;

public class ItemRoutingParcel extends ItemRoutingParcelClient {
	private Path path;

	public ItemRoutingParcel(long id, ItemStack containedItem, Path path) {
		this(id, containedItem, path, Direction.UP);
	}

	public ItemRoutingParcel(long id, ItemStack containedItem, Path path, Direction inDirection) {
		super(id, containedItem, inDirection);
		this.path = path;
		this.outDirection = getCurrentEntry().getDirectionOfEntry();
		this.currentPathIndex = 0;

	}

	public ItemRoutingParcel(CompoundTag nbt) {
		readFromNbt(nbt);
	}

	protected ItemRoutingParcel() {

	}

	public Path getPath() {
		return path;
	}

	public void incrementCurrentPathIndex() {
		incrementCurrentPathIndex(false);
	}


	public void incrementCurrentPathIndex(boolean startHalfWay) {
		if (currentPathIndex < path.getPathEntryCount() - 1) {
			// The direction of entry will be null for the first cable in. We use the one
			// provided in the constructor instead.
			if (getCurrentEntry().getDirectionOfEntry() != null) {
				inDirection = getCurrentEntry().getDirectionOfEntry();
			}
			outDirection = getNextEntry().getDirectionOfEntry();
			currentPathIndex++;

			if (startHalfWay) {
				moveTimer = moveTime / 2;
			}
		}
	}

	/**
	 * Gets the last cable in the path.
	 * 
	 * @return
	 */
	public BlockPos getFinalCablePosition() {
		return getPath().getEntries()[getPath().getPathEntryCount() - 2].getPosition();
	}

	/**
	 * The side of the destination this path will insert into.
	 * 
	 * @return
	 */
	public Direction getFinalInsertSide() {
		return getPath().getEntries()[getPath().getPathEntryCount() - 1].getDirectionOfEntry();
	}

	/**
	 * Checks if we are at the last cable in the path.
	 * 
	 * @return
	 */
	public boolean isAtFinalCable() {
		return currentPathIndex == getPath().getPathEntryCount() - 2;
	}

	/**
	 * Sets how many ticks it should take to travel through the current cable this
	 * parcel is in.
	 * 
	 * @param moveTime
	 */
	public void setMovementTime(int moveTime) {
		this.moveTime = moveTime;
		moveTimer = 0;
	}

	/**
	 * Gets the next path entry in the path.
	 * 
	 * @return
	 */
	public PathEntry getNextEntry() {
		return path.getEntries()[currentPathIndex + 1];
	}

	/**
	 * Gets the current path entry the parcel is at.
	 */
	public PathEntry getCurrentEntry() {
		return path.getEntries()[currentPathIndex];
	}

	/**
	 * Creates an item routing parcel from NBT data.
	 * 
	 * @param nbt
	 * @return
	 */
	public static ItemRoutingParcel create(CompoundTag nbt) {
		ItemRoutingParcel output = new ItemRoutingParcel();
		output.readFromNbt(nbt);
		return output;
	}

	public CompoundTag writeToNbt(CompoundTag nbt) {
		super.writeToNbt(nbt);

		// Serialize the path.
		CompoundTag pathTag = new CompoundTag();
		path.writeToNbt(pathTag);
		nbt.put("path", pathTag);
		nbt.putInt("move_timer", moveTimer);
		return nbt;
	}

	public void readFromNbt(CompoundTag nbt) {
		super.readFromNbt(nbt);

		// Create the path.
		path = new Path(nbt.getCompound("path"));
		moveTimer = nbt.getInt("move_timer");
	}

	@Override
	public String toString() {
		return "ItemRoutingParcel [containedItem=" + containedItem + ", path=" + path + ", currentPathIndex=" + currentPathIndex + ", moveTimer=" + moveTimer + ", moveTime=" + moveTime
				+ ", inDirection=" + inDirection + ", outDirection=" + outDirection + "]";
	}

}
