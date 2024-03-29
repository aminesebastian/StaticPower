package theking530.staticcore.blockentity.components.control.sideconfiguration;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import theking530.staticcore.utilities.SDColor;

// TODO: The NA mode could probably be removed.
public enum MachineSideMode {
	NA("na", ChatFormatting.WHITE, new SDColor(139, 139, 139, 255).fromEightBitToFloat()), Input("input", ChatFormatting.BLUE, new SDColor(80, 130, 179, 255).fromEightBitToFloat()),
	Input2("input2", ChatFormatting.DARK_PURPLE, new SDColor(100, 0, 200, 255).fromEightBitToFloat()), Input3("input3", ChatFormatting.LIGHT_PURPLE, new SDColor(213, 0, 168, 255).fromEightBitToFloat()),
	Output("output", ChatFormatting.GOLD, new SDColor(237, 160, 45, 255).fromEightBitToFloat()), Output2("output2", ChatFormatting.GREEN, new SDColor(0, 200, 50, 255).fromEightBitToFloat()),
	Output3("output3", ChatFormatting.YELLOW, new SDColor(220, 220, 0, 255).fromEightBitToFloat()), Output4("output4", ChatFormatting.AQUA, new SDColor(0, 206, 217, 255).fromEightBitToFloat()),
	Disabled("disabled", ChatFormatting.RED, new SDColor(200, 20, 20, 255).fromEightBitToFloat()), Never("never", ChatFormatting.GRAY, new SDColor(0, 0, 0, 0).fromEightBitToFloat());

	/** The unlocalized name of the mode. */
	private String name;
	/** The font color of the mode. */
	private ChatFormatting fontColor;
	/** The color of the mode for UI/world rendering. */
	private SDColor color;

	private MachineSideMode(String name, ChatFormatting fontColor, SDColor color) {
		this.name = "gui.staticcore.mode." + name;
		this.fontColor = fontColor;
		this.color = color;
	}

	public ChatFormatting getFontColor() {
		return fontColor;
	}

	public Component getName() {
		return Component.literal(fontColor.toString()).append(Component.translatable(name));
	}

	public SDColor getColor() {
		return color;
	}

	public boolean isInputMode() {
		return this == Input || this == Input2 || this == Input3;
	}

	public boolean isOutputMode() {
		return this == Output || this == Output2 || this == Output3 || this == Output4;
	}

	public boolean isDisabledMode() {
		return this == Disabled || this == Never;
	}

	public boolean areOfSameType(MachineSideMode other) {
		if (other.isOutputMode() && this.isOutputMode()) {
			return true;
		}
		if (other.isInputMode() && this.isInputMode()) {
			return true;
		}
		if (other.isDisabledMode() && this.isDisabledMode()) {
			return true;
		}
		return false;
	}
}
