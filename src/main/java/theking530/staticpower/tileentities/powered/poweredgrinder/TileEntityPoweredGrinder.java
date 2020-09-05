package theking530.staticpower.tileentities.powered.poweredgrinder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityPoweredGrinder extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPoweredGrinder> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityPoweredGrinder(), ModBlocks.PoweredGrinder);

	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final int DEFAULT_PROCESSING_COST = 5;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final float DEFAULT_OUTPUT_BONUS_CHANCE = 1.0f;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<GrinderRecipe> processingComponent;

	@UpdateSerialize
	private float bonusOutputChance;

	public TileEntityPoweredGrinder() {
		super(TYPE);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack)).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		upgradesInventory.setModifiedCallback(this::onUpgradesInventoryModifiedCallback);

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<GrinderRecipe>("ProcessingComponent", GrinderRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the bonus output chance.
		bonusOutputChance = DEFAULT_OUTPUT_BONUS_CHANCE;
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(GrinderRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRawOutputItems())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		transferItemInternally(inputInventory, 0, internalInventory, 0);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(GrinderRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRawOutputItems())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(GrinderRecipe recipe) {
		// For each output, insert the contents into the output based on the percentage
		// chance. The clear the internal inventory, mark for synchronization, and
		// return true.
		for (ProbabilityItemStackOutput output : recipe.getOutputItems()) {
			if (SDMath.diceRoll(output.getOutputChance() * bonusOutputChance)) {
				InventoryUtilities.insertItemIntoInventory(outputInventory, output.getItem().copy(), false);
			}
		}
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		bonusOutputChance = DEFAULT_OUTPUT_BONUS_CHANCE;
		UpgradeItemWrapper upgradeWrapper = upgradesInventory.getMaxTierItemForUpgradeType(UpgradeType.OUTPUT_MULTIPLIER);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		float upgradeAmount = DEFAULT_OUTPUT_BONUS_CHANCE;
		if (!upgradeWrapper.isEmpty()) {
			upgradeAmount = 1.0f + (upgradeWrapper.getTier().getTankCapacityUpgrade() * upgradeWrapper.getUpgradeWeight());
		}

		// Set the bonus output amount.
		bonusOutputChance = upgradeAmount;
	}

	public float getBonusChance() {
		return bonusOutputChance;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPoweredGrinder(windowId, inventory, this);
	}
}