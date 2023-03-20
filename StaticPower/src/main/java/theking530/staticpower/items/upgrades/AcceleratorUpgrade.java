package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModUpgradeTypes;

public class AcceleratorUpgrade extends BaseUpgrade {

	public AcceleratorUpgrade() {
		super(new Properties().stacksTo(8));
		addUpgrade(ModUpgradeTypes.DIGISTORE_ACCELERATION.get(), (type, item) -> StaticPowerConfig.SERVER.acceleratorCardImprovment.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(Component.literal(ChatFormatting.GREEN + "Increases the frequency at which"));
		tooltip.add(Component.literal(ChatFormatting.GREEN + "certain digistore attachments will operate."));

		double percentIncrease = StaticPowerConfig.SERVER.acceleratorCardImprovment.get();
		percentIncrease *= (float) stack.getCount() / stack.getMaxStackSize();
		tooltip.add(Component.literal(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(percentIncrease * 100) + "%"
				+ ChatFormatting.GREEN + " Transfer Speed"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}