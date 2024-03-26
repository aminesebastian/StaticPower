package theking530.staticpower.blockentities.power.fluidgenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.PowerStack;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingProduct;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.energy.PowerDistributionComponent;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.power.PowerProducer;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidGenerator;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityFluidGenerator extends BlockEntityMachine implements IRecipeProcessor<FluidGeneratorRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidGenerator> TYPE = new BlockEntityTypeAllocator<BlockEntityFluidGenerator>(
			"fluid_generator", (type, pos, state) -> new BlockEntityFluidGenerator(pos, state),
			ModBlocks.FluidGenerator);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderFluidGenerator::new);
		}
	}

	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FluidGeneratorRecipe> processingComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final LoopingSoundComponent generatingSoundComponent;

	public BlockEntityFluidGenerator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(processingComponent = new RecipeProcessingComponent<FluidGeneratorRecipe>(
				"ProcessingComponent", 0, ModRecipeTypes.FLUID_GENERATOR_RECIPE_TYPE.get()));
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setPreProductionTime(0);

		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		powerStorage.setCanAcceptExternalPower(false).setSideConfiguration(ioSideConfiguration)
				.setUpgradeInventory(upgradesInventory);
		registerComponent(new PowerDistributionComponent("PowerDistributor", powerStorage));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank",
				getTierObject().defaultTankCapacity.get(), (fluidStack) -> {
					return processingComponent
							.getRecipe(new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), fluidStack))
							.isPresent();
				}).setCapabilityExposedModes(MachineSideMode.Input));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerComponent",
				fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));
		registerComponent(new FluidInputServoComponent("InputServo", 20, fluidTankComponent, MachineSideMode.Input));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			if (processingComponent.performedWorkLastTick()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 0.1f, 0.75f,
						getBlockPos(), 64);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}
		}
	}

	protected ProcessingCheckState processingCompleted(FluidGeneratorRecipe recipe) {
		powerStorage.addPower(
				new PowerStack(recipe.getPowerGeneration(), powerStorage.getInputVoltageRange().maximumVoltage()),
				false);
		fluidTankComponent.drain(recipe.getFluid().getAmount(), FluidAction.EXECUTE);
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFluidGenerator(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<FluidGeneratorRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), fluidTankComponent.getFluid());
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<FluidGeneratorRecipe> component, FluidGeneratorRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addPower(component.getPowerProducerId(), recipe.getPowerGeneration());

	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<FluidGeneratorRecipe> component,
			FluidGeneratorRecipe recipe, ConcretizedProductContainer outputContainer) {
		// Check to make sure we can store power.
		double powerAmount = outputContainer.getPower(0);
		if (!powerStorage.canFullyAcceptPower(powerAmount)) {
			return ProcessingCheckState.powerOutputFull();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void prepareComponentForProcessing(RecipeProcessingComponent<FluidGeneratorRecipe> component,
			FluidGeneratorRecipe recipe, ConcretizedProductContainer outputContainer) {
		// Check to make sure we can store power.
		double powerAmount = outputContainer.getPower(0);
		powerStorage.setMaximumInputPower(powerAmount);
		powerStorage.setMaximumOutputPower(powerAmount);
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<FluidGeneratorRecipe> component, FluidGeneratorRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		FluidStack usedFluid = fluidTankComponent.drain(component.getProcessingRecipe().get().getFluid().getAmount(),
				FluidAction.EXECUTE);
		processingContainer.getInputs().addFluid(usedFluid);
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<FluidGeneratorRecipe> component,
			ProcessingContainer processingContainer) {
		ProcessingProduct<ProductType<PowerProducer>, PowerProducer> generatedPower = processingContainer.getOutputs()
				.getProductOfType(StaticCoreProductTypes.Power.get(), 0);
		powerStorage.addPower(
				new PowerStack(generatedPower.getAmount(), powerStorage.getInputVoltageRange().maximumVoltage()),
				false);
	}

}
