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
import theking530.staticcore.init.StaticCoreUpgradeTypes.OutputMultiplierUpgradeValue;

public class BaseOutputMultiplierUpgrade extends BaseUpgrade {

	public BaseOutputMultiplierUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(8));
		addUpgrade(StaticCoreUpgradeTypes.OUTPUT_MULTIPLIER,
				(type, item) -> new OutputMultiplierUpgradeValue(getTierObject().upgradeConfiguration.outputMultiplierUpgrade.get(),
						getTierObject().upgradeConfiguration.outputMultiplierPowerCostUpgrade.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(Component.literal(ChatFormatting.WHITE + "+"
				+ new java.text.DecimalFormat("#").format(getTierObject().upgradeConfiguration.outputMultiplierUpgrade.get() * 100) + "%"
				+ ChatFormatting.GREEN + " Output Chance"));
		tooltip.add(Component.literal(ChatFormatting.WHITE + "+"
				+ new java.text.DecimalFormat("#.#").format(getTierObject().upgradeConfiguration.outputMultiplierPowerCostUpgrade.get() * 100) + "%"
				+ ChatFormatting.RED + " Power Usage"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
