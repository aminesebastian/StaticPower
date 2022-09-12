package theking530.staticcore.utilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ComponentBuilder {
	public String currentOutput;

	public ComponentBuilder() {
		currentOutput = "";
	}

	public ComponentBuilder append(String string) {
		currentOutput += string;
		return this;
	}

	public ComponentBuilder append(String string, ChatFormatting style) {
		currentOutput += style.toString() + new TranslatableComponent(string).getString();
		return this;
	}

	public ComponentBuilder append(String string, Style style) {
		currentOutput += new TranslatableComponent(string).setStyle(style).getString();
		return this;
	}

	public ComponentBuilder append(Component component) {
		currentOutput += ChatFormatting.RESET.toString() + component.getString();
		return this;
	}

	public MutableComponent build() {
		return new TextComponent(currentOutput);
	}
}