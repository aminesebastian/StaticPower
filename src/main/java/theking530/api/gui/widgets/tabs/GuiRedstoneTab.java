package theking530.api.gui.widgets.tabs;

import java.util.Optional;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.api.gui.GuiTextures;
import theking530.api.gui.button.BaseButton;
import theking530.api.gui.button.ItemButton;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.RedstoneModeList.RedstoneMode;

public class GuiRedstoneTab extends BaseGuiTab {

	public final TileEntityBase tileEntity;
	private final FontRenderer fontRenderer;
	public ItemButton ignoreRedstoneButton;
	public ItemButton lowRedstoneButton;
	public ItemButton highRedstoneButton;

	public GuiRedstoneTab(TileEntityBase te) {
		super(100, 85, GuiTextures.RED_TAB, Items.REDSTONE);
		fontRenderer = Minecraft.getInstance().fontRenderer;
		tileEntity = te;

		widgetContainer.registerWidget(ignoreRedstoneButton = new ItemButton(Items.GUNPOWDER, 23, 30, 20, 20, (BaseButton button) -> {
			synchronizeRedstoneMode(RedstoneMode.Ignore);
			updateToggledButton(ignoreRedstoneButton);
		}));
		widgetContainer.registerWidget(lowRedstoneButton = new ItemButton(Items.REDSTONE, 53, 30, 20, 20, (BaseButton button) -> {
			synchronizeRedstoneMode(RedstoneMode.Low);
			updateToggledButton(lowRedstoneButton);
		}));
		widgetContainer.registerWidget(highRedstoneButton = new ItemButton(Blocks.REDSTONE_TORCH.asItem(), 83, 30, 20, 20, (BaseButton button) -> {
			synchronizeRedstoneMode(RedstoneMode.High);
			updateToggledButton(highRedstoneButton);
		}));

		highRedstoneButton.setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.high"));
		ignoreRedstoneButton.setClickSoundPitch(0.7f).setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.ignore"));
		lowRedstoneButton.setClickSoundPitch(0.85f).setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.low"));

		// Get the component if present, and then initialize the correct button.
		Optional<RedstoneControlComponent> redstoneComponent = ComponentUtilities.getComponent(RedstoneControlComponent.class, tileEntity);
		if (redstoneComponent.isPresent()) {
			if (redstoneComponent.get().getRedstoneMode() == RedstoneMode.Ignore) {
				ignoreRedstoneButton.setToggled(true);
			} else if (redstoneComponent.get().getRedstoneMode() == RedstoneMode.Low) {
				lowRedstoneButton.setToggled(true);
			} else {
				highRedstoneButton.setToggled(true);
			}
		}
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		drawButtonBG(xPosition - 3, yPosition - 32);
		super.renderBackground(mouseX, mouseY, partialTicks);
		drawText(xPosition + 5, yPosition - 35);
	}

	public void drawText(int xPos, int yPos) {
		String tabName = TextFormatting.YELLOW + "Redstone Config";
		String redstoneMode = "Mode: ";

		modeText(xPos, yPos);
		fontRenderer.drawStringWithShadow(redstoneMode, xPos - this.fontRenderer.getStringWidth(redstoneMode) / 2 + 24, yPos + 95, 16777215);
		fontRenderer.drawStringWithShadow(tabName, xPos - this.fontRenderer.getStringWidth(tabName) / 2 + 58, yPos + 43, 16777215);
	}

	public void modeText(int tabLeft, int tabTop) {
		Optional<RedstoneControlComponent> redstoneComponent = ComponentUtilities.getComponent(RedstoneControlComponent.class, tileEntity);
		if (redstoneComponent.isPresent()) {
			if (redstoneComponent.get().getRedstoneMode() == RedstoneMode.Low) {
				fontRenderer.drawStringWithShadow("Low", tabLeft + 37, tabTop + 95, 16777215);
				fontRenderer.drawStringWithShadow("This machine will", tabLeft + 8, tabTop + 110, 16777215);
				fontRenderer.drawStringWithShadow("only operate with no", tabLeft + 8, tabTop + 118, 16777215);
				fontRenderer.drawStringWithShadow("signal.", tabLeft + 8, tabTop + 127, 16777215);
			} else if (redstoneComponent.get().getRedstoneMode() == RedstoneMode.High) {
				fontRenderer.drawStringWithShadow("High", tabLeft + 37, tabTop + 95, 16777215);
				fontRenderer.drawStringWithShadow("This machine will", tabLeft + 8, tabTop + 110, 16777215);
				fontRenderer.drawStringWithShadow("only operate with a", tabLeft + 8, tabTop + 118, 16777215);
				fontRenderer.drawStringWithShadow("redstone signal.", tabLeft + 8, tabTop + 127, 16777215);
			} else if (redstoneComponent.get().getRedstoneMode() == RedstoneMode.Ignore) {
				fontRenderer.drawStringWithShadow("Ignore", tabLeft + 37, tabTop + 95, 16777215);
				fontRenderer.drawStringWithShadow("This machine will", tabLeft + 8, tabTop + 110, 16777215);
				fontRenderer.drawStringWithShadow("ignore any redstone", tabLeft + 8, tabTop + 118, 16777215);
				fontRenderer.drawStringWithShadow("signal.", tabLeft + 8, tabTop + 127, 16777215);
			}
		}
	}

	public void drawButtonBG(int xPos, int yPos) {
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

	protected void updateToggledButton(BaseButton selectedButton) {
		ignoreRedstoneButton.setToggled(false);
		lowRedstoneButton.setToggled(false);
		highRedstoneButton.setToggled(false);
		selectedButton.setToggled(true);
	}

	protected void synchronizeRedstoneMode(RedstoneMode mode) {
		// Ensure this tile entity is valid and has the requested component.
		Optional<RedstoneControlComponent> redstoneComponent = ComponentUtilities.getComponent(RedstoneControlComponent.class, tileEntity);
		if (redstoneComponent.isPresent()) {
			// Set the mode.
			redstoneComponent.get().setRedstoneMode(mode);

			// Send a packet to the server with the updated values.
			NetworkMessage msg = new PacketRedstoneTab(mode, tileEntity.getPos());
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
		}
	}
}
