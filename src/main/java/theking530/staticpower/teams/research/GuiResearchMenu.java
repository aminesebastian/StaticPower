package theking530.staticpower.teams.research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.TimeOfDayDrawable;
import theking530.staticcore.gui.widgets.containers.PanBox;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.RenderingUtilities;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.StringUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
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
	protected static final int HISTORY_HEIGHT = 35;

	protected ResourceLocation currentResearch;
	protected ResearchNodeWidget hoveredNode;
	protected SelectedResearchWidget selectedResearchWidget;

	protected PanBox nodePanBox;
	protected Map<ResearchNode, ResearchNodeWidget> researchNodes;

	protected ScrollBox sideBarScrollBox;
	protected List<ResearchHistoryWidget> historyWidgets;

	protected float nodeHoveredTime;
	protected int completedResearchCount;

	public GuiResearchMenu() {
		super(400, 400);
		setDrawDefaultDarkBackground(false);
		Minecraft.getInstance().level.playLocalSound(Minecraft.getInstance().player.getOnPos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.MASTER, 1.0f, 1.5f, true);
	}

	@Override
	public void initializeGui() {
		researchNodes = new HashMap<ResearchNode, ResearchNodeWidget>();
		historyWidgets = new ArrayList<ResearchHistoryWidget>();

		// Initialize the selected research widget.
		registerWidget(selectedResearchWidget = new SelectedResearchWidget(0, 0, 109, 0).setZLevel(500));
		if (getResearchManager().hasSelectedResearch()) {
			selectedResearchWidget.setResearch(getResearchManager().getSelectedResearch().getTrackedResearch(), getResearchManager().getSelectedResearch());
			currentResearch = getResearchManager().getSelectedResearch().getId();
		} else {
			Collection<ResearchInstance> activeResearch = getResearchManager().getAllActiveResearch().values();
			selectedResearchWidget.setResearch(getResearchManager().getLastCompletedResearch(), activeResearch.size() > 0 ? Iterables.getLast(activeResearch) : null);
			currentResearch = getResearchManager().getLastCompletedResearch().getId();
		}

		registerWidget(new TimeOfDayDrawable(56, 1f, 20, Minecraft.getInstance().player.level, Minecraft.getInstance().player.getOnPos()).setZLevel(200));

		registerWidget(nodePanBox = new PanBox(105, 20, 0, 0));
		nodePanBox.setMaxBounds(new Vector4D(-10000, -10000, 10000, 10000));
		nodePanBox.setMaxZoom(2.0f);

		registerWidget(sideBarScrollBox = new ScrollBox(0, 105, 105, 800).setZLevel(100));

		refreshResearchWidgets();
	}

	@Override
	public void tick() {
		super.tick();
		// If the count of completed researchs changed, then refresh the widgets.
		if (completedResearchCount != getResearchManager().getCompletedResearch().size()) {
			refreshResearchWidgets();
		}

		// If we have selected research, then both of the below will be populated.
		// If not, only the last completed research will exist, with no accompanying
		// instance.
		Research selectedResearch = null;
		ResearchInstance selectedResearchInstance = null;
		if (getResearchManager().hasSelectedResearch()) {
			selectedResearchInstance = getResearchManager().getSelectedResearch();
			selectedResearch = selectedResearchInstance.getTrackedResearch();
		} else {
			selectedResearch = getResearchManager().getLastCompletedResearch();
		}

		// Track when the current research changes.
		if (!currentResearch.equals(selectedResearch.getId())) {
			if (selectedResearchInstance != null) {
				currentResearch = selectedResearchInstance.getId();
				selectedResearchChanged(selectedResearch, selectedResearchInstance);
			} else {
				currentResearch = selectedResearch.getId();
				selectedResearchChanged(selectedResearch, selectedResearchInstance);
			}
		}
	}

	@Override
	public void updateBeforeRender() {
		// Handle the expanded node.
		if (hoveredNode != null) {
			if (!hoveredNode.isHovered()) {
				hoveredNode.setExpanded(false);
				hoveredNode = null;
				nodeHoveredTime = 0.0f;
			} else {
				if (nodeHoveredTime > 0.5f && !hoveredNode.isExpanded() && shouldExpandResearch(hoveredNode.getResearch())) {
					hoveredNode.setExpanded(true);
				}
			}
		} else {
			for (ResearchNodeWidget node : researchNodes.values()) {
				if (node.isHovered()) {
					hoveredNode = node;
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
		if (hoveredNode != null) {
			nodeHoveredTime += partialTicks;
		}
	}

	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
		refreshResearchWidgets();
	}

	protected void refreshResearchWidgets() {
		hoveredNode = null;
		captureScreenSize();
		initializeResearchNodes();
		initializeSideBar();
		completedResearchCount = getResearchManager().getCompletedResearch().size();
	}

	protected void initializeResearchNodes() {
		// Remove existing nodes.
		boolean isFirstTime = nodePanBox.getChildren().size() == 0;
		nodePanBox.clearChildren();
		researchNodes.clear();

		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();

		nodePanBox.setPosition(105, 25);
		nodePanBox.setSize(screenWidth - 105, screenHeight - 25);
		Vector2D targetPan = new Vector2D(0, 0);
		boolean lockTargetPan = false;

		ResearchLevels levels = ResearchLevels.getAllResearchLevels();
		for (int y = 0; y < levels.getLevels().size(); y++) {
			ResearchLevel level = levels.getLevels().get(y);
			for (int i = 0; i < level.getResearch().size(); i++) {
				ResearchNode researchNode = level.getResearch().get(i);
				Research research = researchNode.getResearch();
				boolean isCompleted = getResearchManager().hasCompletedResearch(research.getId());
				boolean isAvailable = getResearchManager().isResearchAvailable(research.getId());
				boolean isAnyParentAvailable = false;
				for (ResearchNode parent : researchNode.getAllParents()) {
					if (getResearchManager().isResearchAvailable(parent.getResearch().getId())) {
						isAnyParentAvailable = true;
						break;
					}
				}

				// Only show research that is either completed, marked as hidden until available
				// AND available, or if their parent is available.
				boolean debugMode = false;
				if (debugMode || isCompleted || isAvailable || isAnyParentAvailable && !research.isHiddenUntilAvailable()) {
					// Calculate the reletive position and create the research node widget.
					Vector2D relative = researchNode.getRelativePosition().getScaledVector(50.0f, 65.0f).add(93, 30);
					ResearchNodeWidget widget = new ResearchNodeWidget(researchNode, relative.getX() + ((screenWidth - 130) + 24) / 2, relative.getY() + 24, 24, 24);
					researchNodes.put(researchNode, widget);
					nodePanBox.registerWidget(widget);

					// Capture the largest y value for the last completed research.
					// If we are actively researching, then capture the
					if (!lockTargetPan) {
						if (getResearchManager().getCompletedResearch().indexOf(research.getId()) == getResearchManager().getCompletedResearch().size() - 1) {
							targetPan = relative.copy();
						} else if (getResearchManager().hasSelectedResearch() && getResearchManager().getSelectedResearch().getTrackedResearch().getId().equals(research.getId())) {
							targetPan = relative.copy();
							lockTargetPan = true;
						}
					}
				}
			}
		}

		// Only move the camera to the current node IF this is the first time this GUI
		// is opened. In other words, do NOT do this when refreshing the UI while its
		// open.
		if (isFirstTime) {
			nodePanBox.setTargetPan(new Vector2D(-Math.min(0, (targetPan.getX() - nodePanBox.getWidth() / 4)), -Math.max(0, (targetPan.getY() - nodePanBox.getHeight() / 2))));
		}
	}

	protected void initializeSideBar() {
		// Remove existing history.
		sideBarScrollBox.clearChildren();
		historyWidgets.clear();

		sideBarScrollBox.setPosition(0, selectedResearchWidget.getSize().getY());
		if (getLocalTeam() != null) {
			int index = 0;
			// We need to iterate backwards here.
			List<ResourceLocation> completed = new ArrayList<>(getResearchManager().getCompletedResearch());
			for (int i = completed.size() - 1; i >= 0; i--) {
				Research research = StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, completed.get(i)).orElse(null);
				if (research != null) {
					float tint = index % 2 == 0 ? 0.5f : 0.0f;
					ResearchHistoryWidget historyWidget = new ResearchHistoryWidget(research, 0, 0, 105, HISTORY_HEIGHT).setBackgroundColor(new Color(tint, tint, tint, 0.35f)).setDrawBackground(true);
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

		nodePanBox.setPosition(104, 25);
		nodePanBox.setSize(screenWidth - 105, screenHeight - 25);

		// Size up the sidebar.
		sideBarScrollBox.setSize(102, screenHeight - selectedResearchWidget.getSize().getY());
		for (ResearchHistoryWidget widgets : historyWidgets) {
			widgets.setSize(102, HISTORY_HEIGHT);
		}

	}

	protected void selectedResearchChanged(Research research, @Nullable ResearchInstance researchInstance) {
		selectedResearchWidget.setResearch(research, researchInstance);
		sideBarScrollBox.setPosition(0, selectedResearchWidget.getSize().getY());
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

		// String biomeName =
		// StringUtilities.prettyFormatCamelCase(getMinecraft().player.getLevel().getBiome(player.getOnPos()).getRegistryName().getPath());
		// GuiDrawUtilities.drawStringLeftAligned(pose, biomeName, 134, 14f, 0, 0.75f,
		// Color.EIGHT_BIT_WHITE, true);

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
		RectangleBounds clipMask = nodePanBox.getClipBounds(pose).copy().multiply((float) getMinecraft().getWindow().getGuiScale());
		RenderingUtilities.applyScissorMask(clipMask);

		pose.pushPose();
		nodePanBox.transformPoseBeforeRender(pose);

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

				Vector3D expandedPosition = outerNode.getPosition().promote();
				expandedPosition.add(11, 2, 100);

				Vector3D preReqPosition = node.getPosition().promote();
				preReqPosition.add(11, 20f, 100);

				Color startLineColor = new Color(1.0f, 1.0f, 1.0f, 0.5f);
				Color endLineColor = startLineColor;

				// Draw the line ALWAYS black. We'll color on top of it in the next step if
				// something is expanded.
				GuiDrawUtilities.drawLine(pose, expandedPosition, preReqPosition, startLineColor, endLineColor, 4.0f);

				// If no node is expanded, draw the connecting lines behind everything.
				// If a node is expanded, only draw lines for that node and push them over
				// everything.
				if (hoveredNode != null && outerNode == hoveredNode) {
					preReqPosition.add(0, 0, 500);
					float timeHovered = SDMath.clamp((this.hoveredNode.getTicksHovered() - (index * 2)) / 1f, 0, 1);

					if (!getResearchManager().hasCompletedResearch(node.getResearch().getId())) {
						startLineColor = new Color(0.8f, 0.15f, 0.15f, 1.0f);
						endLineColor = new Color(0.8f, 0.15f, 0.15f, 1.0f);
					} else {
						startLineColor = new Color(0.0f, 0.9f, 0.2f, 1.0f);
						endLineColor = new Color(0.0f, 0.9f, 0.2f, 1.0f);
					}

					startLineColor = startLineColor.multiply(timeHovered);
					timeHovered = SDMath.clamp((this.hoveredNode.getTicksHovered() - (index * 3)) / 4, 0, 1);
					endLineColor = endLineColor.multiply(timeHovered);

					GuiDrawUtilities.drawLine(pose, expandedPosition, preReqPosition, startLineColor, endLineColor, 8.0f);
				}
			}
		}

		pose.popPose();

		// Reset the clip mask.
		RenderingUtilities.clearScissorMask();
	}

	protected boolean shouldExpandResearch(Research research) {
		return getResearchManager().isResearchAvailable(research.getId()) || getResearchManager().hasCompletedResearch(research.getId());
	}

	protected ResearchManager getResearchManager() {
		return getLocalTeam().getResearchManager();
	}

	protected Team getLocalTeam() {
		return TeamManager.getLocalTeam();
	}
}
