package theking530.staticpower.tileentities.powered.lumbermill;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.utilities.SDMath;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityLumberMill extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 1000;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntityLumberMill() {
		super(ModTileEntityTypes.LUMBER_MILL);
		this.disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return getRecipe(stack).isPresent();
			}
		}));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", 2, this::movingCompleted));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::processingCompleted));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory, 0, 1, 2));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000).setCapabilityExposedModes(MachineSideMode.Output));
		fluidTankComponent.setCanFill(false);
	}

	@Override
	public void process() {
		if (canStartProcess() && redstoneControlComponent.passesRedstoneCheck()) {
			moveComponent.startProcessing();
		} else if (processingComponent.isProcessing()) {
			if (!getRecipe(internalInventory.getStackInSlot(0)).isPresent()) {
				processingComponent.pauseProcessing();
			} else {
				processingComponent.continueProcessing();
				energyStorage.getStorage().extractEnergy(DEFAULT_PROCESSING_COST / DEFAULT_PROCESSING_TIME, false);
			}
		}
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing.
	 * 
	 * @return
	 */
	protected boolean movingCompleted() {
		if (hasValidInputRecipe()) {
			if (!getWorld().isRemote) {
				transferItemInternally(inputInventory, 0, internalInventory, 0);
			}
			processingComponent.startProcessing();
		}
		return true;
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		// If on the server.
		if (!getWorld().isRemote) {
			// Get the recipe.
			LumberMillRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).get();

			// Ensure the output slots can take the recipe.
			if (canOutputsTakeRecipeResult(recipe)) {
				if (SDMath.diceRoll(recipe.getPrimaryOutput().getPercentage())) {
					outputInventory.insertItem(0, recipe.getPrimaryOutput().getItem().copy(), false);
				}
				if (SDMath.diceRoll(recipe.getSecondaryOutput().getPercentage())) {
					outputInventory.insertItem(1, recipe.getSecondaryOutput().getItem().copy(), false);
				}
				fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canStartProcess() {
		// Check if there is a valid recipe.
		if (hasValidInputRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			// Gets the recipe and its outputs.
			Optional<LumberMillRecipe> recipe = getRecipe(inputInventory.getStackInSlot(0));

			// If the items cannot be insert into the output, return false.
			if (!canOutputsTakeRecipeResult(recipe.get())) {
				return false;
			}
			// If we passed all the previous checks, return true.
			return true;
		}
		return false;
	}

	/**
	 * Ensure the output slots can take the results of the crafting.
	 * 
	 * @param recipe
	 * @return
	 */
	public boolean canOutputsTakeRecipeResult(LumberMillRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getPrimaryOutput().getItem())) {
			return false;
		} else if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 1, recipe.getSecondaryOutput().getItem())) {
			return false;
		} else if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return false;
		}
		return true;
	}

	// Functionality
	public boolean hasValidInputRecipe() {
		return getRecipe(inputInventory.getStackInSlot(0)).isPresent();
	}

	public Optional<LumberMillRecipe> getRecipe(ItemStack itemStackInput) {
		return StaticPowerRecipeRegistry.getRecipe(LumberMillRecipe.RECIPE_TYPE, new RecipeMatchParameters(itemStackInput).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerLumberMill(windowId, inventory, this);
	}
}
