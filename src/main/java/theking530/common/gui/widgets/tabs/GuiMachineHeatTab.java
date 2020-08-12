package theking530.common.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.ItemDrawable;
import theking530.common.utilities.Color;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

public class GuiMachineHeatTab extends BaseGuiTab {
	private FontRenderer fontRenderer;
	private List<ITextComponent> info;
	private HeatStorageComponent heatStorage;

	public GuiMachineHeatTab(HeatStorageComponent storage) {
		super("Heat I/O", 80, 72, GuiTextures.ORANGE_TAB, new ItemDrawable(Items.CAMPFIRE));
		fontRenderer = Minecraft.getInstance().fontRenderer;
		heatStorage = storage;
		info = new ArrayList<ITextComponent>();
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
			updateHeatText();
			if (info != null && info.size() > 0) {
				drawTextBG();
				drawText();
			}
		}
	}

	protected void updateHeatText() {
		info.clear();
		addInfoLine("Generating", GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getHeatPerTick()), TextFormatting.RED);
		addInfoLine("Dissipating", GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getCooledPerTick()), TextFormatting.AQUA);
		addInfoLine("Max Cooling", GuiTextUtilities.formatHeatRateToString(heatStorage.getStorage().getMaximumHeatTransferRate()), TextFormatting.GREEN);
	}

	private void drawText() {
		fontRenderer.drawStringWithShadow(getTitle(), xPosition + (getTabSide() == TabSide.LEFT ? 11 : 24), yPosition + 8, new Color(100, 255, 255).encodeInInteger());
		int scaleBasedXOffset = 0;
		int scaleBasedYOffset = 0;
		for (int i = 0; i < info.size(); i++) {
			String string = info.get(i).getFormattedText();
			fontRenderer.drawStringWithShadow(string, xPosition + 17 + scaleBasedXOffset, (yPosition + 25) + scaleBasedYOffset + 10.5f * i, 16777215);
		}
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

	protected void addInfoLine(String key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).appendSibling(new StringTextComponent(key).appendText(": ")));
		info.add(value);
	}

}
