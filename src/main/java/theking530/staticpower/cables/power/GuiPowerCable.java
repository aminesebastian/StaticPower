package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.ListGraphDataSet;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.cables.power.PowerNetworkModule.TransferMetrics;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerCable extends StaticPowerTileEntityGui<ContainerPowerCable, TileEntityPowerCable> {
	protected enum MetricDisplayType {
		SECONDS, MINUTES, HOURS
	}

	private DataGraphWidget graphWidget;
	private MetricDisplayType displayType;
	private SpriteButton metricTypeButton;

	public GuiPowerCable(ContainerPowerCable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 120);
		displayType = MetricDisplayType.SECONDS;
	}

	@Override
	public void initializeGui() {
		this.registerWidget(new GuiPowerBarFromEnergyStorage(this.getTileEntity().powerCableComponent, 150, 17, 16, 78));
		this.registerWidget(graphWidget = new DataGraphWidget(10, 17, 130, 78));

		registerWidget(metricTypeButton = new SpriteButton(152, 101, 12, 12, StaticPowerSprites.RANGE_ICON, null, this::buttonPressed));
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
			providedData.add(new Double(recieved));
			receivedData.add(new Double(provided));
			netData.add(new Double(recieved + provided));
		}

		// Add the data.
		graphWidget.setDataSet("received", new ListGraphDataSet(new Color(0, 1.0f, 0.2f, 0.75f), receivedData));
		graphWidget.setDataSet("provided", new ListGraphDataSet(new Color(1.0f, 0, 0.1f, 0.75f), providedData));
		graphWidget.setDataSet("net", new ListGraphDataSet(new Color(0, 0.1f, 1.0f, 1), netData));

		// Generate the labels.
		List<String> labels = new ArrayList<String>();
		for (int i = 0; i < receivedData.size(); i++) {
			labels.add(SDTime.ticksToTimeString(Minecraft.getInstance().world.getGameTime()));
		}
		graphWidget.setXAxisLabels(labels);
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
		} else if (displayType == MetricDisplayType.MINUTES) {
			displayType = MetricDisplayType.HOURS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_hours"));
		} else if (displayType == MetricDisplayType.HOURS) {
			displayType = MetricDisplayType.SECONDS;
			metricTypeButton.setTooltip(new TranslationTextComponent("gui.staticpower.metric_seconds"));
		}
	}

	@Override
	protected void drawForegroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		float textScale = 0.70f;
		float inPosition = 17;
		float outPosition = 65;
		float netPosition = 110;
		float yPos = 101;

		// Draw the values background.
		GuiDrawUtilities.drawSlot(stack, 10, yPos, 130, 12);

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
