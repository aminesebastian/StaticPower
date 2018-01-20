package theking530.staticpower.conduits.itemconduit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.conduits.ConduitPath;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.tileentity.chest.staticchest.TileEntityStaticChest;
import theking530.staticpower.utils.InventoryUtilities;
import theking530.staticpower.utils.WorldUtilities;

public class TileEntityItemConduit extends TileEntityBaseConduit {
	
	public ItemConduitWrapper SLOT;
	public int PULL_RATE = 30;
	public int PULL_TIMER = 0;
	public int PULL_STACKS = 1;
	public int MOVE_RATE = 10;
	public int MOVE_TIMER = 0;
	
	public float MOVE_ANIMATION_ALPHA = 0.0f;
	
	public TileEntityItemConduit() {	
		
	}
	@Override
	public void update() {
		super.update();
		//if(!getWorld().isRemote) {
			if(SLOT == null) {
				SLOT = attemptPullItem();
				if(SLOT != null) {
					ConduitPath path = getClosestPath(SLOT);
					if(path != null) {
						SLOT.setPath(path);
						SLOT.incrementPath();
					}
				}
			}else{
				if(SLOT.getNextBlockPos() == null) {
					ConduitPath path = getClosestPath(SLOT);
					if(path != null) {
						SLOT.setPath(path);
						SLOT.incrementPath();
						MOVE_TIMER = 0;
						MOVE_ANIMATION_ALPHA = 0.0f;
					}
				}else{
					if(SLOT.PATH == null || !canPlaceInInventory(SLOT.PATH.getDestination(), SLOT.PATH.getPenultimateFacing())) {
						ConduitPath path = getClosestPath(SLOT);
						if(path != null) {
							SLOT.setPath(path);
							SLOT.incrementPath();
						}else{
        					WorldUtilities.dropItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(), SLOT.ITEM);
        					SLOT = null;
						}
					}else{
						if(MOVE_TIMER < MOVE_RATE) {
							MOVE_TIMER++;
						}else{
							ConduitPath path = getClosestPath(SLOT);
							if(path != null) {
								SLOT.setPath(path);
								SLOT.incrementPath();
								if(canPlaceInInventory(SLOT.getPath().getDestination(), SLOT.getPath().getPenultimateFacing())) {
									moveContentsForward();
									MOVE_TIMER = 0;
									MOVE_ANIMATION_ALPHA = 0.0f;
								}
							}else{
	        					WorldUtilities.dropItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(), SLOT.ITEM);
	        					SLOT = null;
							}
						}
					}		
				}
			}
		//}
	}	
	    
	//Functionality
	public void acceptNewItem(ItemConduitWrapper newItem) {
		newItem.incrementPath();
		MOVE_TIMER = 0;
		SLOT = newItem;
	}
	public void moveContentsForward() {
		if(SLOT != null) {
			BlockPos nextConduit = SLOT.getNextBlockPos();
			TileEntity te = getWorld().getTileEntity(nextConduit);
			if(te != null) {
				if(te instanceof TileEntityItemConduit) {
					TileEntityItemConduit cond = (TileEntityItemConduit)te;
					if(cond.SLOT == null) {
						cond.acceptNewItem(SLOT);
						SLOT = null;
					}
				}else{
					if(canPlaceInInventory(nextConduit, SLOT.getPath().getPenultimateFacing())) {
						placeInInventory(nextConduit, SLOT.getPath().getPenultimateFacing());
						SLOT = null;
					}
				}
			}
		}
	}
	public ConduitPath getClosestPath(ItemConduitWrapper slot) {	
		Map<BlockPos, ConduitPath> potentialRecievers = new HashMap<BlockPos, ConduitPath>();	
	    //Generate a list of potential receivers
	    Iterator<Entry<BlockPos, TileEntity>> it = GRID.RECIEVER_STORAGE_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<BlockPos, TileEntity> pair = (Map.Entry<BlockPos, TileEntity>)it.next();
	        TileEntity te = getWorld().getTileEntity(pair.getKey());
	        ConduitPath path = GRID.doBFSShortestPath(getPos(), pair.getKey());
	        if(path.size() > 1) {
		    	EnumFacing facing = WorldUtilities.getFacingFromPos(pair.getKey(), path.get(path.size()-2));
				if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) && !pair.getKey().equals(slot.SOURCE)) {
					IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
					if(InventoryUtilities.canFullyInsertItemIntoInventory(itemHandler, slot.ITEM)) {
			        	potentialRecievers.put(pair.getKey(), path);
					}
				} 
	        }
	    }   

	    //Attempt to satisfy all receivers.
    	Map.Entry<BlockPos, ConduitPath>  shortestPath = null;
    	int shortestPathLength = 100000000;
    	
    	//Determine closets receiver and store it in 'shortestPath'.
	    Iterator<Entry<BlockPos, ConduitPath>> testIt = potentialRecievers.entrySet().iterator();
	    while (testIt.hasNext()) {
	        Map.Entry<BlockPos, ConduitPath> pair = (Map.Entry<BlockPos, ConduitPath>)testIt.next();

	        if(pair.getValue().size() < shortestPathLength && !pair.getValue().get(pair.getValue().size()-1).equals(slot.SOURCE)) {
	        	shortestPathLength = pair.getValue().size();
	        	shortestPath = pair;
	        }
	    }   
	    
	    if(shortestPath == null) {
	    	return null;
	    }
	    //Satisfy towards reciever.
	    return shortestPath.getValue();		    
	}
	public ItemConduitWrapper attemptPullItem() {
		for(int i=0; i<6; i++) {
			EnumFacing facing = EnumFacing.values()[i];
			TileEntity te = getWorld().getTileEntity(pos.offset(facing));
			if(te != null && te instanceof TileEntityStaticChest && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
				IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
				for(int j=0; j<itemHandler.getSlots(); j++) {
					if(itemHandler.getStackInSlot(j) != ItemStack.EMPTY) {
						return new ItemConduitWrapper(itemHandler.extractItem(j, 4, false), getPos().offset(facing));
					}
				}
			}
		}
		return null;
	}
	public boolean canPlaceInInventory(BlockPos inventory, EnumFacing facing) {
		TileEntity te = getWorld().getTileEntity(inventory);
		if(te == null) {
			return false;
		}
		if(!(te instanceof TileEntityStaticChest) && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
			IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
			if(InventoryUtilities.canFullyInsertItemIntoInventory(itemHandler, SLOT.ITEM)) {
				return true;
			}
		}
		return false;
	}
	public boolean placeInInventory(BlockPos inventory, EnumFacing facing) {
		TileEntity te = getWorld().getTileEntity(inventory);
		if(!(te instanceof TileEntityStaticChest) && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
			IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
			if(InventoryUtilities.canFullyInsertItemIntoInventory(itemHandler, SLOT.ITEM)) {
				InventoryUtilities.insertItemIntoInventory(itemHandler, SLOT.ITEM);
				SLOT = null;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isConduit(EnumFacing side) {
		if(SIDE_MODES[side.ordinal()] == 1) {
			return false;
		}
		if(getWorld().getTileEntity(pos.offset(side)) instanceof TileEntityItemConduit) {
			return ((TileEntityItemConduit)getWorld().getTileEntity(pos.offset(side))).SIDE_MODES[side.getOpposite().ordinal()] == 0;
		}
		return false;
	}		
	@Override
	public boolean isReciever(EnumFacing side) {
		if(SIDE_MODES[side.ordinal()] == 1) {
			return false;
		}
		if(getWorld().getTileEntity(pos.offset(side)) != null && !(getWorld().getTileEntity(pos.offset(side)) instanceof TileEntityItemConduit) && getWorld().getTileEntity(pos.offset(side)).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
			return true;
		}
		return false;		
	}
	
	//NBT and Sync
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		MOVE_TIMER = nbt.getInteger("MOVE_TIMER");
	}		
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("MOVE_TIMER", MOVE_TIMER);
		return nbt;
	}
}
