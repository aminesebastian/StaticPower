package theking530.staticpower.tileentities.powered.squeezer;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntitySqueezer extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
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

	public TileEntitySqueezer() {
		super(ModTileEntityTypes.SQUEEZER);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return getRecipe(stack).isPresent();
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
		
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output));
		fluidTankComponent.setCanFill(false);
		
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
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
		if (hasValidRecipe() && !moveComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty() && energyStorage.getStorage().getEnergyStored() >= DEFAULT_PROCESSING_COST) {
			// Get the recipe.
			SqueezerRecipe recipe = getRecipe(inputInventory.getStackInSlot(0)).orElse(null);

			// If the recipe has a fluid output but the output tank can't take the input, we
			// can't proceed.
			if (recipe.hasOutputFluid() && fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
				return false;
			}

			// If this recipe has an item output, and if we can insert the output into the
			// output stack, return true.
			if (recipe.hasItemOutput()) {
				ItemStack output = getRecipe(inputInventory.getStackInSlot(0)).get().getOutput().getItem();
				return InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output);
			} else {
				return true;
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
		if (hasValidRecipe()) {
			// Transfer the items to the internal inventory.
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			// Update the processing time.
			SqueezerRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
			processingComponent.setProcessingTime(recipe.getProcessingTime());
			// Trigger a block update.
			markTileEntityForSynchronization();
		}
		return true;
	}

	protected boolean canProcess() {
		SqueezerRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
		return recipe != null && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(recipe.getPowerCost()) && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem());
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
			// Get the recipe.
			SqueezerRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);

			// If we somehow ended up with a null recipe (for example, they save the game,
			// then change a recipe so the item that is currently being processed no longer
			// has a recipe, and then reenter the game), just return true.
			if (recipe == null) {
				// Clear the internal inventory.
				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}

			// If this recipe has an item output that we cannot put into the output slot,
			// continue waiting.
			if (recipe.hasItemOutput() && !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
				return false;
			}

			// If this recipe has a fluid output that we cannot put into the output tank,
			// continue waiting.
			if (recipe.hasOutputFluid() && fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
				return false;
			}

			// Insert the outputs
			// Check the dice roll for the output.
			if (SDMath.diceRoll(recipe.getOutput().getPercentage())) {
				outputInventory.insertItem(0, recipe.getOutput().getItem().copy(), false);
			}

			// Fill the output tank.
			fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

			// Clear the internal inventory.
			internalInventory.setStackInSlot(0, ItemStack.EMPTY);
			markTileEntityForSynchronization();
			return true;

		}
		return false;
	}

	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				getRecipe(internalInventory.getStackInSlot(0)).ifPresent(recipe -> {
					energyStorage.usePower(recipe.getPowerCost());
				});
			}
		}
	}

	// Functionality
	public boolean hasValidRecipe() {
		return getRecipe(inputInventory.getStackInSlot(0)).isPresent();
	}

	public Optional<SqueezerRecipe> getRecipe(ItemStack itemStackInput) {
		return StaticPowerRecipeRegistry.getRecipe(SqueezerRecipe.RECIPE_TYPE, new RecipeMatchParameters().setItems(itemStackInput).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSqueezer(windowId, inventory, this);
	}
}
