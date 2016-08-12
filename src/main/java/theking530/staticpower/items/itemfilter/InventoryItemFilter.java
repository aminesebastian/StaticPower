package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryItemFilter implements IInventory {
	
	private final ItemStack ITEMSTACK;
	private ItemStack[] slots;
	private boolean WHITE_LIST_MODE = true;
	private FilterTier TIER;
		
	public InventoryItemFilter(ItemStack stack, FilterTier tier) {
		ITEMSTACK = stack;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		TIER = tier;
		switch(TIER) {
			case BASIC:
				slots = new ItemStack[4];
				break;
			case UPGRADED:
				slots = new ItemStack[8];
				break;
			case ADVANCED:
				slots = new ItemStack[10];
				break;
			default:
				slots = new ItemStack[10];
				break;
		}
		readFromNBT(stack.getTagCompound());
	}
	public void setWhiteListMode(boolean mode) {
		WHITE_LIST_MODE = mode;
	}
	public boolean getWhiteListMode() {
		return WHITE_LIST_MODE;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		return true;
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack){
		return !(itemstack.getItem() instanceof ItemFilter);
	}
	public void readFromNBT(NBTTagCompound compound){
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		WHITE_LIST_MODE = compound.getBoolean("WHITE_LIST_MODE");
		TIER = FilterTier.values()[compound.getInteger("TIER")];
		for (int i = 0; i < items.tagCount(); ++i){
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				slots[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}
	public void writeToNBT(NBTTagCompound tagcompound){
		NBTTagList items = new NBTTagList();		
		for (int i = 0; i < getSizeInventory(); ++i){
			if (getStackInSlot(i) != null){
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		tagcompound.setInteger("TIER", TIER.ordinal());
		tagcompound.setTag("ItemInventory", items);
		tagcompound.setBoolean("WHITE_LIST_MODE", WHITE_LIST_MODE);
		//System.out.println(WHITE_LIST_MODE);
	}
	@Override
	public int getSizeInventory() {
		return slots.length;
	}
	@Override
	public ItemStack getStackInSlot(int slot){
		if(slot >= 0 && slot < slots.length) {
			return slots[slot];	
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount){
		ItemStack stack = getStackInSlot(slot);
		if(stack != null){
			if(stack.stackSize > amount){
				stack = stack.splitStack(amount);
				markDirty();
			}else{
				setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack){
		slots[slot] = stack;
		markDirty();
	}
	@Override
	public int getInventoryStackLimit(){
		return 1;
	}
	@Override
	public void markDirty(){
		for (int i = 0; i < getSizeInventory(); ++i){
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				slots[i] = null;
			}
		}		
		writeToNBT(ITEMSTACK.getTagCompound());
	}
	@Override
	public String getName() {
		return "container.ItemFilter";
	}
	@Override
	public boolean hasCustomName() {
		return true;
	}
	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}
	@Override
	public void openInventory(EntityPlayer player) {
	}
	@Override
	public void closeInventory(EntityPlayer player) {
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {	
	}
	@Override
	public int getFieldCount() {
		return 0;
	}
	@Override
	public void clear() {
	}
}