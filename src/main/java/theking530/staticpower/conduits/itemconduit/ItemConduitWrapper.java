package theking530.staticpower.conduits.itemconduit;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ItemConduitWrapper {
	public ItemStack ITEM;
	public TileEntity INVENTORY_SOURCE;
	public ArrayList<TileEntity> PATH = new ArrayList();
	public int CURRENT_PATH_INDEX = 0;
	
	public ItemConduitWrapper(ItemStack item, TileEntity inv, TileEntityItemConduit startingConduit) {
		ITEM = item;
		INVENTORY_SOURCE = inv;
		PATH.add(startingConduit);
	}
	public void setStartingConduit(TileEntityItemConduit conduit) {
		PATH.clear();
		PATH.add(conduit);
	}
	public EnumFacing getNextDirection() {
		if(PATH.size() > CURRENT_PATH_INDEX + 1 && PATH.get(CURRENT_PATH_INDEX) != null && PATH.get(CURRENT_PATH_INDEX + 1) != null) {
			//return WorldUtilities.getOppositeDirection(WorldUtilities.getDirectionBetweenEntities(PATH.get(CURRENT_PATH_INDEX), PATH.get(CURRENT_PATH_INDEX + 1)));	
		}
		return null;
	}
	public boolean hasValidInventory() {
		if(PATH.size() > 0 && PATH.get(PATH.size()-1) != null && PATH.get(PATH.size()-1) instanceof ISidedInventory) {
			return true;
		}
		return false;
	}
	public TileEntity getDestination(){
		if(PATH.get(PATH.size()-1) != null && PATH.get(PATH.size()-1) instanceof ISidedInventory) {
			return PATH.get(PATH.size()-1);
		}
		return null;
	}
	public boolean pathCompleted() {
		if(PATH.get(CURRENT_PATH_INDEX+1) != null && PATH.get(CURRENT_PATH_INDEX+1) instanceof ISidedInventory) {
			return true;
		}
		return false;
	}
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("IXCOORD", INVENTORY_SOURCE.getPos().getX());
		tag.setInteger("IYCOORD", INVENTORY_SOURCE.getPos().getY());
		tag.setInteger("IZCOORD", INVENTORY_SOURCE.getPos().getZ());

		
		NBTTagCompound nbt1 = new NBTTagCompound();
		if(ITEM != null) {
			ITEM.writeToNBT(nbt1);
			tag.setTag("Item", nbt1);
		}
	}
	public void readFromNBT(NBTTagCompound tag) {
		ITEM = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item"));
		if(Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(tag.getInteger("IXCOORD"), tag.getInteger("IYCOORD"), tag.getInteger("IZCOORD"))) != null) {
			if(Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(tag.getInteger("IXCOORD"), tag.getInteger("IYCOORD"), tag.getInteger("IZCOORD"))) instanceof ISidedInventory) {
				INVENTORY_SOURCE = Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(tag.getInteger("IXCOORD"), tag.getInteger("IYCOORD"), tag.getInteger("IZCOORD")));
			}
		}
	}
}
