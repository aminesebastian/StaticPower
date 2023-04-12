package theking530.staticcore.research.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.init.StaticCoreKeyBindings;
import theking530.staticcore.research.Research;
import theking530.staticcore.research.gui.ResearchManager.ResearchInstance;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.utilities.SDColor;

public class ActiveResearchWidget extends AbstractGuiWidget<ActiveResearchWidget> {
	private final SimpleProgressBar progressBar;
	private boolean drawBackground;
	private SDColor backgroundTint;
	private ITeam team;
	private Research research;
	private ResearchInstance currentProgress;

	public ActiveResearchWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7));
		progressBar.setMaxProgress(100);
		drawBackground = true;
		backgroundTint = new SDColor(1.0f, 0.8f, 1.2f, 0.07f);
	}

	public Research getResearch() {
		return research;
	}

	public ActiveResearchWidget setResearch(Research research) {
		this.research = research;
		return this;
	}

	public ActiveResearchWidget setResearch(ResearchInstance currentProgress) {
		this.research = currentProgress.getTrackedResearch();
		this.currentProgress = currentProgress;
		return this;
	}

	public ActiveResearchWidget setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	public SDColor getBackgroundTint() {
		return backgroundTint;
	}

	public ActiveResearchWidget setBackgroundTint(SDColor backgroundTint) {
		this.backgroundTint = backgroundTint;
		return this;
	}

	public ITeam getTeam() {
		return team;
	}

	public void setTeam(ITeam team) {
		this.team = team;
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (team != null && research != null) {
			int requirementCount = research.getRequirements().size();
			int width = Math.max(115, requirementCount * 21);
			int height = team.getResearchManager().hasSelectedResearch() ? 45 : 34;
			if (team.getResearchManager().hasSelectedResearch() && research.getRequirements().size() > 4) {
				height += 5;
			}
			this.setSize(width, height);

			// Draw background.
			if (drawBackground) {
				GuiDrawUtilities.drawGenericBackground(pose, width, height, 0, 0, 0, new SDColor(1.0f, 0.8f, 1.2f, 0.9f));
			}

			// Draw icon.
			GuiDrawUtilities.drawItem(pose, research.getIcon().getItemIcon(), 4, 4, 10, 1.0f);
			GuiDrawUtilities.drawStringLeftAligned(pose, Component.translatable(research.getTitle()).getString(), 23, 13, 0.0f, 0.75f, SDColor.EIGHT_BIT_WHITE, true);

			// Draw progress bar.
			progressBar.setPosition(20, 16);
			progressBar.setSize(width * 0.75f, 7);
			if (team.getResearchManager().hasSelectedResearch()) {
				progressBar.setCurrentProgress((int) (currentProgress.getFullfillmentPercentage() * 100));
			} else {
				progressBar.setCurrentProgress(100);
			}

			// Draw requirements.
			if (team.getResearchManager().hasSelectedResearch() && !team.getResearchManager().getSelectedResearch().isCompleted()) {
				for (int i = 0; i < research.getRequirements().size(); i++) {
					int xOffset = i * 14;
					StaticPowerIngredient requirement = research.getRequirements().get(i);
					GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 24 - xOffset, 23, 0, 12f, 12f);

					GuiDrawUtilities.drawString(pose, Integer.toString(requirement.getCount() - currentProgress.getRequirementFullfillment(i)), getSize().getX() - 13f - xOffset, 31, 1, 0.5f,
							SDColor.EIGHT_BIT_YELLOW, true);
				}
			}

			// Draw the tooltip.
			String openTooltip;
			if (team.getResearchManager().hasSelectedResearch()) {
				openTooltip = Component.translatable("gui.staticcore.research_menu_key_tooltip", StaticCoreKeyBindings.OPEN_RESEARCH.getMapping().getKey().getDisplayName().getString().toUpperCase())
						.getString();
			} else {
				openTooltip = Component.translatable("gui.staticcore.research_menu_no_selected_research_key_tooltip", StaticCoreKeyBindings.OPEN_RESEARCH.getMapping().getKey().getDisplayName().getString().toUpperCase())
						.getString();
			}

			GuiDrawUtilities.drawStringLeftAligned(pose, openTooltip, 5, getSize().getY() - 5, 0, 0.5f, SDColor.EIGHT_BIT_YELLOW, true);
		}
	}
}
