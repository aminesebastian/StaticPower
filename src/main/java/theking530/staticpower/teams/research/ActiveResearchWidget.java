package theking530.staticpower.teams.research;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;

public class ActiveResearchWidget extends AbstractGuiWidget<ActiveResearchWidget> {
	private final SimpleProgressBar progressBar;
	private boolean drawBackground;
	private Color backgroundTint;
	private Team team;
	private Research research;
	private ResearchInstance currentProgress;

	public ActiveResearchWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7));
		progressBar.setMaxProgress(100);
		drawBackground = true;
		backgroundTint = new Color(1.0f, 0.8f, 1.2f, 0.07f);
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

	public Color getBackgroundTint() {
		return backgroundTint;
	}

	public ActiveResearchWidget setBackgroundTint(Color backgroundTint) {
		this.backgroundTint = backgroundTint;
		return this;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
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
				GuiDrawUtilities.drawGenericBackground(pose, width, height, 0, 0, 0, new Color(1.0f, 0.8f, 1.2f, 0.8f));
			}

			// Draw icon.
			GuiDrawUtilities.drawItem(pose, research.getIcon().getItemIcon(), 4, 4, 10, 1.0f);
			GuiDrawUtilities.drawStringLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), 23, 13, 0.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

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
					int xOffset = i * 20;
					StaticPowerIngredient requirement = research.getRequirements().get(i);
					GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 20 - xOffset, 22, 0, 12f, 12f);

					GuiDrawUtilities.drawString(pose, Integer.toString(requirement.getCount() - currentProgress.getRequirementFullfillment(i)), getSize().getX() - 7f - xOffset, 40, 1, 0.5f,
							Color.EIGHT_BIT_WHITE, true);
				}
			}

			// Draw the tooltip.
			String openTooltip = new TranslatableComponent("gui.staticpower.research_menu_key_tooltip", ModKeyBindings.OPEN_RESEARCH.getMapping().getKey().getDisplayName().getString().toUpperCase())
					.getString();
			GuiDrawUtilities.drawStringLeftAligned(pose, openTooltip, 5, getSize().getY() - 5, 0, 0.5f, Color.EIGHT_BIT_YELLOW, true);
		}
	}
}
