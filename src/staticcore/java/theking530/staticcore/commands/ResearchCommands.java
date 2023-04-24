package theking530.staticcore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import theking530.staticcore.StaticCore;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class ResearchCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// Lock all research command.
		LiteralArgumentBuilder<CommandSourceStack> lockCommand = Commands.literal("sc")
				.requires((commandSource) -> commandSource.hasPermission(2))
				.then(Commands.literal("research").then(Commands.literal("lock")
						.then(Commands.literal("all").executes(ResearchCommands::lockAllResearch))));
		dispatcher.register(lockCommand);

		// Unlock all research command.
		LiteralArgumentBuilder<CommandSourceStack> unlockCommand = Commands.literal("sc")
				.requires((commandSource) -> commandSource.hasPermission(2))
				.then(Commands.literal("research").then(Commands.literal("unlock")
						.then(Commands.literal("all").executes(ResearchCommands::unlockAllResearch))));
		dispatcher.register(unlockCommand);
	}

	public static int unlockAllResearch(CommandContext<CommandSourceStack> commandContext)
			throws CommandSyntaxException {
		try {
			Player player = commandContext.getSource().getPlayerOrException();
			ITeam team = TeamManager.get(player.level).getTeamForPlayer(player);
			if (team != null) {
				team.getResearchManager().unlockAllResearch(player.level);
			}
			TeamManager.get(player.level).syncToClients();
		} catch (Exception e) {
			StaticCore.LOGGER.error("An error occured when executing the unlock all research command.", e);
		}
		return Command.SINGLE_SUCCESS;
	}

	public static int lockAllResearch(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
		try {
			Player player = commandContext.getSource().getPlayerOrException();
			ITeam team = TeamManager.get(player.level).getTeamForPlayer(player);
			if (team != null) {
				team.getResearchManager().lockAllResearch();
			}
		} catch (Exception e) {
			StaticCore.LOGGER.error("An error occured when executing the unlock all research command.", e);
		}
		return Command.SINGLE_SUCCESS;
	}
}
