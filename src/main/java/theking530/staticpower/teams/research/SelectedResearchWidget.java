package theking530.staticpower.teams.research;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;

public class SelectedResearchWidget extends AbstractGuiWidget<SelectedResearchWidget> {
	private final SimpleProgressBar progressBar;
	private Research research;
	private ResearchInstance currentProgress;

	public SelectedResearchWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7).disableProgressTooltip());
		progressBar.setMaxProgress(100);
	}

	public Research getResearch() {
		return research;
	}

	public SelectedResearchWidget setResearch(ResearchInstance currentProgress) {
		this.research = currentProgress.getTrackedResearch();
		this.currentProgress = currentProgress;
		return this;
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (currentProgress != null) {
			int requirementCount = research.getRequirements().size();

			// Draw icon.
			GuiDrawUtilities.drawItem(pose, research.getItemIcon(), 2, 2, 10, 1.0f);
			GuiDrawUtilities.drawStringLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), 4, 25, 0.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

			// Draw progress bar.
			progressBar.setPosition(20, 6);
			progressBar.setSize(getSize().getX() * 0.75f, 7);
			if (currentProgress.getResearchManager().hasSelectedResearch()) {
				progressBar.setCurrentProgress((int) (currentProgress.getFullfillmentPercentage() * 100));
			} else {
				progressBar.setCurrentProgress(100);
			}

			// Split the description into wrapped lines.
			List<String> lines = GuiDrawUtilities.wrapString(research.getDescription(), getSize().getXi() + 20);
			for (int i = 0; i < lines.size(); i++) {
				GuiDrawUtilities.drawStringLeftAligned(pose, lines.get(i), 4, 33 + (i * 5.5f), 0f, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}

			// Draw requirements.
			if (currentProgress.getResearchManager().hasSelectedResearch() && !currentProgress.getResearchManager().getSelectedResearch().isCompleted()) {
				float requirementsBgSize = Math.max(16, requirementCount + requirementCount * 13.35f);
				GuiDrawUtilities.drawRectangle(pose, requirementsBgSize, 16, getSize().getX() - requirementsBgSize - 4, getSize().getY() - 20, 0, new Color(0.0f, 0.0f, 0.0f, 0.5f));

				for (int i = 0; i < requirementCount; i++) {
					int xOffset = i * 14;
					StaticPowerIngredient requirement = research.getRequirements().get(i);
					GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 20 - xOffset, getSize().getY() - 19, 0, 0.75f, 0.75f);

					GuiDrawUtilities.drawString(pose, Integer.toString(requirement.getCount() - currentProgress.getRequirementFullfillment(i)), getSize().getX() - 9 - xOffset, getSize().getY() - 14,
							1, 0.5f, Color.EIGHT_BIT_WHITE, true);
				}
			}
		}
	}
}
