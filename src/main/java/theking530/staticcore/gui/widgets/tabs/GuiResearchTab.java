package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.team.TeamComponent;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

@OnlyIn(Dist.CLIENT)
public class GuiResearchTab extends AbstractInfoTab {
	private final TeamComponent teamComponent;
	public TextButton setTeamButton;

	public GuiResearchTab(TeamComponent teamComponent) {
		super("gui.staticpower.tab_title.team", new SDColor(255, 255, 255), 100, new SDColor(0.0f, 0.85f, 0.6f, 1.0f), new ItemDrawable(Blocks.LECTERN.asItem()));
		this.teamComponent = teamComponent;
		registerWidget(setTeamButton = new TextButton(14, 18, 16, Component.translatable("gui.staticpower.set_team").getString(), this::setTeamClicked));
		setTeamButton.setTooltip(Component.translatable("gui.staticpower.machine_set_team"));
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
			addKeyValueTwoLiner("team_name", Component.literal("Owners"), Component.literal(teamComponent.getOwningTeam().getName()), ChatFormatting.AQUA);
		} else {
			addLine("team_name", ChatFormatting.RED, Component.literal("Missing Team!"));
		}

		setTeamButton.setPosition(setTeamButton.getPosition().getX(), getHeight() - 28);
		this.setExpandedHeight(getExpandedSize().getY() + 20);
	}
}
