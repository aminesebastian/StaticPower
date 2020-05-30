package theking530.staticpower.tileentities.utilities;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import theking530.api.utilities.Color;

public class SideModeList {

	public enum Mode {
		Regular("Regular", TextFormatting.WHITE, new Color(139, 139, 139).fromEightBitToFloat()), Input("Input", TextFormatting.BLUE, new Color(80, 130, 179).fromEightBitToFloat()),
		Input2("Input2", TextFormatting.DARK_PURPLE, new Color(100, 0, 200).fromEightBitToFloat()), Output("Output", TextFormatting.GOLD, new Color(200, 140, 50).fromEightBitToFloat()),
		Output2("Output1", TextFormatting.GREEN, new Color(0, 200, 50).fromEightBitToFloat()), Output3("Output2", TextFormatting.YELLOW, new Color(220, 220, 0).fromEightBitToFloat()),
		Disabled("Disabled", TextFormatting.RED, new Color(200, 20, 20).fromEightBitToFloat()), Never("Never", TextFormatting.WHITE, new Color(139, 139, 139).fromEightBitToFloat());

		private String name;
		private TextFormatting fontColor;
		private Color borderColor;

		private Mode(String name, TextFormatting fontColor, Color borderColor) {
			this.name = name;
			this.fontColor = fontColor;
			this.borderColor = borderColor;
		}

		public TextFormatting getFontColor() {
			return fontColor;
		}

		public String getName() {
			return "mode." + name;
		}

		public String getLocalizedName() {
			return I18n.format(getName());
		}

		public Color getBorderColor() {
			return borderColor;
		}

		public boolean isInputMode() {
			return this == Input || this == Input2;
		}

		public boolean isOutputMode() {
			return this == Output || this == Output2 || this == Output3;
		}

		public static Mode getModeFromInt(int mode) {
			switch (mode) {
			case 0:
				return Regular;
			case 1:
				return Input;
			case 2:
				return Output;
			case 3:
				return Disabled;
			default:
				break;
			}
			return null;
		}
	}
}
