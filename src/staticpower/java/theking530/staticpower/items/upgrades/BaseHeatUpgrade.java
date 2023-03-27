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
import theking530.staticcore.init.StaticCoreUpgradeTypes.CombinedHeatUpgradeValue;

public class BaseHeatUpgrade extends BaseUpgrade {

	public BaseHeatUpgrade(ResourceLocation tier) {
		super(tier);
		addUpgrade(StaticCoreUpgradeTypes.HEAT_COMBINED,
				(type, item) -> new CombinedHeatUpgradeValue(getTierObject().upgradeConfiguration.heatCapacityUpgrade.get(),
						getTierObject().upgradeConfiguration.heatConductivityUpgrade.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double conductivityUpgrade = getTierObject().upgradeConfiguration.heatConductivityUpgrade.get();
		conductivityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double capacityUpgrade = getTierObject().upgradeConfiguration.heatCapacityUpgrade.get();
		capacityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(capacityUpgrade * 100) + "%"
				+ ChatFormatting.GREEN + " Heat Capacity"));
		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(conductivityUpgrade * 100) + "%"
				+ ChatFormatting.GREEN + " Heat Conductivity"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
