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
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
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

public class BlockEntityAlloyFurnace extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAlloyFurnace> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityAlloyFurnace(pos, state),
			ModBlocks.AlloyFurnace);

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
	public final InventoryComponent internalInventory;
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
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 2));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Input3).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
			}
		}));

		registerComponent(processingComponent = new RecipeProcessingComponent<AlloyFurnaceRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.alloyFurnaceProcessingTime.get(),
				AlloyFurnaceRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted).setShouldControlBlockState(true));
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
			Optional<AlloyFurnaceRecipe> recipe = processingComponent.getPendingProcessingRecipe();
			if (recipe.isPresent() && InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.get().getOutput().getItem())) {
				int burnTime = ForgeHooks.getBurnTime(fuelInventory.getStackInSlot(0), null);
				if (burnTimeRemaining <= 0 && burnTime > 0) {
					lastFuelBurnTime = burnTimeRemaining = burnTime;
					fuelInventory.getStackInSlot(0).shrink(1);
					addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
				}
			}

			if (burnTimeRemaining > 0) {
				furnaceSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE.getRegistryName(), SoundSource.BLOCKS, 1f, 1.0f, getBlockPos(), 64);
			} else {
				furnaceSoundComponent.stopPlayingSound();
				if (processingComponent.getCurrentProcessingTime() > 0) {
					processingComponent.cancelProcessing();
				}
			}
		}
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

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1));
		}
	}

	protected void moveInputs(AlloyFurnaceRecipe recipe) {
		transferItemInternally(recipe.getInput1().getCount(), inputInventory, 0, internalInventory, 0);
		transferItemInternally(recipe.getInput2().getCount(), inputInventory, 1, internalInventory, 1);
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
	}

	protected ProcessingCheckState canProcessRecipe(AlloyFurnaceRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (burnTimeRemaining <= 0) {
			return ProcessingCheckState.error("gui.staticpower.alert.out_of_fuel");
		}
		return ProcessingCheckState.ok();
	}

	protected void processingCompleted(AlloyFurnaceRecipe recipe) {
		// Insert the output into the output inventory.
		ItemStack output = recipe.getOutput().calculateOutput();
		InventoryUtilities.insertItemIntoInventory(outputInventory, output, false);

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
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