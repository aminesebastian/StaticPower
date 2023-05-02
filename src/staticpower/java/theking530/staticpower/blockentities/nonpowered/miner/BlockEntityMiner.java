package theking530.staticpower.blockentities.nonpowered.miner;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.machine.MachineProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityMiner extends AbstractTileEntityMiner {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityMiner> TYPE = new BlockEntityTypeAllocator<>("miner",
			(type, pos, state) -> new BlockEntityMiner(pos, state), ModBlocks.Miner);

	private static final int DEFAULT_FUEL_MOVE_TIME = 4;
	public final InventoryComponent fuelInventory;
	public final InventoryComponent fuelBurningInventory;
	public final MachineProcessingComponent fuelComponent;
	public final MachineProcessingComponent fuelMoveComponent;

	public BlockEntityMiner(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true));
		registerComponent(
				fuelBurningInventory = new InventoryComponent("FuelBurningInventory", 1, MachineSideMode.Never));
		registerComponent(
				fuelMoveComponent = new MachineProcessingComponent("FuelMoveComponent", DEFAULT_FUEL_MOVE_TIME)
						.setRedstoneControlComponent(redstoneControlComponent));
		registerComponent(fuelComponent = new MachineProcessingComponent("FuelComponent", 0)
				.setRedstoneControlComponent(redstoneControlComponent));
		registerComponent(new InputServoComponent("FuelInputServo", 20, fuelInventory));
	}

	@Override
	public void process() {
		super.process();

		// Update the processing cost.
		fuelComponent.setProcessingTicksPerGameTick((int) processingComponent.getCalculatedPowerUsageMultipler());

		// Randomly generate smoke and flame particles.
		if (processingComponent.isBlockStateOn()) {
			if (SDMath.diceRoll(0.25f)) {
				@SuppressWarnings("resource")
				float randomOffset = (2 * getLevel().random.nextFloat()) - 1.0f;
				randomOffset /= 3.5f;

				float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f
						: -0.05f;
				Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(),
						new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
				getLevel().addParticle(ParticleTypes.SMOKE, getBlockPos().getX() + forwardVector.x(),
						getBlockPos().getY() + forwardVector.y(), getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f,
						0.0f);
				getLevel().addParticle(ParticleTypes.FLAME, getBlockPos().getX() + forwardVector.x(),
						getBlockPos().getY() + forwardVector.y(), getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f,
						0.0f);
			}
		}
	}

	public ProcessingCheckState canMoveFuel() {
		if (isValidFuel(fuelInventory.getStackInSlot(0)) && fuelBurningInventory.getStackInSlot(0).isEmpty()
				&& hasDrillBit()) {
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.skip();
	}

	public ProcessingCheckState moveFuel() {
		int burnTime = getFuelBurnTime(fuelInventory.getStackInSlot(0));
		fuelComponent.setBaseProcessingTime(burnTime);
		fuelComponent.setProcessingTicksPerGameTick((int) getFuelUsage());
		transferItemInternally(fuelInventory, 0, fuelBurningInventory, 0);
		return ProcessingCheckState.ok();
	}

	public ProcessingCheckState canStartProcessingFuel() {
		if (!isDoneMining() && isValidFuel(fuelInventory.getStackInSlot(0)) && hasDrillBit()) {
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.error("Missing Fuel!");
	}

	public ProcessingCheckState canContinueProcessingFuel() {
		if (!isDoneMining()) {
			return ProcessingCheckState.ok();
		}
		return processingComponent.getProcessingState();
	}

	public ProcessingCheckState fuelProcessingCompleted() {
		fuelComponent.setBaseProcessingTime(0);
		fuelBurningInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	@Override
	public ProcessingCheckState canStartProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {
		ProcessingCheckState superCall = super.canStartProcessing(component, processingContainer);
		if (!superCall.isOk()) {
			return superCall;
		}

		if (getRemainingFuel() == 0) {
			return ProcessingCheckState.error("Missing fuel!");
		}

		return ProcessingCheckState.ok();
	}

	public ProcessingCheckState canContinueProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {
		ProcessingCheckState superCall = super.canStartProcessing(component, processingContainer);
		if (!superCall.isOk()) {
			return superCall;
		}

		if (getRemainingFuel() == 0) {
			return ProcessingCheckState.error("Missing fuel!");
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public int getProcessingTime() {
		return StaticPowerConfig.SERVER.minerProcessingTime.get();
	}

	@Override
	public int getHeatGeneration() {
		return (int) (StaticPowerConfig.SERVER.minerHeatGeneration.get()
				* processingComponent.getCalculatedHeatGenerationMultiplier());
	}

	@Override
	public int getRadius() {
		// Get the range upgrade.
		UpgradeItemWrapper<Double> upgradeWrapper = upgradesInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.RANGE.get());

		// If there isn't one, return the base level.
		if (upgradeWrapper.isEmpty()) {
			return StaticPowerConfig.SERVER.minerRadius.get();
		}

		// Otherwise, caluclate the new range.
		double newRange = upgradeWrapper.getUpgradeValue() * StaticPowerConfig.SERVER.minerRadius.get();
		return (int) newRange;
	}

	@Override
	public double getFuelUsage() {
		return StaticPowerConfig.SERVER.minerFuelUsage.get() * processingComponent.getCalculatedPowerUsageMultipler();
	}

	public int getFuelBurnTime(ItemStack input) {
		return ForgeHooks.getBurnTime(input, null);
	}

	public boolean isValidFuel(ItemStack input) {
		return ForgeHooks.getBurnTime(input, null) > 0;
	}

	public int getRemainingFuel() {
		return fuelComponent.getProcessingTimer().getMaxTime() - fuelComponent.getProcessingTimer().getCurrentTime();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerMiner(windowId, inventory, this);
	}
}
