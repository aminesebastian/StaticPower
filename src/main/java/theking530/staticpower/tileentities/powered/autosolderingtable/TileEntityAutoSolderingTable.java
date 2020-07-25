package theking530.staticpower.tileentities.powered.autosolderingtable;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.nonpowered.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityAutoSolderingTable extends TileEntitySolderingTable {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent outputInventory;

	public TileEntityAutoSolderingTable() {
		super(ModTileEntityTypes.AUTO_SOLDERING_TABLE);

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));

		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", DEFAULT_MOVING_TIME, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true));

		// Enable the power storage on this tile entity as this is the powered
		// version.
		energyStorage.setEnabled(true);
	}

	public boolean canMoveFromInputToProcessing() {
		if (!redstoneControlComponent.passesRedstoneCheck()) {
			return false;
		}
		// Check if there is a valid recipe.
		if (getCurrentRecipe().isPresent() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && InventoryUtilities.isInventoryEmpty(internalInventory) && hasRequiredItems()) {
			// If we passed all the previous checks, return true.
			return InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, getCurrentRecipe().get().recipeOutput) && energyStorage.hasEnoughPower(DEFAULT_PROCESSING_TIME * DEFAULT_PROCESSING_COST);
		}
		return false;
	}

	protected boolean movingCompleted() {
		// If we still have the recipe, and the required items, move the input items
		// into the internal inventory.
		if (getCurrentRecipe().isPresent() && hasRequiredItems()) {

			// Transfer the materials into the internal inventory.
			for (int i = 0; i < patternInventory.getSlots(); i++) {
				// Get the used item.
				ItemStack item = patternInventory.getStackInSlot(i);

				// Skip holes in the recipe.
				if (item.isEmpty()) {
					continue;
				}

				// Remove the item.
				int index = InventoryUtilities.getFirstSlotContainingItem(item, inventory);
				ItemStack transfered = inventory.extractItem(index, 1, false);
				internalInventory.setStackInSlot(i, transfered.copy());
			}

			markTileEntityForSynchronization();
			return true;
		}
		return false;
	}

	public boolean canProcess() {
		// Get the current recipe.
		SolderingRecipe recipe = getCurrentProcessingRecipe().orElse(null);
		if (recipe == null) {
			InventoryUtilities.clearInventory(internalInventory);
			processingComponent.cancelProcessing();
			return false;
		}
		return redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(DEFAULT_PROCESSING_COST) && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.recipeOutput);
	}

	protected boolean processingCompleted() {
		// If on the server.
		if (!getWorld().isRemote) {
			// Get the recipe from the internal inventory. If this is null, then we reloaded
			// in the middle of processing and removed the recipe. Return true and do
			// nothing.
			SolderingRecipe recipe = getCurrentProcessingRecipe().orElse(null);
			if (recipe == null) {
				return true;
			}

			// If we can insert the soldered item into the output slot, do it. Otherwise,
			// return false and keep spinning.
			if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getRecipeOutput())) {
				// Insert the soldered item into the output.
				outputInventory.insertItem(0, recipe.getRecipeOutput().copy(), false);
				// Clear the internal inventory.
				for (int i = 0; i < internalInventory.getSlots(); i++) {
					internalInventory.setStackInSlot(i, ItemStack.EMPTY);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				getCurrentRecipe().ifPresent(recipe -> {
					energyStorage.usePower(DEFAULT_PROCESSING_COST);
				});
			}
		}
	}

	public Optional<SolderingRecipe> getCurrentProcessingRecipe() {
		// Get the inventory in the form of an itemstack array.
		ItemStack[] pattern = new ItemStack[9];
		for (int i = 0; i < internalInventory.getSlots(); i++) {
			pattern[i] = internalInventory.getStackInSlot(i);
		}
		return getRecipeForItems(pattern);
	}

	@Override
	protected boolean requiresSolderingIron() {
		return false;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerAutoSolderingTable(windowId, inventory, this);
	}
}
