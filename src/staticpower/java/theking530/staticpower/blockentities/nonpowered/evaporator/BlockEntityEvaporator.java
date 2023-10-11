package theking530.staticpower.blockentities.nonpowered.evaporator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.heat.HeatUtilities;
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
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderEvaporator;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityEvaporator extends BlockEntityBase implements IRecipeProcessor<EvaporatorRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityEvaporator> TYPE = new BlockEntityTypeAllocator<BlockEntityEvaporator>(
			"evaporator", (type, pos, state) -> new BlockEntityEvaporator(pos, state), ModBlocks.Evaporator);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderEvaporator::new);
		}
	}

	public static final int DEFAULT_TANK_SIZE = 5000;

	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<EvaporatorRecipe> processingComponent;
	public final FluidTankComponent inputTankComponent;
	public final FluidTankComponent outputTankComponent;
	public final HeatStorageComponent heatStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	public BlockEntityEvaporator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticCoreTier tierObject = getTierObject();
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent",
				RedstoneMode.Ignore));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				DefaultMachineNoFacePreset.INSTANCE));

		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(processingComponent = new RecipeProcessingComponent<EvaporatorRecipe>("ProcessingComponent",
				EvaporatorRecipe.DEFAULT_PROCESSING_TIME, ModRecipeTypes.EVAPORATOR_RECIPE_TYPE.get())
				.setShouldControlOnBlockState(true).setUpgradeInventory(upgradesInventory)
				.setRedstoneControlComponent(redstoneControlComponent).setPreProductionTime(0));

		registerComponent(inputTankComponent = new FluidTankComponent("InputFluidTank",
				tierObject.defaultTankCapacity.get(), (fluidStack) -> {
					return processingComponent
							.getRecipe(new RecipeMatchParameters().setFluids(fluidStack).ignoreFluidAmounts())
							.isPresent();
				}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		inputTankComponent.setAutoSyncPacketsEnabled(true);

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
		return new ContainerEvaporator(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<EvaporatorRecipe> component) {
		return new RecipeMatchParameters().setFluids(inputTankComponent.getFluid());
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<EvaporatorRecipe> component, EvaporatorRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addFluid(recipe.getOutputFluid().copy());
	}

	@Override
	public void prepareComponentForProcessing(RecipeProcessingComponent<EvaporatorRecipe> component,
			EvaporatorRecipe recipe, ConcretizedProductContainer outputContainer) {
		heatStorage.setMinimumHeatThreshold(recipe.getProcessingSection().getMinimumHeat());
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<EvaporatorRecipe> component,
			EvaporatorRecipe recipe, ConcretizedProductContainer outputContainer) {
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
		if (heatStorage.getTemperature() < recipe.getProcessingSection().getMinimumHeat()) {
			return ProcessingCheckState.notEnoughHeatCapacity(recipe.getProcessingSection().getMinimumHeat());
		}

		if (heatStorage.isOverheated()) {
			return ProcessingCheckState.heatStorageTooHot(heatStorage.getOverheatThreshold());
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<EvaporatorRecipe> component, EvaporatorRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addFluid(inputTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE));
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<EvaporatorRecipe> component,
			ProcessingContainer processingContainer) {
		outputTankComponent.fill(processingContainer.getOutputs().getFluid(0), FluidAction.EXECUTE);
		heatStorage.cool(HeatUtilities.calculateHeatFluxRequiredForTemperatureChange(
				component.getProcessingRecipe().get().getProcessingSection().getHeat(), heatStorage.getSpecificHeat(),
				heatStorage.getMass()), HeatTransferAction.EXECUTE);
	}
}
