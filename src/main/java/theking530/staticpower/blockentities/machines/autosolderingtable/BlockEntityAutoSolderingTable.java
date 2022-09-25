package theking530.staticpower.blockentities.machines.autosolderingtable;

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
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.nonpowered.solderingtable.AbstractSolderingTable;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderAutoSolderingTable;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityAutoSolderingTable extends AbstractSolderingTable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoSolderingTable> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoSolderingTable>(
			(type, pos, state) -> new BlockEntityAutoSolderingTable(pos, state), ModBlocks.AutoSolderingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderAutoSolderingTable::new);
		}
	}

	public final RecipeProcessingComponent<SolderingRecipe> processingComponent;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final InventoryComponent outputInventory;
	public final PowerStorageComponent powerStorage;

	public BlockEntityAutoSolderingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		disableFaceInteraction();
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier()));

		// Set the inventory component to the input mode.
		inventory.setMode(MachineSideMode.Input).setSlotsLockable(true);

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(
				processingComponent = new RecipeProcessingComponent<SolderingRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.autoSolderingTableProcessingTime.get(),
						RecipeProcessingComponent.MOVE_TIME, SolderingRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted).setShouldControlBlockState(true)
						.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoSolderingTablePowerUsage.get()));

		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoSolderingTablePowerUsage.get());

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inventory));
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		ItemStack[] pattern = new ItemStack[patternInventory.getSlots()];

		if (location == RecipeProcessingPhase.PROCESSING) {
			for (int i = 0; i < internalInventory.getSlots(); i++) {
				pattern[i] = internalInventory.getStackInSlot(i);
			}
		} else {
			for (int i = 0; i < patternInventory.getSlots(); i++) {
				pattern[i] = patternInventory.getStackInSlot(i);
			}
		}

		return new RecipeMatchParameters(pattern);
	}

	public ProcessingCheckState canProcessRecipe(SolderingRecipe recipe, RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PRE_PROCESSING) {
			if (!hasRequiredItems(recipe)) {
				return ProcessingCheckState.error("Missing items in input inventory!");
			}
		}

		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	public void moveInputs(SolderingRecipe recipe) {
		// If we still have the recipe, and the required items, move the input items
		// into the internal inventory.
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
	}

	public void processingCompleted(SolderingRecipe recipe) {
		outputInventory.insertItem(0, recipe.getResultItem().copy(), false);
		InventoryUtilities.clearInventory(internalInventory);
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
