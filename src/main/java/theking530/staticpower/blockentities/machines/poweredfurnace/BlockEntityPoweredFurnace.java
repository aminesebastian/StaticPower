package theking530.staticpower.blockentities.machines.poweredfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class BlockEntityPoweredFurnace extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPoweredFurnace> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityPoweredFurnace(pos, state), ModBlocks.PoweredFurnace);

	/**
	 * Indicates how many times faster this block will perform compared to the
	 * vanila furnace.
	 */
	public static final float DEFAULT_PROCESSING_TIME_MULT = 1.3f;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<SmeltingRecipe> processingComponent;

	public BlockEntityPoweredFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SmeltingRecipe>("ProcessingComponent", 1, RecipeType.SMELTING, this::getMatchParameters, this::canProcessRecipe,
				this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(SmeltingRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		processingComponent.setMaxProcessingTime(BlockEntityPoweredFurnace.getCookTime(recipe));
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(SmeltingRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(SmeltingRecipe recipe) {
		outputInventory.insertItem(0, recipe.getResultItem().copy(), false);
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	public static int getCookTime(SmeltingRecipe recipe) {
		return (int) (recipe.getCookingTime() / DEFAULT_PROCESSING_TIME_MULT);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPoweredFurnace(windowId, inventory, this);
	}
}
