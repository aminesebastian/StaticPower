package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.utilities.Tier;

public class BaseDigistoreCapacityUpgrade extends BaseUpgrade {

	public BaseDigistoreCapacityUpgrade(String name, Tier tier) {
		super(name, tier, new Properties().maxStackSize(8));
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		return getTier().getDigistoreItemCapacityAmount();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		if (getUpgradeValueAtIndex(stack, 0) < 0) {
			tooltip.add(new StringTextComponent(TextFormatting.RED + "" + new java.text.DecimalFormat("#").format(getUpgradeValueAtIndex(stack, 0)) + TextFormatting.WHITE + "  Items"));
			tooltip.add(new StringTextComponent(TextFormatting.RED + "(" + new java.text.DecimalFormat("#").format(getUpgradeValueAtIndex(stack, 0) / 64) + ") " + TextFormatting.WHITE + "Stacks"));
		} else {
			tooltip.add(new StringTextComponent(TextFormatting.GREEN + "+" + new java.text.DecimalFormat("#").format(getUpgradeValueAtIndex(stack, 0)) + TextFormatting.WHITE + "  Items"));
			tooltip.add(new StringTextComponent(TextFormatting.GREEN + "(+" + new java.text.DecimalFormat("#").format(getUpgradeValueAtIndex(stack, 0) / 64) + ") " + TextFormatting.WHITE + "Stacks"));
		}
	}
}
