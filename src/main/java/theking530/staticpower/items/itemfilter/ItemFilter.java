package theking530.staticpower.items.itemfilter;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;

public class ItemFilter extends ItemBase {
	
	public FilterTier TIER;
	
	public ItemFilter(String name, FilterTier tier) {
		super(name);
		TIER = tier;
	}
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return new ActionResult(EnumActionResult.PASS, itemStack);
		}else if (!player.isSneaking()) {
			FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDItemFilter, world, 0, 0, 0);
			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}else{
			return new ActionResult(EnumActionResult.FAIL, itemStack);
		}
        
    }
	public void onCreated(ItemStack item, World world, EntityPlayer player) {
		super.onCreated(item, world, player);
	}
	public boolean evaluateFilter(ItemStack filter, ItemStack itemstack) {
		int slotCount = TIER == FilterTier.BASIC ? 4 : TIER == FilterTier.UPGRADED ? 8 : 10;
		ItemStack[] slots = new ItemStack[slotCount];
		if(filter.hasTagCompound()) {
			NBTTagList items = filter.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i){
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("Slot");
				if (slot >= 0 && slot < slotCount) {
					slots[slot] = ItemStack.loadItemStackFromNBT(item);
				}
			}
		}
		for(int i=0; i<slotCount; i++) {
			if(slots[i] != null) {
				if(slots[i].isItemEqual(itemstack)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1; 
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		int slotCount = TIER == FilterTier.BASIC ? 4 : TIER == FilterTier.UPGRADED ? 8 : 10;
		ItemStack[] slots = new ItemStack[slotCount];
		
		if(itemstack.hasTagCompound()) {
			NBTTagList items = itemstack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i){
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("Slot");
				if (slot >= 0 && slot < slotCount) {
					slots[slot] = ItemStack.loadItemStackFromNBT(item);
				}
			}
		}
    	if(showHiddenTooltips()) {
    		boolean empty = true;
    		for(int i=0; i<slotCount; i++) {
    			if(slots[i] != null) {
    				list.add("Slot " + (i+1) + ": " + slots[i].getDisplayName());
    				empty=false;
    			}
    		}
    		if(empty) {
    			list.add(EnumTextFormatting.ITALIC + "Empty");
    		}
    	}else{
    		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
	    }
	}
	public static void writePremadeFilter(ItemStack stack, ItemStack...filterItems) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagList items = new NBTTagList();		
		for (int i = 0; i < filterItems.length; ++i){
			if (filterItems[i] != null){
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				filterItems[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		tagCompound.setTag("ItemInventory", items);
		stack.setTagCompound(tagCompound);
	}
	public static void writeQuarryFilter(ItemStack stack) {
		ItemStack[] tempItems = new ItemStack[7];
		
		tempItems[0] = new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE));
		tempItems[1] = new ItemStack(Item.getItemFromBlock(Blocks.DIRT));
		tempItems[2] = new ItemStack(Item.getItemFromBlock(Blocks.STONE), 1, 1);
		tempItems[3] = new ItemStack(Item.getItemFromBlock(Blocks.STONE), 1, 3);
		tempItems[4] = new ItemStack(Item.getItemFromBlock(Blocks.STONE), 1, 5);
		tempItems[5] = new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL));
		tempItems[6] = new ItemStack(Items.FLINT);
		writePremadeFilter(stack, tempItems);
		stack.setStackDisplayName("Quarry Filter");
	}
}
