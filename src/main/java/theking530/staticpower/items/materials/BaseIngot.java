package theking530.staticpower.items.materials;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.staticpower.items.ItemBase;

public class BaseIngot extends ItemBase{

	public BaseIngot(String name) {
		super(name);
        //setHasSubtypes(true);
	}
	/**
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
	    switch(meta) {
		    case 0: return "CopperIngot";
		    case 1: return "TinIngot";
		    case 2: return "SilverIngot";
		    case 3: return "LeadIngot";
		    case 4: return "PlatinumIngot";
	    }
		return "CopperIngot";
	}
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
	    subItems.add(new ItemStack(itemIn, 1, 0));
	    subItems.add(new ItemStack(itemIn, 1, 1));
	    subItems.add(new ItemStack(itemIn, 1, 2));
	    subItems.add(new ItemStack(itemIn, 1, 3));
	    subItems.add(new ItemStack(itemIn, 1, 4));
	}
	*/
}
