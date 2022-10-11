package theking530.staticpower.blockentities.nonpowered.alloyfurnace;

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
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer.CaptureType;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityAlloyFurnace extends BlockEntityConfigurable implements IRecipeProcessor<AlloyFurnaceRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAlloyFurnace> TYPE = new BlockEntityTypeAllocator<>("alloy_furnace",
			(type, pos, state) -> new BlockEntityAlloyFurnace(pos, state), ModBlocks.AlloyFurnace);

	public static final DefaultSideConfiguration SIDE_CONFIGURATION = new DefaultSideConfiguration();
	static {
		SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input2);
		SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		SIDE_CONFIGURATION.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Input3);
		SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input3);
		SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Input3);
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent fuelInventory;
	public final InventoryComponent outputInventory;
	public final RecipeProcessingComponent<AlloyFurnaceRecipe> processingComponent;
	public final LoopingSoundComponent furnaceSoundComponent;

	@UpdateSerialize
	private int lastFuelBurnTime;
	@UpdateSerialize
	private int burnTimeRemaining;

	public BlockEntityAlloyFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		disableFaceInteraction();

		registerComponent(furnaceSoundComponent = new LoopingSoundComponent("FurnaceSoundComponent", 20));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Input3).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
			}
		}));

		registerComponent(processingComponent = new RecipeProcessingComponent<AlloyFurnaceRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.alloyFurnaceProcessingTime.get(),
				AlloyFurnaceRecipe.RECIPE_TYPE, this));
		processingComponent.setShouldControlBlockState(true);
	}

	public void process() {
		if (!getLevel().isClientSide()) {
			// Use fuel always, just like a vanilla furnace.
			// This has to be done first so that the next logic can supply new fuel if we
			// have it before we check to stop the processing.
			if (burnTimeRemaining > 0) {
				burnTimeRemaining--;
			}

			// If we have a recipe that we can place in the output, if we don't have any
			// remaining
			// fuel, use some.
			Optional<AlloyFurnaceRecipe> recipe = processingComponent.getPendingRecipe();
			if (recipe.isPresent() && InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.get().getOutput().getItem())) {
				int burnTime = ForgeHooks.getBurnTime(fuelInventory.getStackInSlot(0), null);
				if (burnTimeRemaining <= 0 && burnTime > 0) {
					lastFuelBurnTime = burnTimeRemaining = burnTime;
					fuelInventory.getStackInSlot(0).shrink(1);
					addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
				}
			}

			if (burnTimeRemaining > 0) {
				furnaceSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f, 1.0f, getBlockPos(), 64);
			} else {
				furnaceSoundComponent.stopPlayingSound();
				if (processingComponent.getCurrentProcessingTime() > 0) {
					processingComponent.cancelProcessing();
				}
			}
		}
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<AlloyFurnaceRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1));
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getOutputItems().get(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (burnTimeRemaining <= 0) {
			return ProcessingCheckState.error("gui.staticpower.alert.out_of_fuel");
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingStarted(RecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, ProcessingOutputContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getInput1().getCount(), false);
		inputInventory.extractItem(1, recipe.getInput2().getCount(), false);
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, ProcessingOutputContainer outputContainer) {
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInput1().getCount(), true), CaptureType.BOTH);
		outputContainer.addInputItem(inputInventory.extractItem(1, recipe.getInput2().getCount(), true), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);

		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public void processingCompleted(RecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, ProcessingOutputContainer outputContainer) {
		InventoryUtilities.insertItemIntoInventory(outputInventory, outputContainer.getOutputItems().get(0).item().copy(), false);
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
		return new ContainerAlloyFurnace(windowId, inventory, this);
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SIDE_CONFIGURATION;
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		// Stick with the default setup ONLy.
		return mode == SIDE_CONFIGURATION.getSideDefaultMode(side);
	}

}