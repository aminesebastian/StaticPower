package theking530.staticpower.tileentities.powered.bottler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityBottler extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBottler> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityBottler(), ModBlocks.Bottler);

	public static final int DEFAULT_PROCESSING_TIME = 40;
	public static final int DEFAULT_PROCESSING_COST = 5;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;

	public final FluidTankComponent fluidTankComponent;

	public TileEntityBottler() {
		super(TYPE);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return isValidInput(stack);
			}

		}));

		// Setup all the other inventories.;
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Use the old processing system because we need to support NON recipe based
		// processing as well as recipe based.
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", 2, this::canMoveFromInputToProcessing, () -> ProcessingCheckState.ok(), this::movingCompleted, true)
				.setRedstoneControlComponent(redstoneControlComponent));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true).setUpgradeInventory(upgradesInventory).setRedstoneControlComponent(redstoneControlComponent).setEnergyComponent(energyStorage)
				.setProcessingPowerUsage(DEFAULT_PROCESSING_COST));

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanDrain(false);
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	/**
	 * Checks to see if the furnace can being processing. It checks for a valid
	 * input item, if there is enough power for one tick of processing (the
	 * processing can get stuck half way through), and checks to see if the output
	 * slot can contain the recipe output.
	 * 
	 * @return
	 */
	protected ProcessingCheckState canMoveFromInputToProcessing() {
		if (hasValidInput() && hasFluidForInput(inputInventory.getStackInSlot(0)) && internalInventory.getStackInSlot(0).isEmpty() && fluidTankComponent.getFluidAmount() > 0
				&& energyStorage.hasEnoughPower(processingComponent.getPowerUsage())) {
			if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, getSimulatedFilledContainer(inputInventory.getStackInSlot(0)))) {
				return ProcessingCheckState.ok();
			}
		}
		return ProcessingCheckState.error("ERROR");
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing.
	 * 
	 * @return
	 */
	protected ProcessingCheckState movingCompleted() {
		if (hasValidInput() && hasFluidForInput(inputInventory.getStackInSlot(0))) {
			// Transfer the items to the internal inventory. If this process is the result
			// of a recipe, make sure we respect the count in the recipe.
			BottleRecipe recipe = getRecipe(inputInventory.getStackInSlot(0));
			if (recipe != null) {
				transferItemInternally(recipe.getEmptyBottle().getCount(), inputInventory, 0, internalInventory, 0);
			} else {
				transferItemInternally(inputInventory, 0, internalInventory, 0);
			}

			// Trigger a block update.
			markTileEntityForSynchronization();
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.skip();
	}

	protected ProcessingCheckState canProcess() {
		ItemStack output = getSimulatedFilledContainer(inputInventory.getStackInSlot(0));

		// If the input is invalid, just keep going.
		if (isValidInput(internalInventory.getStackInSlot(0))) {
			return ProcessingCheckState.ok();
		}

		// Make sure we have the proper input fluid.
		if (hasFluidForInput(inputInventory.getStackInSlot(0))) {
			return ProcessingCheckState.notCorrectFluid();
		}
		if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected ProcessingCheckState processingCompleted() {
		if (!internalInventory.getStackInSlot(0).isEmpty()) {
			// Get simulated output.
			ItemStack output = getSimulatedFilledContainer(internalInventory.getStackInSlot(0));

			// If we can't store the filled output in the output slot, return false.
			if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
				return ProcessingCheckState.outputsCannotTakeRecipe();
			}

			// Attempt to fill the container and capture the result if this is a fluid
			// container, otherwise just put the output into the output slot.
			if (FluidUtil.getFluidHandler(output).isPresent()) {
				FluidActionResult result = FluidUtil.tryFillContainer(internalInventory.getStackInSlot(0), fluidTankComponent, fluidTankComponent.getFluidAmount(), null, true);

				// Insert the filled container into the output slot.
				outputInventory.insertItem(0, result.getResult().copy(), false);
			} else {
				outputInventory.insertItem(0, output.copy(), false);
				fluidTankComponent.drain(getRecipe(internalInventory.getStackInSlot(0)).getFluid().getAmount(), FluidAction.EXECUTE);
			}

			// Clear the internal inventory.
			internalInventory.setStackInSlot(0, ItemStack.EMPTY);
			markTileEntityForSynchronization();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void process() {
		// Use power if we are processing.
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				energyStorage.useBulkPower(DEFAULT_PROCESSING_COST);
			}
		}
	}

	public boolean hasValidInput() {
		return isValidInput(inputInventory.getStackInSlot(0));
	}

	public boolean isValidInput(ItemStack stack) {
		if (FluidUtil.getFluidHandler(stack).isPresent()) {
			IFluidHandler handler = FluidUtil.getFluidHandler(stack).orElse(null);
			if (handler != null && handler.getFluidInTank(0).getAmount() < handler.getTankCapacity(0)) {
				return true;
			}
			return false;
		} else {
			return getRecipe(stack) != null;
		}
	}

	public boolean hasFluidForInput(ItemStack stack) {
		if (!isValidInput(stack)) {
			return false;
		}

		BottleRecipe recipe = getRecipe(inputInventory.getStackInSlot(0));
		if (recipe != null) {
			if (fluidTankComponent.getFluid().isFluidEqual(recipe.getFluid()) && fluidTankComponent.getFluidAmount() >= recipe.getFluid().getAmount()) {
				return true;
			}
		} else {
			return fluidTankComponent.getFluidAmount() > 0;
		}
		return false;
	}

	protected ItemStack getSimulatedFilledContainer(ItemStack stack) {
		ItemStack output = stack.copy();

		if (FluidUtil.getFluidHandler(output).isPresent()) {
			// Attempt to fill the container and capture the result.
			// Simulate to drain the container and capture the result.
			FluidTank simulatedTank = new FluidTank(fluidTankComponent.getTankCapacity(0));
			simulatedTank.fill(fluidTankComponent.getFluidInTank(0), FluidAction.EXECUTE);
			FluidActionResult result = FluidUtil.tryFillContainer(output, simulatedTank, fluidTankComponent.getCapacity(), null, true);

			return result.result;
		} else {
			BottleRecipe recipe = getRecipe(stack);
			if (recipe != null) {
				return recipe.getFilledBottle();
			}
		}
		return ItemStack.EMPTY;
	}

	protected BottleRecipe getRecipe(ItemStack stack) {
		return StaticPowerRecipeRegistry.getRecipe(BottleRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluidTankComponent.getFluid()).setItems(stack)).orElse(null);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerBottler(windowId, inventory, this);
	}
}
