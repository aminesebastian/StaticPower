package theking530.staticpower.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.StaticPower;

public class StaticPowerBurnableItem extends StaticPowerItem {
	private int burnTime;

	public StaticPowerBurnableItem(String name, int burnTime) {
		this(name, burnTime, new Item.Properties());
	}

	public StaticPowerBurnableItem(String name, int burnTime, Item.Properties properties) {
		super(name, properties.tab(StaticPower.CREATIVE_TAB));
		this.burnTime = burnTime;
	}

	public int getBurnTime(ItemStack itemStack) {
		return burnTime;
	}
}
