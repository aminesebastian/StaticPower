package theking530.staticpower.blockentities.machines.hydroponics.farmer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.machines.hydroponics.pod.BlockEntityHydroponicPod;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.tags.ModFluidTags;

public class BlockEntityHydroponicFarmer extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHydroponicFarmer> TYPE = new BlockEntityTypeAllocator<>("hydroponic_farmer",
			(type, pos, state) -> new BlockEntityHydroponicFarmer(pos, state), ModBlocks.HydroponicFarmer);
	public static final int MAX_PODS = 4;

	public final InventoryComponent outputInventory;
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
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		// Setup the fluid tank.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get(),
				(fluidStack) -> ModFluidTags.matches(FluidTags.WATER, fluidStack.getFluid())));
		fluidTankComponent.setAutoSyncPacketsEnabled(true).setUpgradeInventory(upgradesInventory).setCapabilityExposedModes(MachineSideMode.Input);

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		pods = new HashSet<BlockEntityHydroponicPod>();
	}

	public void process() {
		// Check for pods.
		pods.clear();
		for (int i = 0; i < MAX_PODS; i++) {
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

	public List<BlockEntityHydroponicPod> getPods() {
		return pods.stream().sorted((first, second) -> first.getBlockPos().getY() - second.getBlockPos().getY()).toList();
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
