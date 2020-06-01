package theking530.staticpower.items.upgrades;

import java.text.DecimalFormat;
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

public class BaseQuarryingUpgrade extends BaseUpgrade implements IUpgradeItem {

	public BaseQuarryingUpgrade(String name, Tier tier) {
		super(name, tier, new Properties().maxStackSize(1));
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		int extraBlocksPerTick = 1;
		float powerUseMultiplier = 1f;

		switch (getTier()) {
		case STATIC:
			extraBlocksPerTick = 2;
			powerUseMultiplier = 2f;
			break;
		case ENERGIZED:
			extraBlocksPerTick = 3;
			powerUseMultiplier = 3f;
			break;
		case LUMUM:
			extraBlocksPerTick = 5;
			powerUseMultiplier = 5f;
			break;
		default:
			break;
		}
		if (upgradeNumber == 1) {
			return extraBlocksPerTick;
		} else {
			return powerUseMultiplier;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Mines " + getUpgradeValueAtIndex(stack, 0) + TextFormatting.GREEN + " Blocks Per Tick"));
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + new DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 1)) * 100) + "%" + TextFormatting.RED + " Power Use"));
	}
}
