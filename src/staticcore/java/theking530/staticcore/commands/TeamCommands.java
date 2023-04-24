package theking530.staticcore.commands;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class TeamCommands {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// List all teams.
		LiteralArgumentBuilder<CommandSourceStack> listAllTeamsCommand = Commands.literal("sc")
				.requires((commandSource) -> commandSource.hasPermission(0))
				.then(Commands.literal("teams").then(Commands.literal("list").executes(TeamCommands::listAllTeams)));
		dispatcher.register(listAllTeamsCommand);

	}

	public static int listAllTeams(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
		Player player = commandContext.getSource().getPlayerOrException();
		List<ITeam> teams = TeamManager.get(player.level).getTeams();
		for (ITeam team : teams) {
			player.sendSystemMessage(Component.literal(team.getName()));
		}
		return Command.SINGLE_SUCCESS;
	}

}
