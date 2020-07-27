package theking530.staticpower.tileentities.powered.fluidinfuser;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
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

public class TileEntityFluidInfuser extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final int DEFAULT_PROCESSING_COST = 10;
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

	private int powerCost;

	public TileEntityFluidInfuser() {
		super(ModTileEntityTypes.FLUID_INFUSER);

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

		powerCost = DEFAULT_PROCESSING_COST;
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
		if (hasValidInput() && !moveComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			FluidInfusionRecipe recipe = getRecipe(inputInventory.getStackInSlot(0)).get();
			if (energyStorage.getStorage().getEnergyStored() >= recipe.getPowerCost() && fluidTankComponent.getFluidAmount() > 0) {
				return InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem());
			}
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
		if (hasValidInput()) {
			// Update the power cost and processing time.
			FluidInfusionRecipe recipe = getRecipe(inputInventory.getStackInSlot(0)).get();
			powerCost = recipe.getPowerCost();
			processingComponent.setProcessingTime(recipe.getProcessingTime());

			// Transfer the items to the internal inventory.
			transferItemInternally(inputInventory, 0, internalInventory, 0);

			// Trigger a block update.
			markTileEntityForSynchronization();
		}
		return true;
	}

	protected boolean canProcess() {
		if (isValidInput(internalInventory.getStackInSlot(0))) {
			FluidInfusionRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
			return redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(powerCost) && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem());
		}
		return false;
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
			// If we have an item in the internal inventory, but not a valid output, just
			// return true. It is possible that a recipe was modified and no longer is
			// valid.
			if (!isValidInput(internalInventory.getStackInSlot(0))) {
				return true;
			}

			// Get recipe.
			FluidInfusionRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);

			// If we can't store the filled output in the output slot, return false.
			if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
				return false;
			}

			// Output the item if the dice roll passes.
			if (SDMath.diceRoll(recipe.getOutput().getOutputChance())) {
				outputInventory.insertItem(0, recipe.getOutput().getItem().copy(), false);
			}

			// Drain the fluid.
			fluidTankComponent.drain(recipe.getRequiredFluid().getAmount(), FluidAction.EXECUTE);

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
				energyStorage.usePower(powerCost);
			}
		}
	}

	public boolean hasValidInput() {
		return isValidInput(inputInventory.getStackInSlot(0));
	}

	public boolean isValidInput(ItemStack stack) {
		return getRecipe(stack).isPresent();
	}

	protected Optional<FluidInfusionRecipe> getRecipe(ItemStack stack) {
		FluidInfusionRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FluidInfusionRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluidTankComponent.getFluid()).setItems(stack)).orElse(null);
		if (recipe != null) {
			return Optional.of(recipe);
		}
		return Optional.empty();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFluidInfuser(windowId, inventory, this);
	}
}
