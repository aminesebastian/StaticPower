package theking530.staticpower.conduits.itemconduit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.utils.Vector3;

public class ItemConduitNetwork {
	
	final List<NetworkInventoryWrapper> INVENTORIES = new ArrayList<NetworkInventoryWrapper>();
	private final Map<BlockPos, NetworkInventoryWrapper> INVENTORY_MAP = new HashMap<BlockPos, NetworkInventoryWrapper>();
	final Map<BlockPos, TileEntityItemConduit> CONDUIT_MAP = new HashMap<BlockPos, TileEntityItemConduit>();
	boolean REQUIRES_SORT = false;  
	
	public int PULL_RATE = 20;
	public int PULL_TIMER = 0;
	public int PULL_STACKS = 1;
	
	public void updateNetwork() {
		System.out.println(toString());

	}
	
	public void addConduit(TileEntityItemConduit conduit) {
		CONDUIT_MAP.put(conduit.getPos(), conduit);
		
		for(int i=0; i<6; i++) {
			if(conduit.getConectedInventory(EnumFacing.values()[i]) != null){
				
			}
		}
	}
	public void inventoryAdded(TileEntityItemConduit conduit, EnumFacing direction, BlockPos pos, ISidedInventory externalInventory) {
	    NetworkInventoryWrapper inv = new NetworkInventoryWrapper(this, pos, externalInventory, direction);
	    INVENTORIES.add(inv);
	    INVENTORY_MAP.put(pos, inv);
	    REQUIRES_SORT = true;
	}
	public void inventoryRemoved(TileEntityItemConduit conduit, int x, int y, int z) {
	    Vector3 vec = new Vector3(x, y, z);
	    NetworkInventoryWrapper inv = INVENTORY_MAP.get(vec);
	    if(inv != null) {
	    	INVENTORY_MAP.remove(vec);
	    	INVENTORIES.remove(inv);
	    	REQUIRES_SORT = true;
	    }
	}
	public void routesChanged() {
		REQUIRES_SORT = true;
	}
	public void setPathForItem(ItemConduitWrapper item) {
		TileEntityItemConduit sourceConduit = (TileEntityItemConduit)item.PATH.get(0);
		
		
	}
	@Override
	public String toString(){
		return "Conduit Count: " + INVENTORY_MAP.size() + "Inventory Count: " + INVENTORIES.size();
	}
}
