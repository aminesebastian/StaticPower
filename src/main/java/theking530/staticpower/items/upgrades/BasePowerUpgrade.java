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

public class BasePowerUpgrade extends BaseUpgrade implements IUpgradeItem {

	public BasePowerUpgrade(String name, Tier tier) {
		super(name, tier);
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		float capacityUpgrade = 1f;
		float perTickUpgrade = 1f;
		switch (getTier()) {
		case BASIC:
			capacityUpgrade = (1.25f / stack.getMaxStackSize());
			perTickUpgrade = (2.0f / stack.getMaxStackSize());
			break;
		case STATIC:
			capacityUpgrade = (1.5f / stack.getMaxStackSize());
			perTickUpgrade = (3f / stack.getMaxStackSize());
			break;
		case ENERGIZED:
			capacityUpgrade = (1.75f / stack.getMaxStackSize());
			perTickUpgrade = (4f / stack.getMaxStackSize());
			break;
		case LUMUM:
			capacityUpgrade = (2f / stack.getMaxStackSize());
			perTickUpgrade = (6f / stack.getMaxStackSize());
			break;
		default:
			break;
		}

		if (upgradeNumber == 0) {
			return capacityUpgrade * stack.getCount();
		} else {
			return perTickUpgrade * stack.getCount();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(
				new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "%" + TextFormatting.GREEN + " RF Capacity"));
		tooltip.add(
				new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 1)) * 100) + "%" + TextFormatting.GREEN + " RF Per Tick"));
	}
}
