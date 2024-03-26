package theking530.staticpower.blockentities.nonpowered.cokeoven;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiblockComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState.MultiblockStateEntry;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.data.crafting.wrappers.cokeoven.CokeOvenRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModMultiblocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityCokeOven extends BlockEntityBase implements IRecipeProcessor<CokeOvenRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCokeOven> TYPE = new BlockEntityTypeAllocator<>("coke_oven",
			(type, pos, state) -> new BlockEntityCokeOven(pos, state), ModBlocks.CokeOvenBrick);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final RecipeProcessingComponent<CokeOvenRecipe> processingComponent;
	public final SideConfigurationComponent ioSideConfiguration;
	public final LoopingSoundComponent furnaceSoundComponent;
	public final MultiblockComponent multiblockComponent;

	public BlockEntityCokeOven(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(furnaceSoundComponent = new LoopingSoundComponent("FurnaceSoundComponent", 20));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true));

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				CokeOvenSideConfiguration.INSTANCE));
		registerComponent(processingComponent = new RecipeProcessingComponent<CokeOvenRecipe>("ProcessingComponent",
				CokeOvenRecipe.DEFAULT_PROCESSING_TIME, ModRecipeTypes.COKE_OVEN_RECIPE_TYPE.get())
				.setShouldControlOnBlockState(true));

		registerComponent(
				fluidTankComponent = new FluidTankComponent("FluidTank", getTierObject().defaultTankCapacity.get())
						.setCapabilityExposedModes(MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContianerComponent",
				fluidTankComponent).setAllowedModes(true, false));

		registerComponent(
				multiblockComponent = new MultiblockComponent("MultiblockComponent", ModMultiblocks.COKE_OVEN.get()));
	}

	public void process() {
		if (!getLevel().isClientSide()) {
			if (!multiblockComponent.isWellFormed()) {
				return;
			}

			// Update the is_on states.
			for (MultiblockStateEntry entry : multiblockComponent.getState().getBlocks()) {
				if (entry.blockState().hasProperty(BlockCokeOven.SHOW_FACE)
						&& entry.blockState().getValue(BlockCokeOven.SHOW_FACE)) {
					BlockState currentState = getLevel().getBlockState(entry.pos());
					if (currentState.getValue(StaticCoreBlock.IS_ON) != processingComponent.isBlockStateOn()) {
						getLevel().setBlock(entry.pos(),
								currentState.setValue(StaticCoreBlock.IS_ON, processingComponent.isBlockStateOn()), 3);
					}
				}
			}

			if (processingComponent.isBlockStateOn()) {
				furnaceSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f,
						1.0f, getBlockPos(), 64);
			} else {
				furnaceSoundComponent.stopPlayingSound();
			}
		}
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CokeOvenRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<CokeOvenRecipe> component, CokeOvenRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addFluid(recipe.getOutputFluid(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<CokeOvenRecipe> component,
			CokeOvenRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!multiblockComponent.isWellFormed()) {
			return ProcessingCheckState.error("multiblock no");
		}

		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidTankComponent.fill(outputContainer.getFluid(0), FluidAction.SIMULATE) != outputContainer.getFluid(0)
				.getAmount()) {
			if (!fluidTankComponent.getFluid().isEmpty()
					&& fluidTankComponent.getFluid().isFluidEqual(outputContainer.getFluid(0))) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			} else {
				return ProcessingCheckState.fluidOutputFull();
			}
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<CokeOvenRecipe> component, CokeOvenRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(
				inputInventory.extractItem(0, component.getProcessingRecipe().get().getInput().getCount(), false));
	}

	@Override
	public ProcessingCheckState canContinueProcessing(RecipeProcessingComponent<CokeOvenRecipe> component,
			ProcessingContainer processingContainer) {

		if (!multiblockComponent.isWellFormed()) {
			return ProcessingCheckState.skip();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<CokeOvenRecipe> component,
			ProcessingContainer processingContainer) {
		InventoryUtilities.insertItemIntoSlot(outputInventory, 0, processingContainer.getOutputs().getItem(0).copy(),
				false);
		fluidTankComponent.fill(processingContainer.getOutputs().getFluid(0), FluidAction.EXECUTE);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCokeOven(windowId, inventory, this);
	}
}