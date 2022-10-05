package theking530.staticpower.teams.production;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.production.metrics.MetricEntryWidget;
import theking530.staticpower.teams.production.metrics.MetricType;
import theking530.staticpower.teams.production.metrics.PacketGetProductionMetrics;

public class GuiProductionMenu extends StaticPowerDetatchedGui {
	private ScrollBox inputScrollBox;
	private List<MetricEntryWidget> inputMetricWidgets;

	private ScrollBox outputScrollBox;
	private List<MetricEntryWidget> outputMetricWidgets;

	public GuiProductionMenu() {
		super(400, 400);
	}

	@Override
	public void initializeGui() {
		inputMetricWidgets = new LinkedList<MetricEntryWidget>();
		outputMetricWidgets = new LinkedList<MetricEntryWidget>();

		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketGetProductionMetrics());

		this.registerWidget(inputScrollBox = new ScrollBox(10, 10, 200, 200));
		inputScrollBox.setDrawScrollBar(true);
		inputScrollBox.setDrawScrollBarBackground(true);
		this.registerWidget(outputScrollBox = new ScrollBox(10, 10, 200, 200));

		for (int i = 0; i < 20; i++) {
			MetricEntryWidget inputWidget = new MetricEntryWidget(null, MetricType.CONSUMPTION, 0, i * 20, 100, 20);
			inputScrollBox.registerWidget(inputWidget);
			inputMetricWidgets.add(inputWidget);

			MetricEntryWidget outputWidget = new MetricEntryWidget(null, MetricType.PRODUCTION, 0, i * 20, 100, 20);
			outputScrollBox.registerWidget(outputWidget);
			outputMetricWidgets.add(outputWidget);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (Minecraft.getInstance().level.getGameTime() % 20 == 0) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketGetProductionMetrics());
		}

		// Updated the metric widgets.
		for (int i = 0; i < 20; i++) {
			inputMetricWidgets.get(i).setVisible(false);
			outputMetricWidgets.get(i).setVisible(false);
		}

		if (getProductionManager() != null) {
			ProductionManager manager = getProductionManager();
			int maxInputs = Math.min(inputMetricWidgets.size(), manager.tempClientMetrics.getInputs().size());
			for (int i = 0; i < maxInputs; i++) {
				inputMetricWidgets.get(i).setMetric(manager.tempClientMetrics.getInputs().get(i));
				inputMetricWidgets.get(i).setVisible(true);
			}

			int maxOutputs = Math.min(outputMetricWidgets.size(), manager.tempClientMetrics.getOutputs().size());
			for (int i = 0; i < maxOutputs; i++) {
				outputMetricWidgets.get(i).setMetric(manager.tempClientMetrics.getOutputs().get(i));
				outputMetricWidgets.get(i).setVisible(true);
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
			outputMetricWidgets.get(i).setWidth(bottomPanelSize.getX());
			inputMetricWidgets.get(i).setWidth(bottomPanelSize.getX());
		}
	}

	@Override
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	protected ProductionManager getProductionManager() {
		return getLocalTeam().getProductionManager();
	}

	protected Team getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
