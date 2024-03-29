package theking530.staticpower.blockentities.machines.centrifuge;

import java.util.LinkedList;
import java.util.List;

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
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.ModUpgradeTypes;
import theking530.staticpower.init.ModUpgradeTypes.CentrifugeUpgradeValue;

public class BlockEntityCentrifuge extends BlockEntityMachine implements IRecipeProcessor<CentrifugeRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCentrifuge> TYPE = new BlockEntityTypeAllocator<>(
			"centrifuge", (type, pos, state) -> new BlockEntityCentrifuge(pos, state), ModBlocks.Centrifuge);

	public final InventoryComponent inputInventory;
	public final List<InventoryComponent> outputInventories;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<CentrifugeRecipe> processingComponent;

	@UpdateSerialize
	private int currentSpeed;
	@UpdateSerialize
	private int maxSpeed;
	@UpdateSerialize
	private double centrifugeMotorPowerCost;

	public BlockEntityCentrifuge(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		outputInventories = new LinkedList<>();
		// Initialize the current speed.
		currentSpeed = 0;

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return processingComponent
								.getRecipe(new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), stack)
										.ignoreItemCounts())
								.isPresent();
					}
				}));

		// Setup all the other inventories.
		InventoryComponent firstOutputInventory = new InventoryComponent("FirstOutputInventory", 1,
				MachineSideMode.Output2);
		outputInventories.add(firstOutputInventory);
		InventoryComponent secondOutputInventory = new InventoryComponent("SecondOutputInventory", 1,
				MachineSideMode.Output3);
		outputInventories.add(secondOutputInventory);
		InventoryComponent thirdOutputInventory = new InventoryComponent("ThirdOutputInventory", 1,
				MachineSideMode.Output4);
		outputInventories.add(thirdOutputInventory);
		for (InventoryComponent inv : outputInventories) {
			registerComponent(inv);
		}
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		upgradesInventory.setModifiedCallback(this::onUpgradesInventoryModifiedCallback);

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CentrifugeRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.centrifugeProcessingTime.get(), ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo1", 4, firstOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo2", 4, secondOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo3", 4, thirdOutputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Set the max speed and power cost.
		maxSpeed = StaticPowerConfig.SERVER.centrifugeInitialMaxSpeed.get();
		centrifugeMotorPowerCost = StaticPowerConfig.SERVER.centrifugeMotorPowerUsage.get();
	}

	@Override
	public void process() {
		// Maintain the spin.
		if (!getLevel().isClientSide() && redstoneControlComponent.passesRedstoneCheck()) {
			// If we're spinning faster than the current max, start slowing down. Otherwise,
			// either spin up or maintain speed.
			if (currentSpeed > maxSpeed) {
				currentSpeed -= 2;
			} else {
				if (powerStorage.canSupplyPower(centrifugeMotorPowerCost)
						&& redstoneControlComponent.passesRedstoneCheck()) {
					powerStorage.drainPower(centrifugeMotorPowerCost, false);
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
		UpgradeItemWrapper<CentrifugeUpgradeValue> upgradeWrapper = upgradesInventory
				.getMaxTierItemForUpgradeType(ModUpgradeTypes.CENTRIFUGE.get());

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new max speed.
		if (!upgradeWrapper.isEmpty()) {
			maxSpeed = upgradeWrapper.getUpgradeValue().maxRPM();
			centrifugeMotorPowerCost = StaticPowerConfig.SERVER.centrifugeMotorPowerUsage.get()
					* (1.0f + upgradeWrapper.getUpgradeValue().powerUsageIncrease());
		} else {
			maxSpeed = StaticPowerConfig.SERVER.centrifugeInitialMaxSpeed.get();
			centrifugeMotorPowerCost = StaticPowerConfig.SERVER.centrifugeMotorPowerUsage.get();
		}
	}

	protected boolean canInsertRecipeIntoOutputs(ConcretizedProductContainer container) {
		for (int i = 0; i < container.getItems().size(); i++) {
			if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventories.get(i), container.getItem(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return CentrifugeSideConfiguration.INSTANCE;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CentrifugeRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCentrifuge(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<CentrifugeRecipe> component, CentrifugeRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		for (StaticPowerOutputItem output : recipe.getOutputs()) {
			outputContainer.addItem(output.calculateOutput(), CaptureType.BOTH);
		}

	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<CentrifugeRecipe> component,
			CentrifugeRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!canInsertRecipeIntoOutputs(outputContainer)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		if (currentSpeed < recipe.getMinimumSpeed()) {
			return ProcessingCheckState
					.error("Centrifuge not up to required speed of: " + recipe.getMinimumSpeed() + "RPM");
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<CentrifugeRecipe> component, CentrifugeRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(inputInventory.extractItem(0, recipe.getInput().getCount(), false));

	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<CentrifugeRecipe> component,
			ProcessingContainer processingContainer) {
		for (int i = 0; i < processingContainer.getOutputs().getItems().size(); i++) {
			outputInventories.get(i).insertItem(0, processingContainer.getOutputs().getItem(i).copy(), false);
		}
	}
}