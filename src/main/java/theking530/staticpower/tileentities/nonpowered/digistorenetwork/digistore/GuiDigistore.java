package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.GuiDrawItem;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.TextButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.IDigistoreInventory;

public class GuiDigistore extends StaticPowerTileEntityGui<ContainerDigistore, TileEntityDigistore> {

	private GuiInfoTab infoTab;
	private TextButton lockedButton;
	private final GuiDrawItem itemRenderer;
	private final IDigistoreInventory inventory;

	public GuiDigistore(ContainerDigistore container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 150);
		itemRenderer = new GuiDrawItem();
		inventory = getTileEntity().inventory;
	}

	@Override
	public void initializeGui() {

		registerWidget(lockedButton = new TextButton(10, 23, 50, 20, "Locked", this::buttonPressed));
		lockedButton.setToggleable(true);
		lockedButton.setText(getTileEntity().isLocked() ? "Locked" : "Unlocked");
		lockedButton.setToggled(getTileEntity().isLocked());

		getTabManager().registerTab(infoTab = new GuiInfoTab(100, 65));
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
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
		if (inventory.getItemCapacity() > 0) {
			if (mouseX >= guiLeft + 76 && mouseX <= guiLeft + 100 && mouseY >= guiTop + 21 && mouseY <= guiTop + 45) {
				GuiDrawUtilities.drawColoredRectangle(guiLeft + 79, guiTop + 19, 18, 18, 1.0f, new Color(200, 200, 200, 200).fromEightBitToFloat());
				renderTooltip(inventory.getDigistoreStack(0).getStoredItem(), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);

		drawSlot(guiLeft + 78, guiTop + 18, 20, 20);

		if (inventory.getItemCapacity() > 0) {
			RenderHelper.enableStandardItemLighting();
			itemRenderer.drawItem(inventory.getDigistoreStack(0).getStoredItem(), guiLeft, guiTop, 80, 21, 1.0f);
			RenderHelper.disableStandardItemLighting();
		}

		DecimalFormat format = new DecimalFormat("##.###");
		String text = ("Stores a large=amount of a single=item. ==" + TextFormatting.RED + "Max: " + TextFormatting.AQUA + format.format(inventory.getItemCapacity()) + " Items");
		String[] splitMsg = text.split("=");
		infoTab.setText(I18n.format(getTileEntity().getDisplayName().getFormattedText()), Arrays.asList(splitMsg));
	}
}
