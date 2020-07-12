package theking530.staticpower.tileentities.powered.battery;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.button.TextButton;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiBattery extends StaticPowerTileEntityGui<ContainerBattery, TileEntityBattery> {
	private TextButton inputUp;
	private TextButton inputDown;
	private TextButton outputUp;
	private TextButton outputDown;
	private GuiInfoTab infoTab;

	public GuiBattery(ContainerBattery container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 80, 20, 16, 51));
		getTabManager().registerTab(infoTab = new GuiInfoTab(80, 60));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		// getTabManager().registerTab(new GuiPowerControlTab(100, 70, teSBattery));
		getTabManager().registerTab(new GuiSideConfigTab(true, getTileEntity()));
		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT));


		registerWidget(inputUp = new TextButton(45, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(inputDown = new TextButton(45, 48, 20, 20, "-", this::buttonPressed));
		registerWidget(outputUp = new TextButton(111, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(outputDown = new TextButton(111, 48, 20, 20, "-", this::buttonPressed));
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		int deltaValue = 0;
		boolean input = false;
		if (button == inputUp) {
			deltaValue = 1;
			input = true;
		} else if (button == inputDown) {
			deltaValue = -1;
			input = true;
		} else if (button == outputUp) {
			deltaValue = 1;
		} else if (button == outputDown) {
			deltaValue = -1;
		}
		deltaValue *= mouseButton == MouseButton.LEFT ? 1 : 10;
		if (Screen.hasShiftDown()) {
			deltaValue = (deltaValue * 100);
		} else if (!Screen.hasControlDown()) {
			deltaValue = (deltaValue * 50);
		}

		if (input) {
			getTileEntity().setInputLimit(Math.max(0, Math.min(getTileEntity().getInputLimit() + deltaValue, getTileEntity().getMaximumPowerIO())));
		} else {
			getTileEntity().setOutputLimit(Math.max(0, Math.min(getTileEntity().getOutputLimit() + deltaValue, getTileEntity().getMaximumPowerIO())));
		}

		// Create the packet.
		NetworkMessage msg = new BatteryControlSyncPacket(getTileEntity().getInputLimit(), getTileEntity().getOutputLimit(), getTileEntity().getPos());
		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}
}