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

public class BasePowerUpgrade extends BaseUpgrade {

	public BasePowerUpgrade(ResourceLocation tier) {
		super(tier, UpgradeType.POWER);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double powerUpgrade = getTier().powerUpgrade.get();
		powerUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double powerRateUpgrade = getTier().powerIOUpgrade.get();
		powerRateUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerUpgrade * 100) + "%" + ChatFormatting.GREEN + " SV Capacity"));
		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerRateUpgrade * 100) + "%" + ChatFormatting.GREEN + " SV Per Tick"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
