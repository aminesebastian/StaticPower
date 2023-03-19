package theking530.staticpower.blockentities.power.powermonitor;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;

public class GuiPowerMonitor extends StaticCoreBlockEntityScreen<ContainerPowerMonitor, BlockEntityPowerMonitor> {
	@SuppressWarnings("unused")
	private TextButton inputUp;
	@SuppressWarnings("unused")
	private TextButton inputDown;
	@SuppressWarnings("unused")
	private TextButton outputUp;
	@SuppressWarnings("unused")
	private TextButton outputDown;

	public GuiPowerMonitor(ContainerPowerMonitor container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 75, 20, 26, 51));

		GuiInfoTab infoTab;
		getTabManager().registerTab(infoTab = new GuiInfoTab("Battery", 120));
		infoTab.addLine("desc1", Component.literal("A Battery stores power for later usage."));
		infoTab.addLineBreak();
		infoTab.addLine("desc2", Component.literal("Holding alt and shift while left/right clicking on the buttons will change the rates the limits are altered."));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);

		registerWidget(inputUp = new TextButton(52, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(inputDown = new TextButton(52, 48, 20, 20, "-", this::buttonPressed));
		registerWidget(outputUp = new TextButton(104, 23, 20, 20, "+", this::buttonPressed));
		registerWidget(outputDown = new TextButton(104, 48, 20, 20, "-", this::buttonPressed));
	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(stack, partialTicks, mouseX, mouseY);

		// Render the input rate string.
		font.draw(stack, "Input", 15, 32, 4210752);
		String inputRateString = PowerTextFormatting.formatPowerRateToString(getTileEntity().getInputLimit()).getString();
		font.draw(stack, inputRateString, 28 - (font.width(inputRateString) / 2), 42, 4210752);

		// Render the output rate string.
		font.draw(stack, "Output", 132, 32, 4210752);
		String outputRateString = PowerTextFormatting.formatPowerRateToString(getTileEntity().getOutputLimit()).getString();
		font.draw(stack, outputRateString, 149 - (font.width(outputRateString) / 2), 42, 4210752);

		// Add tooltip for the actual value of the input.
		List<Component> tooltips = new ArrayList<Component>();

		// Render the tooltips.
		this.renderComponentTooltip(stack, tooltips, mouseX, mouseY);
	}

	@Override
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {
		String inputRateString = PowerTextFormatting.formatPowerRateToString(getTileEntity().getInputLimit()).getString();
		String outputRateString = PowerTextFormatting.formatPowerRateToString(getTileEntity().getOutputLimit()).getString();

		if (mouseX > leftPos + 28 - (font.width(inputRateString) / 2) && mouseX < leftPos + 28 + (font.width(inputRateString) / 2) && mouseY > this.topPos + 41
				&& mouseY < this.topPos + 50) {
			tooltips.add(Component.literal(inputRateString));
		}

		// Add tooltip for the actual value of the output.
		if (mouseX > leftPos + 149 - (font.width(outputRateString) / 2) && mouseX < leftPos + 149 + (font.width(outputRateString) / 2) && mouseY > this.topPos + 41
				&& mouseY < this.topPos + 50) {
			tooltips.add(Component.literal(outputRateString));
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {

	}
}
