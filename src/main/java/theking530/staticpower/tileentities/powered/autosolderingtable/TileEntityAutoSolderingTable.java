package theking530.staticpower.tileentities.powered.autosolderingtable;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderAutoSolderingTable;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.nonpowered.solderingtable.AbstractSolderingTable;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityAutoSolderingTable extends AbstractSolderingTable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityAutoSolderingTable> TYPE = new TileEntityTypeAllocator<TileEntityAutoSolderingTable>((type) -> new TileEntityAutoSolderingTable(),
			ModBlocks.AutoSolderingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderAutoSolderingTable::new);
		}
	}

	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final InventoryComponent outputInventory;

	public TileEntityAutoSolderingTable() {
		super(TYPE);

		// Enable the power storage on this tile entity as this is the powered
		// version.
		energyStorage.setEnabled(true);

		// Set the inventory component to the input mode.
		inventory.setMode(MachineSideMode.Input).setSlotsLockable(true);

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(moveComponent = MachineProcessingComponent
				.createMovingProcessingComponent("MoveComponent", this::canMoveFromInputToProcessing, () -> ProcessingCheckState.ok(), this::movingCompleted, true)
				.setRedstoneControlComponent(redstoneControlComponent));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", StaticPowerConfig.SERVER.autoSolderingTableProcessingTime.get(), this::canProcess,
				this::canProcess, this::processingCompleted, true).setShouldControlBlockState(true));

		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoSolderingTablePowerUsage.get());

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);

		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inventory));
	}

	public ProcessingCheckState canMoveFromInputToProcessing() {
		// Check if there is a valid recipe.
		if (getCurrentRecipe().isPresent() && !processingComponent.isProcessing() && InventoryUtilities.isInventoryEmpty(internalInventory) && hasRequiredItems()) {
			// If we passed all the previous checks, return true.
			if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, getCurrentRecipe().get().recipeOutput)) {
				return ProcessingCheckState.ok();
			}
		}
		return ProcessingCheckState.skip();
	}

	protected ProcessingCheckState movingCompleted() {
		// If we still have the recipe, and the required items, move the input items
		// into the internal inventory.
		if (getCurrentRecipe().isPresent() && hasRequiredItems()) {
			// Get the recipe.
			SolderingRecipe recipe = getCurrentRecipe().orElse(null);

			// Transfer the materials into the internal inventory.
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
						ItemStack extracted = inventory.extractItem(j, 1, false);
						internalInventory.setStackInSlot(i, extracted.copy());
						break;
					}
				}
			}
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.error("ERROR");
	}

	public ProcessingCheckState canProcess() {
		// Get the current recipe.
		SolderingRecipe recipe = getCurrentProcessingRecipe().orElse(null);
		if (recipe == null) {
			InventoryUtilities.clearInventory(internalInventory);
			processingComponent.cancelProcessing();
			return ProcessingCheckState.cancel();
		}
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.recipeOutput)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted() {

		// Get the recipe from the internal inventory. If this is null, then we reloaded
		// in the middle of processing and removed the recipe. Return true and do
		// nothing.
		SolderingRecipe recipe = getCurrentProcessingRecipe().orElse(null);
		if (recipe == null) {
			return ProcessingCheckState.ok();
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
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.outputsCannotTakeRecipe();
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
