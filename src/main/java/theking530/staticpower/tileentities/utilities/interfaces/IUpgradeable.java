package theking530.staticpower.tileentities.utilities.interfaces;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IUpgradeable {

	public boolean isUpgradeable();

	public boolean canAcceptUpgrade(ItemStack upgrade);

	public boolean hasUpgrade(Item upgradeBase);

	public List<ItemStack> getAllUpgrades();

}
