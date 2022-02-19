package theking530.staticpower.teams.research;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerHUDElement;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.Research.ResearchInstance;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class ActiveResearchHUD extends StaticPowerHUDElement {
	private final ItemDrawable itemRenderer;
	private final SimpleProgressBar progressBar;

	public ActiveResearchHUD() {
		super(0, 0);
		itemRenderer = new ItemDrawable(ItemStack.EMPTY);
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7));
		progressBar.setMaxProgress(100);
	}

	@SuppressWarnings("resource")
	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		int screenRight = getWindow().getGuiScaledWidth();
		Team team = TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player).orElse(null);
		if (team != null) {
			ResearchInstance current = null;
			Research research;

			if (team.isResearching()) {
				current = team.getCurrentResearch();
				research = current.getTrackedResearch();
			} else {
				research = team.getLastCompletedResearch();
			}

			int requirementCount = research.getRequirements().size();
			int width = Math.max(115, requirementCount * 21);

			// Draw background.
			GuiDrawUtilities.drawGenericBackground(width, team.isResearching() ? 40 : 30, screenRight - (width + 3), 3, 0, new Color(1.0f, 0.8f, 1.2f, 0.07f));

			// Draw icon.
			itemRenderer.setItemStack(research.getItemIcon());
			itemRenderer.setSize(1.0f, 1.0f);
			itemRenderer.draw(screenRight - width, 7);
			GuiDrawUtilities.drawStringLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), screenRight - width + 20, 15, 0.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

			// Draw progress bar.
			progressBar.setPosition(screenRight - width + 20, 19);
			progressBar.setSize(width * 0.75f, 7);
			if (team.isResearching()) {
				progressBar.setCurrentProgress((int) (current.getFullfillmentPercentage() * 100));
			} else {
				progressBar.setCurrentProgress(100);
			}

			// Draw requirements.
			if (team.isResearching()) {
				for (int i = 0; i < research.getRequirements().size(); i++) {
					int xOffset = i * 20;
					StaticPowerIngredient requirement = research.getRequirements().get(i);
					itemRenderer.setItemStack(requirement.getIngredient().getItems()[0]);
					itemRenderer.setSize(0.75f, 0.75f);
					itemRenderer.draw(screenRight - 25 - xOffset, 25);

					GuiDrawUtilities.drawString(pose, Integer.toString(requirement.getCount() - current.getRequirementFullfillment(i)), screenRight - 8 - xOffset, 39, 255, 0.75f,
							Color.EIGHT_BIT_WHITE, true);
				}
			}
		}

	}

	@Override
	protected void drawBehindItems(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	@Override
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
	}
}
