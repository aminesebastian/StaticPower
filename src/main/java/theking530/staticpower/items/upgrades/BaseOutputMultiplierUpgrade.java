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

public class BaseOutputMultiplierUpgrade extends BaseUpgrade {

	public BaseOutputMultiplierUpgrade(String name, ResourceLocation tier) {
		super(name, tier, new Properties().stacksTo(8), UpgradeType.OUTPUT_MULTIPLIER);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent(
				ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(getTier().outputMultiplierUpgrade.get() * 100) + "%" + ChatFormatting.GREEN + " Output Chance"));
		tooltip.add(new TextComponent(
				ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#.#").format(getTier().outputMultiplierPowerCostUpgrade.get() * 100) + "%" + ChatFormatting.RED + " Power Usage"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
