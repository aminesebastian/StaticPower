package theking530.staticpower.conduits.itemconduit;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class NetworkInventoryWrapper {
	
	private ItemConduitNetwork NETWORK;
	private BlockPos LOCATION;
	private ISidedInventory INVENTORY;
	private EnumFacing DIRECTION;
	
	public NetworkInventoryWrapper(ItemConduitNetwork network, BlockPos location, ISidedInventory inv, EnumFacing direction) {
		NETWORK = network;
		LOCATION = location;
		INVENTORY = inv;
		DIRECTION = direction;
	}
	
	public ItemConduitNetwork getNetwork() {
		return NETWORK;
	}
	public void setNetwork(ItemConduitNetwork network) {
		NETWORK = network;
	}
	public BlockPos getLocation() {
		return LOCATION;
	}
	public void setLocation(BlockPos location) {
		LOCATION = location;
	}
	public ISidedInventory getInventory() {
		return INVENTORY;
	}
	public void setInventory(ISidedInventory inv) {
		INVENTORY = inv;
	}
	public EnumFacing getDirection() {
		return DIRECTION;
	}
	public void setDirection(EnumFacing direction) {
		DIRECTION = direction;
	}
}
