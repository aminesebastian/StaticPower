package theking530.staticpower.cables.attachments.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.init.ModCreativeTabs;

public abstract class AbstractDigistoreCableAttachment extends AbstractCableAttachment {

	public AbstractDigistoreCableAttachment() {
		super(ModCreativeTabs.CABLES);
	}

	public abstract double getPowerUsage(ItemStack attachment);

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		double powerUsage = getPowerUsage(stack);
		if (powerUsage > 0) {
			tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.digistore_attachment_power_usage").withStyle(ChatFormatting.GREEN)
					.append(ChatFormatting.WHITE.toString() + PowerTextFormatting.formatPowerRateToString(powerUsage).getString())));
		}
	}
}
