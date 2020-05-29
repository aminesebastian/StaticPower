package theking530.api.gui.widgets.tabs;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Items;
import theking530.api.gui.GuiTextures;
import theking530.api.utilities.Color;

public class GuiInfoTab extends BaseGuiTab {

	private FontRenderer fontRenderer;

	private List<String> info;
	private String tabTitle;

	public GuiInfoTab(int width, int height) {
		super(width, height, GuiTextures.GREEN_TAB, Items.PAPER);
		fontRenderer = Minecraft.getInstance().fontRenderer;
	}

	public void setText(String title, List<String> text) {
		info = text;
		tabTitle = title;
	}

	public void setText(String title, String text) {
		info = Arrays.asList(text.split("="));
		int stringLengthMax = 0;
		for (int i = 0; i < info.size(); i++) {
			if (fontRenderer.getStringWidth(info.get(i)) > stringLengthMax) {
				stringLengthMax = fontRenderer.getStringWidth(info.get(i));
			}
		}
		this.tabWidth = Math.max(fontRenderer.getStringWidth(title), stringLengthMax) + 8;
		tabTitle = title;
	}

	@Override
	protected void drawExtra(int xPos, int yPos, float partialTicks) {
		if (isOpen()) {
			if (info != null && info.size() > 0) {
				drawTextBG(xPos, yPos);
				drawText(xPos, yPos);
			}
		}
	}

	private void drawText(int xPos, int yPos) {
		int titleOffset = getTabSide() == TabSide.LEFT ? 8 : 23;
		fontRenderer.drawStringWithShadow(tabTitle, xPos + titleOffset, yPos + 8, new Color(0, 242, 255).encodeInInteger());
		float fontScale = 1.0f;

		for (int i = 0; i < info.size(); i++) {
			String string = (String) info.get(i);

			int textX = (int) ((xPos + 17 - fontScale) / fontScale) - 1;
			int textY = (int) ((yPos + 34 + (11 * i) - 7 * fontScale) / fontScale) - 1;

			GlStateManager.disableDepthTest();
			GlStateManager.disableBlend();

			fontRenderer.drawStringWithShadow(string, textX, textY, 16777215);

			GlStateManager.enableDepthTest();
		}
	}

	public void drawTextBG(int xPos, int yPos) {
		int height = 0;
		if (info != null) {
			height = info.size() * 11;
		}
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos + tabWidth + 15, yPos + height + 28, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPos + tabWidth + 15, yPos + 22, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPos + 10, yPos + 22, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPos + 10, yPos + height + 28, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	protected void handleExtraMouseInteraction(int mouseX, int mouseY, int button) {

	}

	@Override
	protected void handleExtraKeyboardInteraction(char par1, int par2) {

	}

	@Override
	protected void handleExtraMouseMove(int mouseX, int mouseY) {

	}
}
