package theking530.staticpower.blockentities.machines.fermenter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
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
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityFermenter extends BlockEntityMachine implements IRecipeProcessor<FermenterRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFermenter> TYPE = new BlockEntityTypeAllocator<>(
			"fermenter", (type, pos, state) -> new BlockEntityFermenter(pos, state), ModBlocks.Fermenter);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<FermenterRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityFermenter(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent
								.getRecipe(new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), stack)
										.ignoreItemCounts())
								.isPresent();
					}
				}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FermenterRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.fermenterProcessingTime.get(), ModRecipeTypes.FERMENTER_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setBasePowerUsage(StaticPowerConfig.SERVER.fermenterPowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000)
				.setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidOutputServoComponent("FluidInputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected int getSlotToProccess() {
		for (int i = 0; i < 9; i++) {
			FermenterRecipe recipe = CraftingUtilities
					.getRecipe(ModRecipeTypes.FERMENTER_RECIPE_TYPE.get(), new RecipeMatchParameters(
							getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(i)), getLevel())
					.orElse(null);
			if (recipe != null) {
				FluidStack fermentingResult = recipe.getOutputFluidStack();
				if (fluidTankComponent.fill(fermentingResult, FluidAction.SIMULATE) == fermentingResult.getAmount()) {
					if (InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory,
							recipe.getResidualOutput().getItemStack())) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<FermenterRecipe> component) {
		int slot = getSlotToProccess();
		if (slot >= 0) {
			return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(slot));
		}
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId());
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFermenter(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<FermenterRecipe> component, FermenterRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getResidualOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addFluid(recipe.getOutputFluidStack(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<FermenterRecipe> component,
			FermenterRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (outputContainer.hasItems()) {
			if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, outputContainer.getItem(0))) {
				return ProcessingCheckState.outputsCannotTakeRecipe();
			}
		}

		if (!fluidTankComponent.getFluid().isEmpty()
				&& !outputContainer.getFluid(0).isFluidEqual(fluidTankComponent.getFluid())) {
			return ProcessingCheckState.outputFluidDoesNotMatch();
		}

		if (fluidTankComponent.getFluid().getAmount() + outputContainer.getFluid(0).getAmount() > fluidTankComponent
				.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<FermenterRecipe> component, FermenterRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		int slot = getSlotToProccess();
		inputContainer.addItem(inputInventory.extractItem(slot, recipe.getInputIngredient().getCount(), false));

	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<FermenterRecipe> component,
			ProcessingContainer processingContainer) {
		if (processingContainer.getOutputs().hasItems()) {
			outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
		}

		fluidTankComponent.fill(processingContainer.getOutputs().getFluid(0), FluidAction.EXECUTE);
	}
}