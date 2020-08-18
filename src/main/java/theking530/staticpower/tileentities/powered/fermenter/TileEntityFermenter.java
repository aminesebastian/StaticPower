package theking530.staticpower.tileentities.powered.fermenter;

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
import theking530.staticpower.tileentities.components.control.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFermenter extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_COST = 20;
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent fluidContainerInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FermenterRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerComponent fluidContainerComponent;

	public TileEntityFermenter() {
		super(ModTileEntityTypes.FERMENTER);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack)).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FermenterRecipe>("ProcessingComponent", FermenterRecipe.RECIPE_TYPE, DEFAULT_PROCESSING_TIME, this::getMatchParameters,
				this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000).setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidOutputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerComponent("FluidContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			int slot = getSlotToProccess();
			if (slot >= 0) {
				return new RecipeMatchParameters(inputInventory.getStackInSlot(slot));
			}
			return new RecipeMatchParameters();
		}
	}

	protected ProcessingCheckState moveInputs(FermenterRecipe recipe) {
		// Make sure we have a slot to process.
		if (getSlotToProccess() == -1) {
			return ProcessingCheckState.skip();
		}

		// If the items can be insert into the output, transfer the items and return
		// true.
		if (internalInventory.getStackInSlot(0).isEmpty()) {
			return ProcessingCheckState.internalInventoryNotEmpty();
		}
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (!fluidTankComponent.getFluid().isEmpty() && !recipe.getOutputFluidStack().isFluidEqual(fluidTankComponent.getFluid())) {
			return ProcessingCheckState.outputFluidDoesNotMatch();
		}
		if (fluidTankComponent.getFluid().getAmount() + recipe.getOutputFluidStack().getAmount() > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		int slot = getSlotToProccess();
		transferItemInternally(inputInventory, slot, internalInventory, 0);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(FermenterRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, new ItemStack(ModItems.DistilleryGrain))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (!fluidTankComponent.getFluid().isEmpty() && !recipe.getOutputFluidStack().isFluidEqual(fluidTankComponent.getFluid())) {
			return ProcessingCheckState.outputFluidDoesNotMatch();
		}
		if (fluidTankComponent.getFluid().getAmount() + recipe.getOutputFluidStack().getAmount() > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(FermenterRecipe recipe) {
		fluidTankComponent.fill(recipe.getOutputFluidStack(), FluidAction.EXECUTE);
		outputInventory.insertItem(0, new ItemStack(ModItems.DistilleryGrain), false);
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected int getSlotToProccess() {
		for (int i = 0; i < 9; i++) {
			FermenterRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FermenterRecipe.RECIPE_TYPE, new RecipeMatchParameters(inputInventory.getStackInSlot(i))).orElse(null);
			if (recipe != null) {
				FluidStack fermentingResult = recipe.getOutputFluidStack();
				if (fluidTankComponent.fill(fermentingResult, FluidAction.SIMULATE) == fermentingResult.getAmount()) {
					if (InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, new ItemStack(ModItems.DistilleryGrain))) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFermenter(windowId, inventory, this);
	}
}