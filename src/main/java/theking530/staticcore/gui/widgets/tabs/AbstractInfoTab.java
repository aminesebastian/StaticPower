package theking530.staticcore.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
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
		info.add(new StringTextComponent(color.toString()).append(value));
		return info.size() - 1;
	}

	public void addLineBreak() {
		info.add(new StringTextComponent("\n"));
	}

	public int addKeyValueLine(ITextComponent key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).append(key).appendString(": ").append(value));
		return info.size() - 1;
	}

	public int addKeyValueTwoLiner(ITextComponent key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).append(key).appendString(": "));
		info.add(new StringTextComponent(" ").append(value));
		return info.size() - 1;
	}

	public void updateLineByIndex(int index, ITextComponent line) {
		info.set(index, line);
	}

	public void clear() {
		info.clear();
	}

	@Override
	public void renderBackground(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
			if (info != null) {
				drawTextBG(matrix);
				drawTitle(matrix);
				drawInfo(matrix, false);
			}
		}
	}

	@Override
	public void updateData() {
		this.tabHeight = (int) (drawInfo(null, true) + HEIGHT_PADDING);
	}

	protected void drawTitle(MatrixStack stack) {
		// Draw title.
		fontRenderer.drawStringWithShadow(stack, getTitle(), xPosition + (getTabSide() == TabSide.LEFT ? 11 : 24),
				yPosition + 8, titleColor);
	}

	protected float drawInfo(@Nullable MatrixStack stack, boolean simulate) {
		// Scale offsets.
		float lineHeight = 0.0f;
		float height = 0;

		// Iterate through all the info lines.
		for (int i = 0; i < info.size(); i++) {
			// Format the text.
			ITextProperties formattedText = info.get(i);

			if (formattedText.equals("\n")) {
				lineHeight += LINE_BREAK_HEIGHT;
				height += LINE_BREAK_HEIGHT;
				continue;
			}

			// Get the word wrapped result.
			List<IReorderingProcessor> wordWrappedText = fontRenderer.trimStringToWidth(formattedText, this.tabWidth);
			// Render the info text.
			for (IReorderingProcessor text : wordWrappedText) {
				if (!simulate) {
					fontRenderer.func_238422_b_(stack, text, xPosition + 14, (yPosition + 25) + lineHeight, 16777215);
				}
				lineHeight += LINE_HEIGHT;
				height += LINE_HEIGHT;
			}
		}

		return height;
	}

	protected void drawTextBG(MatrixStack stack) {
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