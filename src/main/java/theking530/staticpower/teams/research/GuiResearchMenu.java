package theking530.staticpower.teams.research;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.containers.HorizontalBox;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchLevels;
import theking530.staticpower.data.research.ResearchLevels.ResearchLevel;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;

@SuppressWarnings("resource")
public class GuiResearchMenu extends StaticPowerDetatchedGui {
	protected static final int TIER_LEVEL_HEIGHT = 80;
	protected Team team;
	protected ResearchInstance currentResearch;
	protected ScrollBox nodeScrollBox;
	protected List<HorizontalBox> tierBoxes;
	protected List<ResearchNodeWidget> researchNodes;
	protected ResearchNodeWidget expandedNode;
	protected SelectedResearchWidget selectedResearchWidget;

	public GuiResearchMenu() {
		super(400, 400);
		setDrawDefaultDarkBackground(false);
	}

	@Override
	public void initializeGui() {
		registerWidget(nodeScrollBox = new ScrollBox(105, 20, 10000, 0));
		registerWidget(selectedResearchWidget = new SelectedResearchWidget(0, 0, 109, 76));

		researchNodes = new ArrayList<ResearchNodeWidget>();
		tierBoxes = new ArrayList<HorizontalBox>();

		ResearchLevels levels = ResearchLevels.getAllResearchLevels();
		for (int y = 0; y < levels.getLevels().size(); y++) {
			float tint = y % 2 == 0 ? 0.1f : 0.0f;
			HorizontalBox box = new HorizontalBox(0, y * TIER_LEVEL_HEIGHT, 10000, TIER_LEVEL_HEIGHT).setBackgroundColor(new Color(tint, tint, tint, 0.75f)).setDrawBackground(true);
			ResearchLevel level = levels.getLevels().get(y);

			for (Research research : level.getResearch()) {
				if (!research.isHiddenUntilAvailable()) {
					ResearchNodeWidget widget = new ResearchNodeWidget(research, 0, TIER_LEVEL_HEIGHT / 4, 24, 24);
					box.registerWidget(widget);
					researchNodes.add(widget);
				}
			}
			nodeScrollBox.registerWidget(box);
			tierBoxes.add(box);
		}
	}

	@Override
	public void tick() {
		super.tick();
		Team team = TeamManager.getLocalTeam();
		if (team != null) {
			currentResearch = team.getResearchManager().getSelectedResearch();
			if (currentResearch != null) {
				selectedResearchWidget.setResearch(currentResearch);
				selectedResearchWidget.setVisible(true);
			} else {
				selectedResearchWidget.setVisible(false);
			}
		}

		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		for (HorizontalBox box : tierBoxes) {
			box.setSize(screenWidth - 105, TIER_LEVEL_HEIGHT);
		}
		nodeScrollBox.setSize(nodeScrollBox.getSize().getX(), screenHeight);

		if (expandedNode != null) {
			if (!expandedNode.isHovered()) {
				expandedNode.setExpanded(false);
				expandedNode = null;
			}
		} else {
			for (ResearchNodeWidget node : researchNodes) {
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
		RenderSystem.enableBlend();

		// Draw the sidebar and background bar.
		GuiDrawUtilities.drawRectangle(pose, getMinecraft().screen.width, getMinecraft().screen.height, 0, 0, -0.1f, new Color(0, 0, 0, 0.5f));
		GuiDrawUtilities.drawGenericBackground(pose, 110, getMinecraft().screen.height + 8, -5, -4, 0.0f, new Color(0.75f, 0.5f, 1.0f, 0.85f));
		GuiDrawUtilities.drawGenericBackground(pose, getMinecraft().screen.width, 28, 0, -4, 100.0f, new Color(0.75f, 0.5f, 1.0f, 0.85f));
		GuiDrawUtilities.drawGenericBackground(pose, 113, 80, -4, -4, 0.0f, new Color(0.25f, 0.5f, 1.0f, 1.0f));

		drawConnectingLines(pose, partialTicks, mouseX, mouseY);
	}

	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
	}

	protected void drawConnectingLines(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		for (ResearchNodeWidget outerNode : this.researchNodes) {
			List<ResearchNodeWidget> preReqWidgets = new ArrayList<ResearchNodeWidget>();
			for (ResearchNodeWidget node : this.researchNodes) {
				if (outerNode.getResearch().getPrerequisites().contains(node.getResearch().getId())) {
					preReqWidgets.add(node);
				}
			}

			int index = 0;
			for (ResearchNodeWidget node : preReqWidgets) {
				index++;
				pose.pushPose();
				Vector3D expandedPosition = outerNode.getScreenSpacePosition().promote();
				expandedPosition.add(11, 1, -7);
				Vector3D preReqPosition = node.getScreenSpacePosition().promote();
				preReqPosition.add(11, 20f, -7);

				Color lineColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);

				// If no node is expanded, draw the connecting lines behind everything.
				// If a node is expanded, only draw lines for that node and push them over
				// everything.
				if (this.expandedNode != null) {
					if (outerNode == expandedNode) {
						preReqPosition.add(0, 0, 500);
						float timeHovered = SDMath.clamp((this.expandedNode.getTicksHovered() - (index * 2)) / 10, 0, 1);
						lineColor = new Color(timeHovered, timeHovered, timeHovered, 1);
					}
				}

				GuiDrawUtilities.drawLine(pose, expandedPosition, preReqPosition, lineColor, 4.0f);
				pose.popPose();
			}
		}
	}
}
