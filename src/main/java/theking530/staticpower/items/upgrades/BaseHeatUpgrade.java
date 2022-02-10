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

public class BaseHeatUpgrade extends BaseUpgrade {

	public BaseHeatUpgrade(String name, ResourceLocation tier) {
		super(name, tier, UpgradeType.HEAT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double conductivityUpgrade = getTier().heatConductivityUpgrade.get();
		conductivityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double capacityUpgrade = getTier().heatCapacityUpgrade.get();
		capacityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(capacityUpgrade * 100) + "%" + ChatFormatting.GREEN + " Heat Capacity"));
		tooltip.add(
				new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(conductivityUpgrade * 100) + "%" + ChatFormatting.GREEN + " Heat Conductivity"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
