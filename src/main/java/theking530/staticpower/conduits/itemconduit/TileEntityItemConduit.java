package theking530.staticpower.conduits.itemconduit;

import static net.minecraft.util.EnumFacing.UP;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.utils.WorldUtilities;

public class TileEntityItemConduit extends TileEntityBaseConduit {
	
	public ItemConduitNetwork NETWORK;
	public ItemConduitWrapper slot;
	public int PULL_RATE = 30;
	public int PULL_TIMER = 0;
	public int PULL_STACKS = 1;
	public int MOVE_RATE = 20;
	public int MOVE_TIMER = 0;
	
	public TileEntityItemConduit() {	
		
	}
	
	public void updateEntity() {
		//MOVE_RATE = 20;
		worldObj.markAndNotifyBlock(pos, worldObj.getChunkFromBlockCoords(pos), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 0);
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if(NETWORK == null) {
			onPlaced();
		}else{
			NETWORK.updateNetwork();
		}
		updateConduitRenderConnections();
		updateRecieverRenderConnections();	
	}	
	public void onPlaced(){
	}	
	@Override
	public boolean isConduit(BlockPos pos) {
		return this.worldObj.getTileEntity(pos) instanceof TileEntityItemConduit;
	}		
	@Override
	public boolean isReciever(BlockPos pos) {
		TileEntity te = worldObj.getTileEntity(pos);
		if(te instanceof IInventory) {
			if(te instanceof TileEntityItemConduit) {
				return false;
			}
			return true;
		}
		return false;
	}	
	
	//NBT and Sync
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(slot != null) {
			slot.readFromNBT(nbt);	
		}
		for(int i=0; i<6; i++) {
			SIDE_MODES[i] = nbt.getInteger("SIDE"+i);
		}
	}		
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(slot != null) {
			slot.writeToNBT(nbt);	
		}
		for(int i=0; i<6; i++) {
			nbt.setInteger("SIDE"+i, SIDE_MODES[i]);
		}
		return nbt;
	}	    

	//Functionality
	public TileEntity recursiveSearch(TileEntity previous, ArrayList<TileEntityItemConduit> conduits) {
		if(previous instanceof ISidedInventory) {
			return previous;
		}else if(previous instanceof TileEntityItemConduit) {
			TileEntityItemConduit tempCond = (TileEntityItemConduit)previous;
			if(tempCond.isConnectedToConduit()) {
				System.out.println("Searching next conduit");
				conduits.add(tempCond);
				return recursiveSearch(tempCond.selectRandomJoinedConduit(tempCond), conduits);
			}
			System.out.println("No more conduits!");
		}
		System.out.println("Done search!");
		return null;
	}
	public boolean pullItem(EnumFacing facing) {
		Random rand = new Random();
		TileEntity te = worldObj.getTileEntity(pos.offset(facing));
		if(te != null && te instanceof ISidedInventory) {
			ISidedInventory tempInv = (ISidedInventory)te;
			for(int i=0; i<tempInv.getSizeInventory(); i++) {
				if(tempInv.canExtractItem(i, tempInv.getStackInSlot(i), facing) && tempInv.getStackInSlot(i) != null) {
					slot = new ItemConduitWrapper(tempInv.getStackInSlot(i), te, this);
					tempInv.setInventorySlotContents(i, null);
					return true;
				}
			}
		}
		return false;
	}
	
	//Conduit Connection
	public TileEntityItemConduit selectRandomJoinedConduit(TileEntityItemConduit... excludedConduits) {
		Random rand = new Random();
		TileEntity[] teArray = WorldUtilities.getAdjacentEntities(worldObj, pos);
		ArrayList<TileEntityItemConduit> conduitList = new ArrayList();
		for(int k=0; k<6; k++) {
			if(teArray[k] != null && teArray[k] instanceof TileEntityItemConduit) {
				conduitList.add((TileEntityItemConduit)teArray[k]);
			}
		}
		if(conduitList.size() > 0) {
			for(int x=0; x<conduitList.size(); x++) {
				boolean flag = true;
				TileEntityItemConduit temp = conduitList.get(x);
				for(int i=0; i<excludedConduits.length; i++) {
					if(temp == excludedConduits[i]) {
						flag = false;
					}
				}
				if(flag) {
					return temp;
				}
			}
		}
		return null;
	}
	public boolean isConnectedToConduit(EnumFacing facing) {
		TileEntity teCond = worldObj.getTileEntity(pos.offset(facing));
		if(teCond != null && teCond instanceof TileEntityItemConduit) {
			return true;
		}
		return false;	
	}
	public boolean isConnectedToConduit() {
		for(int i=0; i<6; i++) {
			if(isConnectedToConduit(EnumFacing.values()[i])){
				return true;
			}
		}
		return false;
	}
	public int getConnectedConduitCount() {
		TileEntity[] teArray = WorldUtilities.getAdjacentEntities(worldObj, pos);
		int count = 0;
		for(int k=0; k<6; k++) {
			if(teArray[k] != null && teArray[k] instanceof TileEntityItemConduit) {
				count++;
			}
		}
		return count;
	}
	public void moveItemToConduit(TileEntityItemConduit conduit) {
		if(conduit.slot == null) {
			conduit.slot = slot;
			slot = null;
		}
	}

	//Inventory Connection
	public boolean isConnectedToInventory(ItemConduitWrapper slot, EnumFacing facing) {
		if(!disconected(facing)) {
			TileEntity tempInv = worldObj.getTileEntity(pos.offset(facing));
			if(tempInv != null && tempInv instanceof ISidedInventory && tempInv != slot.INVENTORY_SOURCE) {
				return true;
			}
		}
		return false;	
	}
	public boolean isConnectedToInventory(ItemConduitWrapper slot) {
		for(int i=0; i<6; i++) {
			if(isConnectedToInventory(slot, EnumFacing.values()[i])){
				return true;
			}
		}
		return false;
	}
	public TileEntity getConectedInventory(EnumFacing facing) {
		TileEntity tempInv = worldObj.getTileEntity(pos.offset(facing));
		if(tempInv instanceof ISidedInventory) {
			return tempInv;	
		}
		return null;
	}
	public boolean canPlaceInInvnetory(TileEntity inventory) {
		ISidedInventory tempInv = (ISidedInventory)inventory;
		for(int i=0; i<tempInv.getSizeInventory(); i++) {
			if(tempInv.canInsertItem(i, slot.ITEM, EnumFacing.UP)) {
				if(tempInv.getStackInSlot(i) == null) {
					return true;
				}else{
					int stackSize = tempInv.getStackInSlot(i).stackSize + slot.ITEM.stackSize;
					if(stackSize <= slot.ITEM.getMaxStackSize()) {
						return true;
					}else{
						return false;
					}
				}
			}
		}
		return false;
	}
	public boolean placeInInventory(TileEntity inventory) {
		ISidedInventory tempInv = (ISidedInventory)inventory;
		for(int i=0; i<tempInv.getSizeInventory(); i++) {
			if(tempInv != slot.INVENTORY_SOURCE && tempInv.canInsertItem(i, slot.ITEM, UP)) {
				if(tempInv.getStackInSlot(i) == null) {
					tempInv.setInventorySlotContents(i, slot.ITEM);
				}else{
					slot.ITEM.stackSize += tempInv.getStackInSlot(i).stackSize;
					tempInv.setInventorySlotContents(i, slot.ITEM);
				}
			slot = null;
			return true;
			}
		}
		return false;
	}
	
	public boolean canPullFromSide(int side) {
		if(SIDE_MODES[side] == 1) {
			return true;
		}
		return false;
	}
	
}
