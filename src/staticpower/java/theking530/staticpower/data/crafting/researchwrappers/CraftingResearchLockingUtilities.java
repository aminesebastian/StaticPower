package theking530.staticpower.data.crafting.researchwrappers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import theking530.staticcore.container.FakeCraftingInventory;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class CraftingResearchLockingUtilities {
	public static boolean passesResearchRequirements(Recipe<?> recipe, CraftingContainer container, Level level) {
		// If this is not a blockable research, we can craft it.
		if (!(recipe instanceof IResearchBlockedRecipe)) {
			return true;
		}

		// Try to capture the player by using the output slot.
		Player player = null;
		if (container != null) {
			// If this is coming from a fake crafting inventory, check if the override is
			// set.
			// If not, capture the provided player.
			if (container instanceof FakeCraftingInventory) {
				FakeCraftingInventory fake = (FakeCraftingInventory) container;
				if (fake.shouldSkipResearchChecks()) {
					return true;
				} else {
					player = fake.getPlayer();
				}
			} else if (container instanceof CraftingContainer) {
				CraftingContainer craftCont = (CraftingContainer) container;
				AbstractContainerMenu menu = (AbstractContainerMenu) ObfuscationReflectionHelper.getPrivateValue(CraftingContainer.class, craftCont, "f_39323_"); // Get the "menu"
																																									// field.

				for (Slot slot : menu.slots) {
					if (slot instanceof ResultSlot) {
						ResultSlot rSlot = (ResultSlot) slot;
						player = (Player) ObfuscationReflectionHelper.getPrivateValue(ResultSlot.class, rSlot, "f_40163_"); // Get the "player" field.
					}
				}
			}
		}

		// If we got a player check to see if their team has completed the required
		// research.
		if (player != null) {
			return canPlayerCraftRecipe(player, recipe);
		}

		return false;
	}

	public static boolean canPlayerCraftRecipe(Player player, Recipe<?> recipe) {
		if (player == null) {
			return false;
		}

		ITeam team = TeamManager.get(player.level).getTeamForPlayer(player);
		return canTeamCraftRecipe(team, recipe);
	}

	/**
	 * Checks if the provided recipe can be crafted by the provided team.
	 * 
	 * @param team
	 * @param recipe
	 * @return
	 */
	public static boolean canTeamCraftRecipe(ITeam team, Recipe<?> recipe) {
		if (team == null) {
			return false;
		}

		if (recipe instanceof IResearchBlockedRecipe) {
			IResearchBlockedRecipe blockedRecipe = (IResearchBlockedRecipe) recipe;
			for (ResourceLocation research : blockedRecipe.getRequiredResearch()) {
				if (!team.getResearchManager().hasCompletedResearch(research)) {
					return false;
				}
			}
		}
		return true;
	}
}
