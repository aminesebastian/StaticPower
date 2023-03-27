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

public class BaseRangeUpgrade extends BaseUpgrade {

	public BaseRangeUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(1));
		addUpgrade(StaticCoreUpgradeTypes.RANGE, (type, item) -> getTierObject().upgradeConfiguration.rangeUpgrade.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double upgradeAmount = getTierObject().upgradeConfiguration.rangeUpgrade.get();

		if (upgradeAmount < 0) {
			tooltip.add(Component.literal(
					ChatFormatting.RED + "-" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "% " + ChatFormatting.WHITE + "Range"));
		} else {
			tooltip.add(Component.literal(ChatFormatting.GREEN + "+" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "% "
					+ ChatFormatting.WHITE + "Range"));
		}
	}
}
