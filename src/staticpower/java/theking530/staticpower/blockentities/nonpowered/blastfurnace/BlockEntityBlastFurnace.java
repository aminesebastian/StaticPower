package theking530.staticpower.blockentities.nonpowered.blastfurnace;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState.MultiblockStateEntry;
import theking530.staticcore.blockentity.components.multiblock.newstyle.NewMultiblockComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.wrappers.blastfurnace.BlastFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModMultiblocks;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModItemTags;

public class BlockEntityBlastFurnace extends BlockEntityBase implements IRecipeProcessor<BlastFurnaceRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBlastFurnace> TYPE = new BlockEntityTypeAllocator<>(
			"blast_furnace", (type, pos, state) -> new BlockEntityBlastFurnace(pos, state), ModBlocks.BlastFurnace);

	public final InventoryComponent inputInventory;
	public final InventoryComponent fuelInventory;
	public final InventoryComponent outputInventory;
	public final RecipeProcessingComponent<BlastFurnaceRecipe> processingComponent;
	public final SideConfigurationComponent ioSideConfiguration;
	public final LoopingSoundComponent furnaceSoundComponent;
	public final NewMultiblockComponent<BlockEntityBlastFurnace> multiblockComponent;

	@UpdateSerialize
	private int lastFuelBurnTime;
	@UpdateSerialize
	private int burnTimeRemaining;

	public BlockEntityBlastFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(furnaceSoundComponent = new LoopingSoundComponent("FurnaceSoundComponent", 20));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 2, MachineSideMode.Output));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true));
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0
								&& ModItemTags.matches(ModItemTags.BLAST_FURNACE_FUEL, stack.getItem());
					}
				}));

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				BlastFurnaceSideConfiguration.INSTANCE));
		registerComponent(processingComponent = new RecipeProcessingComponent<BlastFurnaceRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.alloyFurnaceProcessingTime.get(),
				ModRecipeTypes.BLAST_FURNACE_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);

		registerComponent(multiblockComponent = new NewMultiblockComponent<BlockEntityBlastFurnace>(
				"MultiblockComponent", ModMultiblocks.BLAST_FURNACE.get(), MultiblockState.FAILED));
	}

	public void process() {
		if (!getLevel().isClientSide()) {
			if (!multiblockComponent.isWellFormed()) {
				return;
			}

			// Update the is_on states.
			for (MultiblockStateEntry entry : multiblockComponent.getState().getBlocks()) {
				if (entry.blockState().hasProperty(BlockBlastFurnace.SHOW_FACE)
						&& entry.blockState().getValue(BlockBlastFurnace.SHOW_FACE)) {
					BlockState currentState = getLevel().getBlockState(entry.pos());
					if (currentState.getValue(StaticCoreBlock.IS_ON) != processingComponent.isBlockStateOn()) {
						getLevel().setBlock(entry.pos(),
								currentState.setValue(StaticCoreBlock.IS_ON, processingComponent.isBlockStateOn()), 3);
					}
				}
			}

			// Use fuel always, just like a vanilla furnace.
			// This has to be done first so that the next logic can supply new fuel if we
			// have it before we check to stop the processing.
			if (burnTimeRemaining > 0) {
				burnTimeRemaining--;
			}

			// If we have a recipe that we can place in the output, if we don't have any
			// remaining
			// fuel, use some.
			Optional<BlastFurnaceRecipe> recipe = processingComponent.getPendingRecipe();
			if (recipe.isPresent() && InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory,
					recipe.get().getOutput().getItemStack())) {
				int burnTime = ForgeHooks.getBurnTime(fuelInventory.getStackInSlot(0), null);
				if (burnTimeRemaining <= 0 && burnTime > 0) {
					lastFuelBurnTime = burnTimeRemaining = burnTime;
					fuelInventory.getStackInSlot(0).shrink(1);
					addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
				}
			}

			if (burnTimeRemaining > 0) {
				furnaceSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f,
						1.0f, getBlockPos(), 64);
			} else {
				furnaceSoundComponent.stopPlayingSound();
			}
		}
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<BlastFurnaceRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<BlastFurnaceRecipe> component, BlastFurnaceRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addItem(recipe.getSlagOutput().calculateOutput(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<BlastFurnaceRecipe> component,
			BlastFurnaceRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 1, outputContainer.getItem(1))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (burnTimeRemaining <= 0) {
			return ProcessingCheckState.error("gui.staticcore.alert.out_of_fuel");
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<BlastFurnaceRecipe> component, BlastFurnaceRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(
				inputInventory.extractItem(0, component.getProcessingRecipe().get().getInput().getCount(), false));
	}

	@Override
	public ProcessingCheckState canContinueProcessing(RecipeProcessingComponent<BlastFurnaceRecipe> component,
			ProcessingContainer processingContainer) {
		if (burnTimeRemaining < 0) {
			return ProcessingCheckState.cancel();
		}
		if (!multiblockComponent.isWellFormed()) {
			return ProcessingCheckState.cancel();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<BlastFurnaceRecipe> component,
			ProcessingContainer processingContainer) {
		InventoryUtilities.insertItemIntoSlot(outputInventory, 0, processingContainer.getOutputs().getItem(0).copy(),
				false);
		InventoryUtilities.insertItemIntoSlot(outputInventory, 1, processingContainer.getOutputs().getItem(1).copy(),
				false);
	}

	public int getLastFuelBurnTime() {
		return lastFuelBurnTime;
	}

	public int getBurnTimeRemaining() {
		return burnTimeRemaining;
	}

	public float getBurnTimeRemainingRatio() {
		return (float) burnTimeRemaining / lastFuelBurnTime;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerBlastFurnace(windowId, inventory, this);
	}
}