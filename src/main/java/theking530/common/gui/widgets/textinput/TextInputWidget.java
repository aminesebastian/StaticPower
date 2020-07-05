package theking530.common.gui.widgets.textinput;

import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.utilities.Vector2D;

public class TextInputWidget extends AbstractGuiWidget {
	private final TextFieldWidget textField;
	private BiConsumer<TextInputWidget, String> textChangedConsumer;
	private final FontRenderer fontRenderer;

	public TextInputWidget(String initialString, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		fontRenderer = Minecraft.getInstance().fontRenderer;
		textField = new StaticPowerTextFieldWidget(fontRenderer, (int) xPosition, (int) yPosition, (int) width, (int) height, "");
		textField.setText(initialString);
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

	public TextInputWidget setTypedCallback(BiConsumer<TextInputWidget, String> callback) {
		this.textChangedConsumer = callback;
		return this;
	}

	@Override
	public void tick() {
		textField.tick();
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		Vector2D position = this.getScreenSpacePosition();
		GuiDrawUtilities.drawSlot(position.getX(), position.getY(), getSize().getX(), getSize().getY());

		textField.x = (int) (position.getX() + 2);
		textField.y = (int) (position.getY() + 2);
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
		if (textField.charTyped(character, p_charTyped_2_)) {
			if (textChangedConsumer != null) {
				textChangedConsumer.accept(this, getText());
			}
		}
		return textField.isFocused() ? EInputResult.HANDLED : EInputResult.UNHANDLED;
	}

	@Override
	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		if (textField.keyPressed(key, scanCode, modifiers)) {
			textChangedConsumer.accept(this, getText());
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
