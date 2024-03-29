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
import theking530.staticpower.init.ModUpgradeTypes;
import theking530.staticpower.init.ModUpgradeTypes.CentrifugeUpgradeValue;

public class BaseCentrifugeUpgrade extends BaseUpgrade {

	public BaseCentrifugeUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(1));
		addUpgrade(ModUpgradeTypes.CENTRIFUGE,
				(type, item) -> new CentrifugeUpgradeValue(getTierObject().upgradeConfiguration.maxCentrifugeSpeedUpgrade.get(),
						getTierObject().upgradeConfiguration.centrifugeUpgradedPowerIncrease.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		int speedUpgrade = getTierObject().upgradeConfiguration.maxCentrifugeSpeedUpgrade.get();
		double powerUsageUpgrade = getTierObject().upgradeConfiguration.centrifugeUpgradedPowerIncrease.get();

		tooltip.add(Component
				.literal(ChatFormatting.WHITE + new java.text.DecimalFormat("#").format(speedUpgrade) + ChatFormatting.GREEN + " RPM Max Speed"));
		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerUsageUpgrade * 100) + "%"
				+ ChatFormatting.RED + " RF Per Tick"));
	}
}
