package theking530.staticcore.gui.widgets.textinput;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class TextInputWidget extends AbstractGuiWidget<TextInputWidget> {
	public enum TextAlignment {
		LEFT, CENTER, RIGHT
	}

	private final EditBox textField;
	private BiConsumer<TextInputWidget, String> textChangedConsumer;
	private Predicate<String> filter;
	private TextAlignment alignment;

	public TextInputWidget(String initialString, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		textField = new StaticPowerTextFieldWidget(getFontRenderer(), (int) xPosition, (int) yPosition, (int) width, (int) height, new TextComponent(""));
		textField.setValue(initialString);
		alignment = TextAlignment.LEFT;
	}

	/**
	 * Sets the text of the textbox, and moves the cursor to the end.
	 */
	public void setText(String textIn) {
		// Only do something if the text has actually changed.
		if (!textIn.equals(getText())) {
			textField.setValue(textIn);
			if (textChangedConsumer != null) {
				textChangedConsumer.accept(this, getText());
			}
		}
	}

	public String getText() {
		return textField.getValue();
	}

	public boolean isFocused() {
		return textField.isFocused();
	}

	public void setFocused(boolean focused) {
		textField.setFocus(focused);
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
	public void tick() {
		textField.tick();
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		GuiDrawUtilities.drawSlot(matrix, getSize().getX(), getSize().getY(), 0, 0, 0);
		textField.x = getPosition().getXi() + 2;
		textField.y = getPosition().getYi() + 2;

		if (alignment == TextAlignment.CENTER) {
			int currentTextWidth = getFontRenderer().width(getText());
			textField.x = ((getPosition().getXi() + getPosition().getXi() + getSize().getXi()) / 2) - (currentTextWidth / 2);
		}

		textField.setWidth((int) getSize().getX());
		textField.setHeight((int) getSize().getY());

		// We have to translate backwards to render in owner space for this vanilla
		// minecraft element.
		matrix.pushPose();
		matrix.translate(-getPosition().getX(), -getPosition().getY(), 0);
		textField.render(matrix, mouseX, mouseX, partialTicks);
		matrix.popPose();
	}

	@Override
	public EInputResult mouseClick(double mouseX, double mouseY, int button) {
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(getLastRenderMatrix(), getPosition());
		Vector2D adjustedMousePosition = new Vector2D((float) mouseX - screenSpacePosition.getX() + getPosition().getXi(), (float) mouseY - screenSpacePosition.getY() + getPosition().getYi());
		return textField.mouseClicked(adjustedMousePosition.getX(), adjustedMousePosition.getY(), button) ? EInputResult.HANDLED : EInputResult.UNHANDLED;
	}

	@Override
	public EInputResult mouseMove(double mouseX, double mouseY) {
		textField.mouseMoved(mouseX, mouseY);
		return EInputResult.UNHANDLED;
	}

	@Override
	public EInputResult characterTyped(char character, int p_charTyped_2_) {
		// Capture the previous text.
		String oldText = getText();

		// Raise the char typed.
		if (textField.charTyped(character, p_charTyped_2_)) {
			// Check against the filter. If it fails, set it back to the old text.
			if (filter != null && !filter.test(getText())) {
				textField.setValue(oldText);
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
				textField.setValue(oldText);
				return EInputResult.UNHANDLED;
			}

			// Raise the text changed event if we can.
			if (textChangedConsumer != null) {
				textChangedConsumer.accept(this, getText());
			}
		}
		return textField.isFocused() ? EInputResult.HANDLED : EInputResult.UNHANDLED;
	}

	private class StaticPowerTextFieldWidget extends EditBox {

		public StaticPowerTextFieldWidget(Font fontIn, int xIn, int yIn, int widthIn, int heightIn, Component msg) {
			super(fontIn, xIn, yIn, widthIn, heightIn, msg);
			setBordered(false);
		}

		public void renderButton(PoseStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
			super.renderButton(stack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
		}
	}
}
