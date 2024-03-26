package theking530.staticpower.blockentities.machines.caster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
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
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityCaster extends BlockEntityMachine implements IRecipeProcessor<CastingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCaster> TYPE = new BlockEntityTypeAllocator<>("caster",
			(type, pos, state) -> new BlockEntityCaster(pos, state), ModBlocks.Caster);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidTankComponent fluidTankComponent;
	public final RecipeProcessingComponent<CastingRecipe> processingComponent;

	public BlockEntityCaster(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
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
		registerComponent(processingComponent = new RecipeProcessingComponent<CastingRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.casterProcessingTime.get(), ModRecipeTypes.CASTING_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Input));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CastingRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0))
				.setFluids(fluidTankComponent.getFluid());
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCaster(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<CastingRecipe> component, CastingRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);

	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<CastingRecipe> component,
			CastingRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<CastingRecipe> component, CastingRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addFluid(fluidTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE));
		inputContainer.addItem(inputInventory.getStackInSlot(0), CaptureType.NONE, true);

	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<CastingRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}
}