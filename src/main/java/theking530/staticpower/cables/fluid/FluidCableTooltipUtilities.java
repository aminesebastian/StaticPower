package theking530.staticpower.cables.fluid;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class FluidCableTooltipUtilities {

	public static ITextComponent getFluidTrasnferRate(float rate) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Max Transfer: ")).appendText(TextFormatting.BLUE.toString())
				.appendSibling(GuiTextUtilities.formatFluidRateToString(rate));
	}
}
