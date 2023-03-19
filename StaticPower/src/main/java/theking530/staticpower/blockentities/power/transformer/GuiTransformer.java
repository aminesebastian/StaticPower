package theking530.staticpower.blockentities.power.transformer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiTransformer extends StaticCoreBlockEntityScreen<ContainerTransformer, BlockEntityTransformer> {
	private TextButton voltageUp;
	private TextButton voltageDown;

	public GuiTransformer(ContainerTransformer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 145);
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

		registerWidget(voltageDown = new TextButton(66, 37, 16, 16, "-", this::buttonPressed));
		registerWidget(voltageUp = new TextButton(96, 37, 16, 16, "+", this::buttonPressed));
		updateButtons();
	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(stack, partialTicks, mouseX, mouseY);

		String outputRateString = PowerTextFormatting.formatVoltageToString(getTileEntity().powerStorage.getOutputVoltage()).getString();
		GuiDrawUtilities.drawStringCentered(stack, outputRateString, 91, 30, 1, 1.25f, SDColor.EIGHT_BIT_WHITE, true);

		// Add tooltip for the actual value of the input.
		List<Component> tooltips = new ArrayList<Component>();

		// Render the tooltips.
		this.renderComponentTooltip(stack, tooltips, mouseX, mouseY);
	}

	@Override
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {
		String maxVoltage = PowerTextFormatting.formatVoltageToString(getTileEntity().powerStorage.getOutputVoltage()).getString();

		if (mouseX > leftPos + 149 - (font.width(maxVoltage) / 2) && mouseX < leftPos + 149 + (font.width(maxVoltage) / 2) && mouseY > this.topPos + 41
				&& mouseY < this.topPos + 50) {
			tooltips.add(Component.literal(maxVoltage));
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		int currentRatio = getTileEntity().getTransformerRatio();

		if (button == voltageUp) {
			currentRatio = SDMath.clamp(currentRatio + 1, 1, 4);
		} else if (button == voltageDown) {
			currentRatio = SDMath.clamp(currentRatio - 1, 1, 4);
		}

		// Create the packet and send a packet to the server with the updated values.
		NetworkMessage msg = new TransformerRatioPacket(getTileEntity().getBlockPos(), (byte) currentRatio);
		getTileEntity().setTransformerRatio(currentRatio);
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
		updateButtons();
	}

	private void updateButtons() {
		if (getTileEntity().getTier() != StaticPowerTiers.LUMUM) {
			voltageUp.setVisible(false);
			voltageDown.setVisible(false);
			return;
		}

		if (getTileEntity().getTransformerRatio() >= 4) {
			voltageUp.setEnabled(false);
		} else {
			voltageUp.setEnabled(true);
		}

		if (getTileEntity().getTransformerRatio() <= 1) {
			voltageDown.setEnabled(false);
		} else {
			voltageDown.setEnabled(true);
		}
	}
}
