package theking530.staticpower.blockentities.nonpowered.solderingtable;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.ISolderingIron;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;

public abstract class AbstractSolderingTable extends BlockEntityMachine implements MenuProvider {
	public final InventoryComponent patternInventory;
	public final InventoryComponent solderingIronInventory;
	public final InventoryComponent inventory;

	public AbstractSolderingTable(BlockEntityTypeAllocator<? extends AbstractSolderingTable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 9, MachineSideMode.Never));
		registerComponent(inventory = new InventoryComponent("Inventory", 9, MachineSideMode.Never).setShiftClickEnabled(true));
		registerComponent(solderingIronInventory = new InventoryComponent("SolderingIronInventory", 1, MachineSideMode.Never).setShiftClickEnabled(true).setShiftClickPriority(100)
				.setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return stack.getItem() instanceof ISolderingIron;
					}
				}));

		// Don't drop the pattern or output slots.
		patternInventory.setShouldDropContentsOnBreak(false);

		// Disable the power storage on this tile entity as this is the unpowered
		// version.
		powerStorage.setEnabled(false);
	}

	public boolean hasRequiredItems(SolderingRecipe recipe) {
		// If there is no soldering iron, return false. If there is, but it cannot be
		// used to solder, return false.
		if (requiresSolderingIron()) {
			if ((solderingIronInventory.getStackInSlot(0).getItem() instanceof ISolderingIron)) {
				ISolderingIron iron = (ISolderingIron) solderingIronInventory.getStackInSlot(0).getItem();
				if (!iron.canSolder(solderingIronInventory.getStackInSlot(0))) {
					return false;
				}
			} else {
				return false;
			}
		}

		// Create a duplicate inventory.
		ItemStackHandler duplicateInventory = new ItemStackHandler(inventory.getSlots());
		for (int i = 0; i < inventory.getSlots(); i++) {
			duplicateInventory.setStackInSlot(i, inventory.getStackInSlot(i).copy());
		}

		// Allocate a flag to indicate if we found an ingredient match.
		boolean flag = false;

		// Iterate through all the ingredients in the recipe.
		for (int i = 0; i < recipe.getIngredients().size(); i++) {
			// Set the flag to false.
			flag = false;

			// Get the ingredient.
			Ingredient ing = recipe.getIngredients().get(i);

			// Skip empty ingredients.
			if (ing.equals(Ingredient.EMPTY)) {
				continue;
			}

			// Look for an item that matches the ingredient inside the inventory. If we find
			// one, extract the item from the duplicate and continue. If one is not found,
			// we cannot craft this item, return false.
			for (int j = 0; j < duplicateInventory.getSlots(); j++) {
				if (ing.test(duplicateInventory.getStackInSlot(j))) {
					duplicateInventory.extractItem(j, 1, false);
					flag = true;
					break;
				}
			}

			if (!flag) {
				return false;
			}
		}

		// If all the above are met, return true.
		return true;
	}

	public ItemStack craftItem(int amount) {
		// Get the recipe. If we dont have a valid recipe, return 0.
		SolderingRecipe recipe = getCurrentRecipe().orElse(null);
		if (recipe == null || recipe.getIngredients().size() == 0) {
			return ItemStack.EMPTY;
		}

		// Crafted results.
		ItemStack output = ItemStack.EMPTY;

		// Check the pattern.
		for (int k = 0; k < amount; k++) {
			// If we can't craft anymore, stop.
			if (output.getCount() + recipe.getResultItem().getCount() > recipe.getResultItem().getMaxStackSize()) {
				break;
			}

			// Break out of the loop if we're out of items.
			if (!hasRequiredItems(recipe)) {
				break;
			}

			// Use the crafting items.
			for (int i = 0; i < patternInventory.getSlots(); i++) {
				// Get the used ingredient.
				Ingredient ing = recipe.getIngredients().get(i);

				// Skip holes in the recipe.
				if (ing.equals(Ingredient.EMPTY)) {
					continue;
				}

				// Remove the item.
				for (int j = 0; j < inventory.getSlots(); j++) {
					if (ing.test(inventory.getStackInSlot(j))) {
						inventory.extractItem(j, 1, false);
						break;
					}
				}
			}

			// Use the soldering iron.
			ISolderingIron iron = (ISolderingIron) solderingIronInventory.getStackInSlot(0).getItem();
			iron.useSolderingItem(solderingIronInventory.getStackInSlot(0));

			// Grow the output.
			if (output.isEmpty()) {
				output = recipe.getResultItem().copy();
			} else {
				output.grow(recipe.getResultItem().copy().getCount());
			}
		}

		// Return the output.
		return output;
	}

	public Optional<SolderingRecipe> getCurrentRecipe() {
		ItemStack[] pattern = new ItemStack[9];
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			pattern[i] = patternInventory.getStackInSlot(i);
		}
		return getRecipeForItems(pattern);
	}

	public Optional<SolderingRecipe> getRecipeForItems(ItemStack... items) {
		return StaticPowerRecipeRegistry.getRecipe(SolderingRecipe.RECIPE_TYPE, new RecipeMatchParameters(items));
	}

	protected boolean requiresSolderingIron() {
		return true;
	}
}
