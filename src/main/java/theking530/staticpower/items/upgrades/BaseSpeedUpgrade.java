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
import theking530.api.upgrades.UpgradeTypes;

public class BaseSpeedUpgrade extends BaseUpgrade {

	public BaseSpeedUpgrade(ResourceLocation tier) {
		super(tier, UpgradeTypes.SPEED);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double speedUpgrade = getTierObject().upgradeConfiguration.processingSpeedUpgrade.get();
		speedUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double powerRateUpgrade = getTierObject().upgradeConfiguration.processingSpeedPowerCost.get();
		powerRateUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(speedUpgrade * 100) + "%" + ChatFormatting.GREEN + " Processing Speed"));
		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerRateUpgrade * 100) + "%" + ChatFormatting.RED + " Power Use"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}

}
