package theking530.staticpower.tileentities.nonpowered.solderingtable;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.items.tools.ISolderingIron;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntitySolderingTable extends TileEntityMachine implements INamedContainerProvider {
	public final InventoryComponent patternInventory;
	public final InventoryComponent solderingIronInventory;
	public final InventoryComponent inventory;

	public TileEntitySolderingTable(TileEntityType<?> type) {
		super(type);
		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 9, MachineSideMode.Never));
		registerComponent(inventory = new InventoryComponent("Inventory", 9, MachineSideMode.Input));
		registerComponent(solderingIronInventory = new InventoryComponent("SolderingIronInventory", 1, MachineSideMode.Never).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof ISolderingIron;
			}
		}));

		// Don't drop the pattern or output slots.
		patternInventory.setShouldDropContentsOnBreak(false);

		// Disable the power storage on this tile entity as this is the unpowered
		// version.
		energyStorage.setEnabled(false);
	}

	public boolean hasRequiredItems() {
		// If there is no soldering iron, return false.
		if (requiresSolderingIron() && !(solderingIronInventory.getStackInSlot(0).getItem() instanceof ISolderingIron)) {
			return false;
		}

		// Check if we have a recipe.
		Optional<SolderingRecipe> recipe = getCurrentRecipe();

		// If there is no recipe, return false.
		if (!recipe.isPresent()) {
			return false;
		}

		// Create a duplicate inventory.
		ItemStackHandler duplicateInventory = new ItemStackHandler(inventory.getSlots());
		for (int i = 0; i < inventory.getSlots(); i++) {
			duplicateInventory.setStackInSlot(i, inventory.getStackInSlot(i).copy());
		}

		// Allocate a flag to indicate if we found an ingredient match.
		boolean flag = false;

		// Iterate through all the ingredients in the recipe.
		for (int i = 0; i < recipe.get().getIngredients().size(); i++) {
			// Set the flag to false.
			flag = false;

			// Get the ingredient.
			Ingredient ing = recipe.get().getIngredients().get(i);

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

	public int getPossibleAmountToCraft() {
		// Get the recipe. If we dont have a valid recipe, return 0.
		SolderingRecipe recipe = getCurrentRecipe().orElse(null);
		if (recipe == null || recipe.getIngredients().size() == 0) {
			return 0;
		}

		// Create a duplicate inventory.
		ItemStackHandler duplicateInventory = new ItemStackHandler(inventory.getSlots());
		for (int i = 0; i < inventory.getSlots(); i++) {
			duplicateInventory.setStackInSlot(i, inventory.getStackInSlot(i).copy());
		}

		// Loop through and see how many we can craft (k=10000 is arbitrary, it should
		// always return early. while(true) would also work too, but the 10000 covers us
		// in case someone makes a really weird recipe).
		int craftable = 0;
		for (int k = 0; k < 10000; k++) {
			for (Ingredient ing : recipe.getIngredients()) {
				// Set the flag to false.
				boolean flag = false;

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
					return craftable;
				}
			}
			craftable++;
		}
		return craftable;
	}

	public ItemStack craftItem() {
		// Get the recipe. If we dont have a valid recipe, return 0.
		SolderingRecipe recipe = getCurrentRecipe().orElse(null);
		if (recipe == null || recipe.getIngredients().size() == 0) {
			return ItemStack.EMPTY;
		}

		// Check the pattern.
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			// Get the used item.
			ItemStack item = patternInventory.getStackInSlot(i);

			// Skip holes in the recipe.
			if (item.isEmpty()) {
				continue;
			}

			// Remove the item.
			int index = InventoryUtilities.getFirstSlotContainingItem(item, inventory);
			if (index >= 0) {
				inventory.extractItem(index, 1, false);
			}
		}

		// Use the soldering iron.
		if (solderingIronInventory.getStackInSlot(0).attemptDamageItem(1, getTileEntity().getWorld().rand, null)) {
			solderingIronInventory.setStackInSlot(0, ItemStack.EMPTY);
		}

		// Return the output.
		return recipe.getRecipeOutput().copy();
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

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSolderingTable(windowId, inventory, this);
	}
}
