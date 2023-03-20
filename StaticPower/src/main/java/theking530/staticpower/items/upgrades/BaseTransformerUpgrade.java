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
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.init.StaticCoreUpgradeTypes;

public class BaseTransformerUpgrade extends BaseUpgrade {

	public BaseTransformerUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(1));
		addUpgrade(StaticCoreUpgradeTypes.POWER_TRANSFORMER.get(), (type, item) -> getTargetVoltage());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		StaticPowerVoltage fromVoltage = getTargetVoltage().downgrade();
		StaticPowerVoltage toVoltage = getTargetVoltage();

		tooltip.add(Component.translatable("gui.staticpower.transformer_upgrade_tooltip", Component.translatable(fromVoltage.getShortName()),
				Component.translatable(toVoltage.getShortName())));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}

	public StaticPowerVoltage getTargetVoltage() {
		if (getTier() == StaticCoreTiers.STATIC) {
			return StaticPowerVoltage.HIGH;
		} else if (getTier() == StaticCoreTiers.ENERGIZED) {
			return StaticPowerVoltage.EXTREME;
		} else if (getTier() == StaticCoreTiers.LUMUM) {
			return StaticPowerVoltage.BONKERS;
		}
		return StaticPowerVoltage.MEDIUM;
	}
}
