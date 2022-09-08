package theking530.staticpower.cables.attachments.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;

public abstract class AbstractDigistoreCableAttachment extends AbstractCableAttachment {

	public AbstractDigistoreCableAttachment() {
		super();
	}

	public abstract double getPowerUsage(ItemStack attachment);

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		double powerUsage = getPowerUsage(stack);
		if (powerUsage > 0) {
			tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.digistore_attachment_power_usage").withStyle(ChatFormatting.GREEN)
					.append(ChatFormatting.WHITE.toString() + StaticPowerEnergyTextUtilities.formatPowerRateToString(powerUsage).getString())));
		}
	}
}
