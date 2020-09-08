package theking530.staticcore.gui.widgets.textinput;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

public class TextInputWidget extends AbstractGuiWidget {
	public enum TextAlignment {
		LEFT, CENTER, RIGHT
	}

	private final TextFieldWidget textField;
	private BiConsumer<TextInputWidget, String> textChangedConsumer;
	private Predicate<String> filter;
	private final FontRenderer fontRenderer;
	private TextAlignment alignment;

	public TextInputWidget(String initialString, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		fontRenderer = Minecraft.getInstance().fontRenderer;
		textField = new StaticPowerTextFieldWidget(fontRenderer, (int) xPosition, (int) yPosition, (int) width, (int) height, "");
		textField.setText(initialString);
		alignment = TextAlignment.LEFT;
	}

	/**
	 * Sets the text of the textbox, and moves the cursor to the end.
	 */
	public void setText(String textIn) {
		// Only do something if the text has actually changed.
		if (!textIn.equals(getText())) {
			textField.setText(textIn);
			if (textChangedConsumer != null) {
				textChangedConsumer.accept(this, getText());
			}
		}
	}

	public String getText() {
		return textField.getText();
	}

	public boolean isFocused() {
		return textField.isFocused();
	}

	public void setFocused(boolean focused) {
		textField.setFocused2(focused);
	}

	public TextInputWidget setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public TextInputWidget setTypedCallback(BiConsumer<TextInputWidget, String> callback) {
		this.textChangedConsumer = callback;
		return this;
	}

	public TextInputWidget setTextFilter(Predicate<String> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public void updateData() {
		textField.tick();
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		Vector2D position = this.getScreenSpacePosition();
		GuiDrawUtilities.drawSlot(position.getX(), position.getY(), getSize().getX(), getSize().getY());

		textField.x = (int) (position.getX() + 2);
		textField.y = (int) (position.getY() + 2);

		if (alignment == TextAlignment.CENTER) {
			int currentTextWidth = fontRenderer.getStringWidth(getText());
			textField.x = ((position.getXi() + position.getXi() + getSize().getXi()) / 2) - (currentTextWidth / 2);
		}

		textField.setWidth((int) getSize().getX());
		textField.setHeight((int) getSize().getY());
		textField.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		return textField.mouseClicked(mouseX, mouseY, button) ? EInputResult.HANDLED : EInputResult.UNHANDLED;
	}

	@Override
	public void mouseMove(int mouseX, int mouseY) {
		textField.mouseMoved(mouseX, mouseY);
	}

	@Override
	public EInputResult characterTyped(char character, int p_charTyped_2_) {
		// Capture the previous text.
		String oldText = getText();

		// Raise the char typed.
		if (textField.charTyped(character, p_charTyped_2_)) {
			// Check against the filter. If it fails, set it back to the old text.
			if (filter != null && !filter.test(getText())) {
				textField.setText(oldText);
				return EInputResult.UNHANDLED;
			}

			// Raise the text changed event if we can.
			if (textChangedConsumer != null) {
				textChangedConsumer.accept(this, getText());
			}
		}
		return textField.isFocused() ? EInputResult.HANDLED : EInputResult.UNHANDLED;
	}

	@Override
	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		// Capture the previous text.
		String oldText = getText();

		// Raise the key pressed.
		if (textField.keyPressed(key, scanCode, modifiers)) {
			// Check against the filter. If it fails, set it back to the old text.
			if (filter != null && !filter.test(getText())) {
				textField.setText(oldText);
				return EInputResult.UNHANDLED;
			}

			// Raise the text changed event if we can.
			if (textChangedConsumer != null) {
				textChangedConsumer.accept(this, getText());
			}
		}
		return textField.isFocused() ? EInputResult.HANDLED : EInputResult.UNHANDLED;
	}

	private class StaticPowerTextFieldWidget extends TextFieldWidget {

		public StaticPowerTextFieldWidget(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, String msg) {
			super(fontIn, xIn, yIn, widthIn, heightIn, msg);
			setEnableBackgroundDrawing(false);
		}

		public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
			super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
		}
	}
}
