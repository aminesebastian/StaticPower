package theking530.staticpower.blockentities.machines.poweredfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.init.ModBlocks;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class BlockEntityPoweredFurnace extends BlockEntityMachine implements IRecipeProcessor<SmeltingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPoweredFurnace> TYPE = new BlockEntityTypeAllocator<>(
			"powered_furnace", (type, pos, state) -> new BlockEntityPoweredFurnace(pos, state),
			ModBlocks.PoweredFurnace);

	/**
	 * Indicates how many times faster this block will perform compared to the
	 * vanila furnace.
	 */
	public static final float DEFAULT_PROCESSING_TIME_MULT = 1.25f;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<SmeltingRecipe> processingComponent;

	public BlockEntityPoweredFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

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
		registerComponent(processingComponent = new RecipeProcessingComponent<SmeltingRecipe>("ProcessingComponent", 0,
				RecipeType.SMELTING));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setBasePowerUsage(StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	public static int getCookTime(SmeltingRecipe recipe) {
		return (int) (recipe.getCookingTime() / DEFAULT_PROCESSING_TIME_MULT);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPoweredFurnace(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<SmeltingRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<SmeltingRecipe> component, SmeltingRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getResultItem().copy(), CaptureType.BOTH);
	}

	@Override
	public void prepareComponentForProcessing(RecipeProcessingComponent<SmeltingRecipe> component,
			SmeltingRecipe recipe, ConcretizedProductContainer outputContainer) {
		component.setBaseProcessingTime(getCookTime(component.getProcessingRecipe().get()));
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<SmeltingRecipe> component,
			SmeltingRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<SmeltingRecipe> component, SmeltingRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {

		processingContainer.getInputs().addItem(inputInventory.extractItem(0, 1, false), CaptureType.BOTH);
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<SmeltingRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}
}
