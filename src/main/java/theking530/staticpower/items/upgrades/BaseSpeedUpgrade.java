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

public class BaseSpeedUpgrade extends BaseUpgrade implements IMachineUpgrade {

	public BaseSpeedUpgrade(String name, Tier tier) {
		super(name, tier);
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		float upgradeValue = 0.0f;
		if (upgradeNumber == 0) {
			switch (getTier()) {
			case BASIC:
				upgradeValue = (1.05f / stack.getMaxStackSize());
				break;
			case STATIC:
				upgradeValue = (1.3f / stack.getMaxStackSize());
				break;
			case ENERGIZED:
				upgradeValue = (1.75f / stack.getMaxStackSize());
				break;
			case LUMUM:
				upgradeValue = (3.0f / stack.getMaxStackSize());
				break;
			case CREATIVE:
				upgradeValue = (10.0f / stack.getMaxStackSize());
				break;
			default:
				break;
			}
			return (upgradeValue * stack.getCount());
		} else {
			switch (getTier()) {
			case BASIC:
				upgradeValue = (1.25f / stack.getMaxStackSize());
				break;
			case STATIC:
				upgradeValue = (1.5f / stack.getMaxStackSize());
				break;
			case ENERGIZED:
				upgradeValue = (2.0f / stack.getMaxStackSize());
				break;
			case LUMUM:
				upgradeValue = (2.5f / stack.getMaxStackSize());
				break;
			case CREATIVE:
				upgradeValue = 0.0f;
				break;
			default:
				break;
			}
			return (upgradeValue * stack.getCount());
		}

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(
				TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "%" + TextFormatting.GREEN + " Processing Speed"));
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 1)) * 100) + "%" + TextFormatting.RED + " Power Use"));
	}

}
