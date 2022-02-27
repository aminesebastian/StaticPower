package theking530.staticpower.teams.research;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchIcon;
import theking530.staticpower.data.research.ResearchLevels.ResearchNode;
import theking530.staticpower.data.research.ResearchUnlock;
import theking530.staticpower.data.research.ResearchUnlock.ResearchUnlockType;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;
import theking530.staticpower.teams.research.network.PacketSetSelectedResearch;

public class ResearchNodeWidget extends AbstractGuiWidget<ResearchNodeWidget> {
	private final String title;
	private final List<String> wrappedDescription;
	private final Research research;
	private final ResearchNode node;
	private final SimpleProgressBar progressBar;
	private final Vector2D collapsedSize;
	private final Vector2D maxExpandedSize;
	private Color tileColor;
	private Color bodyColor;
	private Team team;
	private ResearchManager manager;
	private boolean expand;
	private float expandedAlpha;

	public ResearchNodeWidget(ResearchNode node, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.research = node.getResearch();
		this.node = node;
		registerWidget(progressBar = new SimpleProgressBar(28, 20, 86, 7).setMaxProgress(100).disableProgressTooltip());
		progressBar.setVisible(false);
		title = new TranslatableComponent(research.getTitle()).getString();
		this.collapsedSize = new Vector2D(width, height);
		this.maxExpandedSize = new Vector2D(Math.min(getFontRenderer().width(title) + 15, 100), 100);
		this.tileColor = new Color(1, 1, 1, 1);
		this.bodyColor = new Color(1, 1, 1, 1);
		wrappedDescription = GuiDrawUtilities.wrapString(research.getDescription(), maxExpandedSize.getXi() * 2 - 32);
	}

	public void updateBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		super.updateBeforeRender(matrixStack, parentSize, partialTicks, mouseX, mouseY);

		// Update the team and manager.
		team = TeamManager.getLocalTeam();
		manager = team.getResearchManager();

		// Drive the hovered alpha.
		if (isExpanded()) {
			setZLevel(150);
			expandedAlpha = (float) Math.min(1, expandedAlpha + (partialTicks * 0.45));
		} else {
			setZLevel(1);
			expandedAlpha = (float) Math.max(0, expandedAlpha - (partialTicks * 0.45));
		}

		// Update the colors.
		updatePanelColors();

		// Scale the widget depending on if it is expanded.
		float maxWidth = (getFontRenderer().width(title) * .85f) * getExpandedAlpha();
		float maxHeight = 15 + (wrappedDescription.size() * 5) + (research.getUnlocks().size() > 0 ? 15 : 0);
		setSize(Math.min(maxExpandedSize.getX(), collapsedSize.getX() + (maxWidth * getExpandedAlpha())), collapsedSize.getY() + (maxHeight * getExpandedAlpha()));

		// Move the widget up if hovered and to the up and left if going off screen.
		Vector2D screenSpaceInitial = GuiDrawUtilities.translatePositionByMatrix(matrixStack, getInitialPosition());

		float parentMaxX = getParent().getOwningWidget().getScreenSpacePosition().getX() + getParent().getOwningWidget().getSize().getX();
		float thisMaxX = screenSpaceInitial.getX() + getSize().getX();
		float maxOffsetX = getExpandedAlpha() * Math.max(thisMaxX - parentMaxX + 4, 0);

		float maxOffsetY = (getExpandedAlpha() * (getSize().getY() / 2 - collapsedSize.getY() / 2));
		setPosition(getInitialPosition().getX() - maxOffsetX, getInitialPosition().getY() - maxOffsetY);
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
			GuiDrawUtilities.drawGenericBackground(pose, collapsedSize.getX() + 4, collapsedSize.getY() + 4, -2, -2, 0, new Color(2.0f, 1.0f, 0.5f, 1));
		} else if (manager.hasCompletedResearch(research.getId())) {
			GuiDrawUtilities.drawGenericBackground(pose, collapsedSize.getX() + 6, collapsedSize.getY() + 6, -3, -3, 0, new Color(0.5f, 2.0f, 0.5f, 1));
		}

		// Draw the tile and its icon.
		GuiDrawUtilities.drawGenericBackground(pose, collapsedSize.getX(), collapsedSize.getY(), 0, 0, 0, tileColor);
		GuiDrawUtilities.drawItem(pose, research.getIcon().getItemIcon(), 4, 4, 99 + getExpandedAlpha() * 100, 1.0f);

		if (expand) {
			// Draw the title.
			GuiDrawUtilities.drawStringLeftAligned(pose, title, 29, 16, 1.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

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

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getTooltips(mousePosition, tooltips, showAdvanced);
	}

	public void setExpanded(boolean expanded) {
		playSoundLocally(SoundEvents.BOOK_PAGE_TURN, 0.75f, expanded ? 2.0f : 1.5f);
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

	public EInputResult mouseClick(double mouseX, double mouseY, int button) {
		if (isHovered() && button == 0) {
			boolean isAvailable = manager.isResearchAvailable(research.getId());
			if (isAvailable) {
				// Set the selected research on both the client AND the server.
				manager.setSelectedResearch(research.getId());
				StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketSetSelectedResearch(team.getId(), research.getId()));
				playSoundLocally(SoundEvents.BOOK_PAGE_TURN, 2.0f, 1.5f);
				playSoundLocally(SoundEvents.UI_BUTTON_CLICK, 0.15f, 1.5f);
			} else if (manager.hasCompletedResearch(research.getId())) {
				playSoundLocally(SoundEvents.BREWING_STAND_BREW, 0.5f, 2.0f);
			} else {
				playSoundLocally(SoundEvents.VILLAGER_NO, 1.0f, 2.0f);
			}
			playSoundLocally(SoundEvents.BOOK_PUT, 1.0f, 2.0f);
			return EInputResult.HANDLED;
		}

		return super.mouseClick(mouseX, mouseY, button);
	}

	private void drawRearchRequirement(PoseStack pose, @Nullable ResearchInstance instance, StaticPowerIngredient requirement, int requirementIndex, float x, float y) {
		GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], x, y, 1, 8f, 8f);

		if (instance != null) {
			GuiDrawUtilities.drawStringCentered(pose, GuiTextUtilities.formatNumberAsString(requirement.getCount() - instance.getRequirementFullfillment(requirementIndex)).getString(), x + 8f,
					y + 6.5f, 10, 0.5f, Color.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringCentered(pose, GuiTextUtilities.formatNumberAsString(requirement.getCount()).getString(), x + 8f, y + 6.5f, 10, 0.5f, Color.EIGHT_BIT_WHITE, true);
		}
	}

	private void updateProgressBar(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (team != null) {
			progressBar.setSize(getSize().getX() - 32, 7);
			if (manager.isResearching(research.getId())) {
				progressBar.setCurrentProgress((int) (manager.getResearchProgress(research.getId()).getFullfillmentPercentage() * 100));
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
		GuiDrawUtilities.drawRectangle(pose, requirementsBgSize, 11, getSize().getX() - requirementsBgSize - 1, getSize().getY() - 11, 0, new Color(0.0f, 0.0f, 0.0f, 0.5f));

		for (int i = 0; i < research.getRequirements().size(); i++) {
			int xOffset = i * 10;
			StaticPowerIngredient requirement = research.getRequirements().get(i);
			drawRearchRequirement(pose, null, requirement, i, getSize().getX() - 14 - xOffset, getSize().getY() - 12);
		}

		// Split the description into wrapped lines.
		float descriptionHeight = wrappedDescription.size() * 5;
		// Draw the description.
		for (int i = 0; i < wrappedDescription.size(); i++) {
			GuiDrawUtilities.drawStringLeftAligned(pose, wrappedDescription.get(i), 9, 33 + (i * 5), 0f, 0.5f, Color.EIGHT_BIT_WHITE, true);
		}

		// Draw the unlocks.
		if (research.getUnlocks().size() > 0) {
			GuiDrawUtilities.drawStringLeftAligned(pose, "Unlocks:", 9, 37 + descriptionHeight, 0f, 0.5f, Color.EIGHT_BIT_WHITE, true);
			for (int i = 0; i < research.getUnlocks().size(); i++) {
				ResearchUnlock unlock = research.getUnlocks().get(i);
				if (unlock.getType() == ResearchUnlockType.CRAFTING && unlock.getIcon() != null) {
					ResearchIcon.draw(unlock.getIcon(), pose, 6.5f + (i * 11), 36 + descriptionHeight, 155, 11f, 11f);
					Recipe<?> recipe = unlock.getAsRecipe();
					if (recipe != null) {

					}
				}
			}
		}
	}

	private void updatePanelColors() {
		boolean isAvailable = manager.isResearchAvailable(research.getId());
		boolean isCompleted = manager.hasCompletedResearch(research.getId());

		// Get the tile color and lighten it on hover.
		tileColor = research.getColor().copy();
		if (!isAvailable && !manager.hasCompletedResearch(research.getId())) {
			tileColor = new Color(0.3f, 0.3f, 0.3f, 0.95f);
		}

		if (isHovered()) {
			tileColor.add(0.1f, 0.1f, 0.1f);
		}

		// Get the color for the body of the node.
		bodyColor = new Color(0.75f, 0.75f, 0.75f, 0.95f);

		if (isHovered()) {
			if (isAvailable || isCompleted) {
				bodyColor = new Color(0.75f, 0.75f, 1.0f, 0.95f);
			} else {
				bodyColor = new Color(0.35f, 0.35f, 0.35f, 0.85f);
			}
		} else if (manager.isResearching(research.getId()) || manager.isSelectedResearch(research.getId())) {
			bodyColor = new Color(0.0f, 1.0f, 1.0f, 0.95f);
		} else {
			if (isAvailable) {
				bodyColor = new Color(0.75f, 0.75f, 0.75f, 0.95f);
			} else {
				bodyColor = new Color(0.35f, 0.35f, 0.35f, 0.85f);
			}
		}
	}

	private float getExpandedAlpha() {
		return expandedAlpha;
	}
}
