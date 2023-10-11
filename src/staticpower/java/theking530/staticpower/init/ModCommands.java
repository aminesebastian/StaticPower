package theking530.staticpower.init;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.StaticCore;

public class ModCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> logMissingRecipes = Commands.literal("sp")
				.requires((commandSource) -> commandSource.hasPermission(2)).then(Commands.literal("recipes")
						.then(Commands.literal("missing").executes(ModCommands::listUncraftableItems)));
		dispatcher.register(logMissingRecipes);
	}

	public static int listUncraftableItems(CommandContext<CommandSourceStack> commandContext)
			throws CommandSyntaxException {
		try {
			Player player = commandContext.getSource().getPlayerOrException();
			List<CraftingRecipe> craftingRecipes = player.getLevel().getRecipeManager()
					.getAllRecipesFor(RecipeType.CRAFTING);
			Set<ResourceLocation> missingRecipeItems = new HashSet<>(ForgeRegistries.ITEMS.getKeys());

			for (CraftingRecipe recipe : craftingRecipes) {
				ResourceLocation outputKey = ForgeRegistries.ITEMS.getKey(recipe.getResultItem().getItem());
				if (missingRecipeItems.contains(outputKey)) {
					missingRecipeItems.remove(outputKey);
				}
			}

			for (ResourceLocation missing : missingRecipeItems) {
				if (missing.getNamespace().equals("staticpower")) {
					System.out.println(missing);
				}
			}

		} catch (Exception e) {
			StaticCore.LOGGER.error("An error occured when executing the unlock all research command.", e);
		}
		return Command.SINGLE_SUCCESS;
	}
}
