package theking530.staticpower.tileentities.powered.autosmith;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.itemattributes.capability.CapabilityAttributable;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe.RecipeModifierWrapper;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityAutoSmith extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityAutoSmith> TYPE = new TileEntityTypeAllocator<TileEntityAutoSmith>((allocator) -> new TileEntityAutoSmith(), ModBlocks.AutoSmith);

	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final int DEFAULT_PROCESSING_COST = 5;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batteryInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<AutoSmithRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntityAutoSmith() {
		super(TYPE);

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input).setShiftClickEnabled(true));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 2));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		});
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<AutoSmithRecipe>("ProcessingComponent", AutoSmithRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs, this::canProcessRecipe,
				this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanDrain(false);
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(AutoSmithRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Transfer the inputs to internal buffers.
		transferItemInternally(recipe.getSmithTarget().getCount(), inputInventory, 0, internalInventory, 0);
		transferItemInternally(recipe.getModifierMaterial().getCount(), inputInventory, 1, internalInventory, 1);

		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(AutoSmithRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(AutoSmithRecipe recipe) {
		// Modify the input that we put in the buffer, and then put it into the output.
		ItemStack output = internalInventory.getStackInSlot(0);

		// Add the modifiers.
		output.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributes -> {
			// For all of the modifiers, if the item has the attribute, add the modifier.
			for (RecipeModifierWrapper modifier : recipe.getModifiers()) {
				if (attributes.hasAttribute(modifier.getAttributeId())) {
					attributes.getAttribute(modifier.getAttributeId()).addModifier(modifier.getModifier(), false);
				}
			}
		});

		// Put the item into the output slot.
		outputInventory.insertItem(0, output, false);

		// Drain the fluid.
		fluidTankComponent.drain(recipe.getModifierFluid().getAmount(), FluidAction.EXECUTE);

		// Clear the internal inventory.
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		internalInventory.setStackInSlot(1, ItemStack.EMPTY);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerAutoSmith(windowId, inventory, this);
	}
}
