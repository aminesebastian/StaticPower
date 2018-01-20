package theking530.staticpower.conduits.itemconduit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.conduits.ConduitPath;
import theking530.staticpower.utils.WorldUtilities;

public class ItemConduitWrapper {
	public ItemStack ITEM;
	public BlockPos SOURCE;
	public ConduitPath PATH;
	
	private int CURRENT_INDEX;
	
	public ItemConduitWrapper(ItemStack item, BlockPos source) {
		ITEM = item;
		SOURCE = source;
		CURRENT_INDEX = 0;
	}
	public void setPath(ConduitPath path) {
		PATH = path;
		CURRENT_INDEX=0;
	}
	public void incrementPath() {
		CURRENT_INDEX++;
	}
	public BlockPos getNextBlockPos() {
		if(PATH == null || CURRENT_INDEX >= PATH.size()) {
			return null;
		}
		return PATH.get(CURRENT_INDEX);
	}
	public BlockPos getCurrentBlockPos() {
		if(PATH == null || CURRENT_INDEX >= PATH.size()) {
			return null;
		}
		return CURRENT_INDEX == 0 ? PATH.get(CURRENT_INDEX) : PATH.get(CURRENT_INDEX-1);
	}
	public ConduitPath getPath() {
		return PATH;
	}
	public BlockPos getSource() {
		return SOURCE;
	}
	public ItemStack getItemStack() {
		return ITEM;
	}
	public EnumFacing getCurrentDirection() {
		return WorldUtilities.getFacingFromPos(getCurrentBlockPos(), getNextBlockPos());
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		
	}		
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return nbt;
	}
}
