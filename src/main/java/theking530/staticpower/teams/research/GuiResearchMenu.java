package theking530.staticpower.teams.research;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.TimeOfDayDrawable;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.RenderingUtilities;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.StringUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchLevels;
import theking530.staticpower.data.research.ResearchLevels.ResearchLevel;
import theking530.staticpower.data.research.ResearchLevels.ResearchNode;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;

@SuppressWarnings("resource")
public class GuiResearchMenu extends StaticPowerDetatchedGui {
	protected static final int TIER_LEVEL_HEIGHT = 80;
	protected static final int HISTORY_HEIGHT = 35;

	protected ResearchInstance currentResearch;
	protected ResearchNodeWidget expandedNode;
	protected SelectedResearchWidget selectedResearchWidget;

	protected ScrollBox nodeScrollBox;
	protected Map<ResearchNode, ResearchNodeWidget> researchNodes;

	protected ScrollBox sideBarScrollBox;
	protected List<ResearchHistoryWidget> historyWidgets;

	public GuiResearchMenu() {
		super(400, 400);
		setDrawDefaultDarkBackground(false);
		Minecraft.getInstance().level.playLocalSound(Minecraft.getInstance().player.getOnPos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.MASTER, 1.0f, 1.5f, true);
	}

	@Override
	public void initializeGui() {
		researchNodes = new HashMap<ResearchNode, ResearchNodeWidget>();
		historyWidgets = new ArrayList<ResearchHistoryWidget>();
		registerWidget(selectedResearchWidget = new SelectedResearchWidget(getLocalTeam().getResearchManager(), 0, 0, 109, 76).setZLevel(500));
		registerWidget(new TimeOfDayDrawable(56, 1f, 20, Minecraft.getInstance().player.level, Minecraft.getInstance().player.getOnPos()).setZLevel(200));

		registerWidget(nodeScrollBox = new ScrollBox(105, 20, 10000, 0));
		registerWidget(sideBarScrollBox = new ScrollBox(0, 105, 105, 800).setZLevel(100));

		captureScreenSize();
		initializeResearchNodes();
		initializeSideBar();
	}

	public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
		super.resize(p_96575_, p_96576_, p_96577_);
		captureScreenSize();
		initializeResearchNodes();
		initializeSideBar();
	}

	protected void initializeResearchNodes() {
		// Remove existing nodes.
		nodeScrollBox.clearChildren();
		// tierBoxes.clear();
		researchNodes.clear();

		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();

		nodeScrollBox.setPosition(104, 25);
		nodeScrollBox.setSize(screenWidth - 105, screenHeight - 25);
		HashMap<ResearchNode, Integer> childrenPlaced = new HashMap<ResearchNode, Integer>();

		ResearchLevels levels = ResearchLevels.getAllResearchLevels();
		for (int y = 0; y < levels.getLevels().size(); y++) {
			float tint = y % 2 == 0 ? 0.05f : 0.0f;
			ResearchLevel level = levels.getLevels().get(y);

			for (int i = 0; i < level.getResearch().size(); i++) {
				ResearchNode research = level.getResearch().get(i);
				ResearchNode parent = research.getParent();
				Vector2D parentPosition = new Vector2D(0, 0);

				int childCount = 0;
				int childIndex = 0;
				int balancedIndex = 0;
				if (childrenPlaced.containsKey(parent)) {
					childIndex = childrenPlaced.get(parent);
					balancedIndex = childIndex - (parent.getChildren().size() / 2);
					childCount = parent.getChildren().size();
				}

				if (parent != null) {
					parentPosition = researchNodes.get(parent).getPosition();
				}

				if (!childrenPlaced.containsKey(research)) {
					childrenPlaced.put(research, 0);
				}

				if (childrenPlaced.containsKey(parent)) {
					childrenPlaced.put(parent, childrenPlaced.get(parent) + 1);
				} else {
					parentPosition = new Vector2D(nodeScrollBox.getSize().getX() / 2, -TIER_LEVEL_HEIGHT / 2 + 10);
				}

				float distanceBetween = nodeScrollBox.getSize().getX() / 2;
				if (parent != null) {
					distanceBetween = nodeScrollBox.getSize().getX() / (parent.getChildren().size() + 1);
					if (research.getChildren().size() == 0) {
						distanceBetween /= 4;
					}
				}
				int offset = childCount < 3 ? 0 : childIndex % 2 == 0 ? 7 : -7;
				ResearchNodeWidget widget = new ResearchNodeWidget(research, parentPosition.getX() + (balancedIndex * distanceBetween), parentPosition.getY() + (TIER_LEVEL_HEIGHT - 20 + offset), 24,
						24);
				researchNodes.put(research, widget);
				nodeScrollBox.registerWidget(widget);
			}
		}
	}

	protected void initializeSideBar() {
		// Remove existing history.
		sideBarScrollBox.clearChildren();
		historyWidgets.clear();

		if (TeamManager.getLocalTeam() != null) {
			int index = 0;
			// We need to iterate backwards here.
			List<ResourceLocation> completed = new ArrayList<>(TeamManager.getLocalTeam().getResearchManager().getCompletedResearch());
			for (int i = completed.size() - 1; i >= 0; i--) {
				Research research = StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, completed.get(i)).orElse(null);
				if (research != null) {
					float tint = index % 2 == 0 ? 0.5f : 0.0f;
					ResearchHistoryWidget historyWidget = new ResearchHistoryWidget(research, 0, index * HISTORY_HEIGHT, 105, HISTORY_HEIGHT).setBackgroundColor(new Color(tint, tint, tint, 0.35f))
							.setDrawBackground(true);
					historyWidgets.add(historyWidget);
					sideBarScrollBox.registerWidget(historyWidget);
					index++;
				}
			}
		}
	}

	protected void captureScreenSize() {
		// Capture the screen size.
		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();

		nodeScrollBox.setPosition(104, 25);
		nodeScrollBox.setSize(screenWidth - 105, screenHeight - 25);

		// Size up the sidebar.
		sideBarScrollBox.setPosition(0, selectedResearchWidget.getSize().getY());
		sideBarScrollBox.setSize(102, screenHeight - selectedResearchWidget.getSize().getY());
		for (ResearchHistoryWidget widgets : historyWidgets) {
			widgets.setSize(102, HISTORY_HEIGHT);
		}

	}

	@Override
	public void updateBeforeRender() {
		// Handle the expanded node.
		if (expandedNode != null) {
			if (!expandedNode.isHovered()) {
				expandedNode.setExpanded(false);
				expandedNode = null;
			}
		} else {
			for (ResearchNodeWidget node : researchNodes.values()) {
				if (node.isHovered()) {
					expandedNode = node;
					expandedNode.setExpanded(true);
					break;
				}
			}
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// We have to do this as Screens don't usually have this called.
		this.renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
	}

	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		// Draw the sidebar and background bar.
		GuiDrawUtilities.drawRectangle(pose, getMinecraft().screen.width, getMinecraft().screen.height, 0, 0, -100.1f, new Color(0, 0, 0, 0.8f));
		GuiDrawUtilities.drawGenericBackground(pose, 110, getMinecraft().screen.height + 8, -5, -4, 0.0f, new Color(0.75f, 0.5f, 1.0f, 0.95f));
		GuiDrawUtilities.drawGenericBackground(pose, getMinecraft().screen.width, 28, 108, -4, 500.0f, new Color(0.75f, 0.5f, 1.0f, 0.95f));
		drawConnectingLines(pose, partialTicks, mouseX, mouseY);
	}

	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		Player player = getMinecraft().player;

		String biomeName = StringUtilities.prettyFormatCamelCase(getMinecraft().player.getLevel().getBiome(player.getOnPos()).getRegistryName().getPath());
		GuiDrawUtilities.drawStringLeftAligned(pose, biomeName, 134, 14f, 0, 0.75f, Color.EIGHT_BIT_WHITE, true);

		String dimensionName = StringUtilities.prettyFormatCamelCase(getMinecraft().player.getLevel().dimensionType().effectsLocation().getPath());
		GuiDrawUtilities.drawStringCentered(pose, dimensionName, getMinecraft().getWindow().getGuiScaledWidth() / 2 + 62, 11f, 0, 1, Color.EIGHT_BIT_WHITE, true);

		// Draw the current time.
		long time = (getMinecraft().player.getLevel().dayTime() + 6000) % 24000;
		long hour = time / 1000;
		long minute = time % 1000;
		minute *= 0.06f;

		String formattedHour = hour < 10 ? "0" + hour : Long.toString(hour);
		String formattedMinute = minute < 10 ? "0" + minute : Long.toString(minute);

		String formattedTime = String.format("%1$s:%2$s", formattedHour, formattedMinute);
		GuiDrawUtilities.drawStringCentered(pose, formattedTime, getMinecraft().getWindow().getGuiScaledWidth() / 2 + 62, 18f, 0, 0.5f, Color.EIGHT_BIT_WHITE, true);
	}

	protected void drawConnectingLines(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		// Clip the lines to the scroll box area.
		RectangleBounds clipMask = nodeScrollBox.getClipBounds(pose);
		RenderingUtilities.applyScissorMask(clipMask);

		// Draw the lines.
		for (ResearchNodeWidget outerNode : this.researchNodes.values()) {
			List<ResearchNodeWidget> preReqWidgets = new ArrayList<ResearchNodeWidget>();
			for (ResearchNodeWidget node : this.researchNodes.values()) {
				if (outerNode.getResearch().getPrerequisites().contains(node.getResearch().getId())) {
					preReqWidgets.add(node);
				}
			}

			int index = 0;
			for (ResearchNodeWidget node : preReqWidgets) {
				index++;
				pose.pushPose();
				Vector3D expandedPosition = outerNode.getScreenSpacePosition().promote();
				expandedPosition.add(11, 1, 100);
				Vector3D preReqPosition = node.getScreenSpacePosition().promote();
				preReqPosition.add(11, 20f, 100);

				Color startLineColor = new Color(1.0f, 1.0f, 1.0f, 0.5f);
				Color endLineColor = startLineColor;

				// Draw the line ALWAYS black. We'll color on top of it in the next step if
				// something is expanded.
				GuiDrawUtilities.drawLine(pose, expandedPosition, preReqPosition, startLineColor, endLineColor, 4.0f);

				// If no node is expanded, draw the connecting lines behind everything.
				// If a node is expanded, only draw lines for that node and push them over
				// everything.
				if (expandedNode != null && outerNode == expandedNode) {
					preReqPosition.add(0, 0, 500);
					float timeHovered = SDMath.clamp((this.expandedNode.getTicksHovered() - (index * 2)) / 2, 0, 1);

					if (!getLocalTeam().getResearchManager().hasCompletedResearch(node.getResearch().getId())) {
						startLineColor = new Color(0.8f, 0.15f, 0.15f, 1.0f);
						endLineColor = new Color(0.8f, 0.15f, 0.15f, 1.0f);
					} else {
						startLineColor = new Color(0.0f, 1.0f, 0.2f, 1.0f);
						endLineColor = new Color(0.0f, 1.0f, 0.2f, 1.0f);
					}

					startLineColor = startLineColor.multiply(timeHovered);
					timeHovered = SDMath.clamp((this.expandedNode.getTicksHovered() - (index * 3)) / 3, 0, 1);
					endLineColor = endLineColor.multiply(timeHovered);

					GuiDrawUtilities.drawLine(pose, expandedPosition, preReqPosition, startLineColor, endLineColor, 8.0f);
				}

				pose.popPose();
			}
		}

		// Reset the clip mask.
		RenderingUtilities.clearScissorMask();
	}

	protected Team getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
