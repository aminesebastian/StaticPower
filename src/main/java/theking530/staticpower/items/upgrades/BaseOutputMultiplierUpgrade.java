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

public class BaseOutputMultiplierUpgrade extends BaseUpgrade implements IUpgradeItem {

	public BaseOutputMultiplierUpgrade(String name, Tier tier) {
		super(name, tier, new Properties().maxStackSize(8));
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		switch (getTier()) {
		case BASIC:
			return 0.01f * stack.getCount();
		case STATIC:
			return 0.02f * stack.getCount();
		case ENERGIZED:
			return 0.03f * stack.getCount();
		case LUMUM:
			return 0.04f * stack.getCount();
		default:
			return 0.0f;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(
				TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 0)) * 100) + "%" + TextFormatting.GREEN + " Output Chance"));
		tooltip.add(new StringTextComponent(
				TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#.#").format((getUpgradeValueAtIndex(stack, 0) / 2) * 100) + "%" + TextFormatting.RED + " Power Usage"));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize()));
	}
}
