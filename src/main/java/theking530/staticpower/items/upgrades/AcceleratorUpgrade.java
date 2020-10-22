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
import theking530.staticpower.StaticPowerConfig;

public class AcceleratorUpgrade extends BaseUpgrade {

	public AcceleratorUpgrade(String name) {
		super(name, new Properties().maxStackSize(8), UpgradeType.DIGISTORE_ATTACHMENT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "Increases the frequency at which"));
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "certain digistore attachments will operate."));

		double percentIncrease = StaticPowerConfig.acceleratorCardMaxImprovment;
		percentIncrease *= (float) stack.getCount() / stack.getMaxStackSize();
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(percentIncrease * 100) + "%" + TextFormatting.GREEN + " Transfer Speed"));
	}
}