package theking530.staticpower.tileentities.powered.battery;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.button.TextButton;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.utilities.MetricConverter;

public class GuiBattery extends StaticPowerTileEntityGui<ContainerBattery, TileEntityBattery> {
	private TextButton inputUp;
	private TextButton inputDown;
	private TextButton outputUp;
	private TextButton outputDown;

	public GuiBattery(ContainerBattery container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 75, 20, 26, 51));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		// getTabManager().registerTab(new GuiPowerControlTab(100, 70, teSBattery));
		getTabManager().registerTab(new GuiSideConfigTab(true, getTileEntity()));
		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT));

		registerWidget(inputUp = new TextButton(52, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(inputDown = new TextButton(52, 48, 20, 20, "-", this::buttonPressed));
		registerWidget(outputUp = new TextButton(104, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(outputDown = new TextButton(104, 48, 20, 20, "-", this::buttonPressed));
	}

	@Override
	protected void drawForegroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(partialTicks, mouseX, mouseY);

		// Render the input rate string.
		font.drawString("Input", this.guiLeft + 15, this.guiTop + 32, 4210752);
		MetricConverter input = new MetricConverter(this.getTileEntity().getInputLimit());
		String inputRateString = input.getValueAsString(true) + "FE/t";
		font.drawString(inputRateString, guiLeft + 28 - (font.getStringWidth(inputRateString) / 2), guiTop + 42, 4210752);

		// Render the output rate string.
		font.drawString("Output", this.guiLeft + 132, this.guiTop + 32, 4210752);
		MetricConverter output = new MetricConverter(this.getTileEntity().getOutputLimit());
		String outputRateString = output.getValueAsString(true) + "FE/t";
		font.drawString(outputRateString, guiLeft + 149 - (font.getStringWidth(outputRateString) / 2), guiTop + 42, 4210752);

		// Add tooltip for the actual value of the input.
		List<String> tooltips = new ArrayList<String>();
		if (mouseX > guiLeft + 28 - (font.getStringWidth(inputRateString) / 2) && mouseX < guiLeft + 28 + (font.getStringWidth(inputRateString) / 2) && mouseY > this.guiTop + 41
				&& mouseY < this.guiTop + 50) {
			tooltips.add(getTileEntity().getInputLimit() + " FE/t");
		}

		// Add tooltip for the actual value of the output.
		if (mouseX > guiLeft + 149 - (font.getStringWidth(inputRateString) / 2) && mouseX < guiLeft + 149 + (font.getStringWidth(inputRateString) / 2) && mouseY > this.guiTop + 41
				&& mouseY < this.guiTop + 50) {
			tooltips.add(getTileEntity().getOutputLimit() + " FE/t");
		}

		// Render the tooltips.
		Minecraft.getInstance().currentScreen.renderTooltip(tooltips, mouseX, mouseY, Minecraft.getInstance().fontRenderer);
	}

	@Override
	public void updateData() {

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
