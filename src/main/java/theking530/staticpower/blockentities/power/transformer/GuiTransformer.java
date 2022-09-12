package theking530.staticpower.blockentities.power.transformer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiTransformer extends StaticPowerTileEntityGui<ContainerTransformer, BlockEntityTransformer> {
	private TextButton inputUp;
	private TextButton inputDown;
	private TextButton outputUp;
	private TextButton outputDown;

	public GuiTransformer(ContainerTransformer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		GuiInfoTab infoTab;
		getTabManager().registerTab(infoTab = new GuiInfoTab("Battery", 120));
		infoTab.addLine("desc1", new TextComponent("A Battery stores power for later usage."));
		infoTab.addLineBreak();
		infoTab.addLine("desc2", new TextComponent("Holding alt and shift while left/right clicking on the buttons will change the rates the limits are altered."));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		registerWidget(inputUp = new TextButton(52, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(inputDown = new TextButton(52, 48, 20, 20, "-", this::buttonPressed));
		registerWidget(outputUp = new TextButton(104, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(outputDown = new TextButton(104, 48, 20, 20, "-", this::buttonPressed));
	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(stack, partialTicks, mouseX, mouseY);

		// Render the output current.
		font.draw(stack, "Current", 8, 36, 4210752);
		String inputRateString = PowerTextFormatting.formatPowerRateToString(getTileEntity().powerStorage.getMaximumPowerOutput()).getString();
		font.draw(stack, inputRateString, 28 - (font.width(inputRateString) / 2), 46, 4210752);

		// Render the output voltage.
		font.draw(stack, "Voltage", 131, 36, 4210752);
		String outputRateString = PowerTextFormatting.formatVoltageToString(getTileEntity().powerStorage.getOutputVoltage()).getString();
		font.draw(stack, outputRateString, 149 - (font.width(outputRateString) / 2), 46, 4210752);

		// Add tooltip for the actual value of the input.
		List<Component> tooltips = new ArrayList<Component>();

		// Render the tooltips.
		this.renderComponentTooltip(stack, tooltips, mouseX, mouseY);
	}

	@Override
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {
		String maxPower = PowerTextFormatting.formatPowerRateToString(getTileEntity().powerStorage.getMaximumPowerOutput()).getString();
		String maxVoltage = PowerTextFormatting.formatVoltageToString(getTileEntity().powerStorage.getOutputVoltage()).getString();

		if (mouseX > leftPos + 28 - (font.width(maxPower) / 2) && mouseX < leftPos + 28 + (font.width(maxPower) / 2) && mouseY > this.topPos + 41
				&& mouseY < this.topPos + 50) {
			tooltips.add(new TextComponent(maxPower));
		}

		// Add tooltip for the actual value of the output.
		if (mouseX > leftPos + 149 - (font.width(maxVoltage) / 2) && mouseX < leftPos + 149 + (font.width(maxVoltage) / 2) && mouseY > this.topPos + 41
				&& mouseY < this.topPos + 50) {
			tooltips.add(new TextComponent(maxVoltage));
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		double deltaValue = 0;
		boolean voltage = false;
		if (button == inputUp) {
			deltaValue = 1;
		} else if (button == inputDown) {
			deltaValue = -1;
		} else if (button == outputUp) {
			deltaValue = 1;
			voltage = true;
		} else if (button == outputDown) {
			deltaValue = -1;
			voltage = true;
		}

		deltaValue *= mouseButton == MouseButton.LEFT ? 1 : 10;
		if (Screen.hasShiftDown()) {
			deltaValue = (deltaValue * 10);
		} else if (Screen.hasAltDown()) {
			deltaValue = (deltaValue / 2);
		}

		// Create the packet and send a packet to the server with the updated values.
		NetworkMessage msg;
		if (voltage) {
			msg = new TransformerControlSyncPacket(getTileEntity().getBlockPos(), voltage, deltaValue);
			getTileEntity().addOutputVoltageDelta(deltaValue);
		} else {
			msg = new TransformerControlSyncPacket(getTileEntity().getBlockPos(), voltage, deltaValue);
			getTileEntity().addMaximumOutputCurrentDelta(deltaValue);
		}
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}
}
