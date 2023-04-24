package theking530.staticpower.blockentities.machines.enchanter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
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
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.enchanter.EnchanterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityEnchanter extends BlockEntityMachine implements IRecipeProcessor<EnchanterRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityEnchanter> TYPE = new BlockEntityTypeAllocator<>(
			"enchanter", (type, pos, state) -> new BlockEntityEnchanter(pos, state), ModBlocks.Enchanter);

	public final InventoryComponent inputInventory;
	public final InventoryComponent enchantableInventory;
	public final InventoryComponent outputInventory;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<EnchanterRecipe> processingComponent;

	public BlockEntityEnchanter(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 3, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts())
								.isPresent();
					}
				}));

		registerComponent(
				enchantableInventory = new InventoryComponent("EnchantableInventory", 1, MachineSideMode.Input2)
						.setShiftClickEnabled(true));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<EnchanterRecipe>("ProcessingComponent", 0,
				ModRecipeTypes.ENCHANTER_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setBasePowerUsage(StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0, 1, 2));
		registerComponent(new InputServoComponent("EnchantableServo", 4, enchantableInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return EnchanterSideConfiguration.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerEnchanter(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<EnchanterRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1),
				inputInventory.getStackInSlot(2), enchantableInventory.getStackInSlot(0))
				.setFluids(fluidTankComponent.getFluid());
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<EnchanterRecipe> component, EnchanterRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		ItemStack itemToEnchant = enchantableInventory.extractItem(0, 0, true);
		outputContainer.addItem(recipe.getEnchantedVersion(itemToEnchant.copy()), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<EnchanterRecipe> component,
			EnchanterRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<EnchanterRecipe> component, EnchanterRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		ItemStack itemToEnchant = enchantableInventory.extractItem(0, 0, false);
		inputContainer.addItem(itemToEnchant);
		inputContainer.addFluid(fluidTankComponent.getFluid(), recipe.getInputFluidStack().getAmount());
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<EnchanterRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}
}
