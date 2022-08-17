package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseRangeUpgrade extends BaseUpgrade {

	public BaseRangeUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(1), UpgradeType.RANGE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double upgradeAmount = getTier().rangeUpgrade.get();

		if (upgradeAmount < 0) {
			tooltip.add(new TextComponent(ChatFormatting.RED + "-" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "% " + ChatFormatting.WHITE + "Range"));
		} else {
			tooltip.add(new TextComponent(ChatFormatting.GREEN + "+" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "% " + ChatFormatting.WHITE + "Range"));
		}
	}
}
