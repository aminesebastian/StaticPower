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

public class BaseRangeUpgrade extends BaseUpgrade implements IMachineUpgrade {
	public BaseRangeUpgrade(String name, Tier tier) {
		super(name, tier, new Properties().maxStackSize(1));
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		switch (getTier()) {
		case BASIC:
			return 0.75f;
		case STATIC:
			return 1.5f;
		case ENERGIZED:
			return 2.5f;
		case LUMUM:
			return 3.0f;
		default:
			return 0.0f;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		if (getUpgradeValueAtIndex(stack, 0) < 1) {
			tooltip.add(new StringTextComponent(TextFormatting.RED + "-" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "% " + TextFormatting.WHITE + "Range"));
		} else {
			tooltip.add(
					new StringTextComponent(TextFormatting.GREEN + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "% " + TextFormatting.WHITE + "Range"));
		}
	}
}
