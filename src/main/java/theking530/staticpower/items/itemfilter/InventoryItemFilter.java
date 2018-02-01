package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryItemFilter implements IInventory {
	
	public final ItemStack ITEMSTACK;
	private ItemStack[] slots;
	private boolean WHITE_LIST_MODE = true;
    private boolean MATCH_METADATA = false;
    private boolean MATCH_NBT = false;
    private boolean MATCH_ORE_DICT = false;
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
		for(int i=0; i<slots.length; i++) {
			slots[i] = ItemStack.EMPTY;
		}
		readFromNBT(stack.getTagCompound());
	
	}
	public void getMatchMetadata(boolean mode) {
		MATCH_METADATA = mode;
	}
	public boolean getMatchMetadata() {
		return MATCH_METADATA;
	}
	
	public void setMatchNBT(boolean mode) {
		MATCH_NBT = mode;
	}
	public boolean getMatchNBT() {
		return MATCH_NBT;
	}
	
	public void setMatchOreDictionary(boolean mode) {
		MATCH_ORE_DICT = mode;
	}
	public boolean getMatchOreDictionary() {
		return MATCH_ORE_DICT;
	}
	
	public void setWhiteListMode(boolean mode) {
		WHITE_LIST_MODE = mode;
	}
	public boolean getWhiteListMode() {
		return WHITE_LIST_MODE;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack){
		return !(itemstack.getItem() instanceof ItemFilter);
	}
	public void readFromNBT(NBTTagCompound compound){
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		WHITE_LIST_MODE = compound.getBoolean("WHITE_LIST_MODE");
		MATCH_METADATA = compound.getBoolean("MATCH_METADATA");
		MATCH_NBT = compound.getBoolean("MATCH_NBT");
		MATCH_ORE_DICT = compound.getBoolean("MATCH_ORE_DICT");
		
		TIER = FilterTier.values()[compound.getInteger("TIER")];
		for (int i = 0; i < items.tagCount(); ++i){
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				slots[slot] = new ItemStack(item);
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
		tagcompound.setBoolean("MATCH_METADATA", MATCH_METADATA);
		tagcompound.setBoolean("MATCH_NBT", MATCH_NBT);
		tagcompound.setBoolean("MATCH_ORE_DICT", MATCH_ORE_DICT);
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
		if(stack != ItemStack.EMPTY){
			if(stack.getCount() > amount){
				stack = stack.splitStack(amount);
				markDirty();
			}else{
				setInventorySlotContents(slot, ItemStack.EMPTY);
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
			if (getStackInSlot(i) != ItemStack.EMPTY && getStackInSlot(i).getCount() == 0) {
				slots[i] = ItemStack.EMPTY;
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
		return ItemStack.EMPTY;
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
	@Override
	public boolean isEmpty() {
		return false;
	}
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}
}