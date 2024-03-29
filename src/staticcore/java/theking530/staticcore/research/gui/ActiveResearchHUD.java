package theking530.staticcore.research.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import theking530.staticcore.gui.screens.StaticCoreHUDElement;
import theking530.staticcore.teams.ClientTeam;
import theking530.staticcore.teams.TeamManager;

public class ActiveResearchHUD extends StaticCoreHUDElement {
	private final ActiveResearchWidget activeResearch;

	public ActiveResearchHUD() {
		super(0, 0);
		setDrawDefaultDarkBackground(false);
		int screenRight = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		registerWidget(activeResearch = new ActiveResearchWidget(screenRight - 100, 30, 86, 7));
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		super.tick();

		Screen currentScreen = Minecraft.getInstance().screen;
		setVisible(currentScreen == null);

		// Keep the research updated.
		ClientTeam team = TeamManager.getLocalTeam();
		if (team != null) {
			activeResearch.setTeam(team);
			if (team.getResearchManager().hasSelectedResearch()) {
				activeResearch.setResearch(team.getResearchManager().getSelectedResearch());
			} else {
				activeResearch.setResearch(team.getResearchManager().getLastCompletedResearch());
			}
		}

		// We constantly update the position to glue this widget to the top right.
		int screenRight = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		activeResearch.setPosition(screenRight - activeResearch.getSize().getX() - 3, 3);
		setVisible(activeResearch.getResearch() != null);
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	@Override
	protected void drawBehindItems(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	@Override
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
	}
}
