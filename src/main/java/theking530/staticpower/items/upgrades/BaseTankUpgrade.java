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

public class BaseTankUpgrade extends BaseUpgrade {

	public BaseTankUpgrade(ResourceLocation tier) {
		super(tier, UpgradeTypes.TANK);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double upgradeAmount = getTierObject().upgradeConfiguration.tankCapacityUpgrade.get();
		upgradeAmount *= (float) stack.getCount() / stack.getMaxStackSize();
		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "%" + ChatFormatting.GREEN + " Tank Capacity"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
