package theking530.staticpower.blockentities.machines.autosmith;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityAutoSmith extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoSmith> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoSmith>(
			(allocator, pos, state) -> new BlockEntityAutoSmith(pos, state), ModBlocks.AutoSmith);

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent completedOutputInventory;
	public final InventoryComponent batteryInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<AutoSmithRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityAutoSmith(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticPowerTier tier = StaticPowerConfig.getTier(getTier());

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				if (slot == 0) {
					return isValidInput(stack, false);
				}
				return isValidInput(stack, true);
			}
		}));

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 2));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output2) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		});

		registerComponent(completedOutputInventory = new InventoryComponent("CompletedOutputInventory", 1, MachineSideMode.Output3) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		});

		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<AutoSmithRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.autoSmithProcessingTime.get(),
				RecipeProcessingComponent.MOVE_TIME, AutoSmithRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));
		registerComponent(new OutputServoComponent("CompletedOutputServo", completedOutputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input)
				.setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanDrain(false);
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(AutoSmithRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getResultItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Transfer the inputs to internal buffers.
		int transferCount = recipe.isWildcardRecipe() ? 1 : recipe.getSmithTarget().getCount();
		transferItemInternally(transferCount, inputInventory, 0, internalInventory, 0);
		transferItemInternally(recipe.getModifierMaterial().getCount(), inputInventory, 1, internalInventory, 1);

		// Set the power usage and processing time.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(AutoSmithRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getResultItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(AutoSmithRecipe recipe) {
		// Modify the input that we put in the buffer, and then put it into the output.
		ItemStack output = internalInventory.getStackInSlot(0);

		// Apply the recipe.
		recipe.applyToItemStack(output);

		// Make a hybrid of recipe parameters with the output as the smithing target,
		// but the inputs as the rest.
		RecipeMatchParameters nextRecipeParameters = new RecipeMatchParameters(output, inputInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());

		// Check to get the recipe that will be processed next based on the modifier.
		Optional<AutoSmithRecipe> nextRecipe = processingComponent.getRecipeMatchingParameters(nextRecipeParameters);

		// Put the item into the appropriate output slot.
		if (nextRecipe.isPresent() && nextRecipe.get().canApplyToItemStack(output)) {
			outputInventory.insertItem(0, output, false);
		} else if (inputInventory.getStackInSlot(1).isEmpty()) {
			completedOutputInventory.insertItem(0, output, false);
		} else {
			completedOutputInventory.insertItem(0, output, false);
		}

		// Drain the fluid.
		fluidTankComponent.drain(recipe.getModifierFluid().getAmount(), FluidAction.EXECUTE);

		// Play the crafting sound.
		getLevel().playSound(null, getBlockPos().getX(), getBlockPos().getY() + 0.5, getBlockPos().getZ(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.1F,
				((getLevel().getRandom().nextFloat() * .75f) + 1.25f));

		// Clear the internal inventory.
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		internalInventory.setStackInSlot(1, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	public boolean isValidInput(ItemStack stack, boolean modifier) {
		if (!modifier) {
			return stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).isPresent();
		} else {
			// Test for modifier materials.
			List<AutoSmithRecipe> recipes = getLevel().getRecipeManager().getAllRecipesFor(AutoSmithRecipe.RECIPE_TYPE);
			for (AutoSmithRecipe recipe : recipes) {
				if (recipe.getModifierMaterial().test(stack)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Output2
				|| mode == MachineSideMode.Output3;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoSmith(windowId, inventory, this);
	}
}
