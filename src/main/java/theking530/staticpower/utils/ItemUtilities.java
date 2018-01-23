package theking530.staticpower.utils;

import java.util.List;

import org.lwjgl.input.Keyboard;

public class ItemUtilities {
	public static void formatTooltip(List<String> tooltip) {
		if(!showHiddenTooltips()) {
			tooltip.clear();
			tooltip.add(EnumTextFormatting.ITALIC + "Hold Shift");
		}
	}
	public static boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}
