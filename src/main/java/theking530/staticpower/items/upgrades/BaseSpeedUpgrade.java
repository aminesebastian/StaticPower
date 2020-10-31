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

public class BaseSpeedUpgrade extends BaseUpgrade {

	public BaseSpeedUpgrade(String name, ResourceLocation tier) {
		super(name, tier, UpgradeType.SPEED);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		double speedUpgrade = getTier().processingSpeedUpgrade.get();
		speedUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		double powerRateUpgrade = getTier().processingSpeedPowerCost.get();
		powerRateUpgrade *= (float) stack.getCount() / stack.getMaxStackSize();

		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(speedUpgrade * 100) + "%" + TextFormatting.GREEN + " Processing Speed"));
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerRateUpgrade * 100) + "%" + TextFormatting.RED + " Power Use"));
	}

}
