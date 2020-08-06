package theking530.common.gui.widgets.tabs.redstonecontrol;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.widgets.button.ItemButton;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.staticpower.tileentities.utilities.RedstoneMode;

public abstract class AbstractGuiRedstoneTab extends BaseGuiTab {

	public ItemButton ignoreRedstoneButton;
	public ItemButton lowRedstoneButton;
	public ItemButton highRedstoneButton;

	public AbstractGuiRedstoneTab(RedstoneMode currentMode) {
		super("Redstone Control", 100, 85, GuiTextures.RED_TAB, Items.REDSTONE);

		widgetContainer.registerWidget(ignoreRedstoneButton = new ItemButton(Items.GUNPOWDER, 23, 30, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Ignore);
			updateToggledButton(ignoreRedstoneButton);
		}));
		widgetContainer.registerWidget(lowRedstoneButton = new ItemButton(Items.REDSTONE, 53, 30, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Low);
			updateToggledButton(lowRedstoneButton);
		}));
		widgetContainer.registerWidget(highRedstoneButton = new ItemButton(Blocks.REDSTONE_TORCH.asItem(), 83, 30, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.High);
			updateToggledButton(highRedstoneButton);
		}));

		highRedstoneButton.setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.high"));
		ignoreRedstoneButton.setClickSoundPitch(0.7f).setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.ignore"));
		lowRedstoneButton.setClickSoundPitch(0.85f).setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.low"));

		// Initialize the correct button.
		if (currentMode == RedstoneMode.Ignore) {
			ignoreRedstoneButton.setToggled(true);
		} else if (currentMode == RedstoneMode.Low) {
			lowRedstoneButton.setToggled(true);
		} else {
			highRedstoneButton.setToggled(true);
		}
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		drawButtonBG(xPosition - 3, yPosition - 32);
		super.renderBackground(mouseX, mouseY, partialTicks);
		drawText(xPosition + 5, yPosition - 35);
	}

	protected void drawText(int xPos, int yPos) {
		String tabName = TextFormatting.YELLOW + "Redstone Config";
		String redstoneMode = "Mode: ";

		modeText(xPos, yPos);
		fontRenderer.drawStringWithShadow(redstoneMode, xPos - this.fontRenderer.getStringWidth(redstoneMode) / 2 + 24, yPos + 95, 16777215);
		fontRenderer.drawStringWithShadow(tabName, xPos - this.fontRenderer.getStringWidth(tabName) / 2 + 58, yPos + 43, 16777215);
	}

	protected void modeText(int tabLeft, int tabTop) {
		if (getCurrentMode() == RedstoneMode.Low) {
			fontRenderer.drawStringWithShadow("Low", tabLeft + 37, tabTop + 95, 16777215);
			fontRenderer.drawStringWithShadow("This machine will", tabLeft + 8, tabTop + 110, 16777215);
			fontRenderer.drawStringWithShadow("only operate with no", tabLeft + 8, tabTop + 118, 16777215);
			fontRenderer.drawStringWithShadow("signal.", tabLeft + 8, tabTop + 127, 16777215);
		} else if (getCurrentMode() == RedstoneMode.High) {
			fontRenderer.drawStringWithShadow("High", tabLeft + 37, tabTop + 95, 16777215);
			fontRenderer.drawStringWithShadow("This machine will", tabLeft + 8, tabTop + 110, 16777215);
			fontRenderer.drawStringWithShadow("only operate with a", tabLeft + 8, tabTop + 118, 16777215);
			fontRenderer.drawStringWithShadow("redstone signal.", tabLeft + 8, tabTop + 127, 16777215);
		} else if (getCurrentMode() == RedstoneMode.Ignore) {
			fontRenderer.drawStringWithShadow("Ignore", tabLeft + 37, tabTop + 95, 16777215);
			fontRenderer.drawStringWithShadow("This machine will", tabLeft + 8, tabTop + 110, 16777215);
			fontRenderer.drawStringWithShadow("ignore any redstone", tabLeft + 8, tabTop + 118, 16777215);
			fontRenderer.drawStringWithShadow("signal.", tabLeft + 8, tabTop + 127, 16777215);
		}
	}

	protected void drawButtonBG(int xPos, int yPos) {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos + 114, yPos + 88, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPos + 114, yPos + 57, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPos + 17, yPos + 57, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPos + 17, yPos + 88, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);

	}

	protected void updateToggledButton(StandardButton selectedButton) {
		ignoreRedstoneButton.setToggled(false);
		lowRedstoneButton.setToggled(false);
		highRedstoneButton.setToggled(false);
		selectedButton.setToggled(true);
	}

	protected abstract RedstoneMode getCurrentMode();

	protected abstract void synchronizeRedstoneMode(RedstoneMode mode);
}
