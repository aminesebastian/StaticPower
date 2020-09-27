package theking530.staticpower.cables.fluid;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class FluidCableTooltipUtilities {

	public static ITextComponent getFluidTrasnferRate(float rate) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).append(new StringTextComponent("Max Transfer: ")).appendString(TextFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatFluidRateToString(rate));
	}
}
