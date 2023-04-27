package theking530.staticcore.productivity.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.screens.StaticPowerDetatchedGui;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.DynamicGraphDataSet;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.containers.HorizontalBox;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.PacketRequestProductionMetrics;
import theking530.staticcore.productivity.metrics.PacketRequestProductionTimeline;
import theking530.staticcore.productivity.metrics.PacketRequestProductionTimeline.TimelineRequest;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.metrics.ProductionMetrics;
import theking530.staticcore.productivity.metrics.ProductivityTimeline;
import theking530.staticcore.productivity.metrics.ProductivityTimeline.ProductivityTimelineEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.ClientTeam;
import theking530.staticcore.teams.TeamManager;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;

public class GuiProductionMenu extends StaticPowerDetatchedGui {
	private static final int TOP_PANEL_HEIGHT = 20;
	private static final int POST_TOP_PANEL_MARGIN = 20;
	private static final float GRAPH_PANEL_PADDING = 30;
	private static final float GRAPH_PANEL_OFFSET = 2;

	private long lastClientFetchTime;
	private ProductType<?> displayedProductType;

	private HorizontalBox productButtonContainer;

	private ScrollBox consumptionScrollBox;
	private MetricEntryContainer consumptionMetrics;
	private DataGraphWidget consumptionGraph;
	private Set<Integer> selectedConsumptionProduct;

	private ScrollBox productionScrollBox;
	private MetricEntryContainer productionMetrics;
	private DataGraphWidget productionGraph;
	private Set<Integer> selectedProductionProduct;

	private MetricPeriod selectedMetricPeriod;

	private Vector2D overallPadding;
	private Vector2D overallTopLeft;
	private Vector2D overallSize;

	private Vector2D graphPanelPos;
	private Vector2D graphPanelSize;

	private Vector2D bottomPanelPos;
	private Vector2D bottomPanelSize;

	private List<ProductivityTimeline> productivityTimelines;

	public GuiProductionMenu() {
		super(400, 400);
	}

	@Override
	public void initializeGui() {
		selectedMetricPeriod = MetricPeriod.MINUTE;
		selectedConsumptionProduct = new HashSet<>();
		selectedProductionProduct = new HashSet<>();
		displayedProductType = StaticCoreProductTypes.Item.get();
		productButtonContainer = new HorizontalBox(0, 0, 0, 20).setEvenlyDivideSpace(true);
		lastClientFetchTime = 0;

		productivityTimelines = new ArrayList<>();

		registerWidget(consumptionGraph = new DataGraphWidget(0, 0, 0, 0));
		registerWidget(productionGraph = new DataGraphWidget(0, 0, 0, 0));

		// Create the product type buttons.
		for (ProductType<?> prodType : StaticCoreRegistries.ProductRegistry().getValues()) {
			StandardButton button = new TextButton(0, 0, 14,
					Component.translatable(prodType.getUnlocalizedName(2)).getString(), (self, mouseButton) -> {
						for (AbstractGuiWidget<?> otherButton : productButtonContainer.getChildren()) {
							TextButton castOtherButton = (TextButton) otherButton;
							if (castOtherButton != null) {
								castOtherButton.setToggled(false);
							}
						}
						self.setToggled(true);
						changeDisplayedProductType(prodType);
					});
			button.setTooltip(Component.translatable(prodType.getUnlocalizedName(2)));
			if (displayedProductType == prodType) {
				button.setToggled(true);
			}
			productButtonContainer.registerWidget(button);
		}
		registerWidget(productButtonContainer);

		registerWidget(consumptionScrollBox = new ScrollBox(10, 10, 200, 200));
		consumptionScrollBox.setDrawScrollBar(true);
		consumptionScrollBox.setDrawScrollBarBackground(true);
		registerWidget(productionScrollBox = new ScrollBox(10, 10, 200, 200));
		productionScrollBox.setDrawScrollBar(true);
		productionScrollBox.setDrawScrollBarBackground(true);

		consumptionMetrics = new MetricEntryContainer(MetricType.CONSUMPTION, 0, 0, 0, 0);
		consumptionMetrics.setSelectedMetricChanged(this::metricEntryClicked);
		consumptionScrollBox.registerWidget(consumptionMetrics);

		productionMetrics = new MetricEntryContainer(MetricType.PRODUCTION, 0, 0, 0, 0);
		productionMetrics.setSelectedMetricChanged(this::metricEntryClicked);
		productionScrollBox.registerWidget(productionMetrics);

		requestMetricUpdateFromServer();
		recalculateSizes();
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		super.tick();
		if (Minecraft.getInstance().level.getGameTime() % 20 == 0) {
			requestMetricUpdateFromServer();
		}

		// If the data has changed since we last fetched, update the display values.
		if (getProductionManager() != null && getProductionManager().getProductTypeCache(displayedProductType)
				.haveClientValuesUpdatedSince(lastClientFetchTime)) {
			updateDisplayValues();
			updateTimelineValues(selectedMetricPeriod);
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);

		updateTimeline(displayedProductType, MetricPeriod.SECOND, productivityTimelines, partialTicks);
	}

	protected void updateTimeline(ProductType<?> productType, MetricPeriod period, List<ProductivityTimeline> timelines,
			float partialTicks) {
		productionGraph.clearAllData();
		consumptionGraph.clearAllData();
		
		// If there is no data returned, get out.
		if (timelines.isEmpty()) {
			return;
		}

		@SuppressWarnings("resource")
		long currentTick = Minecraft.getInstance().level.getGameTime();
		long sinceLastUpdate = currentTick - lastClientFetchTime;
		float offset = ((sinceLastUpdate % 20) + partialTicks) / 20.0f;
		
		for (ProductivityTimeline timeline : timelines) {

			List<Double> expandedData = new ArrayList<>();
			for (int i = 0; i < 60; i++) {
				expandedData.add(0.0);
			}

			double minValue = 0, maxValue = 0;
			for (ProductivityTimelineEntry timelineEntry : timeline.entries()) {
				double value = timelineEntry.value();
				if (value > maxValue) {
					maxValue = value;
				}
				if (value < minValue) {
					minValue = value;
				}

				float closestIndex = (currentTick - timelineEntry.tick());
				closestIndex = closestIndex / period.getMetricPeriodInTicks();
				int roundedIndex = Math.round(closestIndex);
				if (roundedIndex >= 0 && roundedIndex < 60) {
					expandedData.set(roundedIndex, value);
				}
			}

			DynamicGraphDataSet data = new DynamicGraphDataSet(
					productType.getProductColor(timeline.serializedProduct()), 0, 60, minValue, maxValue);

			for (int i = 0; i < 60; i++) {
				data.addNewDataPoint(i + offset, expandedData.get(i));
			}

			if (timeline.metricType() == MetricType.PRODUCTION) {
				productionGraph.setDataSet(timeline.serializedProduct() + "_production", data);
			} else {
				consumptionGraph.setDataSet(timeline.serializedProduct() + "_consumption", data);
			}
		}
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
		recalculateSizes();
	}

	private void changeDisplayedProductType(ProductType<?> product) {
		displayedProductType = product;
		selectedConsumptionProduct.clear();
		selectedProductionProduct.clear();

		consumptionMetrics.clearChildren();
		productionMetrics.clearChildren();

		requestMetricUpdateFromServer();
	}

	@SuppressWarnings("unused")
	private void changeDisplayedTimePeriod(MetricPeriod period) {
		this.selectedMetricPeriod = period;
		updateDisplayValues();
	}

	private void metricEntryClicked(MetricEntryWidget widget, Integer productHash) {
		updateTimelineValues(selectedMetricPeriod);
		updateDisplayValues();

		if(widget.getMetricType() == MetricType.CONSUMPTION) {
			selectedConsumptionProduct.clear();
			selectedConsumptionProduct.add(productHash);
		}else {
			selectedProductionProduct.clear();
			selectedProductionProduct.add(productHash);
		}
	}

	private void requestMetricUpdateFromServer() {
		StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new PacketRequestProductionMetrics(displayedProductType));
	}

	public void recieveTimelineData(long requestAtTime, MetricPeriod period, List<ProductivityTimeline> timelines) {
		this.productivityTimelines = timelines;
	}

	@SuppressWarnings("resource")
	private void updateDisplayValues() {
		ClientProductionManager manager = getProductionManager();
		ProductionMetrics metrics = manager.getProductTypeCache(displayedProductType)
				.getProductionMetrics(this.selectedMetricPeriod);

		consumptionMetrics.updateMetrics(displayedProductType, metrics.getMetrics().values());
		productionMetrics.updateMetrics(displayedProductType, metrics.getMetrics().values());

		if (!metrics.isEmpty()) {
			if (selectedProductionProduct.isEmpty()) {
				for (ProductionMetric metric : metrics.getMetrics().values()) {
					if (!metric.getProduced().isZero()) {
						selectedProductionProduct.add(metric.getProductHash());
						break;
					}
				}
			}

			if (selectedConsumptionProduct.isEmpty()) {
				for (ProductionMetric metric : metrics.getMetrics().values()) {
					if (!metric.getConsumed().isZero()) {
						selectedConsumptionProduct.add(metric.getProductHash());
						break;
					}
				}
			}
		}

		lastClientFetchTime = Minecraft.getInstance().level.getGameTime();
	}

	@SuppressWarnings("resource")
	private void updateTimelineValues(MetricPeriod period) {
		if (period == MetricPeriod.SECOND) {
			throw new RuntimeException("We can't get a timeline for a single second!");
		}

		List<TimelineRequest> requests = new ArrayList<>();
		for (Integer hash : selectedConsumptionProduct) {
			requests.add(new TimelineRequest(this.displayedProductType, hash, MetricType.CONSUMPTION));
		}
		for (Integer hash : selectedProductionProduct) {
			requests.add(new TimelineRequest(this.displayedProductType, hash, MetricType.PRODUCTION));
		}

		StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new PacketRequestProductionTimeline(Minecraft.getInstance().level.getGameTime(), requests,
						MetricPeriod.values()[period.ordinal() - 1]));
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		Vector2D overallHalfPadding = overallPadding.copy().divide(2);
		GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), overallSize.getY(), overallHalfPadding.getX(),
				overallHalfPadding.getY(), 0, new SDColor(0.1f, 0.1f, 0.1f, 0.75f));

		{
			GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), TOP_PANEL_HEIGHT,
					overallHalfPadding.getX(), overallHalfPadding.getY() + TOP_PANEL_HEIGHT - 4, 0,
					new SDColor(0.35f, 0.35f, 0.35f));
			productButtonContainer.setWidth(overallSize.getX() - 6);
			productButtonContainer.setPosition(overallHalfPadding.getX() + 3,
					overallHalfPadding.getY() + TOP_PANEL_HEIGHT - 1);
		}

		{
			GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), TOP_PANEL_HEIGHT,
					overallHalfPadding.getX(), overallHalfPadding.getY(), 1, new SDColor(0.5f, 0.5f, 0.5f));
			GuiDrawUtilities.drawStringLeftAligned(pose, "Production", overallHalfPadding.getX() + 8,
					overallHalfPadding.getY() + 13, 1, 1, SDColor.EIGHT_BIT_WHITE, true);
		}

		{
			GuiDrawUtilities.drawStringLeftAligned(pose, "Production", overallTopLeft.getX() + 14,
					graphPanelPos.getY() - 5, 1, 1, SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4,
					graphPanelSize.getY(), graphPanelPos.getX(), graphPanelPos.getY(), 1,
					new SDColor(0.1f, 0.1f, 0.1f));
			productionGraph.setSize(graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY());
			productionGraph.setPosition(graphPanelPos.getX(), graphPanelPos.getY());

			GuiDrawUtilities.drawStringLeftAligned(pose, "Consumption",
					graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, graphPanelPos.getY() - 5,
					1, 1, SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4,
					graphPanelSize.getY(), graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4,
					graphPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
			consumptionGraph.setSize(graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY());
			consumptionGraph.setPosition(graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4,
					graphPanelPos.getY());
		}

		{
			GuiDrawUtilities.drawSlotWithBorder(pose, bottomPanelSize.getX(), bottomPanelSize.getY(),
					bottomPanelPos.getX(), bottomPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
			GuiDrawUtilities.drawSlotWithBorder(pose, bottomPanelSize.getX(), bottomPanelSize.getY(),
					bottomPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, bottomPanelPos.getY(),
					1, new SDColor(0.1f, 0.1f, 0.1f));
		}
	}

	@Override
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	protected void recalculateSizes() {
		overallPadding = new Vector2D(getScreenBounds().getWidth() / 4, getScreenBounds().getHeight() / 8);
		Vector2D overallHalfPadding = overallPadding.copy().divide(2);
		overallTopLeft = new Vector2D(overallPadding.getX() / 2, overallPadding.getY() / 2);
		overallSize = new Vector2D(getScreenBounds().getWidth(), getScreenBounds().getHeight())
				.subtract(overallPadding);

		graphPanelPos = new Vector2D(GRAPH_PANEL_PADDING / 2 + overallHalfPadding.getX(), overallHalfPadding.getY()
				+ TOP_PANEL_HEIGHT + POST_TOP_PANEL_MARGIN + GRAPH_PANEL_PADDING / 2 + GRAPH_PANEL_OFFSET);
		graphPanelSize = new Vector2D(overallSize.getX() / 2 - GRAPH_PANEL_PADDING, overallSize.getY() / 4);

		bottomPanelPos = new Vector2D(overallTopLeft.getX() + GRAPH_PANEL_PADDING / 2,
				overallTopLeft.getY() + TOP_PANEL_HEIGHT + POST_TOP_PANEL_MARGIN + graphPanelSize.getY()
						+ GRAPH_PANEL_PADDING / 2 + GRAPH_PANEL_OFFSET + 4);
		bottomPanelSize = new Vector2D(graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4,
				overallSize.getY() - graphPanelSize.getY() - TOP_PANEL_HEIGHT - POST_TOP_PANEL_MARGIN
						- GRAPH_PANEL_PADDING - GRAPH_PANEL_OFFSET - 2);

		productionScrollBox.setPosition(bottomPanelPos.getX(), bottomPanelPos.getY());
		productionScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());
		productionMetrics.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

		consumptionScrollBox.setPosition(bottomPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4,
				bottomPanelPos.getY());
		consumptionScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());
		consumptionMetrics.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

	}

	protected ClientProductionManager getProductionManager() {
		return getLocalTeam().getProductionManager();
	}

	protected ClientTeam getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
