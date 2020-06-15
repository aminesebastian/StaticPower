package theking530.staticpower.tileentities.cables.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path.PathEntry;

public class ItemRoutingPacket {
	private final ItemStack containedItem;
	private Path path;
	private int currentPathIndex;

	public ItemRoutingPacket(ItemStack containedItem, Path path) {
		this.containedItem = containedItem;
		this.path = path;
		this.currentPathIndex = 0;
	}

	public ItemStack getContainedItem() {
		return containedItem;
	}

	public Path getPath() {
		return path;
	}

	public void incrementCurrentPathIndex() {
		currentPathIndex++;
	}

	public Direction getInDirection() {
		return getNextEntry().getDirectionOfApproach();
	}

	public Direction getOutDirection() {
		return getNextEntry().getDirectionOfApproach();
	}

	public boolean hasNext() {
		return currentPathIndex < getPath().getLength() - 1;
	}

	public PathEntry getNextEntry() {
		return path.getPath()[currentPathIndex + 1];
	}

	public PathEntry getCurrentEntry() {
		return path.getPath()[currentPathIndex];
	}
}
