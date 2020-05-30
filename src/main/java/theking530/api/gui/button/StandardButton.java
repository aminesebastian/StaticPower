package theking530.api.gui.button;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.api.gui.GuiTextures;
import theking530.api.utilities.StaticVertexBuffer;

public class StandardButton extends BaseButton {

	public StandardButton(int width, int height, int xPos, int yPos, Consumer<BaseButton> onClicked) {
		super(width, height, xPos, yPos, onClicked);
	}

	@Override
	protected void drawButton() {
		int buttonLeft = owningGui.getGuiLeft() + xPosition;
		int buttonTop = owningGui.getGuiTop() + yPosition;
		float uPixel = 1.0f / 200.0f;
		float vPixel = 1.0f / 20.0f;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder tes = tessellator.getBuffer();
		tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		if (isClicked() || isHovered() || isToggled()) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_HOVER);
		} else {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON);
		}

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// Top
		StaticVertexBuffer.pos(buttonLeft + width, buttonTop + 2, 0, 0, vPixel * 2);
		StaticVertexBuffer.pos(buttonLeft + width, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop + 2, 0, 1, vPixel * 2);

		// Bottom
		StaticVertexBuffer.pos(buttonLeft + width, buttonTop + (height), 0, 0, vPixel * 20);
		StaticVertexBuffer.pos(buttonLeft + width, buttonTop + (height - 3), 0, 0, vPixel * 17);
		StaticVertexBuffer.pos(buttonLeft, buttonTop + (height - 3), 0, 1, vPixel * 17);
		StaticVertexBuffer.pos(buttonLeft, buttonTop + (height), 0, 1, vPixel * 20);

		// Right
		StaticVertexBuffer.pos(buttonLeft + width, buttonTop + (height), 0, 0, vPixel * 20);
		StaticVertexBuffer.pos(buttonLeft + width, buttonTop, 0, 0, 0);
		StaticVertexBuffer.pos(buttonLeft - 2 + width, buttonTop, 0, uPixel * 2, 0);
		StaticVertexBuffer.pos(buttonLeft - 2 + width, buttonTop + (height), 0, uPixel * 2, vPixel * 20);

		// Left
		StaticVertexBuffer.pos(buttonLeft + 2, buttonTop + (height), 0, uPixel * 198, 1);
		StaticVertexBuffer.pos(buttonLeft + 2, buttonTop, 0, uPixel * 198, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop, 0, 1, 0);
		StaticVertexBuffer.pos(buttonLeft, buttonTop + (height), 0, 1, 1);

		// Body
		StaticVertexBuffer.pos(buttonLeft + width - 2, buttonTop - 3 + (height), 0, uPixel * 2, vPixel * 17);
		StaticVertexBuffer.pos(buttonLeft + width - 2, buttonTop + 2, 0, uPixel * 2, vPixel * 2);
		StaticVertexBuffer.pos(buttonLeft + 2, buttonTop + 2, 0, uPixel * 198, vPixel * 2);
		StaticVertexBuffer.pos(buttonLeft + 2, buttonTop - 3 + (height), 0, uPixel * 198, vPixel * 17);

		tessellator.draw();
	}

}
