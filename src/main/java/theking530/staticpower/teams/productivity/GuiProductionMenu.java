package theking530.staticpower.teams.productivity;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.containers.HorizontalBox;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.productivity.ProductionManager;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.PacketRequestProductionMetrics;
import theking530.staticcore.productivity.metrics.SerializedMetricPeriod;
import theking530.staticcore.productivity.metrics.SertializedBiDirectionalMetrics;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class GuiProductionMenu extends StaticPowerDetatchedGui {
	private static final int TOP_PANEL_HEIGHT = 20;
	private static final int POST_TOP_PANEL_MARGIN = 20;
	private static final float GRAPH_PANEL_PADDING = 30;
	private static final float GRAPH_PANEL_OFFSET = 2;

	private ScrollBox inputScrollBox;
	private List<MetricEntryWidget> inputMetricWidgets;
	private List<HorizontalBox> inputHorizontalBoxes;

	private ScrollBox outputScrollBox;
	private List<MetricEntryWidget> outputMetricWidgets;
	private List<HorizontalBox> outputHorizontalBoxes;

	private Vector2D overallPadding;
	private Vector2D overallTopLeft;
	private Vector2D overallSize;

	private Vector2D graphPanelPos;
	private Vector2D graphPanelSize;

	private Vector2D bottomPanelPos;
	private Vector2D bottomPanelSize;

	public GuiProductionMenu() {
		super(400, 400);
	}

	@Override
	public void initializeGui() {
		inputMetricWidgets = new LinkedList<>();
		outputMetricWidgets = new LinkedList<>();
		inputHorizontalBoxes = new LinkedList<>();
		outputHorizontalBoxes = new LinkedList<>();

		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketRequestProductionMetrics(ModProducts.Item.get()));

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

		recalculateSizes();
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		super.tick();
		if (Minecraft.getInstance().level.getGameTime() % 20 == 0) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketRequestProductionMetrics(ModProducts.Item.get()));
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
			SertializedBiDirectionalMetrics metrics = manager.getClientSyncedMetrics(ModProducts.Item.get());

			int maxInputs = Math.min(inputMetricWidgets.size(), metrics.getInputs().size());
			int widgetIndex = 0;
			for (int i = 0; i < metrics.getInputs().size(); i++) {
				SerializedMetricPeriod periodMetrics = metrics.getInputs().get(i);
				if (periodMetrics.getConsumption() > 0) {
					inputHorizontalBoxes.get(i / 2).setVisible(true);
					inputMetricWidgets.get(widgetIndex).setMetric(periodMetrics);
					widgetIndex++;
				}

				if (widgetIndex > maxInputs) {
					break;
				}
			}

			int maxOutputs = Math.min(outputMetricWidgets.size(), metrics.getOutputs().size());
			widgetIndex = 0;
			for (int i = 0; i < metrics.getOutputs().size(); i++) {
				SerializedMetricPeriod periodMetrics = metrics.getOutputs().get(i);
				if (periodMetrics.getProduction() > 0) {
					outputHorizontalBoxes.get(i / 2).setVisible(true);
					outputMetricWidgets.get(widgetIndex).setMetric(periodMetrics);
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
		recalculateSizes();
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		Vector2D overallHalfPadding = overallPadding.copy().divide(2);
		GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), overallSize.getY(), overallHalfPadding.getX(), overallHalfPadding.getY(), 1,
				new SDColor(0.1f, 0.1f, 0.1f, 0.75f));

		{
			GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), TOP_PANEL_HEIGHT, overallHalfPadding.getX(), overallHalfPadding.getY() + TOP_PANEL_HEIGHT - 4, 1,
					new SDColor(0.35f, 0.35f, 0.35f));

			float factor = overallSize.getX() / 4;
			for (int i = 0; i < 4; i++) {
				GuiDrawUtilities.drawRectangle(pose, 1, 13, overallHalfPadding.getX() + 3 + (i * factor), overallHalfPadding.getY() + TOP_PANEL_HEIGHT, 1, SDColor.DARK_GREY);
				GuiDrawUtilities.drawStringLeftAligned(pose, "Items", overallHalfPadding.getX() + 50 + (i * factor), overallHalfPadding.getY() + TOP_PANEL_HEIGHT + 9, 1, 0.75f,
						SDColor.EIGHT_BIT_WHITE, true);
				GuiDrawUtilities.drawRectangle(pose, 1, 13, overallHalfPadding.getX() - 3 + ((i + 1) * factor), overallHalfPadding.getY() + TOP_PANEL_HEIGHT, 1, SDColor.DARK_GREY);
			}
		}

		{
			GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), TOP_PANEL_HEIGHT, overallHalfPadding.getX(), overallHalfPadding.getY(), 1,
					new SDColor(0.5f, 0.5f, 0.5f));
			GuiDrawUtilities.drawStringLeftAligned(pose, "Production", overallHalfPadding.getX() + 8, overallHalfPadding.getY() + 13, 1, 1, SDColor.EIGHT_BIT_WHITE, true);
		}

		{
			GuiDrawUtilities.drawStringLeftAligned(pose, "Production", overallTopLeft.getX() + 14, graphPanelPos.getY() - 5, 1, 1, SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY(), graphPanelPos.getX(), graphPanelPos.getY(), 1,
					new SDColor(0.1f, 0.1f, 0.1f));

			GuiDrawUtilities.drawStringLeftAligned(pose, "Consumption", graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, graphPanelPos.getY() - 5, 1, 1,
					SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY(),
					graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, graphPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
		}

		{
			GuiDrawUtilities.drawSlotWithBorder(pose, bottomPanelSize.getX(), bottomPanelSize.getY(), bottomPanelPos.getX(), bottomPanelPos.getY(), 1,
					new SDColor(0.1f, 0.1f, 0.1f));
			GuiDrawUtilities.drawSlotWithBorder(pose, bottomPanelSize.getX(), bottomPanelSize.getY(), bottomPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4,
					bottomPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
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

	protected void recalculateSizes() {
		overallPadding = new Vector2D(getScreenBounds().getWidth() / 4, getScreenBounds().getHeight() / 8);
		Vector2D overallHalfPadding = overallPadding.copy().divide(2);
		overallTopLeft = new Vector2D(overallPadding.getX() / 2, overallPadding.getY() / 2);
		overallSize = new Vector2D(getScreenBounds().getWidth(), getScreenBounds().getHeight()).subtract(overallPadding);

		graphPanelPos = new Vector2D(GRAPH_PANEL_PADDING / 2 + overallHalfPadding.getX(),
				overallHalfPadding.getY() + TOP_PANEL_HEIGHT + POST_TOP_PANEL_MARGIN + GRAPH_PANEL_PADDING / 2 + GRAPH_PANEL_OFFSET);
		graphPanelSize = new Vector2D(overallSize.getX() / 2 - GRAPH_PANEL_PADDING, overallSize.getY() / 4);

		bottomPanelPos = new Vector2D(overallTopLeft.getX() + GRAPH_PANEL_PADDING / 2,
				overallTopLeft.getY() + TOP_PANEL_HEIGHT + POST_TOP_PANEL_MARGIN + graphPanelSize.getY() + GRAPH_PANEL_PADDING / 2 + GRAPH_PANEL_OFFSET + 4);
		bottomPanelSize = new Vector2D(graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4,
				overallSize.getY() - graphPanelSize.getY() - TOP_PANEL_HEIGHT - POST_TOP_PANEL_MARGIN - GRAPH_PANEL_PADDING - GRAPH_PANEL_OFFSET - 2);

		outputScrollBox.setPosition(bottomPanelPos.getX(), bottomPanelPos.getY());
		outputScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

		inputScrollBox.setPosition(bottomPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, bottomPanelPos.getY());
		inputScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

		for (int i = 0; i < 20; i++) {
			outputMetricWidgets.get(i).setWidth((outputScrollBox.getSize().getX() - 10) / 2);
			inputMetricWidgets.get(i).setWidth((inputScrollBox.getSize().getX() - 10) / 2);
		}
	}

	protected ProductionManager getProductionManager() {
		return getLocalTeam().getProductionManager();
	}

	protected Team getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
