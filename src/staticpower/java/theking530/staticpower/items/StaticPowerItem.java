package theking530.staticpower.items;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import theking530.staticcore.item.StaticCoreItem;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.tags.ModItemTags;

/**
 * Base class for most static power items.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerItem extends StaticCoreItem {
	/**
	 * Base constructor for a static power item. Uses the default item properties
	 * and adds the item to the static power default item group.
	 * 
	 * @param name The registry name for this item sans namespace.
	 */
	public StaticPowerItem() {
		super(new Item.Properties().tab(ModCreativeTabs.GENERAL));
	}

	public StaticPowerItem(Item.Properties properties) {
		super(properties);
	}

	public StaticPowerItem(CreativeModeTab tab) {
		super(new Item.Properties().tab(tab));
	}

	@Override
	public Collection<CreativeModeTab> getCreativeTabs() {
		List<CreativeModeTab> output = new LinkedList<>();
		output.add(ModCreativeTabs.GENERAL);

		if (ModItemTags.matches(ModItemTags.MATERIALS, this)) {
			output.add(ModCreativeTabs.MATERIALS);
		}

		if (ModItemTags.matches(ModItemTags.TOOLS, this)) {
			output.add(ModCreativeTabs.TOOLS);
		}

		if (ModItemTags.matches(ModItemTags.CABLES, this)) {
			output.add(ModCreativeTabs.CABLES);
		}

		return output;
	}
}
