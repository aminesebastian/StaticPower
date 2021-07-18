package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.ListGraphDataSet;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.cables.power.PowerNetworkModule.TransferMetrics;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerCable extends StaticPowerTileEntityGui<ContainerPowerCable, TileEntityPowerCable> {
	protected enum MetricDisplayType {
		SECONDS, MINUTES, HOURS
	}

	private DataGraphWidget graphWidget;
	private MetricDisplayType displayType;
	private TextButton metricTypeButton;

	public GuiPowerCable(ContainerPowerCable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 190, 125);
		displayType = MetricDisplayType.SECONDS;
	}

	@Override
	public void initializeGui() {
		this.registerWidget(new GuiPowerBarFromEnergyStorage(this.getTileEntity().powerCableComponent, 164, 20, 16, 78));
		this.registerWidget(graphWidget = new DataGraphWidget(10, 20, 146, 78));

		registerWidget(metricTypeButton = new TextButton(164, 102, 16, 16, "S", this::buttonPressed));
		metricTypeButton.setTooltip(new StringTextComponent("Seconds"));
	}

	@Override
	public void updateData() {
		// Get the metrics.
		TransferMetrics metrics = getMetrics();

		// Allocate the metrics containers.
		List<Double> receivedData = new ArrayList<Double>();
		List<Double> providedData = new ArrayList<Double>();
		List<Double> netData = new ArrayList<Double>();

		// Avoid extra allocations.
		float recieved;
		float provided;

		// Capture the data.
		for (int i = 0; i < metrics.getProvidedData().size(); i++) {
			// Get the value.
			recieved = metrics.getProvidedData().get(i);
			provided = -metrics.getProvidedData().get(i);

			// Capture the data.
			providedData.add(new Double(CapabilityStaticVolt.convertmSVtoSV((long) recieved)));
			receivedData.add(new Double(CapabilityStaticVolt.convertmSVtoSV((long) provided)));
			netData.add(new Double(CapabilityStaticVolt.convertmSVtoSV((long) (recieved + provided))));
		}

		// Add the data.
		graphWidget.setDataSet("received", new ListGraphDataSet(new Color(0, 1.0f, 0.2f, 0.75f), receivedData));
		graphWidget.setDataSet("provided", new ListGraphDataSet(new Color(1.0f, 0, 0.1f, 0.75f), providedData));
		graphWidget.setDataSet("net", new ListGraphDataSet(new Color(0, 0.1f, 1.0f, 1), netData));
	}

	protected TransferMetrics getMetrics() {
		if (displayType == MetricDisplayType.SECONDS) {
			return getContainer().getSecondsMetrics();
		} else if (displayType == MetricDisplayType.MINUTES) {
			return getContainer().getMinuteMetrics();
		} else if (displayType == MetricDisplayType.HOURS) {
			return getContainer().getHourlyMetrics();
		}
		return null;
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (displayType == MetricDisplayType.SECONDS) {
			displayType = MetricDisplayType.MINUTES;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_minutes"));
			metricTypeButton.setText("M");
		} else if (displayType == MetricDisplayType.MINUTES) {
			displayType = MetricDisplayType.HOURS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_hours"));
			metricTypeButton.setText("H");
		} else if (displayType == MetricDisplayType.HOURS) {
			displayType = MetricDisplayType.SECONDS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_seconds"));
			metricTypeButton.setText("S");
		}
	}

	@Override
	protected void drawForegroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		float textScale = 0.70f;
		float xPos = 10;
		float yPos = 104;
		float inPosition = xPos + 7;
		float outPosition = xPos + 60;
		float netPosition = xPos + 110;

		// Draw the values background.
		GuiDrawUtilities.drawSlot(stack, xPos, yPos, 146, 12, 0);

		// Draw the labels.
		GuiDrawUtilities.drawStringWithSize(stack, "In:", inPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSize(stack, "Out:", outPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSize(stack, "Net:", netPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);

		float recieve = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String recivePerTick = GuiTextUtilities.formatEnergyRateToString(recieve).getString();
		int recieveWidth = (int) (this.font.getStringWidth(recivePerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, recivePerTick, inPosition + 14 + (recieveWidth / 2), yPos + 9, textScale, new Color(0.0f, 255.0f, 50.0f), true);

		float extract = -this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String extractPerTick = GuiTextUtilities.formatEnergyRateToString(extract).getString();
		int extractWidth = (int) (this.font.getStringWidth(extractPerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, extractPerTick, outPosition + 14 + (extractWidth / 2), yPos + 9, textScale, new Color(255.0f, 0.0f, 30.0f), true);

		float net = recieve + extract;
		String netPerTick = GuiTextUtilities.formatEnergyRateToString(net).getString();
		int netWidth = (int) (this.font.getStringWidth(netPerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, netPerTick, netPosition + 14 + (netWidth / 2), yPos + 9, textScale, new Color(0.0f, 100.0f, 255.0f), true);
	}
}
