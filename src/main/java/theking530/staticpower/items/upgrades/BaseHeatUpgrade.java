package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseHeatUpgrade extends BaseUpgrade {

	public BaseHeatUpgrade(String name, ResourceLocation tier) {
		super(name, tier, UpgradeType.HEAT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		double conductivityUpgrade = getTier().heatConductivityUpgrade.get();
		conductivityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double capacityUpgrade = getTier().heatCapacityUpgrade.get();
		capacityUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(capacityUpgrade * 100) + "%" + TextFormatting.GREEN + " Heat Capacity"));
		tooltip.add(
				new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(conductivityUpgrade * 100) + "%" + TextFormatting.GREEN + " Heat Conductivity"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
