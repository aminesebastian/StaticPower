package theking530.staticpower.tileentities.powered.fermenter;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFermenter extends TileEntityMachine {
	public static final int DEFAULT_POWER_USAGE = 20;
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent fluidContainerInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerComponent fluidContainerComponent;

	private int powerCost;

	public TileEntityFermenter() {
		super(ModTileEntityTypes.FERMENTER);
		disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return getRecipe(stack).isPresent();
			}
		}));
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", DEFAULT_MOVING_TIME, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000).setCapabilityExposedModes(MachineSideMode.Output));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory));
		registerComponent(fluidContainerComponent = new FluidContainerComponent("FluidContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1).setMode(FluidContainerInteractionMode.FILL));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));

		powerCost = DEFAULT_POWER_USAGE;
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canMoveFromInputToProcessing() {
		if (!redstoneControlComponent.passesRedstoneCheck() || processingComponent.isProcessing()) {
			return false;
		}
		return getSlotToProccess() >= 0;
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing. Return true regardless so the
	 * movement component resets.
	 * 
	 * @return
	 */
	protected boolean movingCompleted() {
		if (hasValidRecipe()) {
			transferItemInternally(inputInventory, getSlotToProccess(), internalInventory, 0);
			markTileEntityForSynchronization();
		}
		return true;
	}

	/**
	 * Indicates if we can start or continue processing. If this returns false and
	 * we are processing, processing pauses. If we are not processing and this
	 * returns true, we will start processing.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		FermenterRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
		return recipe != null && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(powerCost)
				&& InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, new ItemStack(ModItems.DistilleryGrain));
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		FermenterRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
		if (recipe != null) {
			fluidTankComponent.fill(recipe.getOutputFluidStack(), FluidAction.EXECUTE);
			outputInventory.insertItem(0, new ItemStack(ModItems.DistilleryGrain), false);
			internalInventory.setStackInSlot(0, ItemStack.EMPTY);
			markTileEntityForSynchronization();
			return true;
		}
		return false;
	}

	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				energyStorage.usePower(powerCost);
			}
		}
	}

	public boolean hasValidRecipe() {
		for (int i = 0; i < 9; i++) {
			if (getRecipe(inputInventory.getStackInSlot(i)).isPresent()) {
				return true;
			}
		}
		return false;
	}

	protected int getSlotToProccess() {
		for (int i = 0; i < 9; i++) {
			FermenterRecipe recipe = getRecipe(inputInventory.getStackInSlot(i)).orElse(null);
			if (recipe != null) {
				FluidStack fermentingResult = recipe.getOutputFluidStack();
				if (fluidTankComponent.fill(fermentingResult, FluidAction.SIMULATE) == fermentingResult.getAmount()) {
					if (energyStorage.hasEnoughPower(powerCost) && InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, new ItemStack(ModItems.DistilleryGrain))) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Checks if the provided itemstack forms a valid recipe.
	 * 
	 * @param itemStackInput The itemstack to check for.
	 * @return
	 */
	public Optional<FermenterRecipe> getRecipe(ItemStack itemStackInput) {
		return StaticPowerRecipeRegistry.getRecipe(FermenterRecipe.RECIPE_TYPE, new RecipeMatchParameters(itemStackInput).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFermenter(windowId, inventory, this);
	}
}