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

public class BaseHeatCapacityUpgrade extends BaseUpgrade {

	public BaseHeatCapacityUpgrade(ResourceLocation tier) {
		super(tier);
		addUpgrade(StaticCoreUpgradeTypes.HEAT_CAPACITY, (type, item) -> getTierObject().upgradeConfiguration.heatCapacityUpgrade.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double capacityUpgrade = getTierObject().upgradeConfiguration.heatCapacityUpgrade.get();
		capacityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(capacityUpgrade * 100) + "%"
				+ ChatFormatting.GREEN + " Heat Capacity"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
