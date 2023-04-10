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
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer;
import theking530.staticcore.blockentity.components.control.processing.basic.BasicProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityAutoSmith extends BlockEntityMachine implements IRecipeProcessor<AutoSmithRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoSmith> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoSmith>(
			"auto_smith", (allocator, pos, state) -> new BlockEntityAutoSmith(pos, state), ModBlocks.AutoSmith);

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
		StaticCoreTier tier = StaticCoreConfig.getTier(getTier());

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					@Override
					public boolean canInsertItem(int slot, ItemStack stack) {
						if (slot == 0) {
							return true;
						}
						return isValidModifier(stack);
					}
				}));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output2) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		});

		registerComponent(completedOutputInventory = new InventoryComponent("CompletedOutputInventory", 1,
				MachineSideMode.Output3) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		});

		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<AutoSmithRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.autoSmithProcessingTime.get(), ModRecipeTypes.AUTO_SMITH_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));
		registerComponent(new OutputServoComponent("CompletedOutputServo", completedOutputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	public boolean isValidModifier(ItemStack stack) {
		// Check to see if we have a tool we can repair.
		if (!inputInventory.getStackInSlot(0).isEmpty()) {
			ItemStack toolStack = inputInventory.getStackInSlot(0);
			if (toolStack.isRepairable() && toolStack.getItem().isValidRepairItem(toolStack, stack)) {
				return true;
			}
		}

		// Test for modifier materials.
		List<AutoSmithRecipe> recipes = getLevel().getRecipeManager()
				.getAllRecipesFor(ModRecipeTypes.AUTO_SMITH_RECIPE_TYPE.get());
		for (AutoSmithRecipe recipe : recipes) {
			if (recipe.getModifierMaterial().test(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return AutoSmithSideConfiguration.INSTANCE;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<AutoSmithRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1))
				.setFluids(fluidTankComponent.getFluid());
	}

	@Override
	public ProcessingCheckState captureOutputs(RecipeProcessingComponent<AutoSmithRecipe> component,
			AutoSmithRecipe recipe, ProcessingOutputContainer outputContainer) {
		int transferCount = recipe.isWildcardRecipe() ? 1 : recipe.getSmithTarget().getCount();
		ItemStack toModifyItem = inputInventory.extractItem(0, transferCount, true);
		if (!recipe.applyToItemStack(toModifyItem)) {
			return ProcessingCheckState.skip();
		}

		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, toModifyItem)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		outputContainer.addOutput(StaticCoreProductTypes.Item.get(), toModifyItem, toModifyItem.getCount(),
				CaptureType.BOTH, false);

		return ProcessingCheckState.ok();
	}

	@Override
	public void onRecipeProcessingStarted(RecipeProcessingComponent<AutoSmithRecipe> component, AutoSmithRecipe recipe,
			ProcessingOutputContainer outputContainer, ProcessingContainer processingContainer) {
		int transferCount = recipe.isWildcardRecipe() ? 1 : recipe.getSmithTarget().getCount();
		processingContainer.addInputItem(inputInventory.extractItem(0, transferCount, false), CaptureType.NONE, false);
		processingContainer.addInputItem(inputInventory.extractItem(1, recipe.getModifierMaterial().getCount(), false),
				CaptureType.BOTH);
		processingContainer.addInputFluid(
				fluidTankComponent.drain(recipe.getModifierFluid().getAmount(), FluidAction.EXECUTE), CaptureType.BOTH);
	}

	@Override
	public void onProcessingCompleted(BasicProcessingComponent<?, ?> component, ProcessingContainer outputContainer) {
		ItemStack output = outputContainer.getOutputItem(0).copy();

		// Make a hybrid of recipe parameters with the output as the smithing target,
		// but the inputs as the rest.
		RecipeMatchParameters nextRecipeParameters = new RecipeMatchParameters(output, inputInventory.getStackInSlot(1))
				.setFluids(fluidTankComponent.getFluid());

		// Check to get the recipe that will be processed next based on the modifier.
		Optional<AutoSmithRecipe> nextRecipe = processingComponent.getRecipe(nextRecipeParameters);

		// Put the item into the appropriate output slot.
		if (nextRecipe.isPresent() && nextRecipe.get().canApplyToItemStack(output)) {
			outputInventory.insertItem(0, output, false);
		} else {
			completedOutputInventory.insertItem(0, output, false);
		}

		// Play the crafting sound.
		getLevel().playSound(null, getBlockPos().getX(), getBlockPos().getY() + 0.5, getBlockPos().getZ(),
				SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.1F,
				((getLevel().getRandom().nextFloat() * .75f) + 1.25f));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoSmith(windowId, inventory, this);
	}
}
