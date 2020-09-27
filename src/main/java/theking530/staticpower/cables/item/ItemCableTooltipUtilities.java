package theking530.staticpower.cables.item;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ItemCableTooltipUtilities {

	public static ITextComponent getMaxSpeedTooltip(float maxSpeed) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).append(new StringTextComponent("Max Speed: ")).appendString(TextFormatting.BLUE.toString())
				.append(new StringTextComponent(String.valueOf(maxSpeed)));
	}

	public static ITextComponent getAccelerationTooltip(float acceleration) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).append(new StringTextComponent("Acceleration: ")).appendString(TextFormatting.GREEN.toString())
				.append(new StringTextComponent(String.valueOf(acceleration)));
	}

	public static ITextComponent getFrictionTooltip(float friction) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).append(new StringTextComponent("Friction: ")).appendString(TextFormatting.RED.toString())
				.append(new StringTextComponent(String.valueOf(friction)));
	}
}
