package theking530.staticpower.assists.utilities;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtilities {
	public static void formatTooltip(List<String> tooltip) {
		if(!showHiddenTooltips()) {
			tooltip.clear();
			tooltip.add(EnumTextFormatting.ITALIC + "Hold Shift");
		}
	}
	public static boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	public static boolean filterItems(List<ItemStack> filterItems, ItemStack itemToCheck, boolean whitelist, boolean matchMetadata, boolean matchNBT, boolean matchOreDict) {
		boolean match = false;

		for(int i=0; i<filterItems.size(); i++) {
			if(filterItems.get(i) != ItemStack.EMPTY) {
				if(ItemStack.areItemsEqual(filterItems.get(i), itemToCheck)) {					
					match =  true;
				}
			}
		}
		if(!match && matchMetadata) {
			for(int i=0; i<filterItems.size(); i++) {
				if(filterItems.get(i) != ItemStack.EMPTY) {
					if(ItemStack.areItemsEqualIgnoreDurability(filterItems.get(i), itemToCheck)) {
						match = true;
						break;
					}
				}
			}
		}
		if(!match && matchNBT) {
			for(int i=0; i<filterItems.size(); i++) {
				if(filterItems.get(i) != ItemStack.EMPTY) {
					if(filterItems.get(i).hasTagCompound() && itemToCheck.hasTagCompound() && ItemStack.areItemStackTagsEqual(filterItems.get(i), itemToCheck)) {	
						match = true;
						break;
					}
				}
			}
		}
		if(!match && matchOreDict) {
			for(int n=0; n<filterItems.size(); n++) {
				if(!filterItems.get(n).isEmpty() && !itemToCheck.isEmpty()) {
					int[] itemstackIDs = OreDictionary.getOreIDs(itemToCheck);
					int[] filterIDs = OreDictionary.getOreIDs(filterItems.get(n));
					for(int i=0; i<itemstackIDs.length; i++) {
						for(int j=0; j<filterIDs.length; j++) {
							if(itemstackIDs[i] == filterIDs[j]) {
								match = true;
								break;
							}
						}
					}
				}
			}			
		}
		if(match) {
			return whitelist ? true : false;
		}
		return whitelist ? false : true;
	}
}
