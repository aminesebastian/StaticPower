package theking530.staticpower.blockentities.machines.squeezer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
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
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderSqueezer;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntitySqueezer extends BlockEntityMachine implements IRecipeProcessor<SqueezerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySqueezer> TYPE = new BlockEntityTypeAllocator<BlockEntitySqueezer>(
			"squeezer", (type, pos, state) -> new BlockEntitySqueezer(pos, state), ModBlocks.Squeezer);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderSqueezer::new);
		}
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final RecipeProcessingComponent<SqueezerRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntitySqueezer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticCoreTier tier = getTierObject();

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts())
								.isPresent();
					}

				}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SqueezerRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.squeezerProcessingTime.get(), ModRecipeTypes.SQUEEZER_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Setup the fluid tank and fluid output servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setAutoSyncPacketsEnabled(true);
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<SqueezerRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSqueezer(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<SqueezerRecipe> component, SqueezerRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addFluid(recipe.getOutputFluid(), recipe.getOutputFluid().getAmount(), CaptureType.BOTH);

	}

	@Override
	public void prepareComponentForProcessing(RecipeProcessingComponent<SqueezerRecipe> component,
			SqueezerRecipe recipe, ConcretizedProductContainer outputContainer) {
		component.setBasePowerUsage(recipe.getPowerCost());
		component.setBaseProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<SqueezerRecipe> component,
			SqueezerRecipe recipe, ConcretizedProductContainer outputContainer) {
		// If this recipe has an item output that we cannot put into the output slot,
		// continue waiting.
		if (outputContainer.hasItems()
				&& !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (outputContainer.hasFluids() && fluidTankComponent.fill(outputContainer.getFluid(0),
				FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<SqueezerRecipe> component, SqueezerRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(inputInventory.extractItem(0, recipe.getInput().getCount(), false));
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<SqueezerRecipe> component,
			ProcessingContainer processingContainer) {
		if (!processingContainer.getOutputs().getItems().isEmpty()) {
			outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
		}
		if (!processingContainer.getOutputs().getFluids().isEmpty()) {
			fluidTankComponent.fill(processingContainer.getOutputs().getFluid(0).copy(), FluidAction.EXECUTE);
		}
	}
}
