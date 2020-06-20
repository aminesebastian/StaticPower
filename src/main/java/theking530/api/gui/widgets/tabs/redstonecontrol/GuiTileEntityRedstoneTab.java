package theking530.api.gui.widgets.tabs.redstonecontrol;

import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.RedstoneMode;

public class GuiTileEntityRedstoneTab extends AbstractGuiRedstoneTab {
	private RedstoneControlComponent tileEntityRedstoneComponent;

	public GuiTileEntityRedstoneTab(RedstoneControlComponent tileEntityRedstoneComponent) {
		super(tileEntityRedstoneComponent.getRedstoneMode());
		this.tileEntityRedstoneComponent = tileEntityRedstoneComponent;
	}

	@Override
	protected void synchronizeRedstoneMode(RedstoneMode mode) {
		// Set the client's redstone mode.
		tileEntityRedstoneComponent.setRedstoneMode(mode);
		// Create the packet.
		NetworkMessage msg = new PacketRedstoneComponentSync(mode, tileEntityRedstoneComponent.getPos(), tileEntityRedstoneComponent.getComponentName());
		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	@Override
	protected RedstoneMode getCurrentMode() {
		return tileEntityRedstoneComponent.getRedstoneMode();
	}
}
