package theking530.common.gui.widgets.tabs;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.SpriteDrawable;
import theking530.common.utilities.Color;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class GuiMachinePowerInfoTab extends BaseGuiTab {

	private FontRenderer fontRenderer;

	private List<String> info;
	private String tabTitle;
	private EnergyStorageComponent energyStorage;

	public GuiMachinePowerInfoTab(EnergyStorageComponent storage) {
		super("Power I/O", 80, 72, GuiTextures.PURPLE_TAB, new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16));
		fontRenderer = Minecraft.getInstance().fontRenderer;
		energyStorage = storage;
	}

	protected void setText(String title, String text) {
		info = Arrays.asList(text.split("="));
		int stringLengthMax = 0;
		for (int i = 0; i < info.size(); i++) {
			if (fontRenderer.getStringWidth(info.get(i)) > stringLengthMax) {
				stringLengthMax = fontRenderer.getStringWidth(info.get(i));
			}
		}
		this.tabWidth = stringLengthMax + 8;
		tabTitle = title;
	}

	protected void setPowerInfoText() {
		String text = (TextFormatting.GREEN + "Current Input: =" + energyStorage.getStorage().getRecievedPerTick() + " RF/t=" + TextFormatting.RED + "Current Usage: ="
				+ energyStorage.getStorage().getExtractedPerTick() + " RF/t=" + TextFormatting.AQUA + "Max Recieve:=" + energyStorage.getStorage().getMaxReceive() + " RF/t");
		setText("Power I/O", text);
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
			setPowerInfoText();
			if (info != null && info.size() > 0) {
				drawTextBG();
				drawText();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void drawText() {
		fontRenderer.drawStringWithShadow(tabTitle, xPosition + (getTabSide() == TabSide.LEFT ? 11 : 24), yPosition + 8, new Color(242, 0, 255).encodeInInteger());
		float fontScale = 1.0f;
		int scaleBasedXOffset = 0;
		int scaleBasedYOffset = 0;
		GlStateManager.scalef(fontScale, fontScale, fontScale);
		for (int i = 0; i < info.size(); i++) {
			String string = (String) info.get(i);
			fontRenderer.drawStringWithShadow(string, xPosition + 17 + scaleBasedXOffset, (yPosition + 25) + scaleBasedYOffset + 10.5f * i, 16777215);
		}
		GlStateManager.scalef(1.0f / fontScale, 1.0f / fontScale, 1.0f / fontScale);
	}

	public void drawTextBG() {
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
		vertexbuffer.pos(xPosition + tabWidth + 15, yPosition + height + 22, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPosition + tabWidth + 15, yPosition + 22, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPosition + 10, yPosition + 22, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPosition + 10, yPosition + height + 22, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
}
