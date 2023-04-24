package theking530.staticpower.blockentities.machines.lumbermill;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityLumberMill extends BlockEntityMachine implements IRecipeProcessor<LumberMillRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityLumberMill> TYPE = new BlockEntityTypeAllocator<>(
			"lumber_mill", (type, pos, state) -> new BlockEntityLumberMill(pos, state), ModBlocks.LumberMill);

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<LumberMillRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityLumberMill(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticCoreTier tierObject = StaticCoreConfig.getTier(getTier());

		// Create the input inventory.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts())
								.isPresent();
					}
				}));

		// Setup all the other inventories.
		registerComponent(
				mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1,
				MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<LumberMillRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.lumberMillProcessingTime.get(), ModRecipeTypes.LUMBER_MILL_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", mainOutputInventory));
		registerComponent(new OutputServoComponent("SecondaryOutputServo", secondaryOutputInventory));

		// Setup the fluid tank and fluid servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	/**
	 * Ensure the output slots can take the results of the crafting.
	 * 
	 * @param recipe
	 * @return
	 */
	protected boolean canOutputsTakeRecipeResult(ConcretizedProductContainer outputContainer) {
		if (outputContainer.hasItems()) {
			if (!InventoryUtilities.canFullyInsertStackIntoSlot(mainOutputInventory, 0, outputContainer.getItem(0))) {
				return false;
			}

			if (outputContainer.getItems().size() > 1) {
				if (!InventoryUtilities.canFullyInsertStackIntoSlot(secondaryOutputInventory, 0,
						outputContainer.getItem(1))) {
					return false;
				}
			}
		}

		if (outputContainer.hasFluids()) {
			if (fluidTankComponent.fill(outputContainer.getFluid(0), FluidAction.SIMULATE) != outputContainer
					.getFluid(0).getAmount()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLumberMill(windowId, inventory, this);
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return LumbermillSideConfiguration.INSTANCE;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<LumberMillRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<LumberMillRecipe> component, LumberMillRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getPrimaryOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addItem(recipe.getSecondaryOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addFluid(recipe.getOutputFluid().copy(), CaptureType.BOTH);

	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<LumberMillRecipe> component,
			LumberMillRecipe recipe, ConcretizedProductContainer outputContainer) {
		// If the recipe cannot be insert into the output, return false.
		if (!canOutputsTakeRecipeResult(outputContainer)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid()
				.getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<LumberMillRecipe> component, LumberMillRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(inputInventory.extractItem(0, recipe.getInput().getCount(), false));
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<LumberMillRecipe> component,
			ProcessingContainer processingContainer) {
		if (processingContainer.getOutputs().hasItems()) {
			mainOutputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
			if (processingContainer.getOutputs().getItems().size() > 1) {
				secondaryOutputInventory.insertItem(0, processingContainer.getOutputs().getItem(1).copy(), false);
			}
		}

		if (processingContainer.getOutputs().hasFluids()) {
			fluidTankComponent.fill(processingContainer.getOutputs().getFluid(0), FluidAction.EXECUTE);
		}
	}
}
