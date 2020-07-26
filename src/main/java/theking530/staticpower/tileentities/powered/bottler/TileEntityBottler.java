package theking530.staticpower.tileentities.powered.bottler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityBottler extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 40;
	public static final int DEFAULT_PROCESSING_COST = 15;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batterySlot;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntityBottler() {
		super(ModTileEntityTypes.BOTTLER);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return isValidInput(stack);
			}

		}));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batterySlot = new InventoryComponent("BatterySlot", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));

		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", 2, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true));

		registerComponent(new BatteryComponent("BatteryComponent", batterySlot, 0, energyStorage.getStorage()));
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Input));
		fluidTankComponent.setCanDrain(false);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));
	}

	/**
	 * Checks to see if the furnace can being processing. It checks for a valid
	 * input item, if there is enough power for one tick of processing (the
	 * processing can get stuck half way through), and checks to see if the output
	 * slot can contain the recipe output.
	 * 
	 * @return
	 */
	protected boolean canMoveFromInputToProcessing() {
		if (hasValidInput() && hasFluidForInput(inputInventory.getStackInSlot(0)) && !moveComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty() && energyStorage.getStorage().getEnergyStored() >= DEFAULT_PROCESSING_COST
				&& fluidTankComponent.getFluidAmount() > 0) {
			return InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, getSimulatedFilledContainer(inputInventory.getStackInSlot(0)));
		}
		return false;
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing.
	 * 
	 * @return
	 */
	protected boolean movingCompleted() {
		if (hasValidInput() && hasFluidForInput(inputInventory.getStackInSlot(0))) {
			// Transfer the items to the internal inventory.
			transferItemInternally(inputInventory, 0, internalInventory, 0);

			// Trigger a block update.
			markTileEntityForSynchronization();
		}
		return true;
	}

	protected boolean canProcess() {
		ItemStack output = getSimulatedFilledContainer(inputInventory.getStackInSlot(0));
		return isValidInput(internalInventory.getStackInSlot(0)) && hasFluidForInput(inputInventory.getStackInSlot(0)) && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(DEFAULT_PROCESSING_COST)
				&& InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output);
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		if (!getWorld().isRemote && !internalInventory.getStackInSlot(0).isEmpty()) {
			// Get simulated output.
			ItemStack output = getSimulatedFilledContainer(internalInventory.getStackInSlot(0));

			// If we can't store the filled output in the output slot, return false.
			if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
				return false;
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
			return true;

		}
		return false;
	}

	@Override
	public void process() {
		// Use power if we are processing.
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				energyStorage.usePower(DEFAULT_PROCESSING_COST);
			}
		}
	}

	public boolean hasValidInput() {
		return isValidInput(inputInventory.getStackInSlot(0));
	}

	public boolean isValidInput(ItemStack stack) {
		if (FluidUtil.getFluidHandler(stack).isPresent()) {
			return true;
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
		BottleRecipe recipe = StaticPowerRecipeRegistry.getRecipe(BottleRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluidTankComponent.getFluid()).setItems(stack)).orElse(null);
		if (recipe != null) {
			return recipe;
		}
		return null;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerBottler(windowId, inventory, this);
	}
}