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
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer.CaptureType;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
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
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityAutoSmith extends BlockEntityMachine implements IRecipeProcessor<AutoSmithRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoSmith> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoSmith>("auto_smith",
			(allocator, pos, state) -> new BlockEntityAutoSmith(pos, state), ModBlocks.AutoSmith);

	public final InventoryComponent inputInventory;
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
				ModRecipeTypes.AUTO_SMITH_RECIPE_TYPE.get(), this));

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
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	public boolean isValidInput(ItemStack stack, boolean modifier) {
		if (!modifier) {
			return stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).isPresent();
		} else {
			// Test for modifier materials.
			List<AutoSmithRecipe> recipes = getLevel().getRecipeManager().getAllRecipesFor(ModRecipeTypes.AUTO_SMITH_RECIPE_TYPE.get());
			for (AutoSmithRecipe recipe : recipes) {
				if (recipe.getModifierMaterial().test(stack)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return AutoSmithPreset.INSTANCE;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<AutoSmithRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<AutoSmithRecipe> component, AutoSmithRecipe recipe, ProcessingOutputContainer outputContainer) {
		// Transfer the inputs to internal buffers.
		int transferCount = recipe.isWildcardRecipe() ? 1 : recipe.getSmithTarget().getCount();
		ItemStack toModifyItem = inputInventory.extractItem(0, transferCount, false);
		outputContainer.addInputItem(toModifyItem, CaptureType.NONE, true);
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getModifierMaterial().getCount(), true), CaptureType.BOTH);
		outputContainer.addInputFluid(fluidTankComponent.drain(recipe.getModifierFluid().getAmount(), FluidAction.SIMULATE), CaptureType.BOTH);

		recipe.applyToItemStack(toModifyItem);
		outputContainer.addOutputItem(toModifyItem, CaptureType.BOTH);

		// Set the power usage and processing time.
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public void processingStarted(RecipeProcessingComponent<AutoSmithRecipe> component, AutoSmithRecipe recipe, ProcessingOutputContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getModifierMaterial().getCount(), false);
		if (outputContainer.hasInputFluids()) {
			fluidTankComponent.drain(outputContainer.getInputFluid(0).fluid(), FluidAction.EXECUTE);
		}
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<AutoSmithRecipe> component, AutoSmithRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getOutputItem(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(RecipeProcessingComponent<AutoSmithRecipe> component, AutoSmithRecipe recipe, ProcessingOutputContainer outputContainer) {
		ItemStack output = outputContainer.getOutputItem(0).item().copy();

		// Make a hybrid of recipe parameters with the output as the smithing target,
		// but the inputs as the rest.
		RecipeMatchParameters nextRecipeParameters = new RecipeMatchParameters(output, inputInventory.getStackInSlot(1)).setFluids(fluidTankComponent.getFluid());

		// Check to get the recipe that will be processed next based on the modifier.
		Optional<AutoSmithRecipe> nextRecipe = processingComponent.getRecipeMatchingParameters(nextRecipeParameters);

		// Put the item into the appropriate output slot.
		if (nextRecipe.isPresent() && nextRecipe.get().canApplyToItemStack(output)) {
			outputInventory.insertItem(0, output, false);
		} else {
			completedOutputInventory.insertItem(0, output, false);
		}

		// Play the crafting sound.
		getLevel().playSound(null, getBlockPos().getX(), getBlockPos().getY() + 0.5, getBlockPos().getZ(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.1F,
				((getLevel().getRandom().nextFloat() * .75f) + 1.25f));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoSmith(windowId, inventory, this);
	}

}
