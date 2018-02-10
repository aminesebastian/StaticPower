package theking530.staticpower.machines;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IUpgradeable {

	public ItemStack getUpgrade(Item upgradeBase);
	public boolean hasUpgrade(Item upgradeBase);
	public List<ItemStack> getAllUpgrades();
	
}
