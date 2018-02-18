package theking530.staticpower.assists.utilities;

import java.awt.Color;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideModeList {
	
	public enum Mode {
		Regular("Regular", EnumTextFormatting.WHITE, new Color(139, 139, 139)),
		Input("Input", EnumTextFormatting.BLUE, new Color(80, 130, 179)),
		Output("Output", EnumTextFormatting.GOLD, new Color(200, 140, 50)),
		Disabled("Disabled", EnumTextFormatting.RED, new Color(200, 20, 20));

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
