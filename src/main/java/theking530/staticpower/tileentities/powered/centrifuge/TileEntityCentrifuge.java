package theking530.staticpower.tileentities.powered.centrifuge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
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

public class TileEntityCentrifuge extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityCentrifuge> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityCentrifuge(), ModBlocks.Centrifuge);

	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_CENTRIFUGE_MOTOR_COST = 5;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_MAX_SPEED = 500;

	public final InventoryComponent inputInventory;

	public final InventoryComponent firstOutputInventory;
	public final InventoryComponent secondOutputInventory;
	public final InventoryComponent thirdOutputInventory;

	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<CentrifugeRecipe> processingComponent;

	@UpdateSerialize
	private int currentSpeed;
	@UpdateSerialize
	private int maxSpeed;
	@UpdateSerialize
	private int centrifugeMotorPowerCost;

	public TileEntityCentrifuge() {
		super(TYPE, StaticPowerTiers.ADVANCED);
		// Initialize the current speed.
		currentSpeed = 0;

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(firstOutputInventory = new InventoryComponent("FirstOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondOutputInventory = new InventoryComponent("SecondOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(thirdOutputInventory = new InventoryComponent("ThirdOutputInventory", 1, MachineSideMode.Output4));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		upgradesInventory.setModifiedCallback(this::onUpgradesInventoryModifiedCallback);

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CentrifugeRecipe>("ProcessingComponent", CentrifugeRecipe.RECIPE_TYPE, DEFAULT_PROCESSING_TIME,
				this::getMatchParameters, this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo1", 4, firstOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo2", 4, secondOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo3", 4, thirdOutputInventory));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);

		// Set the max speed and power cost.
		maxSpeed = DEFAULT_MAX_SPEED;
		centrifugeMotorPowerCost = DEFAULT_CENTRIFUGE_MOTOR_COST;
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(CentrifugeRecipe recipe) {
		// Check the required speed.
		if (currentSpeed < recipe.getMinimumSpeed()) {
			return ProcessingCheckState.error("Centrifuge not up to required speed of: " + recipe.getMinimumSpeed() + "RPM");
		}

		// If we don't have enough inputs, return false.
		if (inputInventory.getStackInSlot(0).getCount() < recipe.getInput().getCount()) {
			return ProcessingCheckState.skip();
		}

		// If the items can be insert into the output, transfer the items and return
		// true.
		if (internalInventory.getStackInSlot(0).isEmpty() && canInsertRecipeIntoOutputs(recipe)) {
			transferItemInternally(recipe.getInput().getCount(), inputInventory, 0, internalInventory, 0);
			markTileEntityForSynchronization();
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
	}

	protected ProcessingCheckState canProcessRecipe(CentrifugeRecipe recipe) {
		if (!canInsertRecipeIntoOutputs(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		if (currentSpeed < recipe.getMinimumSpeed()) {
			return ProcessingCheckState.error("Centrifuge not up to required speed of: " + recipe.getMinimumSpeed() + "RPM");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(CentrifugeRecipe recipe) {
		// Ensure the output slots can take the recipe.
		if (canInsertRecipeIntoOutputs(recipe)) {
			// For each output, insert the contents into the output based on the percentage
			// chance. The clear the internal inventory, mark for synchronization, and
			// return true.
			// Insert the output.
			ItemStack output1 = recipe.getOutput1().calculateOutput();
			ItemStack output2 = recipe.getOutput2().calculateOutput();
			ItemStack output3 = recipe.getOutput3().calculateOutput();

			InventoryUtilities.insertItemIntoInventory(firstOutputInventory, output1, false);
			InventoryUtilities.insertItemIntoInventory(secondOutputInventory, output2, false);
			InventoryUtilities.insertItemIntoInventory(thirdOutputInventory, output3, false);

			InventoryUtilities.clearInventory(internalInventory);
			markTileEntityForSynchronization();
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.outputsCannotTakeRecipe();
	}

	@Override
	public void process() {
		// Maintain the spin.
		if (!getWorld().isRemote && redstoneControlComponent.passesRedstoneCheck()) {
			// If we're spinning faster than the current max, start slowing down. Otherwise,
			// either spin up or maintain speed.
			if (currentSpeed > maxSpeed) {
				currentSpeed -= 2;
			} else {
				if (energyStorage.hasEnoughPower(centrifugeMotorPowerCost) && redstoneControlComponent.passesRedstoneCheck()) {
					energyStorage.useBulkPower(centrifugeMotorPowerCost);
					currentSpeed = SDMath.clamp(currentSpeed + 1, 0, maxSpeed);
				} else {
					currentSpeed = SDMath.clamp(currentSpeed - 1, 0, maxSpeed);
				}
			}
		}
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		// Get the centrifuge upgrade.
		UpgradeItemWrapper upgradeWrapper = upgradesInventory.getMaxTierItemForUpgradeType(UpgradeType.CENTRIFUGE);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new max speed.
		if (!upgradeWrapper.isEmpty()) {
			maxSpeed = upgradeWrapper.getTier().maxCentrifugeSpeedUpgrade.get();
			centrifugeMotorPowerCost = (int) (DEFAULT_CENTRIFUGE_MOTOR_COST * (1.0f + upgradeWrapper.getTier().centrifugeUpgradedPowerIncrease.get()));
		} else {
			maxSpeed = DEFAULT_MAX_SPEED;
			centrifugeMotorPowerCost = DEFAULT_CENTRIFUGE_MOTOR_COST;
		}
	}

	protected boolean canInsertRecipeIntoOutputs(CentrifugeRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(firstOutputInventory, recipe.getOutput1().getItem())) {
			return false;
		}
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(secondOutputInventory, recipe.getOutput2().getItem())) {
			return false;
		}
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(thirdOutputInventory, recipe.getOutput3().getItem())) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Output2
				|| mode == MachineSideMode.Output3 || mode == MachineSideMode.Output4;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCentrifuge(windowId, inventory, this);
	}
}