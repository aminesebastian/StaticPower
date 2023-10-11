package theking530.staticpower.blockentities.nonpowered.condenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityCondenser extends BlockEntityBase implements IRecipeProcessor<CondensationRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCondenser> TYPE = new BlockEntityTypeAllocator<>(
			"condenser", (type, pos, state) -> new BlockEntityCondenser(pos, state), ModBlocks.Condenser);

	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<CondensationRecipe> processingComponent;
	public final FluidTankComponent inputTankComponent;
	public final FluidTankComponent outputTankComponent;
	public final HeatStorageComponent heatStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	public BlockEntityCondenser(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticCoreTier tierObject = getTierObject();
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent",
				RedstoneMode.Ignore));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				DefaultMachineNoFacePreset.INSTANCE));

		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(processingComponent = new RecipeProcessingComponent<CondensationRecipe>("ProcessingComponent",
				CondensationRecipe.DEFAULT_PROCESSING_TIME, ModRecipeTypes.CONDENSATION_RECIPE_TYPE.get())
				.setShouldControlOnBlockState(true).setUpgradeInventory(upgradesInventory)
				.setRedstoneControlComponent(redstoneControlComponent).setPreProductionTime(0));

		registerComponent(inputTankComponent = new FluidTankComponent("InputFluidTank",
				tierObject.defaultTankCapacity.get(), (fluidStack) -> {
					return processingComponent
							.getRecipe(new RecipeMatchParameters().setFluids(fluidStack).ignoreFluidAmounts())
							.isPresent();
				}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));

		registerComponent(
				outputTankComponent = new FluidTankComponent("OutputFluidTank", tierObject.defaultTankCapacity.get())
						.setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, inputTankComponent,
				MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, outputTankComponent,
				MachineSideMode.Output));

		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tierObject));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCondenser(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CondensationRecipe> component) {
		return new RecipeMatchParameters().setFluids(inputTankComponent.getFluid());
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<CondensationRecipe> component, CondensationRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addFluid(recipe.getOutputFluid().copy());
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<CondensationRecipe> component,
			CondensationRecipe recipe, ConcretizedProductContainer outputContainer) {
		// Check if the output fluid matches the already exists fluid if one exists.
		if (!outputTankComponent.getFluid().isEmpty()
				&& !outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid())) {
			return ProcessingCheckState.outputFluidDoesNotMatch();
		}

		// Check the fluid capacity.
		if (outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent
				.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		// Check the heat level.
		if (heatStorage.getTemperature() + recipe.getProcessingSection().getHeat() > heatStorage
				.getOverheatThreshold()) {
			return ProcessingCheckState.notEnoughHeatCapacity(recipe.getProcessingSection().getHeat());
		}
		if (heatStorage.getTemperature() > recipe.getProcessingSection().getMinimumHeat()) {
			return ProcessingCheckState.heatStorageTooHot(recipe.getProcessingSection().getMinimumHeat());
		}

		// If all the checks pass, return ok.
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<CondensationRecipe> component, CondensationRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addFluid(inputTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE));
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<CondensationRecipe> component,
			ProcessingContainer processingContainer) {
		outputTankComponent.fill(processingContainer.getOutputs().getFluid(0), FluidAction.EXECUTE);
		heatStorage.heat(component.getProcessingRecipe().get().getProcessingSection().getHeat(),
				HeatTransferAction.EXECUTE);
	}
}
