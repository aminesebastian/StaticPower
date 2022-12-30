package theking530.staticpower.blockentities.power.transformer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.api.energy.StaticPowerVoltage;
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
	private TextButton voltageUp;
	private TextButton voltageDown;

	public GuiTransformer(ContainerTransformer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		GuiInfoTab infoTab;
		getTabManager().registerTab(infoTab = new GuiInfoTab("Battery", 120));
		infoTab.addLine("desc1", Component.literal("A Battery stores power for later usage."));
		infoTab.addLineBreak();
		infoTab.addLine("desc2", Component.literal("Holding alt and shift while left/right clicking on the buttons will change the rates the limits are altered."));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		registerWidget(voltageUp = new TextButton(104, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(voltageDown = new TextButton(104, 48, 20, 20, "-", this::buttonPressed));
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

		if (mouseX > leftPos + 28 - (font.width(maxPower) / 2) && mouseX < leftPos + 28 + (font.width(maxPower) / 2) && mouseY > this.topPos + 41 && mouseY < this.topPos + 50) {
			tooltips.add(Component.literal(maxPower));
		}

		// Add tooltip for the actual value of the output.
		if (mouseX > leftPos + 149 - (font.width(maxVoltage) / 2) && mouseX < leftPos + 149 + (font.width(maxVoltage) / 2) && mouseY > this.topPos + 41
				&& mouseY < this.topPos + 50) {
			tooltips.add(Component.literal(maxVoltage));
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		StaticPowerVoltage voltage = StaticPowerVoltage.getVoltageClass(getTileEntity().powerStorage.getOutputVoltage());

		if (button == voltageUp) {
			voltage = voltage.upgrade();
		} else if (button == voltageDown) {
			voltage = voltage.downgrade();
		}

		// Create the packet and send a packet to the server with the updated values.
		NetworkMessage msg = new TransformerControlSyncPacket(getTileEntity().getBlockPos(), voltage);
		getTileEntity().setOutputVoltage(voltage);
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}
}
