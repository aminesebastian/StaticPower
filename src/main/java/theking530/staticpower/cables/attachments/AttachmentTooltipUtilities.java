package theking530.staticpower.cables.attachments;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

public class AttachmentTooltipUtilities {
	
	public static void addSlotsCountTooltip(String unlocalizedLabel, int amount, List<Component> tooltip) {
		tooltip.add(new TranslatableComponent(unlocalizedLabel).append(" " + ChatFormatting.DARK_AQUA.toString() + amount));
	}
}
