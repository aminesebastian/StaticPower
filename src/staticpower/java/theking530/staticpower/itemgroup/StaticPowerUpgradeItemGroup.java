package theking530.staticpower.itemgroup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.init.ModItems;

public class StaticPowerUpgradeItemGroup extends CreativeModeTab {

	public StaticPowerUpgradeItemGroup() {
		super("staticpower.upgrades");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon() {
		return new ItemStack(ModItems.EnergizedSpeedUpgrade.get());
	}
}