package theking530.common.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.IDrawable;
import theking530.common.utilities.Color;

public abstract class AbstractInfoTab extends BaseGuiTab {
	public static final float LINE_HEIGHT = 10.5f;
	public static final float LINE_BREAK_HEIGHT = 8.0f;
	public static final float HEIGHT_PADDING = 9.0f;

	private List<ITextComponent> info;
	private int titleColor;

	public AbstractInfoTab(String title, int width, ResourceLocation tabBackground, IDrawable icon, Color titleColor) {
		super(title, width, 0, tabBackground, icon);
		info = new ArrayList<ITextComponent>();
		this.titleColor = titleColor.encodeInInteger();
	}

	public int addLine(ITextComponent value) {
		info.add(value);
		return info.size() - 1;
	}

	public int addLine(TextFormatting color, ITextComponent value) {
		info.add(new StringTextComponent(color.toString()).appendSibling(value));
		return info.size() - 1;
	}

	public void addLineBreak() {
		info.add(new StringTextComponent("\n"));
	}

	public int addKeyValueLine(ITextComponent key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).appendSibling(key).appendText(": ").appendSibling(value));
		return info.size() - 1;
	}

	public int addKeyValueTwoLiner(ITextComponent key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).appendSibling(key).appendText(": "));
		info.add(new StringTextComponent(" ").appendSibling(value));
		return info.size() - 1;
	}

	public void updateLineByIndex(int index, ITextComponent line) {
		info.set(index, line);
	}

	public void clear() {
		info.clear();
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
			if (info != null && info.size() > 0) {
				drawTextBG();
				drawTitle();
				drawInfo(false);
			}
		}
	}

	@Override
	public void updateData() {
		this.tabHeight = (int) (drawInfo(false) + HEIGHT_PADDING);
	}

	protected void drawTitle() {
		// Draw title.
		fontRenderer.drawStringWithShadow(getTitle(), xPosition + (getTabSide() == TabSide.LEFT ? 11 : 24), yPosition + 8, titleColor);
	}

	protected float drawInfo(boolean simulate) {
		// Scale offsets.
		float lineHeight = 0.0f;
		float height = 0;

		// Iterate through all the info lines.
		for (int i = 0; i < info.size(); i++) {
			// Format the text.
			String formattedText = info.get(i).getFormattedText();

			if (formattedText.equals("\n")) {
				lineHeight += LINE_BREAK_HEIGHT;
				height += LINE_BREAK_HEIGHT;
				continue;
			}

			// Get the word wrapped result.
			List<String> wordWrappedText = fontRenderer.listFormattedStringToWidth(formattedText, this.tabWidth);
			// Render the info text.
			for (String text : wordWrappedText) {
				if (!simulate) {
					fontRenderer.drawStringWithShadow(text, xPosition + 14, (yPosition + 25) + lineHeight, 16777215);
				}
				lineHeight += LINE_HEIGHT;
				height += LINE_HEIGHT;
			}
		}

		return height;
	}

	protected void drawTextBG() {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPosition + tabWidth + 15, yPosition + tabHeight + 17, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPosition + tabWidth + 15, yPosition + 22, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPosition + 10, yPosition + 22, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPosition + 10, yPosition + tabHeight + 17, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
}