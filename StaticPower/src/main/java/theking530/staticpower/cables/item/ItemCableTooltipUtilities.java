package theking530.staticpower.cables.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ItemCableTooltipUtilities {

	public static Component getMaxSpeedTooltip(double maxSpeed) {
		return Component.literal(ChatFormatting.GRAY.toString()).append(Component.literal("Max Speed: ")).append(ChatFormatting.BLUE.toString())
				.append(Component.literal(String.valueOf(maxSpeed)));
	}

	public static Component getAccelerationTooltip(double acceleration) {
		return Component.literal(ChatFormatting.GRAY.toString()).append(Component.literal("Acceleration: ")).append(ChatFormatting.GREEN.toString())
				.append(Component.literal(String.valueOf(acceleration)));
	}

	public static Component getFrictionTooltip(double friction) {
		return Component.literal(ChatFormatting.GRAY.toString()).append(Component.literal("Friction: ")).append(ChatFormatting.RED.toString())
				.append(Component.literal(String.valueOf(friction)));
	}
}
