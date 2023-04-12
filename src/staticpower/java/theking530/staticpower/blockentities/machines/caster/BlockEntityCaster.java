package theking530.staticpower.blockentities.machines.caster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
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
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityCaster extends BlockEntityMachine implements IOldRecipeProcessor<CastingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCaster> TYPE = new BlockEntityTypeAllocator<>("caster", (type, pos, state) -> new BlockEntityCaster(pos, state),
			ModBlocks.Caster);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidTankComponent fluidTankComponent;
	public final OldRecipeProcessingComponent<CastingRecipe> processingComponent;

	public BlockEntityCaster(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return StaticPowerRecipeRegistry.isValidCastingMold(stack);
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new OldRecipeProcessingComponent<CastingRecipe>("ProcessingComponent", ModRecipeTypes.CASTING_RECIPE_TYPE.get(), this));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input)
				.setUpgradeInventory(upgradesInventory));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<CastingRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0)).setFluids(fluidTankComponent.getFluid());
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<CastingRecipe> component, CastingRecipe recipe, OldProcessingContainer outputContainer) {
		outputContainer.addInputFluid(fluidTankComponent.getFluid(), recipe.getInputFluid().getAmount(), CaptureType.BOTH);
		outputContainer.addInputItem(inputInventory.getStackInSlot(0), CaptureType.NONE, true);
		outputContainer.addOutputItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);

		// Set the power usage.
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<CastingRecipe> component, CastingRecipe recipe, OldProcessingContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getOutputItem(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<CastingRecipe> component, CastingRecipe recipe, OldProcessingContainer outputContainer) {
		outputInventory.insertItem(0, outputContainer.getOutputItem(0).item().copy(), false);
		fluidTankComponent.drain(outputContainer.getInputFluid(0).fluid(), FluidAction.EXECUTE);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCaster(windowId, inventory, this);
	}
}