package theking530.staticpower.cables.attachments;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class AttachmentTooltipUtilities {
	
	public static void addSlotsCountTooltip(String unlocalizedLabel, int amount, List<ITextComponent> tooltip) {
		tooltip.add(new TranslationTextComponent(unlocalizedLabel).appendString(" " + TextFormatting.DARK_AQUA.toString() + amount));
	}
}
