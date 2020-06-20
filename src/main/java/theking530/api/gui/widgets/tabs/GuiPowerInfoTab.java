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
import net.minecraft.util.text.TextFormatting;
import theking530.api.gui.GuiTextures;
import theking530.api.utilities.Color;
import theking530.staticpower.initialization.ModUpgrades;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class GuiPowerInfoTab extends BaseGuiTab {

	private FontRenderer fontRenderer;

	private List<String> info;
	private String tabTitle;
	private EnergyStorageComponent energyStorage;

	public GuiPowerInfoTab(EnergyStorageComponent storage) {
		super(80, 60, GuiTextures.PURPLE_TAB, ModUpgrades.BasicPowerUpgrade);
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
		int energyPerTick = energyStorage.getEnergyIO();
		String text = (TextFormatting.GREEN + "Current I/O: =" + energyPerTick + " RF/t=" + TextFormatting.AQUA + "Max Recieve:=" + energyStorage.getStorage().getMaxReceive() + " RF/t");
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
		fontRenderer.drawStringWithShadow(tabTitle, xPosition + 11, yPosition + 8, new Color(242, 0, 255).encodeInInteger());
		float fontScale = 1.0f;
		int scaleBasedXOffset = 0;
		int scaleBasedYOffset = 0;
		GlStateManager.scalef(fontScale, fontScale, fontScale);
		for (int i = 0; i < info.size(); i++) {
			String string = (String) info.get(i);
			fontRenderer.drawStringWithShadow(string, xPosition + 17 + scaleBasedXOffset, (yPosition + 27) + scaleBasedYOffset + 11 * i, 16777215);
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
		vertexbuffer.pos(xPosition + tabWidth + 15, yPosition + height + 28, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPosition + tabWidth + 15, yPosition + 22, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPosition + 10, yPosition + 22, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPosition + 10, yPosition + height + 28, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
}
