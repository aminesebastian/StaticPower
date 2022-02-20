package theking530.staticpower.teams.research;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;
import theking530.staticpower.teams.research.network.PacketSetSelectedResearch;

public class ResearchNodeWidget extends AbstractGuiWidget<ResearchNodeWidget> {
	private final String title;
	private final Research research;
	private final SimpleProgressBar progressBar;
	private final Vector2D collapsedSize;
	private float hoveredAlpha;
	private Team team;
	private ResearchManager manager;
	private boolean expand;

	public ResearchNodeWidget(Research research, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		registerWidget(progressBar = new SimpleProgressBar(28, 20, 86, 7).setMaxProgress(100));
		progressBar.setVisible(false);
		title = new TranslatableComponent(research.getTitle()).getString();
		this.research = research;
		this.collapsedSize = new Vector2D(width, height);
	}

	public void updateBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		super.updateBeforeRender(matrixStack, parentSize, partialTicks, mouseX, mouseY);

		// Update the team and manager.
		team = TeamManager.getLocalTeam();
		manager = team.getResearchManager();

		// Drive the hovered alpha.
		if (isExpanded()) {
			hoveredAlpha = Math.min(1, hoveredAlpha + partialTicks * 0.45f);
			setZLevel(100);

		} else {
			hoveredAlpha = Math.max(0, hoveredAlpha - partialTicks * 0.45f);
			setZLevel(1);
		}

		// Scale the widget depending on if it is expanded.
		List<String> description = GuiDrawUtilities.wrapString(research.getDescription(), getSize().getXi() + 10);
		float maxWidth = (getFontRenderer().width(title) * .85f) * hoveredAlpha;
		float maxHeight = 15 + (description.size() * 5);
		setSize(collapsedSize.getX() + (maxWidth * hoveredAlpha), collapsedSize.getY() + (maxHeight * hoveredAlpha));

		// Move the widget up if hovered and to the left if going off screen.
		float parentMaxX = getParent().getOwningWidget().getScreenSpacePosition().getX() + getParent().getOwningWidget().getSize().getX();
		Vector2D screenSpaceInitial = GuiDrawUtilities.translatePositionByMatrix(matrixStack, getInitialPosition());
		float thisMaxX = screenSpaceInitial.getX() + getSize().getX();
		float maxOffset = Math.max(thisMaxX - parentMaxX + 4, 0);
		setPosition(getInitialPosition().getX() - maxOffset, getInitialPosition().getY() - (hoveredAlpha * getSize().getY() / 4));
	}

	public Research getResearch() {
		return research;
	}

	@Override
	public void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		boolean isAvailable = manager.isResearchAvailable(research.getId());
		boolean isCompleted = manager.hasCompletedResearch(research.getId());

		// Get the tile color and lighten it on hover.
		Color tileColor = new Color(0.4f, 0.4f, 0.4f, 0.9f);
		if (isAvailable || manager.hasCompletedResearch(research.getId())) {
			tileColor = research.getColor().copy();
		}
		if (isHovered()) {
			tileColor.add(0.1f, 0.1f, 0.1f);
		}

		// Get the color for the body of the node.
		Color bodyColor = new Color(0.75f, 0.75f, 0.75f, 0.95f);

		if (isHovered()) {
			if (isAvailable || isCompleted) {
				bodyColor = new Color(0.75f, 0.75f, 1.0f, 0.95f);
			} else {
				bodyColor = new Color(0.35f, 0.35f, 0.35f, 0.5f);
			}
		} else if (manager.isResearching(research.getId()) || manager.isSelectedResearch(research.getId())) {
			bodyColor = new Color(0.0f, 1.0f, 1.0f, 0.95f);
		} else {
			if (isAvailable) {
				bodyColor = new Color(0.75f, 0.75f, 0.75f, 0.95f);
			} else {
				bodyColor = new Color(0.35f, 0.35f, 0.35f, 0.5f);
			}
		}

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
		GuiDrawUtilities.drawItem(pose, research.getItemIcon(), 4, 4, 99 + hoveredAlpha * 100, 1.0f);

		if (expand) {
			// Draw the title.
			GuiDrawUtilities.drawStringLeftAligned(pose, title, 28, 16, 1.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

			// Update the bar.
			progressBar.setVisible(true);
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

			// Only draw the requirements and description when the node is fully expanded.
			if (hoveredAlpha >= 1) {
				// Draw requirements.
				float requirementsBgSize = research.getRequirements().size() + research.getRequirements().size() * 9.25f;
				GuiDrawUtilities.drawRectangle(pose, requirementsBgSize, 9, getSize().getX() - requirementsBgSize - 1, getSize().getY() - 9, 0, new Color(0.0f, 0.0f, 0.0f, 0.5f));

				for (int i = 0; i < research.getRequirements().size(); i++) {
					int xOffset = i * 10;
					StaticPowerIngredient requirement = research.getRequirements().get(i);
					drawRearchRequirement(pose, null, requirement, i, getSize().getX() - 14 - xOffset, getSize().getY() - 12);
				}

				// Split the description into wrapped lines.
				List<String> lines = GuiDrawUtilities.wrapString(research.getDescription(), getSize().getXi() + 10);

				// Draw the description.
				for (int i = 0; i < lines.size(); i++) {
					GuiDrawUtilities.drawStringLeftAligned(pose, lines.get(i), 9, 33 + (i * 5), 0f, 0.5f, Color.EIGHT_BIT_WHITE, true);
				}
			}
		} else {
			progressBar.setVisible(false);
		}
	}

	public void drawRearchRequirement(PoseStack pose, @Nullable ResearchInstance instance, StaticPowerIngredient requirement, int requirementIndex, float x, float y) {
		GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], x, y, hoveredAlpha * 100, 0.5f, 0.5f, 1.0f);

		if (instance != null) {
			GuiDrawUtilities.drawStringCentered(pose, Integer.toString(requirement.getCount() - instance.getRequirementFullfillment(requirementIndex)), x + 7.5f, y + 11.5f, 1, 0.5f,
					Color.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringCentered(pose, Integer.toString(requirement.getCount()), x + 7.5f, y + 11.5f, 1, 0.5f, Color.EIGHT_BIT_WHITE, true);
		}
	}

	@Override
	public void renderWidgetForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
	}

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {

	}

	public void setExpanded(boolean expanded) {
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
	public EInputResult mouseMove(int mouseX, int mouseY) {
		if (isHovered()) {
			return EInputResult.HANDLED;
		}
		return super.mouseMove(mouseX, mouseY);
	}

	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		if (isHovered()) {
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
			return EInputResult.HANDLED;
		}

		return super.mouseClick(mouseX, mouseY, button);
	}
}
