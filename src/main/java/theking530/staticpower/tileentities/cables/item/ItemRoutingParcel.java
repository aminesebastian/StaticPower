package theking530.staticpower.tileentities.cables.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path.PathEntry;

public class ItemRoutingParcel extends ItemRoutingParcelClient {
	private Path path;

	public ItemRoutingParcel(long id, ItemStack containedItem, Path path) {
		this(id, containedItem, path, Direction.UP);
	}

	public ItemRoutingParcel(long id, ItemStack containedItem, Path path, Direction inDirection) {
		super(id, containedItem, inDirection);
		this.path = path;
		this.outDirection = getCurrentEntry().getDirectionOfEntry();
	}

	protected ItemRoutingParcel() {

	}

	public Path getPath() {
		return path;
	}

	public void incrementCurrentPathIndex() {
		if (currentPathIndex < path.getLength() - 1) {
			if (getCurrentEntry().getDirectionOfEntry() != null) { // The direction of entry will be null for the first cable in. We use the one
																	// provided in the constructor instead.
				inDirection = getCurrentEntry().getDirectionOfEntry();
			}
			outDirection = getNextEntry().getDirectionOfEntry();
			currentPathIndex++;
		}
	}

	public boolean isAtFinalCable() {
		return currentPathIndex == getPath().getLength() - 2;
	}

	public boolean isAtDestination() {
		return currentPathIndex == getPath().getLength() - 1;
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

	public static ItemRoutingParcel create(CompoundNBT nbt) {
		ItemRoutingParcel output = new ItemRoutingParcel();
		output.readFromNbt(nbt);
		return output;
	}
}
