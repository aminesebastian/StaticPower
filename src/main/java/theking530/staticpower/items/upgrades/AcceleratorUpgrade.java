package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPowerConfig;

import net.minecraft.world.item.Item.Properties;
import theking530.api.IUpgradeItem.UpgradeType;

public class AcceleratorUpgrade extends BaseUpgrade {

	public AcceleratorUpgrade(String name) {
		super(name, new Properties().stacksTo(8), UpgradeType.DIGISTORE_ATTACHMENT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent(ChatFormatting.GREEN + "Increases the frequency at which"));
		tooltip.add(new TextComponent(ChatFormatting.GREEN + "certain digistore attachments will operate."));

		double percentIncrease = StaticPowerConfig.SERVER.acceleratorCardImprovment.get();
		percentIncrease *= (float) stack.getCount() / stack.getMaxStackSize();
		tooltip.add(new TextComponent(ChatFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(percentIncrease * 100) + "%" + ChatFormatting.GREEN + " Transfer Speed"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}