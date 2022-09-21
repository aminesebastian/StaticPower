package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.blockentities.components.team.TeamComponent;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

@OnlyIn(Dist.CLIENT)
public class GuiResearchTab extends AbstractInfoTab {
	private final TeamComponent teamComponent;
	public TextButton setTeamButton;

	public GuiResearchTab(TeamComponent teamComponent) {
		super("gui.staticpower.tab_title.research", new Color(255, 255, 255), 100, new Color(0.0f, 0.85f, 0.6f, 1.0f), new ItemDrawable(ModItems.ResearchTier1.get()));
		this.teamComponent = teamComponent;
		registerWidget(setTeamButton = new TextButton(12, 25, 16, new TranslatableComponent("gui.staticpower.set_team").getString(), this::setTeamClicked));
		setTeamButton.setTooltip(new TranslatableComponent("gui.staticpower.machine_set_team"));
	}

	public void setTeamClicked(StandardButton button, MouseButton mouseButton) {
		Team localTeam = TeamManager.getLocalTeam();
		if (localTeam != null) {
			teamComponent.setTeam(localTeam.getId());
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (teamComponent.getOwningTeam() != null) {
			addKeyValueTwoLiner("team_name", new TextComponent("Owners"), new TextComponent(teamComponent.getOwningTeam().getName()), ChatFormatting.AQUA);
		} else {
			addLine("team_name", ChatFormatting.RED, new TextComponent("Missing Team!"));
		}
		
		setTeamButton.setPosition(setTeamButton.getPosition().getX(), getHeight() - 26);
		this.setExpandedHeight(getExpandedSize().getY() + 16);
	}
}
