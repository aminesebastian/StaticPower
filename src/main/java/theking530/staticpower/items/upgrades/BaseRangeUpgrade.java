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

public class BaseRangeUpgrade extends BaseUpgrade implements IUpgradeItem {
	public final float rangeUpgrade;

	public BaseRangeUpgrade(String name, Tier tier) {
		super(name, tier, new Properties().maxStackSize(1));
		switch (getTier()) {
		case BASIC:
			rangeUpgrade = 2.0f;
			break;
		case STATIC:
			rangeUpgrade = 3.0f;
			break;
		case ENERGIZED:
			rangeUpgrade = 4.0f;
			break;
		case LUMUM:
			rangeUpgrade = 5.0f;
			break;
		default:
			rangeUpgrade = 0.0f;
			break;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		if (getUpgradeValueAtIndex(stack, 0) < 1) {
			tooltip.add(new StringTextComponent(TextFormatting.RED + "-" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "% " + TextFormatting.WHITE + "Range"));
		} else {
			tooltip.add(new StringTextComponent(TextFormatting.GREEN + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "% " + TextFormatting.WHITE + "Range"));
		}
	}
}
