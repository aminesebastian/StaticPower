package theking530.staticpower.teams.research;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchIcon;
import theking530.staticpower.data.research.ResearchUnlock;
import theking530.staticpower.data.research.ResearchUnlockUtilities;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;

public class SelectedResearchWidget extends AbstractGuiWidget<SelectedResearchWidget> {
	private final SimpleProgressBar progressBar;
	private Research research;
	private ResearchInstance researchProgress;
	private List<String> description;

	public SelectedResearchWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7).disableProgressTooltip());
		progressBar.setMaxProgress(100);
	}

	public void tick() {
		Team team = TeamManager.getLocalTeam();
		if (research != null && team != null) {
			if (team.getResearchManager() != null) {
				if (team.getResearchManager().isResearching(research.getId())) {
					researchProgress = team.getResearchManager().getResearchProgress(research.getId());
				}
			}
		}
	}

	public void setResearch(Research research, @Nullable ResearchInstance instance) {
		this.research = research;
		researchProgress = instance;
		// Split the description into wrapped lines and cache it. Also resize the widget
		// here and not on the render method for optimization reasons.
		if (research != null) {
			description = GuiDrawUtilities.wrapString(new TranslatableComponent(research.getDescription()).getString(), getSize().getXi() * 2 - 35);
			setSize(getSize().getX(), Math.max(65, 50 + (description.size() * 5)));
		}
		
		if(researchProgress != null && research != null && researchProgress.getTrackedResearch() != research) {
			System.out.println("WTF");
		}
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Draw background.
		GuiDrawUtilities.drawGenericBackground(pose, getSize().getX() + 4, getSize().getY() + 3.5f, -4, -4, 0.0f, new Color(0.25f, 0.5f, 1.0f, 1.0f));

		try {
			if (research != null) {
				for (int i = 0; i < description.size(); i++) {
					GuiDrawUtilities.drawStringLeftAligned(pose, description.get(i), 4, 33 + (i * 5.5f), 0f, 0.5f, Color.EIGHT_BIT_WHITE, true);
				}

				int requirementCount = research.getRequirements().size();

				// Draw icon.
				GuiDrawUtilities.drawItem(pose, research.getIcon().getItemIcon(), 2, 2, 100, 1.0f);
				GuiDrawUtilities.drawStringLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), 4, 25, 0.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

				// Draw progress bar.
				progressBar.setPosition(20, 6);
				progressBar.setSize(getSize().getX() * 0.75f, 7);
				if (researchProgress != null) {
					progressBar.setCurrentProgress((int) (researchProgress.getFullfillmentPercentage() * 100));
				} else {
					progressBar.setCurrentProgress(100);
				}

				// Draw requirements.
				float requirementsBgSize = requirementCount * 14;
				GuiDrawUtilities.drawRectangle(pose, requirementsBgSize, 14, getSize().getX() - requirementsBgSize - 7, getSize().getY() - 19, -1000,
						new Color(0.0f, 0.0f, 0.0f, 0.35f));

				int xOffset = 0;
				for (int i = 0; i < requirementCount; i++) {
					StaticPowerIngredient requirement = research.getRequirements().get(i);
					GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 22 - xOffset, getSize().getY() - 19.5f, 100, 12f, 12f);

					int remainingCount = requirement.getCount();
					if (researchProgress != null) {
						remainingCount -= researchProgress.getRequirementFullfillment(i);
					}
					GuiDrawUtilities.drawString(pose, Integer.toString(remainingCount), getSize().getX() - 11 - xOffset, getSize().getY() - 13, 101, 0.5f, Color.EIGHT_BIT_YELLOW,
							true);
					xOffset += 14;
				}

				List<ResearchUnlock> unlocks = ResearchUnlockUtilities.getCollapsedUnlocks(research);
				if (unlocks.size() > 0) {
					GuiDrawUtilities.drawRectangle(pose, 0.4f, 14, getSize().getX() - 9f - xOffset, getSize().getY() - 19, 10, new Color(0, 0, 0, 0.3f));

					// Draw the unlocks.
					for (ResearchUnlock unlock : unlocks) {
						if (unlock.getIcon() != null) {
							ResearchIcon.draw(unlock.getIcon(), pose, getSize().getX() - 25.5f - xOffset, getSize().getY() - 20, 200, 12f, 12f);
						}
						xOffset += 12.1f;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
