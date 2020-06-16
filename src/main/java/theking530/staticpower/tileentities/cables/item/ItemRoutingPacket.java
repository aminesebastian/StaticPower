package theking530.staticpower.tileentities.cables.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path.PathEntry;

public class ItemRoutingPacket {
	private ItemStack containedItem;
	private Path path;
	private int currentPathIndex;
	private int moveTimer;
	private int moveTime;
	private Direction inDirection;
	private Direction outDirection;

	public ItemRoutingPacket(ItemStack containedItem, Path path) {
		this.containedItem = containedItem;
		this.path = path;
		this.currentPathIndex = 0;
		this.moveTimer = 0;
		this.outDirection = getNextEntry().getDirectionOfApproach();
	}

	public ItemRoutingPacket(ItemStack containedItem, Path path, Direction inDirection) {
		this.containedItem = containedItem;
		this.path = path;
		this.currentPathIndex = 0;
		this.moveTimer = 0;
		this.inDirection = inDirection;
		this.outDirection = getNextEntry().getDirectionOfApproach();
	}

	private ItemRoutingPacket() {

	}

	public ItemStack getContainedItem() {
		return containedItem;
	}

	public Path getPath() {
		return path;
	}

	public void incrementCurrentPathIndex() {
		currentPathIndex++;
		inDirection = getCurrentEntry().getDirectionOfApproach();
		outDirection = getNextEntry().getDirectionOfApproach();
	}

	public Direction getInDirection() {
		return inDirection;
	}

	public Direction getOutDirection() {
		return outDirection;
	}

	public boolean isAtFinalCable() {
		return currentPathIndex == getPath().getLength() - 2;
	}

	public boolean isAtDestination() {
		return currentPathIndex == getPath().getLength() - 1;
	}

	public Direction getItemAnimationDirection() {
		return moveTimer > moveTime / 2 ? getOutDirection() : getInDirection();
	}

	/**
	 * Returns the item move percent mapped to the range [-1, 1].
	 * 
	 * @return
	 */
	public float getItemMoveLerp() {
		return (getItemMovePercent() * 2.0f) - 1.0f;
	}

	public float getItemMovePercent() {
		return (float) moveTimer / moveTime;
	}

	public boolean incrementMoveTimer() {
		if (moveTimer < moveTime) {
			moveTimer++;
		}
		return moveTimer >= moveTime;
	}

	public void setMoveTimer(int moveTime) {
		this.moveTime = moveTime;
		moveTimer = 0;
	}

	public PathEntry getNextEntry() {
		return path.getPath()[currentPathIndex + 1];
	}

	public PathEntry getCurrentEntry() {
		return path.getPath()[currentPathIndex];
	}

	public static ItemRoutingPacket create(CompoundNBT nbt) {
		ItemRoutingPacket output = new ItemRoutingPacket();
		output.readFromNbt(nbt);
		return output;
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt) {
		// Serialize the contained item.
		CompoundNBT containedItemTag = new CompoundNBT();
		containedItem.write(containedItemTag);
		nbt.put("contained_item", containedItemTag);

		// Serialize the move times.
		nbt.putInt("move_timer", moveTimer);
		nbt.putInt("move_time", moveTime);

		if (inDirection != null) {
			nbt.putInt("in_direction", inDirection.ordinal());
		}
		if (outDirection != null) {
			nbt.putInt("out_direction", outDirection.ordinal());
		}

		return nbt;
	}

	public void readFromNbt(CompoundNBT nbt) {
		// Deserialize the contained item.
		CompoundNBT containedItemTag = nbt.getCompound("contained_item");
		containedItem = ItemStack.read(containedItemTag);

		// Deserialize the move times.
		moveTimer = nbt.getInt("move_timer");
		moveTime = nbt.getInt("move_time");

		if (nbt.contains("in_direction")) {
			inDirection = Direction.values()[nbt.getInt("in_direction")];
		}
		if (nbt.contains("out_direction")) {
			outDirection = Direction.values()[nbt.getInt("out_direction")];
		}
	}
}
