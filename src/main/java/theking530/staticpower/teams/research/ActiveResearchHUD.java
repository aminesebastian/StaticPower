package theking530.staticpower.teams.research;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticpower.client.gui.StaticPowerHUDElement;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class ActiveResearchHUD extends StaticPowerHUDElement {
	private final ActiveResearchWidget activeResearch;

	public ActiveResearchHUD() {
		super(0, 0);
		setDrawDefaultDarkBackground(false);
		int screenRight = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		registerWidget(activeResearch = new ActiveResearchWidget(screenRight - 100, 30, 86, 7));
	}

	@Override
	public void tick() {
		super.tick();

		// Keep the research updated.
		Team team = TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player).orElse(null);
		if (team != null) {
			activeResearch.setTeam(team);
			if (team.isResearching()) {
				activeResearch.setResearch(team.getCurrentResearch());
			} else {
				activeResearch.setResearch(team.getLastCompletedResearch());
			}
		}

		// We constantly update the position to glue this widget to the top right.
		int screenRight = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		activeResearch.setPosition(screenRight - activeResearch.getSize().getX() - 3, 3);
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
