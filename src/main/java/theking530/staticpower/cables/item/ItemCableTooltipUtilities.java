package theking530.staticpower.cables.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ItemCableTooltipUtilities {

	public static Component getMaxSpeedTooltip(double maxSpeed) {
		return new TextComponent(ChatFormatting.GRAY.toString()).append(new TextComponent("Max Speed: ")).append(ChatFormatting.BLUE.toString())
				.append(new TextComponent(String.valueOf(maxSpeed)));
	}

	public static Component getAccelerationTooltip(double acceleration) {
		return new TextComponent(ChatFormatting.GRAY.toString()).append(new TextComponent("Acceleration: ")).append(ChatFormatting.GREEN.toString())
				.append(new TextComponent(String.valueOf(acceleration)));
	}

	public static Component getFrictionTooltip(double friction) {
		return new TextComponent(ChatFormatting.GRAY.toString()).append(new TextComponent("Friction: ")).append(ChatFormatting.RED.toString())
				.append(new TextComponent(String.valueOf(friction)));
	}
}
