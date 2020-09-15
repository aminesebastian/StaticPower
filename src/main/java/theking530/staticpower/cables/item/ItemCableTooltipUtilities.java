package theking530.staticpower.cables.item;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ItemCableTooltipUtilities {

	public static ITextComponent getMaxSpeedTooltip(float maxSpeed) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Max Speed: ")).appendText(TextFormatting.BLUE.toString())
				.appendSibling(new StringTextComponent(String.valueOf(maxSpeed)));
	}

	public static ITextComponent getAccelerationTooltip(float acceleration) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Acceleration: ")).appendText(TextFormatting.GREEN.toString())
				.appendSibling(new StringTextComponent(String.valueOf(acceleration)));
	}

	public static ITextComponent getFrictionTooltip(float friction) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Friction: ")).appendText(TextFormatting.RED.toString())
				.appendSibling(new StringTextComponent(String.valueOf(friction)));
	}
}
