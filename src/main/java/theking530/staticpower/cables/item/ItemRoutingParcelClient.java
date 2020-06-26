package theking530.staticpower.cables.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class ItemRoutingParcelClient {
	private long id;
	protected ItemStack containedItem;
	protected int moveTimer;
	protected int moveTime;
	protected Direction inDirection;
	protected Direction outDirection;

	public ItemRoutingParcelClient(long id, ItemStack containedItem) {
		this(id, containedItem, null);
	}

	public ItemRoutingParcelClient(long id, ItemStack containedItem, Direction inDirection) {
		this.containedItem = containedItem;
		this.moveTimer = 0;
		this.inDirection = inDirection;
		this.id = id;
	}

	protected ItemRoutingParcelClient() {

	}

	public ItemStack getContainedItem() {
		return containedItem;
	}

	public long getId() {
		return id;
	}

	public Direction getInDirection() {
		return inDirection;
	}

	public Direction getOutDirection() {
		return outDirection;
	}

	public Direction getItemAnimationDirection() {
		return moveTimer > moveTime / 2 ? outDirection : inDirection;
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

	public static ItemRoutingParcelClient create(CompoundNBT nbt) {
		ItemRoutingParcelClient output = new ItemRoutingParcelClient();
		output.readFromNbt(nbt);
		return output;
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt) {
		// Serialize the contained item.
		CompoundNBT containedItemTag = new CompoundNBT();
		containedItem.write(containedItemTag);
		nbt.put("contained_item", containedItemTag);
		nbt.putLong("id", id);
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
		id = nbt.getLong("id");
		moveTime = nbt.getInt("move_time");
		if (nbt.contains("in_direction")) {
			inDirection = Direction.values()[nbt.getInt("in_direction")];
		}
		if (nbt.contains("out_direction")) {
			outDirection = Direction.values()[nbt.getInt("out_direction")];
		}
	}
}
