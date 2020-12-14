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

public class BaseOutputMultiplierUpgrade extends BaseUpgrade {

	public BaseOutputMultiplierUpgrade(String name, ResourceLocation tier) {
		super(name, tier, new Properties().maxStackSize(8), UpgradeType.OUTPUT_MULTIPLIER);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent(
				TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(getTier().outputMultiplierUpgrade.get() * 100) + "%" + TextFormatting.GREEN + " Output Chance"));
		tooltip.add(new StringTextComponent(
				TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#.#").format(getTier().outputMultiplierPowerCostUpgrade.get() * 100) + "%" + TextFormatting.RED + " Power Usage"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
