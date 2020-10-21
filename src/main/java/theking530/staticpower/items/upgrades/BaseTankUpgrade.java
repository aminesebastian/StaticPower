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

public class BaseTankUpgrade extends BaseUpgrade {

	public BaseTankUpgrade(String name, ResourceLocation tier) {
		super(name, tier, UpgradeType.TANK);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		float upgradeAmount = getTier().getTankCapacityUpgrade();
		upgradeAmount *= (float) stack.getCount() / stack.getMaxStackSize();
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(upgradeAmount * 100) + "%" + TextFormatting.GREEN + " Tank Capacity"));
	}
}
