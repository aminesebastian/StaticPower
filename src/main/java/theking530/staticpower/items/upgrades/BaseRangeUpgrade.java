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

public class BaseRangeUpgrade extends BaseUpgrade {

	public BaseRangeUpgrade(String name, ResourceLocation tier) {
		super(name, tier, new Properties().maxStackSize(1), UpgradeType.RANGE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		float upgradeAmount = getTier().getRangeUpgrade();

		if (upgradeAmount < 0) {
			tooltip.add(new StringTextComponent(TextFormatting.RED + "-" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "% " + TextFormatting.WHITE + "Range"));
		} else {
			tooltip.add(new StringTextComponent(TextFormatting.GREEN + "+" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "% " + TextFormatting.WHITE + "Range"));
		}
	}
}
