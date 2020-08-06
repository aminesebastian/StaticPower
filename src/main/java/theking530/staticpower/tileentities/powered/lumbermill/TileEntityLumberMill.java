package theking530.staticpower.tileentities.powered.lumbermill;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent;
import theking530.staticpower.tileentities.components.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityLumberMill extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final InventoryComponent fluidContainerInventory;

	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerComponent fluidContainerComponent;
	
	public TileEntityLumberMill() {
		super(ModTileEntityTypes.LUMBER_MILL);
		this.disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return getRecipe(stack).isPresent();
			}
		}));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", DEFAULT_MOVING_TIME, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 1, mainOutputInventory));
		registerComponent(new OutputServoComponent("SecondaryOutputServo", 1, secondaryOutputInventory));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output));
		fluidTankComponent.setCanFill(false);
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2, MachineSideMode.Never));
		registerComponent(fluidContainerComponent = new FluidContainerComponent("FluidFillContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1).setMode(FluidContainerInteractionMode.FILL));
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canMoveFromInputToProcessing() {
		if (!redstoneControlComponent.passesRedstoneCheck()) {
			return false;
		}
		// Check if there is a valid recipe.
		if (hasValidRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			// Gets the recipe and its outputs.
			Optional<LumberMillRecipe> recipe = getRecipe(inputInventory.getStackInSlot(0));

			// If the recipe cannot be insert into the output, return false.
			if (!canOutputsTakeRecipeResult(recipe.get())) {
				return false;
			}

			// If we passed all the previous checks, return true.
			return energyStorage.hasEnoughPower(recipe.get().getPowerCost());
		}
		return false;
	}

	public boolean canProcess() {
		LumberMillRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
		return recipe != null && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(recipe.getPowerCost()) && canOutputsTakeRecipeResult(recipe)
				&& fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) == recipe.getOutputFluid().getAmount();
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				getRecipe(internalInventory.getStackInSlot(0)).ifPresent(recipe -> {
					energyStorage.usePower(recipe.getPowerCost());
				});
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
		if (hasValidRecipe()) {
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			markTileEntityForSynchronization();
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
				if (SDMath.diceRoll(recipe.getPrimaryOutput().getOutputChance())) {
					mainOutputInventory.insertItem(0, recipe.getPrimaryOutput().getItem().copy(), false);
				}
				if (SDMath.diceRoll(recipe.getSecondaryOutput().getOutputChance())) {
					secondaryOutputInventory.insertItem(0, recipe.getSecondaryOutput().getItem().copy(), false);
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
		if (hasValidRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
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
	protected boolean canOutputsTakeRecipeResult(LumberMillRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(mainOutputInventory, 0, recipe.getPrimaryOutput().getItem())) {
			return false;
		} else if (!InventoryUtilities.canFullyInsertStackIntoSlot(secondaryOutputInventory, 0, recipe.getSecondaryOutput().getItem())) {
			return false;
		} else if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Output2
				|| mode == MachineSideMode.Output3;
	}

	// Functionality
	public boolean hasValidRecipe() {
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
