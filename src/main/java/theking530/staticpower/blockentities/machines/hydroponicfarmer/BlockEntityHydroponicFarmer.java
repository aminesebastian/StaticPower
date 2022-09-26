package theking530.staticpower.blockentities.machines.hydroponicfarmer;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.machines.hydroponicpod.BlockEntityHydroponicPod;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityHydroponicFarmer extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHydroponicFarmer> TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityHydroponicFarmer(pos, state), ModBlocks.HydroponicFarmer);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;

	private final Set<BlockEntityHydroponicPod> pods;

	public BlockEntityHydroponicFarmer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticPowerTier tier = getTierObject();

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof BlockItem;
			}
		}));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input)
				.setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanDrain(false).setAutoSyncPacketsEnabled(true).setUpgradeInventory(upgradesInventory);

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		pods = new HashSet<BlockEntityHydroponicPod>();
	}

	public void process() {
		// Check for pods.
		for (int i = 0; i < 8; i++) {
			BlockPos testPos = getBlockPos().above(i + 1);
			BlockEntityHydroponicPod pod = (BlockEntityHydroponicPod) getLevel().getBlockEntity(testPos);

			// Stop as soon as the chain of pods is broken.
			if (pod == null) {
				break;
			}

			pod.farmerCheckin(this);
			pods.add(pod);
		}
	}

	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
		for (BlockEntityHydroponicPod pod : pods) {
			if (pod != null) {
				pod.farmerBroken();
			} else {
				StaticPower.LOGGER.error("Encountered a null pod from a hydroponic farmer!");
			}
		}
	}

	public void podBroken(BlockEntityHydroponicPod pod) {
		pods.remove(pod);
	}

	protected ProcessingCheckState canStart() {
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canContinue() {
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted() {
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerHydroponicFarmer(windowId, inventory, this);
	}
}
