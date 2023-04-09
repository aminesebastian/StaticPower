package theking530.staticpower.blockentities.power.fluidgenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.PowerStack;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.energy.PowerDistributionComponent;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidGenerator;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityFluidGenerator extends BlockEntityMachine implements IOldRecipeProcessor<FluidGeneratorRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidGenerator> TYPE = new BlockEntityTypeAllocator<BlockEntityFluidGenerator>("fluid_generator",
			(type, pos, state) -> new BlockEntityFluidGenerator(pos, state), ModBlocks.FluidGenerator);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderFluidGenerator::new);
		}
	}

	public final UpgradeInventoryComponent upgradesInventory;
	public final OldRecipeProcessingComponent<FluidGeneratorRecipe> processingComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final LoopingSoundComponent generatingSoundComponent;

	public BlockEntityFluidGenerator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(processingComponent = new OldRecipeProcessingComponent<FluidGeneratorRecipe>("ProcessingComponent", ModRecipeTypes.FLUID_GENERATOR_RECIPE_TYPE.get(), this));
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setMaxProcessingTime(0);
		processingComponent.setMoveTime(0);

		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		powerStorage.setCanAcceptExternalPower(false).setSideConfiguration(ioSideConfiguration).setUpgradeInventory(upgradesInventory);
		registerComponent(new PowerDistributionComponent("PowerDistributor", powerStorage));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", getTierObject().defaultTankCapacity.get(), (fluidStack) -> {
			return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(fluidStack)).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));
		registerComponent(new FluidInputServoComponent("InputServo", 20, fluidTankComponent, MachineSideMode.Input));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			if (processingComponent.performedWorkLastTick()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 0.1f, 0.75f, getBlockPos(), 64);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}
		}
	}

	protected ProcessingCheckState processingCompleted(FluidGeneratorRecipe recipe) {
		powerStorage.addPower(new PowerStack(recipe.getPowerGeneration(), powerStorage.getInputVoltageRange().maximumVoltage()), false);
		fluidTankComponent.drain(recipe.getFluid().getAmount(), FluidAction.EXECUTE);
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFluidGenerator(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<FluidGeneratorRecipe> component) {
		return new RecipeMatchParameters(fluidTankComponent.getFluid());
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<FluidGeneratorRecipe> component, FluidGeneratorRecipe recipe,
			OldProcessingContainer outputContainer) {
		// Do nothing if the input tank is empty.
		if (fluidTankComponent.getFluidAmount() == 0) {
			return ProcessingCheckState.skip();
		}

		// Check to make sure we can store power.
		powerStorage.setMaximumInputPower(recipe.getPowerGeneration());
		if (powerStorage.canFullyAcceptPower(recipe.getPowerGeneration())) {
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.error("Energy Storage Full!");
		}
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<FluidGeneratorRecipe> component, FluidGeneratorRecipe recipe, OldProcessingContainer outputContainer) {
		powerStorage.setMaximumOutputPower(recipe.getPowerGeneration());
		powerStorage.setMaximumInputPower(recipe.getPowerGeneration());
		outputContainer.setOutputPower(recipe.getPowerGeneration());
		outputContainer.addInputFluid(fluidTankComponent.getFluid(), recipe.getFluid().getAmount(), CaptureType.BOTH);
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<FluidGeneratorRecipe> component, FluidGeneratorRecipe recipe, OldProcessingContainer outputContainer) {
		powerStorage.addPower(new PowerStack(recipe.getPowerGeneration(), powerStorage.getInputVoltageRange().maximumVoltage()), false);
		fluidTankComponent.drain(outputContainer.getInputFluid(0).fluid(), FluidAction.EXECUTE);
	}
}
