package theking530.staticpower.blockentities.machines.poweredgrinder;

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
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.init.StaticCoreUpgradeTypes.OutputMultiplierUpgradeValue;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityPoweredGrinder extends BlockEntityMachine implements IRecipeProcessor<GrinderRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPoweredGrinder> TYPE = new BlockEntityTypeAllocator<>(
			"grinder", (type, pos, state) -> new BlockEntityPoweredGrinder(pos, state), ModBlocks.PoweredGrinder);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<GrinderRecipe> processingComponent;

	@UpdateSerialize
	private double bonusOutputChance;

	public BlockEntityPoweredGrinder(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent
								.getRecipe(new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), stack)
										.ignoreItemCounts())
								.isPresent();
					}
				}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		upgradesInventory.setModifiedCallback(this::onUpgradesInventoryModifiedCallback);

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<GrinderRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.poweredGrinderProcessingTime.get(), ModRecipeTypes.GRINDER_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the bonus output chance.
		bonusOutputChance = StaticPowerConfig.SERVER.poweredGrinderOutputBonusChance.get();
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		bonusOutputChance = StaticPowerConfig.SERVER.poweredGrinderOutputBonusChance.get();
		UpgradeItemWrapper<OutputMultiplierUpgradeValue> upgradeWrapper = upgradesInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.OUTPUT_MULTIPLIER.get());

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		double upgradeAmount = bonusOutputChance;
		if (!upgradeWrapper.isEmpty()) {
			upgradeAmount = (1.0f + (upgradeWrapper.getUpgradeValue().outputMultiplierIncrease()
					* upgradeWrapper.getUpgradeWeight()));
		}

		// Set the bonus output amount.
		bonusOutputChance = upgradeAmount;
	}

	public double getBonusChance() {
		return bonusOutputChance;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPoweredGrinder(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<GrinderRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<GrinderRecipe> component, GrinderRecipe recipe,
			ConcretizedProductContainer outputContainer) {

		for (StaticPowerOutputItem output : recipe.getOutputItems()) {
			outputContainer.addItem(output.calculateOutput(), CaptureType.BOTH);
		}
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<GrinderRecipe> component,
			GrinderRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, outputContainer.getItems())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<GrinderRecipe> component, GrinderRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(inputInventory.extractItem(0,
				component.getPendingRecipe().get().getInputIngredient().getCount(), false), CaptureType.BOTH);

	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<GrinderRecipe> component,
			ProcessingContainer processingContainer) {
		for (ItemStack output : processingContainer.getOutputs().getItems()) {
			InventoryUtilities.insertItemIntoInventory(outputInventory, output.copy(), false);
		}
	}
}