package theking530.staticcore.research.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.research.Research;
import theking530.staticcore.research.ResearchLevels.ResearchNode;
import theking530.staticcore.research.ResearchUnlock;
import theking530.staticcore.research.ResearchUnlockUtilities;
import theking530.staticcore.research.network.PacketSetSelectedResearch;
import theking530.staticcore.teams.ClientTeam;
import theking530.staticcore.teams.TeamManager;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticcore.utilities.math.Vector3D;

public class ResearchNodeWidget extends AbstractGuiWidget<ResearchNodeWidget> {
	private final String title;
	private final List<String> wrappedDescription;
	private final Research research;
	private final List<ResearchNodeUnlockWidget> unlockWidgets;
	private final List<ResearchRequirementWidget> requirementWidgets;
	private final ResearchNode node;
	private final SimpleProgressBar progressBar;
	private final Vector2D collapsedSize;
	private final Vector2D maxExpandedSize;
	private SDColor tileColor;
	private SDColor bodyColor;
	private ClientTeam team;
	private ResearchManager manager;
	private boolean expand;
	private float expandedAlpha;

	public ResearchNodeWidget(ResearchNode node, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.research = node.getResearch();
		this.unlockWidgets = new ArrayList<>();
		this.requirementWidgets = new ArrayList<>();
		this.node = node;
		registerWidget(progressBar = new SimpleProgressBar(28, 20, 86, 7).setMaxProgress(100).disableProgressTooltip());
		progressBar.setVisible(false);
		title = Component.translatable(research.getTitle()).getString();
		this.collapsedSize = new Vector2D(width, height);
		int minWidth = 100;
		this.maxExpandedSize = new Vector2D(Math.max(getFontRenderer().width(title), minWidth) + 10, 100);
		this.tileColor = new SDColor(1, 1, 1, 1);
		this.bodyColor = new SDColor(1, 1, 1, 1);
		wrappedDescription = GuiDrawUtilities.wrapString(Component.translatable(research.getDescription()).getString(),
				maxExpandedSize.getXi() * 2 - 50);

		List<ResearchUnlock> unlocks = ResearchUnlockUtilities.getCollapsedUnlocks(research);
		if (unlocks.size() > 0) {
			for (int i = 0; i < unlocks.size(); i++) {
				ResearchUnlock unlock = unlocks.get(i);
				if (unlock.getIcon() != null) {
					ResearchNodeUnlockWidget widget = new ResearchNodeUnlockWidget(unlock, 0, 0, 11, 11);
					registerWidget(widget);
					unlockWidgets.add(widget);
				}
			}
		}

		for (StaticPowerIngredient requirement : research.getRequirements()) {
			ResearchRequirementWidget widget = new ResearchRequirementWidget(requirement, null, 0, 0, 11, 11);
			registerWidget(widget);
			requirementWidgets.add(widget);
		}
	}

	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX,
			int mouseY) {

		// Update the team and manager.
		team = TeamManager.getLocalTeam();
		manager = team.getResearchManager();

		// Drive the hovered alpha.
		if (isExpanded()) {
			setZLevel(150);
			expandedAlpha = (float) Math.min(1, expandedAlpha + (partialTicks * 0.75));
		} else {
			setZLevel(1);
			expandedAlpha = (float) Math.max(0, expandedAlpha - (partialTicks * 0.75));
		}

		for (ResearchNodeUnlockWidget unlock : unlockWidgets) {
			unlock.setVisible(expandedAlpha >= 1.0f);
		}

		for (ResearchRequirementWidget requirement : requirementWidgets) {
			requirement.setVisible(expandedAlpha >= 1.0f);
		}

		// Update the colors.
		updatePanelColors();

		// Scale the widget depending on if it is expanded.
		float maxHeight = 15 + (wrappedDescription.size() * 5.5f) + (research.getUnlocks().size() > 0 ? 15 : 0);
		setSize(Math.min(maxExpandedSize.getX(), collapsedSize.getX() + (maxExpandedSize.getX() * getExpandedAlpha())),
				collapsedSize.getY() + (maxHeight * getExpandedAlpha()));

		// Move the widget up if hovered and to the up and left if going off screen.
		float maxOffsetY = (getExpandedAlpha() * (getSize().getY() / 2.5f - collapsedSize.getY() / 2));
		setPosition(getInitialPosition().getX(), getInitialPosition().getY() - maxOffsetY);

	}

	@Override
	protected void updateBounds(RectangleBounds bounds, Vector2D screenSpacePosition, Vector3D scale) {
		float expandedScale = expandedAlpha * 4;
		bounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(),
				getSize().getX() * scale.getX() + expandedScale, getSize().getY() * scale.getY() + expandedScale);
	}

	public Research getResearch() {
		return research;
	}

	public ResearchNode getNode() {
		return node;
	}

	@Override
	public void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Draw the body layer.
		GuiDrawUtilities.drawGenericBackground(pose, getSize().getX(), getSize().getY(), 4, 4, 0, bodyColor);

		// Draw the outline indicator.
		if (manager.isSelectedResearch(research.getId())) {
			GuiDrawUtilities.drawGenericBackground(pose, collapsedSize.getX() + 6, collapsedSize.getY() + 6, -3, -3, 0,
					new SDColor(2.0f, 1.0f, 0.5f, 1));
		} else if (manager.hasCompletedResearch(research.getId())) {
			GuiDrawUtilities.drawGenericBackground(pose, collapsedSize.getX() + 4, collapsedSize.getY() + 4, -2, -2, 0,
					new SDColor(0.5f, 2.0f, 0.5f, 1));
		}

		// Draw the tile and its icon.
		GuiDrawUtilities.drawGenericBackground(pose, collapsedSize.getX(), collapsedSize.getY(), 0, 0, 0, tileColor);

		// Draw an item silhouette if the research is not available.
		if (manager.hasCompletedResearch(research.getId()) || manager.isResearchAvailable(research.getId())) {
			GuiDrawUtilities.drawItem(pose, research.getIcon().getItemIcon(), 4, 4, 99 + getExpandedAlpha() * 100);
		} else {
			GuiDrawUtilities.drawItemSilhouette(pose, research.getIcon().getItemIcon(), 4, 4,
					99 + getExpandedAlpha() * 100);
		}

		if (expand) {
			// Draw the title.
			GuiDrawUtilities.drawStringLeftAligned(pose, title, 29, 16, 1.0f, 0.75f, SDColor.EIGHT_BIT_WHITE, true);

			// Update the bar.
			progressBar.setVisible(true);
			updateProgressBar(pose, mouseX, mouseY, partialTicks);

			// Only draw the requirements and description when the node is fully expanded.
			if (getExpandedAlpha() >= 1) {
				drawRequirementsAndUnlocks(pose, mouseX, mouseY, partialTicks);
			}
		} else {
			progressBar.setVisible(false);
		}
	}

	@Override
	public void renderWidgetForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
	}

	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getWidgetTooltips(mousePosition, tooltips, showAdvanced);
	}

	public void setExpanded(boolean expanded) {
		if (!this.expand && expanded) {
			playSoundLocally(SoundEvents.BOOK_PAGE_TURN, 0.75f, expanded ? 2.0f : 1.5f);
		}
		this.expand = expanded;
	}

	public boolean isExpanded() {
		return expand;
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		return team != null && manager != null;
	}

	@Override
	public EInputResult mouseMove(double mouseX, double mouseY) {
		if (isHovered()) {
			return EInputResult.HANDLED;
		}
		return super.mouseMove(mouseX, mouseY);
	}

	@Override
	public EInputResult mouseReleased(double mouseX, double mouseY, int button) {
		if (isHovered() && (button <= 1)) {
			if (button == 0) {
				boolean isAvailable = manager.isResearchAvailable(research.getId());
				if (isAvailable) {
					// Set the selected research on both the client AND the server.
					manager.setSelectedResearch(research.getId());
					StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
							new PacketSetSelectedResearch(team.getId(), research.getId()));
					playSoundLocally(SoundEvents.UI_BUTTON_CLICK, 0.1f, 1.5f);
				} else if (manager.hasCompletedResearch(research.getId())) {
					playSoundLocally(SoundEvents.BREWING_STAND_BREW, 0.5f, 2.0f);
				} else {
					playSoundLocally(SoundEvents.VILLAGER_NO, 1.0f, 2.0f);
				}
			} else if (button == 1) {
				// The only research we CAN'T clear is the first research. This is to ensure
				// there is always either a selected OR last completed research to display in
				// the UI.
				if (!getResearch().getId().equals(ResearchManager.getInitialResearch())) {
					manager.clearSelectedResearch();
					playSoundLocally(SoundEvents.STONE_BUTTON_CLICK_OFF, 1.0f, 2.0f);
				}
			}

			playSoundLocally(SoundEvents.BOOK_PUT, 1.0f, 2.0f);
			return EInputResult.HANDLED;
		}

		return super.mouseClick(mouseX, mouseY, button);
	}

	private void updateProgressBar(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (team != null) {
			progressBar.setSize(getSize().getX() - 32, 7);
			if (manager.isResearching(research.getId())) {
				progressBar.setCurrentProgress(
						(int) (manager.getResearchProgress(research.getId()).getFullfillmentPercentage() * 100));
			} else if (manager.hasCompletedResearch(research.getId())) {
				progressBar.setCurrentProgress(100);
			} else {
				progressBar.setCurrentProgress(0);
			}
		}
	}

	private void drawRequirementsAndUnlocks(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Draw requirements.
		float requirementsBgSize = research.getRequirements().size() + research.getRequirements().size() * 9.25f;
		GuiDrawUtilities.drawRectangle(pose, requirementsBgSize, 11, getSize().getX() - requirementsBgSize - 1,
				getSize().getY() - 12, 0, new SDColor(0.0f, 0.0f, 0.0f, 0.5f));

		for (int i = 0; i < this.requirementWidgets.size(); i++) {
			int xOffset = i * 10;
			ResearchRequirementWidget requirementWidget = requirementWidgets.get(i);
			requirementWidget.setPosition(getSize().getX() - 14 - xOffset, getSize().getY() - 13.5f);
			requirementWidget.setZLevel(155);
		}

		// Split the description into wrapped lines.
		float descriptionHeight = wrappedDescription.size() * 5;
		// Draw the description.
		for (int i = 0; i < wrappedDescription.size(); i++) {
			GuiDrawUtilities.drawStringLeftAligned(pose, wrappedDescription.get(i), 9, 33 + (i * 5), 0f, 0.5f,
					SDColor.EIGHT_BIT_WHITE, true);
		}

		// Draw the unlocks.
		List<ResearchUnlock> unlocks = ResearchUnlockUtilities.getCollapsedUnlocks(research);
		if (unlocks.size() > 0) {
			GuiDrawUtilities.drawStringLeftAligned(pose, "Unlocks:", 9, 40 + descriptionHeight, 0f, 0.5f,
					SDColor.EIGHT_BIT_WHITE, true);
			for (int i = 0; i < unlockWidgets.size(); i++) {
				ResearchNodeUnlockWidget unlock = unlockWidgets.get(i);
				unlock.setPosition(6.5f + (i * 11), 40 + descriptionHeight);
				unlock.setZLevel(155);
			}
		}
	}

	private void updatePanelColors() {
		boolean isAvailable = manager.isResearchAvailable(research.getId());
		boolean isCompleted = manager.hasCompletedResearch(research.getId());

		// Get the tile color and lighten it on hover.
		tileColor = research.getColor().copy();
		if (!isAvailable && !manager.hasCompletedResearch(research.getId())) {
			tileColor = new SDColor(0.075f, 0.075f, 0.075f);
		}

		// Get the color for the body of the node.
		bodyColor = new SDColor(0.75f, 0.75f, 0.75f, 0.95f);

		if (isHovered()) {
			if (isAvailable || isCompleted) {
				bodyColor = new SDColor(0.75f, 0.75f, 1.0f, 0.95f);
			} else {
				bodyColor = new SDColor(0.35f, 0.35f, 0.35f, 0.85f);
			}
		} else if (manager.isResearching(research.getId())) {
			bodyColor = new SDColor(0.1f, 0.6f, 1.0f, 0.95f);
		} else {
			if (isAvailable) {
				bodyColor = new SDColor(0.75f, 0.75f, 0.75f, 0.95f);
			} else {
				bodyColor = new SDColor(0.35f, 0.35f, 0.35f, 0.85f);
			}
		}

		if (isHovered()) {
			tileColor.add(0.085f, 0.085f, 0.085f);
		}
	}

	private float getExpandedAlpha() {
		return expandedAlpha;
	}
}
