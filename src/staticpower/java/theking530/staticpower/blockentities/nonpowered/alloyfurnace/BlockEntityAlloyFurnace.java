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
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityAlloyFurnace extends BlockEntityBase implements IOldRecipeProcessor<AlloyFurnaceRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAlloyFurnace> TYPE = new BlockEntityTypeAllocator<>("alloy_furnace",
			(type, pos, state) -> new BlockEntityAlloyFurnace(pos, state), ModBlocks.AlloyFurnace);

	public final InventoryComponent inputInventory;
	public final InventoryComponent fuelInventory;
	public final InventoryComponent outputInventory;
	public final OldRecipeProcessingComponent<AlloyFurnaceRecipe> processingComponent;
	public final SideConfigurationComponent ioSideConfiguration;
	public final LoopingSoundComponent furnaceSoundComponent;

	@UpdateSerialize
	private int lastFuelBurnTime;
	@UpdateSerialize
	private int burnTimeRemaining;

	public BlockEntityAlloyFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(furnaceSoundComponent = new LoopingSoundComponent("FurnaceSoundComponent", 20));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Input3).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
			}
		}));

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", AlloyFurnaceSideConfiguration.INSTANCE));
		registerComponent(processingComponent = new OldRecipeProcessingComponent<AlloyFurnaceRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.alloyFurnaceProcessingTime.get(),
				ModRecipeTypes.ALLOY_FURNACE_RECIPE_TYPE.get(), this));
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
			if (recipe.isPresent() && InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.get().getOutput().getItemStack())) {
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
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<AlloyFurnaceRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1));
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, OldProcessingContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getOutputItems().get(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (burnTimeRemaining <= 0) {
			return ProcessingCheckState.error("gui.staticcore.alert.out_of_fuel");
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingStarted(OldRecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, OldProcessingContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getInput1().getCount(), false);
		inputInventory.extractItem(1, recipe.getInput2().getCount(), false);
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, OldProcessingContainer outputContainer) {
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInput1().getCount(), true), CaptureType.BOTH);
		outputContainer.addInputItem(inputInventory.extractItem(1, recipe.getInput2().getCount(), true), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);

		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<AlloyFurnaceRecipe> component, AlloyFurnaceRecipe recipe, OldProcessingContainer outputContainer) {
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
}