package theking530.staticpower.teams.productivity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.DynamicGraphDataSet;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.containers.HorizontalBox;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.productivity.ProductionManager;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.PacketRequestProductionMetrics;
import theking530.staticcore.productivity.metrics.PacketRequestProductionTimeline;
import theking530.staticcore.productivity.metrics.ProductionMetrics;
import theking530.staticcore.productivity.metrics.ProductivityTimeline;
import theking530.staticcore.productivity.metrics.ProductivityTimeline.ProductivityTimelineEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPowerRegistries;
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

	public GuiProductionMenu() {
		super(400, 400);
	}

	@Override
	public void initializeGui() {
		selectedMetricPeriod = MetricPeriod.MINUTE;
		selectedConsumptionProduct = new HashSet<>();
		selectedProductionProduct = new HashSet<>();
		displayedProductType = ModProducts.Item.get();
		productButtonContainer = new HorizontalBox(0, 0, 0, 20).setEvenlyDivideSpace(true);
		lastClientFetchTime = 0;
		registerWidget(consumptionGraph = new DataGraphWidget(0, 0, 0, 0));
		registerWidget(productionGraph = new DataGraphWidget(0, 0, 0, 0));

		// Create the product type buttons.
		for (ProductType<?> prodType : StaticPowerRegistries.ProductRegistry().getValues()) {
			StandardButton button = new TextButton(0, 0, 14, Component.translatable(prodType.getUnlocalizedName(2)).getString(), (self, mouseButton) -> {
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
			if (!selectedConsumptionProduct.isEmpty()) {
				updateTimelineValues(selectedConsumptionProduct, MetricType.CONSUMPTION, selectedMetricPeriod);
			}
			if (!selectedProductionProduct.isEmpty()) {
				updateTimelineValues(selectedProductionProduct, MetricType.PRODUCTION, selectedMetricPeriod);
			}
		}

		// If the data has changed since we last fetched, update the display values.
		if (getProductionManager() != null && getProductionManager().getCache(displayedProductType).haveClientValuesUpdatedSince(lastClientFetchTime)) {
			updateDisplayValues();
		}
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

	private void changeDisplayedProductType(ProductType<?> product) {
		displayedProductType = product;
		selectedConsumptionProduct.clear();
		selectedProductionProduct.clear();
		requestMetricUpdateFromServer();
	}

	private void changeDisplayedTimePeriod(MetricPeriod period) {
		this.selectedMetricPeriod = period;
		updateDisplayValues();
	}

	private void metricEntryClicked(MetricEntryWidget widget, Integer productHash) {
		if (widget.getMetricType() == MetricType.PRODUCTION) {
			selectedProductionProduct.clear();
			selectedProductionProduct.add(productHash);
		} else {
			selectedConsumptionProduct.clear();
			selectedConsumptionProduct.add(productHash);
		}
		updateDisplayValues();
	}

	private void requestMetricUpdateFromServer() {
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketRequestProductionMetrics(displayedProductType));
	}

	public void recieveTimelineData(ProductType<?> productType, MetricPeriod period, MetricType type, List<ProductivityTimeline> timelines) {
		// First ensure we clear all the data from the graphs before we do anything
		// else.
		if (type == MetricType.PRODUCTION) {
			productionGraph.clearAllData();
		} else {
			consumptionGraph.clearAllData();
		}

		// If there is no data returned, get out.
		if (timelines.isEmpty()) {
			return;
		}

		for (ProductivityTimeline timeline : timelines) {
			DynamicGraphDataSet data = new DynamicGraphDataSet(productType.getProductColor(timeline.serializedProduct()));

			for (int i = 0; i < 60; i++) {
				ProductivityTimelineEntry entry = timeline.entries().get(i);
				data.addNewDataPoint(type == MetricType.PRODUCTION ? entry.produced() : entry.consumed());
			}

			if (type == MetricType.PRODUCTION) {
				productionGraph.setDataSet(timeline.serializedProduct() + "_production", data);
			} else {
				consumptionGraph.setDataSet(timeline.serializedProduct() + "_consumption", data);
			}
		}
	}

	@SuppressWarnings("resource")
	private void updateDisplayValues() {
		ProductionManager manager = getProductionManager();
		ProductionMetrics metrics = manager.getCache(displayedProductType).getProductionMetrics(selectedMetricPeriod);

		consumptionMetrics.updateMetrics(displayedProductType, metrics.getConsumption());
		productionMetrics.updateMetrics(displayedProductType, metrics.getProduction());

		if (!metrics.isEmpty()) {
			if (selectedConsumptionProduct.isEmpty() && !metrics.getConsumption().isEmpty()) {
				selectedConsumptionProduct.add(metrics.getConsumption().get(0).getProductHash());
			}
			if (selectedProductionProduct.isEmpty() && !metrics.getProduction().isEmpty()) {
				selectedProductionProduct.add(metrics.getProduction().get(0).getProductHash());
			}
		}

		updateTimelineValues(selectedConsumptionProduct, MetricType.CONSUMPTION, selectedMetricPeriod);
		updateTimelineValues(selectedProductionProduct, MetricType.PRODUCTION, selectedMetricPeriod);
		lastClientFetchTime = Minecraft.getInstance().level.getGameTime();
	}

	private void updateTimelineValues(Collection<Integer> products, MetricType type, MetricPeriod period) {
		if (period == MetricPeriod.SECOND) {
			throw new RuntimeException("We can't get a timeline for a single second!");
		}
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL,
				new PacketRequestProductionTimeline(displayedProductType, products, MetricPeriod.values()[period.ordinal() - 1], type));
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		Vector2D overallHalfPadding = overallPadding.copy().divide(2);
		GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), overallSize.getY(), overallHalfPadding.getX(), overallHalfPadding.getY(), 0,
				new SDColor(0.1f, 0.1f, 0.1f, 0.75f));

		{
			GuiDrawUtilities.drawGenericBackground(pose, overallSize.getX(), TOP_PANEL_HEIGHT, overallHalfPadding.getX(), overallHalfPadding.getY() + TOP_PANEL_HEIGHT - 4, 0,
					new SDColor(0.35f, 0.35f, 0.35f));
			productButtonContainer.setWidth(overallSize.getX() - 6);
			productButtonContainer.setPosition(overallHalfPadding.getX() + 3, overallHalfPadding.getY() + TOP_PANEL_HEIGHT - 1);
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
			productionGraph.setSize(graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY());
			productionGraph.setPosition(graphPanelPos.getX(), graphPanelPos.getY());

			GuiDrawUtilities.drawStringLeftAligned(pose, "Consumption", graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, graphPanelPos.getY() - 5, 1, 1,
					SDColor.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawSlotWithBorder(pose, graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY(),
					graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, graphPanelPos.getY(), 1, new SDColor(0.1f, 0.1f, 0.1f));
			consumptionGraph.setSize(graphPanelSize.getX() + GRAPH_PANEL_PADDING / 4, graphPanelSize.getY());
			consumptionGraph.setPosition(graphPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, graphPanelPos.getY());
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

		productionScrollBox.setPosition(bottomPanelPos.getX(), bottomPanelPos.getY());
		productionScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());
		productionMetrics.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

		consumptionScrollBox.setPosition(bottomPanelPos.getX() + (overallSize.getX() / 2) - GRAPH_PANEL_PADDING / 4, bottomPanelPos.getY());
		consumptionScrollBox.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());
		consumptionMetrics.setSize(bottomPanelSize.getX(), bottomPanelSize.getY());

	}

	protected ProductionManager getProductionManager() {
		return getLocalTeam().getProductionManager();
	}

	protected Team getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
