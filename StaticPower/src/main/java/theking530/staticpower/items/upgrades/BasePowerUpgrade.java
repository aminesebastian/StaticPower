package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.init.StaticCoreUpgradeTypes.CombinedPowerUpgradeValue;

public class BasePowerUpgrade extends BaseUpgrade {

	public BasePowerUpgrade(ResourceLocation tier) {
		super(tier);
		addUpgrade(StaticCoreUpgradeTypes.POWER_COMBINED_UPGRADE.get(),
				(type, item) -> new CombinedPowerUpgradeValue(getTierObject().upgradeConfiguration.powerUpgrade.get(),
						getTierObject().upgradeConfiguration.powerIOUpgrade.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double powerUpgrade = getTierObject().upgradeConfiguration.powerUpgrade.get();
		powerUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double powerRateUpgrade = getTierObject().upgradeConfiguration.powerIOUpgrade.get();
		powerRateUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerUpgrade * 100) + "%"
				+ ChatFormatting.GREEN + " SV Capacity"));
		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerRateUpgrade * 100) + "%"
				+ ChatFormatting.GREEN + " SV Per Tick"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
