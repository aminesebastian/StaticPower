package theking530.staticpower.tileentities.powered.fermenter;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFermenter extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFermenter> TYPE = new BlockEntityTypeAllocator<>((type) -> new TileEntityFermenter(), ModBlocks.Fermenter);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<FermenterRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntityFermenter() {
		super(TYPE, StaticPowerTiers.BASIC);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FermenterRecipe>("ProcessingComponent", FermenterRecipe.RECIPE_TYPE,
				StaticPowerConfig.SERVER.fermenterProcessingTime.get(), this::getMatchParameters, this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.fermenterPowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000).setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidOutputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

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
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getResultItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (!fluidTankComponent.getFluid().isEmpty() && !recipe.getOutputFluidStack().isFluidEqual(fluidTankComponent.getFluid())) {
			return ProcessingCheckState.outputFluidDoesNotMatch();
		}
		if (fluidTankComponent.getFluid().getAmount() + recipe.getOutputFluidStack().getAmount() > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		int slot = getSlotToProccess();
		transferItemInternally(recipe.getInputIngredient().getCount(), inputInventory, slot, internalInventory, 0);
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
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFermenter(windowId, inventory, this);
	}
}