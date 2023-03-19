package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.upgrades.UpgradeTypes;
import theking530.staticpower.data.StaticPowerTiers;

public class BaseTransformerUpgrade extends BaseUpgrade {

	public BaseTransformerUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(1), UpgradeTypes.POWER_TRANSFORMER);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		StaticPowerVoltage fromVoltage = StaticPowerVoltage.LOW;
		StaticPowerVoltage toVoltage = StaticPowerVoltage.MEDIUM;

		if (getTier() == StaticPowerTiers.STATIC) {
			fromVoltage = StaticPowerVoltage.MEDIUM;
			toVoltage = StaticPowerVoltage.HIGH;
		} else if (getTier() == StaticPowerTiers.ENERGIZED) {
			fromVoltage = StaticPowerVoltage.HIGH;
			toVoltage = StaticPowerVoltage.EXTREME;
		} else if (getTier() == StaticPowerTiers.LUMUM) {
			fromVoltage = StaticPowerVoltage.EXTREME;
			toVoltage = StaticPowerVoltage.BONKERS;
		}

		tooltip.add(Component.translatable("gui.staticpower.transformer_upgrade_tooltip", Component.translatable(fromVoltage.getShortName()),
				Component.translatable(toVoltage.getShortName())));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
