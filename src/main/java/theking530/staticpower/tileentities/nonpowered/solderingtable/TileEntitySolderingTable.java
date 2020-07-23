package theking530.staticpower.tileentities.nonpowered.solderingtable;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.items.tools.ISolderingIron;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class TileEntitySolderingTable extends TileEntityBase implements INamedContainerProvider {
	public final InventoryComponent patternInventory;
	public final InventoryComponent solderingIronInventory;
	public final InventoryComponent inventory;

	public TileEntitySolderingTable() {
		super(ModTileEntityTypes.SOLDERING_TABLE);
		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 9, MachineSideMode.Never));
		registerComponent(inventory = new InventoryComponent("Inventory", 9, MachineSideMode.Input));
		registerComponent(solderingIronInventory = new InventoryComponent("SolderingIronInventory", 1, MachineSideMode.Never).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof ISolderingIron;
			}
		}));

		// Don't drop the pattern or output slots.
		patternInventory.setShouldDropContentsOnBreak(false);
	}

	public boolean hasRequiredItems() {
		// If there is no soldering iron, return false.
		if (!(solderingIronInventory.getStackInSlot(0).getItem() instanceof ISolderingIron)) {
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

	public Optional<SolderingRecipe> getCurrentRecipe() {
		ItemStack[] pattern = new ItemStack[9];
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			pattern[i] = patternInventory.getStackInSlot(i);
		}
		return StaticPowerRecipeRegistry.getRecipe(SolderingRecipe.RECIPE_TYPE, new RecipeMatchParameters(pattern));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSolderingTable(windowId, inventory, this);
	}
}
