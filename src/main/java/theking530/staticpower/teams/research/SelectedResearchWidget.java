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
	private final ResearchManager manager;
	private Research research;
	private ResearchInstance researchProgress;

	public SelectedResearchWidget(ResearchManager manager, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.manager = manager;
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7).disableProgressTooltip());
		progressBar.setMaxProgress(100);
		cacheResearch();
	}

	public void updateData() {
		cacheResearch();
	}

	public void cacheResearch() {
		if (manager != null) {
			if (manager.hasSelectedResearch()) {
				research = manager.getSelectedResearch().getTrackedResearch();
				researchProgress = manager.getSelectedResearch();
			} else {
				research = manager.getLastCompletedResearch();
			}
		}
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Draw background.
		GuiDrawUtilities.drawGenericBackground(pose, getSize().getX() + 4, getSize().getY() + 4, -4, -4, 0.0f, new Color(0.25f, 0.5f, 1.0f, 1.0f));

		if (research != null) {
			// Split the description into wrapped lines.
			List<String> lines = GuiDrawUtilities.wrapString(research.getDescription(), getSize().getXi() * 2 - 35);
			for (int i = 0; i < lines.size(); i++) {
				GuiDrawUtilities.drawStringLeftAligned(pose, lines.get(i), 4, 33 + (i * 5.5f), 0f, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}
			setSize(getSize().getX(), Math.max(65, 50 + (lines.size() * 5)));

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
			float requirementsBgSize =  getSize().getX() - 8;
			GuiDrawUtilities.drawRectangle(pose,  requirementsBgSize, 14, getSize().getX() -  requirementsBgSize - 5, getSize().getY() - 18, -1000, new Color(0.0f, 0.0f, 0.0f, 0.35f));

			for (int i = 0; i < requirementCount; i++) {
				int xOffset = i * 14;
				StaticPowerIngredient requirement = research.getRequirements().get(i);
				GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 22 - xOffset, getSize().getY() - 18.5f, 100, 0.75f, 0.75f);

				int remainingCount = requirement.getCount();
				if (researchProgress != null) {
					remainingCount -= researchProgress.getRequirementFullfillment(i);
				}
				GuiDrawUtilities.drawString(pose, Integer.toString(remainingCount), getSize().getX() - 11 - xOffset, getSize().getY() - 12, 1, 0.5f, Color.EIGHT_BIT_YELLOW, true);
			}
		}
	}
}
