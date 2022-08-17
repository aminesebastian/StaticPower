package theking530.staticpower.tileentities.powered.tumbler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
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

public class TileEntityTumbler extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTumbler> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityTumbler(pos, state), ModBlocks.Tumbler.get());

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<TumblerRecipe> processingComponent;

	@UpdateSerialize
	private double bonusOutputChance;
	@UpdateSerialize
	private int currentSpeed;

	public TileEntityTumbler(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ENERGIZED);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		upgradesInventory.setModifiedCallback(this::onUpgradesInventoryModifiedCallback);

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<TumblerRecipe>("ProcessingComponent", TumblerRecipe.RECIPE_TYPE,
				StaticPowerConfig.SERVER.tumblerProcessingTime.get(), this::getMatchParameters, this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the bonus output chance.
		bonusOutputChance = StaticPowerConfig.SERVER.tumblerOutputBonusChance.get();

		// Set the current speed to 0.
		currentSpeed = 0;
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(TumblerRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRawOutputItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Check the current speed.
		if (currentSpeed < StaticPowerConfig.SERVER.tumblerRequiredSpeed.get()) {
			return ProcessingCheckState.error("Tumbler has not reached the required speed of " + StaticPowerConfig.SERVER.tumblerRequiredSpeed.get() + "RPM");
		}

		transferItemInternally(inputInventory, 0, internalInventory, 0);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(TumblerRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRawOutputItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Check the current speed.
		if (currentSpeed < StaticPowerConfig.SERVER.tumblerRequiredSpeed.get()) {
			return ProcessingCheckState.error("Tumbler has not reached the required speed of " + StaticPowerConfig.SERVER.tumblerRequiredSpeed.get() + "RPM");
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(TumblerRecipe recipe) {
		// For each output, insert the contents into the output based on the percentage
		// chance. The clear the internal inventory, mark for synchronization, and
		// return true.
		ItemStack outputItem = recipe.getOutput().calculateOutput(bonusOutputChance - 1.0f);
		InventoryUtilities.insertItemIntoInventory(outputInventory, outputItem, false);

		InventoryUtilities.clearInventory(internalInventory);

		return ProcessingCheckState.ok();
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		bonusOutputChance = StaticPowerConfig.SERVER.tumblerOutputBonusChance.get();
		UpgradeItemWrapper upgradeWrapper = upgradesInventory.getMaxTierItemForUpgradeType(UpgradeType.OUTPUT_MULTIPLIER);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		double upgradeAmount = bonusOutputChance;
		if (!upgradeWrapper.isEmpty()) {
			upgradeAmount = (float) (1.0f + (upgradeWrapper.getTier().outputMultiplierUpgrade.get() * upgradeWrapper.getUpgradeWeight()));
		}

		// Set the bonus output amount.
		bonusOutputChance = upgradeAmount;
	}

	public double getBonusChance() {
		return bonusOutputChance;
	}

	@Override
	public void process() {
		// Maintain the spin.
		if (!getLevel().isClientSide) {
			// If we're spinning faster than the current max, start slowing down. Otherwise,
			// either spin up or maintain speed.
			if (currentSpeed > StaticPowerConfig.SERVER.tumblerRequiredSpeed.get()) {
				currentSpeed -= 2;
			} else {
				if (energyStorage.hasEnoughPower(StaticPowerConfig.SERVER.tumblerMotorPowerUsage.get()) && redstoneControlComponent.passesRedstoneCheck()) {
					energyStorage.useBulkPower(StaticPowerConfig.SERVER.tumblerMotorPowerUsage.get());
					currentSpeed = SDMath.clamp(currentSpeed + 1, 0, StaticPowerConfig.SERVER.tumblerRequiredSpeed.get());
				} else {
					currentSpeed = SDMath.clamp(currentSpeed - 1, 0, StaticPowerConfig.SERVER.tumblerRequiredSpeed.get());
				}
			}
		}
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public int getRequiredSpeed() {
		return StaticPowerConfig.SERVER.tumblerRequiredSpeed.get();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTumbler(windowId, inventory, this);
	}
}