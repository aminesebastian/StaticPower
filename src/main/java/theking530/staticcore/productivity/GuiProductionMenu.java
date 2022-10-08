package theking530.staticcore.productivity;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.containers.HorizontalBox;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.productivity.metrics.MetricEntryWidget;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.PacketGetProductionMetrics;
import theking530.staticcore.productivity.metrics.SerializedMetricPeriod;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class GuiProductionMenu extends StaticPowerDetatchedGui {
	private ScrollBox inputScrollBox;
	private List<MetricEntryWidget> inputMetricWidgets;
	private List<HorizontalBox> inputHorizontalBoxes;

	private ScrollBox outputScrollBox;
	private List<MetricEntryWidget> outputMetricWidgets;
	private List<HorizontalBox> outputHorizontalBoxes;

	public GuiProductionMenu() {
		super(400, 400);
	}

	@Override
	public void initializeGui() {
		inputMetricWidgets = new LinkedList<>();
		outputMetricWidgets = new LinkedList<>();
		inputHorizontalBoxes = new LinkedList<>();
		outputHorizontalBoxes = new LinkedList<>();

		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketGetProductionMetrics(ModProducts.Item.get()));

		this.registerWidget(inputScrollBox = new ScrollBox(10, 10, 200, 200));
		inputScrollBox.setDrawScrollBar(true);
		inputScrollBox.setDrawScrollBarBackground(true);
		this.registerWidget(outputScrollBox = new ScrollBox(10, 10, 200, 200));
		outputScrollBox.setDrawScrollBar(true);
		outputScrollBox.setDrawScrollBarBackground(true);

		for (int i = 0; i < 20; i++) {
			MetricEntryWidget inputWidget = new MetricEntryWidget(null, MetricType.CONSUMPTION, 0, 0, 0, 20);
			inputMetricWidgets.add(inputWidget);

			MetricEntryWidget outputWidget = new MetricEntryWidget(null, MetricType.PRODUCTION, 0, 0, 0, 20);
			outputMetricWidgets.add(outputWidget);
		}

		for (int i = 0; i < 20; i += 2) {
			HorizontalBox inputBox = new HorizontalBox(0, 0, 20, 20);
			inputHorizontalBoxes.add(inputBox);
			inputBox.registerWidget(inputMetricWidgets.get(i));
			inputBox.registerWidget(inputMetricWidgets.get(i + 1));
			inputScrollBox.registerWidget(inputBox);

			HorizontalBox outputBox = new HorizontalBox(0, 0, 20, 20);
			outputHorizontalBoxes.add(outputBox);
			outputBox.registerWidget(outputMetricWidgets.get(i));
			outputBox.registerWidget(outputMetricWidgets.get(i + 1));
			outputScrollBox.registerWidget(outputBox);
		}
		
		// Updated the metric widgets.
		for (HorizontalBox box : inputHorizontalBoxes) {
			box.setVisible(false);
		}
		for (HorizontalBox box : outputHorizontalBoxes) {
			box.setVisible(false);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (Minecraft.getInstance().level.getGameTime() % 20 == 0) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketGetProductionMetrics(ModProducts.Item.get()));
		}

		// Updated the metric widgets.
		for (HorizontalBox box : inputHorizontalBoxes) {
			box.setVisible(false);
		}
		for (HorizontalBox box : outputHorizontalBoxes) {
			box.setVisible(false);
		}

		if (getProductionManager() != null) {
			ProductionManager manager = getProductionManager();
			int maxInputs = Math.min(inputMetricWidgets.size(), manager.tempClientMetrics.getInputs().size());
			int widgetIndex = 0;
			for (int i = 0; i < manager.tempClientMetrics.getInputs().size(); i++) {
				SerializedMetricPeriod metrics = manager.tempClientMetrics.getInputs().get(i);
				if (metrics.getConsumption() > 0) {
					inputHorizontalBoxes.get(i / 2).setVisible(true);
					inputMetricWidgets.get(widgetIndex).setMetric(metrics);
					widgetIndex++;
				}

				if (widgetIndex > maxInputs) {
					break;
				}
			}

			int maxOutputs = Math.min(outputMetricWidgets.size(), manager.tempClientMetrics.getOutputs().size());
			widgetIndex = 0;
			for (int i = 0; i < manager.tempClientMetrics.getOutputs().size(); i++) {
				SerializedMetricPeriod metrics = manager.tempClientMetrics.getOutputs().get(i);
				if (metrics.getProduction() > 0) {
					outputHorizontalBoxes.get(i / 2).setVisible(true);
					outputMetricWidgets.get(widgetIndex).setMetric(metrics);
					widgetIndex++;
				}

				if (widgetIndex > maxOutputs) {
					break;
				}
			}
		}
	}

	@Override
	public void updateBeforeRender() {

	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		Vector2D overallPadding = new Vector2D(getScreenBounds().getWidth() / 4, getScreenBounds().getHeight() / 8);
		Vector2D overallHalfPadding = overallPadding.copy().divide(2);
		Vector2D topLeft = new Vector2D(overallPadding.getX() / 2, overallPadding.getY() / 2);
		Vector2D bgSize = new Vector2D(getScreenBounds().getWidth(), getScreenBounds().getHeight()).subtract(overallPadding);
		GuiDrawUtilities.drawGenericBackground(pose, bgSize.getX(), bgSize.getY(), overallHalfPadding.getX(), overallHalfPadding.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f, 0.75f));

		int topPanelHeight = 20;
		int afterTopPanelMargin = 0;
		{
			GuiDrawUtilities.drawGenericBackground(pose, bgSize.getX(), topPanelHeight, overallHalfPadding.getX(), overallHalfPadding.getY(), 1, new SDColor(0.5f, 0.5f, 0.5f));
			GuiDrawUtilities.drawStringLeftAligned(pose, "Production", overallHalfPadding.getX() + 8, overallHalfPadding.getY() + 13, 1, 1, SDColor.EIGHT_BIT_WHITE, true);
		}

		float graphPanelPadding = 30;
		float graphPanelTopOffset = 2;
		Vector2D gridPanelPos = new Vector2D(graphPanelPadding / 2 + overallHalfPadding.getX(),
				overallHalfPadding.getY() + topPanelHeight + afterTopPanelMargin + graphPanelPadding / 2 + graphPanelTopOffset);
		Vector2D graphPanelSize = new Vector2D(bgSize.getX() / 2 - graphPanelPadding, bgSize.getY() / 4);
		{
			GuiDrawUtilities.drawStringLeftAligned(pose, "Production", topLeft.getX() + 14, topLeft.getY() + topPanelHeight + afterTopPanelMargin + 12, 1, 1,
					SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + graphPanelPadding / 4, graphPanelSize.getY(), gridPanelPos.getX(), gridPanelPos.getY(), 1,
					new SDColor(0.1f, 0.1f, 0.1f));

			GuiDrawUtilities.drawStringLeftAligned(pose, "Consumption", topLeft.getX() + graphPanelSize.getX() + graphPanelPadding + 6,
					topLeft.getY() + topPanelHeight + afterTopPanelMargin + 12, 1, 1, SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + graphPanelPadding / 4, graphPanelSize.getY(),
					gridPanelPos.getX() + (bgSize.getX() / 2) - graphPanelPadding / 4, gridPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
		}

		Vector2D bottomPanelPos = new Vector2D(topLeft.getX() + graphPanelPadding / 2,
				topLeft.getY() + topPanelHeight + afterTopPanelMargin + graphPanelSize.getY() + graphPanelPadding / 2 + graphPanelTopOffset + 4);
		Vector2D bottomPanelSize = new Vector2D(graphPanelSize.getX() + graphPanelPadding / 4,
				bgSize.getY() - graphPanelSize.getY() - topPanelHeight - afterTopPanelMargin - graphPanelPadding - graphPanelTopOffset - 2);
		{
			GuiDrawUtilities.drawSlotWithBorder(pose, bottomPanelSize.getX(), bottomPanelSize.getY(), bottomPanelPos.getX(), bottomPanelPos.getY(), 1,
					new SDColor(0.1f, 0.1f, 0.1f));
			GuiDrawUtilities.drawSlotWithBorder(pose, bottomPanelSize.getX(), bottomPanelSize.getY(), bottomPanelPos.getX() + (bgSize.getX() / 2) - graphPanelPadding / 4,
					bottomPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
		}

		outputScrollBox.setPosition(bottomPanelPos.getX(), bottomPanelPos.getY());
		outputScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

		inputScrollBox.setPosition(bottomPanelPos.getX() + (bgSize.getX() / 2) - graphPanelPadding / 4, bottomPanelPos.getY());
		inputScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

		for (int i = 0; i < 20; i++) {
			outputMetricWidgets.get(i).setWidth((outputScrollBox.getSize().getX() - 10) / 2);
			inputMetricWidgets.get(i).setWidth((inputScrollBox.getSize().getX() - 10) / 2);
		}
	}

	@Override
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		GuiDrawUtilities.drawRectangle(pose, 1f, outputScrollBox.getSize().getY(), outputScrollBox.getPosition().getX() + (outputScrollBox.getSize().getX() / 2) - 5.25f,
				outputScrollBox.getPosition().getY(), 10, SDColor.DARK_GREY);
		GuiDrawUtilities.drawRectangle(pose, 1f, outputScrollBox.getSize().getY(), outputScrollBox.getPosition().getX() + 1 + (outputScrollBox.getSize().getX() / 2) - 5.25f,
				outputScrollBox.getPosition().getY(), 10, SDColor.GREY);

		GuiDrawUtilities.drawRectangle(pose, 1f, inputScrollBox.getSize().getY(), inputScrollBox.getPosition().getX() + (inputScrollBox.getSize().getX() / 2) - 5.25f,
				inputScrollBox.getPosition().getY(), 10, SDColor.DARK_GREY);
		GuiDrawUtilities.drawRectangle(pose, 1f, inputScrollBox.getSize().getY(), inputScrollBox.getPosition().getX() + 1 + (inputScrollBox.getSize().getX() / 2) - 5.25f,
				inputScrollBox.getPosition().getY(), 10, SDColor.GREY);
	}

	protected ProductionManager getProductionManager() {
		return getLocalTeam().getProductionManager();
	}

	protected Team getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
