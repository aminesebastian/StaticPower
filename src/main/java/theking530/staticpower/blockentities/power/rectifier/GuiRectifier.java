package theking530.staticpower.blockentities.power.rectifier;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiRectifier extends StaticPowerTileEntityGui<ContainerRectifier, BlockEntityRectifier> {

	public GuiRectifier(ContainerRectifier container, Inventory invPlayer, Component name) {
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

	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(stack, partialTicks, mouseX, mouseY);

		// Render the output current.
		font.draw(stack, "Current", 8, 36, 4210752);
		String inputRateString = StaticPowerEnergyTextUtilities.formatCurrentToString(getTileEntity().powerStorage.getMaximumCurrentOutput()).getString();
		font.draw(stack, inputRateString, 28 - (font.width(inputRateString) / 2), 46, 4210752);

		// Render the output voltage.
		font.draw(stack, "Voltage", 131, 36, 4210752);
		String outputRateString = StaticPowerEnergyTextUtilities.formatVoltageToString(getTileEntity().powerStorage.getOutputVoltage()).getString();
		font.draw(stack, outputRateString, 149 - (font.width(outputRateString) / 2), 46, 4210752);

		// Add tooltip for the actual value of the input.
		List<Component> tooltips = new ArrayList<Component>();

		// Render the tooltips.
		this.renderComponentTooltip(stack, tooltips, mouseX, mouseY);
	}

}
