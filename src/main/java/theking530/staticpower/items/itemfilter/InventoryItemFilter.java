package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryItemFilter implements IInventory {
	
	public final ItemStack owningItemStack;
	private ItemStack[] slots;
	private boolean whiteListMode = true;
    private boolean checkMetadata = false;
    private boolean checkNBT = false;
    private boolean checkOreDictionary = false;
    private boolean checkModDomain = false;
	private FilterTier filterTier;
		
	public InventoryItemFilter(ItemStack stack, FilterTier tier) {
		owningItemStack = stack;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		filterTier = tier;

		slots = new ItemStack[filterTier.getSlotCount()+1];
		for(int i=0; i<slots.length; i++) {
			slots[i] = ItemStack.EMPTY;
		}
		readFromNBT(stack.getTagCompound());
	}
	public FilterTier getFilterTier() {
		return filterTier;
	}
	public void setMatchMetadata(boolean mode) {
		checkMetadata = mode;
	}
	public boolean getMatchMetadata() {
		return checkMetadata;
	}
	public void setMatchNBT(boolean mode) {
		checkNBT = mode;
	}
	public boolean getMatchNBT() {
		return checkNBT;
	}
	
	public void setMatchOreDictionary(boolean mode) {
		checkOreDictionary = mode;
	}
	public boolean getMatchOreDictionary() {
		return checkOreDictionary;
	}
	public void setMatchModeID(boolean mode) {
		checkModDomain = mode;
	}
	public boolean getMatchModID() {
		return checkModDomain;
	}
	public void setWhiteListMode(boolean mode) {
		whiteListMode = mode;
	}
	public boolean getWhiteListMode() {
		return whiteListMode;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack){
		return !(itemstack.getItem() instanceof ItemFilter);
	}
	public void readFromNBT(NBTTagCompound compound){
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
		whiteListMode = compound.getBoolean("WHITE_LIST_MODE");
		checkMetadata = compound.getBoolean("MATCH_METADATA");
		checkNBT = compound.getBoolean("MATCH_NBT");
		checkOreDictionary = compound.getBoolean("MATCH_ORE_DICT");
		
		//filterTier = FilterTier.values()[compound.getInteger("TIER")];
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
		//tagcompound.setInteger("TIER", filterTier.ordinal());
		tagcompound.setTag("ItemInventory", items);
		tagcompound.setBoolean("WHITE_LIST_MODE", whiteListMode);
		tagcompound.setBoolean("MATCH_METADATA", checkMetadata);
		tagcompound.setBoolean("MATCH_NBT", checkNBT);
		tagcompound.setBoolean("MATCH_ORE_DICT", checkOreDictionary);
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
		writeToNBT(owningItemStack.getTagCompound());
	}
	@Override
	public String getName() {
		String name = "ItemFilter";
		switch(filterTier) {
		case BASIC:
			name = "BasicItemFilter";
			break;
		case UPGRADED:
			name = "UpgradedItemFilter";
			break;
		case ADVANCED:
			name = "AdvancedItemFilter";
			break;
		}
		return "container." + name;
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