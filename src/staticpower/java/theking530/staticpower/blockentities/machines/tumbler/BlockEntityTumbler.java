package theking530.staticpower.blockentities.machines.tumbler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
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
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.init.StaticCoreUpgradeTypes.OutputMultiplierUpgradeValue;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityTumbler extends BlockEntityMachine implements IOldRecipeProcessor<TumblerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTumbler> TYPE = new BlockEntityTypeAllocator<>("tumbler",
			(type, pos, state) -> new BlockEntityTumbler(pos, state), ModBlocks.Tumbler);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final OldRecipeProcessingComponent<TumblerRecipe> processingComponent;

	@UpdateSerialize
	private double bonusOutputChance;
	@UpdateSerialize
	private int currentSpeed;

	public BlockEntityTumbler(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true)
				.setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
					}
				}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		upgradesInventory.setModifiedCallback(this::onUpgradesInventoryModifiedCallback);

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new OldRecipeProcessingComponent<TumblerRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.tumblerProcessingTime.get(), ModRecipeTypes.TUMBLER_RECIPE_TYPE.get(), this));
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the bonus output chance.
		bonusOutputChance = StaticPowerConfig.SERVER.tumblerOutputBonusChance.get();

		// Set the current speed to 0.
		currentSpeed = 0;
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		bonusOutputChance = StaticPowerConfig.SERVER.tumblerOutputBonusChance.get();
		UpgradeItemWrapper<OutputMultiplierUpgradeValue> upgradeWrapper = upgradesInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.OUTPUT_MULTIPLIER.get());

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		double upgradeAmount = bonusOutputChance;
		if (!upgradeWrapper.isEmpty()) {
			upgradeAmount = (1.0f + (upgradeWrapper.getUpgradeValue().outputMultiplierIncrease() * upgradeWrapper.getUpgradeWeight()));
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
		if (!getLevel().isClientSide()) {
			// If we're spinning faster than the current max, start slowing down. Otherwise,
			// either spin up or maintain speed.
			if (currentSpeed > StaticPowerConfig.SERVER.tumblerRequiredSpeed.get()) {
				currentSpeed -= 2;
			} else {
				if (powerStorage.canSupplyPower(StaticPowerConfig.SERVER.tumblerMotorPowerUsage.get())
						&& redstoneControlComponent.passesRedstoneCheck()) {
					powerStorage.drainPower(StaticPowerConfig.SERVER.tumblerMotorPowerUsage.get(), false);
					currentSpeed = SDMath.clamp(currentSpeed + 1, 0, StaticPowerConfig.SERVER.tumblerRequiredSpeed.get());
				} else {
					currentSpeed = SDMath.clamp(currentSpeed - 1, 0, StaticPowerConfig.SERVER.tumblerRequiredSpeed.get());
				}
			}
		}
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<TumblerRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public void processingStarted(OldRecipeProcessingComponent<TumblerRecipe> component, TumblerRecipe recipe,
			OldProcessingContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getInputIngredient().getCount(), false);
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<TumblerRecipe> component, TumblerRecipe recipe,
			OldProcessingContainer outputContainer) {
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInputIngredient().getCount(), true), CaptureType.BOTH);

		ItemStack outputItem = recipe.getOutput().calculateOutput(bonusOutputChance - 1.0f);
		outputContainer.addOutputItem(outputItem, CaptureType.BOTH);

		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<TumblerRecipe> component, TumblerRecipe recipe,
			OldProcessingContainer outputContainer) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getOutputItem(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Check the current speed.
		if (currentSpeed < StaticPowerConfig.SERVER.tumblerRequiredSpeed.get()) {
			return ProcessingCheckState
					.error("Tumbler has not reached the required speed of " + StaticPowerConfig.SERVER.tumblerRequiredSpeed.get() + "RPM");
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<TumblerRecipe> component, TumblerRecipe recipe,
			OldProcessingContainer outputContainer) {
		InventoryUtilities.insertItemIntoInventory(outputInventory, outputContainer.getOutputItem(0).item().copy(), false);
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