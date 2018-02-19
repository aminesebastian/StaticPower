package theking530.staticpower.tileentity;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IUpgradeable {

	public boolean isUpgradeable();
	public boolean canAcceptUpgrade(ItemStack upgrade);
	public ItemStack getUpgrade(Item upgradeBase);
	public boolean hasUpgrade(Item upgradeBase);
	public ItemStack getUpgrade(ItemStack upgrade);
	public boolean hasUpgrade(ItemStack upgrade);
	public List<ItemStack> getAllUpgrades();
	
}
