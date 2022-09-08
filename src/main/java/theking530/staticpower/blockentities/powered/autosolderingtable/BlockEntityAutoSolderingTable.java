package theking530.staticpower.blockentities.powered.autosolderingtable;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.control.MachineProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.nonpowered.solderingtable.AbstractSolderingTable;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderAutoSolderingTable;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityAutoSolderingTable extends AbstractSolderingTable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoSolderingTable> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoSolderingTable>((type, pos, state) -> new BlockEntityAutoSolderingTable(pos, state),
			ModBlocks.AutoSolderingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderAutoSolderingTable::new);
		}
	}

	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final InventoryComponent outputInventory;

	public BlockEntityAutoSolderingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Enable the power storage on this tile entity as this is the powered
		// version.
		powerStorage.setEnabled(true);

		// Set the inventory component to the input mode.
		inventory.setMode(MachineSideMode.Input).setSlotsLockable(true);

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(moveComponent = MachineProcessingComponent
				.createMovingProcessingComponent("MoveComponent", this::canMoveFromInputToProcessing, () -> ProcessingCheckState.ok(), this::movingCompleted, true)
				.setRedstoneControlComponent(redstoneControlComponent));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", StaticPowerConfig.SERVER.autoSolderingTableProcessingTime.get(), this::canProcess,
				this::canProcess, this::processingCompleted, true).setShouldControlBlockState(true));

		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoSolderingTablePowerUsage.get());

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

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
		if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			// Insert the soldered item into the output.
			outputInventory.insertItem(0, recipe.getResultItem().copy(), false);
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
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoSolderingTable(windowId, inventory, this);
	}
}
