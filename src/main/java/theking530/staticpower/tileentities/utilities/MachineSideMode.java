package theking530.staticpower.tileentities.utilities;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.api.utilities.Color;

public enum MachineSideMode {
	Regular("regular", TextFormatting.WHITE, new Color(139, 139, 139).fromEightBitToFloat()), Input("input", TextFormatting.BLUE, new Color(80, 130, 179).fromEightBitToFloat()),
	Input2("input2", TextFormatting.DARK_PURPLE, new Color(100, 0, 200).fromEightBitToFloat()), Output("output", TextFormatting.GOLD, new Color(200, 140, 50).fromEightBitToFloat()),
	Output2("output1", TextFormatting.GREEN, new Color(0, 200, 50).fromEightBitToFloat()), Output3("output2", TextFormatting.YELLOW, new Color(220, 220, 0).fromEightBitToFloat()),
	Disabled("disabled", TextFormatting.RED, new Color(200, 20, 20).fromEightBitToFloat()), Never("never", TextFormatting.WHITE, new Color(0, 0, 0, 0).fromEightBitToFloat());

	/** The unlocalized name of the mode. */
	private String name;
	/** The font color of the mode. */
	private TextFormatting fontColor;
	/** The color of the mode for UI/world rendering. */
	private Color color;

	private MachineSideMode(String name, TextFormatting fontColor, Color color) {
		this.name = "gui.staticpower.mode." + name;
		this.fontColor = fontColor;
		this.color = color;
	}

	public TextFormatting getFontColor() {
		return fontColor;
	}

	public ITextComponent getName() {
		return new StringTextComponent(fontColor.toString()).appendSibling(new TranslationTextComponent(name));
	}

	public Color getColor() {
		return color;
	}

	public boolean isInputMode() {
		return this == Input || this == Input2;
	}

	public boolean isOutputMode() {
		return this == Output || this == Output2 || this == Output3;
	}

	public boolean isDisabledMode() {
		return this == Disabled || this == Never;
	}
}
