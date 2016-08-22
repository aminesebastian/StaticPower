package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper.GrinderOutput;
import theking530.staticpower.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class GrinderRecipeRegistry {

	private static final GrinderRecipeRegistry GRINDER_BASE = new GrinderRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map grindingList = new HashMap();

	public static GrinderRecipeRegistry Grinding() {
		return GRINDER_BASE;
	}	
	private GrinderRecipeRegistry() {
		
	}
	public void addRecipe(ItemStack input, GrinderOutput... outputs){
		GrinderOutputWrapper tempWrapper = null;
		GrinderOutput nullOutput = new GrinderOutput(null, 0);
		if(outputs.length == 1) {
			tempWrapper = new GrinderOutputWrapper(outputs[0], nullOutput, nullOutput);
		}else if(outputs.length == 2) {
			tempWrapper = new GrinderOutputWrapper(outputs[0], outputs[1], nullOutput);
		}else if(outputs.length == 3) {
			tempWrapper = new GrinderOutputWrapper(outputs[0], outputs[1], outputs[2]);
		}
		putLists(input, tempWrapper);
	}
	@SuppressWarnings("unchecked")
	public void putLists(ItemStack input, GrinderOutputWrapper outputs){
		grindingList.put(input, outputs);
	}
    public Map getGrindingList() {
        return this.grindingList;
    }
	@SuppressWarnings("rawtypes")
	public GrinderOutputWrapper getgrindingResult(ItemStack itemstack) {
		Iterator iterator = this.grindingList.entrySet().iterator();
		Entry entry;

		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = (Entry) iterator.next();
		} while (!canBeGrinded(itemstack, (ItemStack) entry.getKey()));
		return (GrinderOutputWrapper) entry.getValue();
	}
	private boolean canBeGrinded(ItemStack itemstack, ItemStack itemstack2) {
		return itemstack2.isItemEqual(itemstack);
	}
}
