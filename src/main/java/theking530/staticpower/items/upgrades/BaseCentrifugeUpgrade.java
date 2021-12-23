package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;
import theking530.api.IUpgradeItem.UpgradeType;

public class BaseCentrifugeUpgrade extends BaseUpgrade {

	public BaseCentrifugeUpgrade(String name, ResourceLocation tier) {
		super(name, tier, new Properties().stacksTo(1), UpgradeType.CENTRIFUGE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		int speedUpgrade = getTier().maxCentrifugeSpeedUpgrade.get();
		double powerUsageUpgrade = getTier().centrifugeUpgradedPowerIncrease.get();

		tooltip.add(new TextComponent(ChatFormatting.WHITE + new java.text.DecimalFormat("#").format(speedUpgrade) + ChatFormatting.GREEN + " RPM Max Speed"));
		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerUsageUpgrade * 100) + "%" + ChatFormatting.RED + " RF Per Tick"));
	}
}
