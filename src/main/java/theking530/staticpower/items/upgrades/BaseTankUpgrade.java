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

public class BaseTankUpgrade extends BaseUpgrade implements IUpgradeItem {
	public BaseTankUpgrade(String name, Tier tier) {
		super(name, tier);
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		float capacityBoost = 0.0f;
		switch (getTier()) {
		case BASIC:
			capacityBoost = (1.0f / stack.getMaxStackSize());
			break;
		case STATIC:
			capacityBoost = (1.5f / stack.getMaxStackSize());
			break;
		case ENERGIZED:
			capacityBoost = (2.0f / stack.getMaxStackSize());
			break;
		case LUMUM:
			capacityBoost = (2.5f / stack.getMaxStackSize());
			break;
		default:
			break;
		}
		return (capacityBoost * stack.getCount());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(
				TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "%" + TextFormatting.GREEN + " Tank Capacity"));
	}
}
