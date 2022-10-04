package theking530.staticpower.teams.production;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.production.metrics.PacketGetProductionMetrics;
import theking530.staticpower.teams.production.metrics.SerializedMetricPeriod;

public class GuiProductionMenu extends StaticPowerDetatchedGui {

	public GuiProductionMenu() {
		super(400, 400);

	}

	@Override
	public void initializeGui() {
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketGetProductionMetrics());
	}

	@Override
	public void tick() {
		super.tick();

		if (Minecraft.getInstance().level.getGameTime() % 20 == 0) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketGetProductionMetrics());
		}
	}

	@Override
	public void updateBeforeRender() {

	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(pose);

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

		if (getProductionManager() != null) {
			ProductionManager manager = getProductionManager();
			int yOffset = bottomPanelPos.getYi() + 2;
			int xOffset = bottomPanelPos.getXi() + 4;
			for (SerializedMetricPeriod metric : manager.tempClientMetrics) {
				try {
					CompoundTag tag = TagParser.parseTag(metric.getSerializedProduct());
					tag.putByte("Count", (byte) 1);
					ItemStack product = ItemStack.of(tag);

					GuiDrawUtilities.drawItem(pose, product, xOffset, yOffset, 10, 16, 16);
					GuiDrawUtilities.drawStringLeftAligned(pose, GuiTextUtilities.formatNumberAsStringOneDecimal(metric.getInput()).getString() + "/m", xOffset + 20, yOffset + 11,
							1, 0.75f, SDColor.EIGHT_BIT_WHITE, true);
					GuiDrawUtilities.drawStringLeftAligned(pose, GuiTextUtilities.formatNumberAsStringOneDecimal(metric.getOutput()).getString() + "/m", xOffset + 52, yOffset + 11,
							1, 0.75f, SDColor.EIGHT_BIT_WHITE, true);
					GuiDrawUtilities.drawStringLeftAligned(pose, GuiTextUtilities.formatNumberAsStringOneDecimal(metric.getInput() - metric.getOutput()).getString() + "/m",
							xOffset + 86, yOffset + 11, 1, 0.75f, SDColor.EIGHT_BIT_WHITE, true);
					yOffset += 18;
				} catch (CommandSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

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
