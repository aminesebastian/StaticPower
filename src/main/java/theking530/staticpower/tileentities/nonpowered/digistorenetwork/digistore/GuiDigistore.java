package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.gui.widgets.GuiDrawItem;
import theking530.api.gui.widgets.button.StandardButton;
import theking530.api.gui.widgets.button.TextButton;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiDigistore extends StaticPowerTileEntityGui<ContainerDigistore, TileEntityDigistore> {

	private GuiInfoTab infoTab;
	private TextButton lockedButton;
	private GuiDrawItem itemRenderer;

	public GuiDigistore(ContainerDigistore container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 150);
		itemRenderer = new GuiDrawItem(true);
	}

	@Override
	public void initializeGui() {

		registerWidget(lockedButton = new TextButton(10, 23, 50, 20, "Locked", this::buttonPressed));
		lockedButton.setToggleable(true);
		lockedButton.setText(getTileEntity().isLocked() ? "Locked" : "Unlocked");
		lockedButton.setToggled(getTileEntity().isLocked());

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 65));
	}

	public void buttonPressed(StandardButton button) {
		if (getTileEntity() != null) {
			getTileEntity().setLocked(!getTileEntity().isLocked());
			button.setToggled(getTileEntity().isLocked());
			lockedButton.setText(getTileEntity().isLocked() ? "Locked" : "Unlocked");

			NetworkMessage msg = new PacketLockDigistore(getTileEntity().isLocked(), getTileEntity().getPos());
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
		}
	}

	@Override
	protected void drawForegroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(partialTicks, mouseX, mouseY);
		if (!getTileEntity().getStoredItem().isEmpty()) {
			if (mouseX >= guiLeft + 80 && mouseX <= guiLeft + 96 && mouseY >= guiTop + 25 && mouseY <= guiTop + 41) {
				GuiDrawUtilities.drawColoredRectangle(guiLeft + 76, guiTop + 21, 24, 44, 1.0f, new Color(255, 255, 255, 150).fromEightBitToFloat());
				renderTooltip(getTileEntity().getStoredItem(), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		drawGenericBackground();
		drawPlayerInventorySlots(guiLeft + 8, guiTop + ySize - 83);

		drawSlot(guiLeft + 76, guiTop + 21, 24, 24);

		drawSlot(guiLeft + 152, guiTop + 9, 16, 16);
		drawSlot(guiLeft + 152, guiTop + 27, 16, 16);
		drawSlot(guiLeft + 152, guiTop + 45, 16, 16);

		RenderHelper.enableStandardItemLighting();
		itemRenderer.drawItem(getTileEntity().getStoredItem(), guiLeft, guiTop, 80, 25, 1.0f);
		RenderHelper.disableStandardItemLighting();

		// String storedAmountString = "" + getTileEntity().getStoredAmount();
		// drawStringWithSize(storedAmountString, guiLeft + 100, guiTop + 45, 0.6f, new
		// Color(255, 255, 255).fromEightBitToFloat(), true);

		DecimalFormat format = new DecimalFormat("##.###");
		String text = ("Stores a large=amount of a single=item. ==" + TextFormatting.RED + "Max: " + TextFormatting.AQUA + format.format(getTileEntity().getMaxStoredAmount()) + " Items");
		String[] splitMsg = text.split("=");
		infoTab.setText(I18n.format(getTileEntity().getDisplayName().getFormattedText()), Arrays.asList(splitMsg));
	}
}
