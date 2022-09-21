package theking530.staticpower.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import theking530.staticpower.StaticPower;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class ModCommands {
	public static class ResearchCommands {
		public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
			// Lock all research command.
			LiteralArgumentBuilder<CommandSourceStack> lockCommand = Commands.literal("sp").requires((commandSource) -> commandSource.hasPermission(2))
					.then(Commands.literal("research").then(Commands.literal("lock").then(Commands.literal("all").executes(ResearchCommands::lockAllResearch))));
			dispatcher.register(lockCommand);

			// Unlock all research command.
			LiteralArgumentBuilder<CommandSourceStack> unlockCommand = Commands.literal("sp").requires((commandSource) -> commandSource.hasPermission(2))
					.then(Commands.literal("research").then(Commands.literal("unlock").then(Commands.literal("all").executes(ResearchCommands::unlockAllResearch))));
			dispatcher.register(unlockCommand);
		}

		public static int unlockAllResearch(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			try {
				Player player = commandContext.getSource().getPlayerOrException();
				Team team = TeamManager.get().getTeamForPlayer(player);
				if (team != null) {
					team.getResearchManager().unlockAllResearch();
				}
			} catch (Exception e) {
				StaticPower.LOGGER.error("An error occured when executing the unlock all research command.", e);
			}
			return 1;
		}

		public static int lockAllResearch(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
			try {
				Player player = commandContext.getSource().getPlayerOrException();
				Team team = TeamManager.get().getTeamForPlayer(player);
				if (team != null) {
					team.getResearchManager().lockAllResearch();
				}
			} catch (Exception e) {
				StaticPower.LOGGER.error("An error occured when executing the unlock all research command.", e);
			}
			return 1;
		}
	}
}
