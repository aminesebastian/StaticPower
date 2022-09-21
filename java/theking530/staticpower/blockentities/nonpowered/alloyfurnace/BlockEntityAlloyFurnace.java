package theking530.staticpower.blockentities.nonpowered.alloyfurnace;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
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
	public final RecipeProcessingComponent<FusionFurnaceRecipe> processingComponent;
	public final LoopingSoundComponent furnaceSoundComponent;

	@UpdateSerialize
	private int lastFuelBurnTime;
	@UpdateSerialize
	private int burnTimeRemaining;

	public BlockEntityAlloyFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		disableFaceInteraction();

		registerComponent(furnaceSoundComponent = new LoopingSoundComponent("FurnaceSoundComponent", 20));

		// Setup the input inventory with no filtering (no point since there are
		// multiple inputs).
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2).setMode(MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1).setMode(MachineSideMode.Input3).setShiftClickEnabled(true));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 2));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FusionFurnaceRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.alloyFurnaceProcessingTime.get(),
				FusionFurnaceRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
	}

	public void process() {
		if (!getLevel().isClientSide()) {
			// If we have a recipe that we can place in the output, if we don't have any
			// remaining
			// fuel, use some.
			Optional<FusionFurnaceRecipe> recipe = processingComponent.getPendingProcessingRecipe();
			if (recipe.isPresent() && InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.get().getOutput().getItem())) {
				int burnTime = ForgeHooks.getBurnTime(fuelInventory.getStackInSlot(0), null);
				if (burnTimeRemaining <= 0 && burnTime > 0) {
					lastFuelBurnTime = burnTimeRemaining = burnTime;
					fuelInventory.getStackInSlot(0).shrink(1);
					addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
				}
			}

			// Use fuel always, just like a vanilla furnace.
			if (burnTimeRemaining > 0) {
				burnTimeRemaining--;
			}

			if (burnTimeRemaining > 0) {
				furnaceSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE.getRegistryName(), SoundSource.BLOCKS, 1f, 1.0f, getBlockPos(), 64);
			} else {
				furnaceSoundComponent.stopPlayingSound();
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

	protected void moveInputs(FusionFurnaceRecipe recipe) {
		// Transfer the items.
		for (int i = 0; i < 2; i++) {
			int count = recipe.getRequiredCountOfItem(inputInventory.getStackInSlot(i));
			if (count > 0) {
				transferItemInternally(count, inputInventory, i, internalInventory, i);
			}
		}
	}

	protected ProcessingCheckState canProcessRecipe(FusionFurnaceRecipe recipe, RecipeProcessingPhase location) {
		if (recipe.getBlockAlloyFurnace()) {
			return ProcessingCheckState.error("This recipe is not valid for the alloy furnace!");
		}
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (burnTimeRemaining <= 0) {
			return ProcessingCheckState.error("Out of fuel!");
		}
		return ProcessingCheckState.ok();
	}

	protected void processingCompleted(FusionFurnaceRecipe recipe) {
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