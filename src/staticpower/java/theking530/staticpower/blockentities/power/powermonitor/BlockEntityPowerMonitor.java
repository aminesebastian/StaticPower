package theking530.staticpower.blockentities.power.powermonitor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.metrics.IPowerMetricsSyncConsumer;
import theking530.api.energy.metrics.PowerTransferMetrics;
import theking530.staticcore.blockentity.components.energy.PowerDistributionComponent;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;
import theking530.staticcore.gui.widgets.DataGraphWidget.DynamicGraphDataSet;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderPowerMonitor;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPowerMonitor extends BlockEntityMachine implements IPowerMetricsSyncConsumer {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerMonitor> TYPE = new BlockEntityTypeAllocator<BlockEntityPowerMonitor>(
			"power_monitor", (allocator, pos, state) -> new BlockEntityPowerMonitor(allocator, pos, state),
			ModBlocks.PowerMonitor);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderPowerMonitor::new);
		}
	}

	@SaveSerialize
	private PowerTransferMetrics metrics;
	private DynamicGraphDataSet recievedData;
	private DynamicGraphDataSet providedData;

	private long minPowerThreshold;
	private long maxPowerThreshold;

	private long maxPowerIO;

	private long inputRFTick;
	private long outputRFTick;

	protected PowerDistributionComponent powerDistributor;

	public BlockEntityPowerMonitor(BlockEntityTypeAllocator<BlockEntityPowerMonitor> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", powerStorage));

		// Setup the energy storage component.
		powerStorage.setAutoSyncPacketsEnabled(true);
		powerStorage.setSideConfiguration(ioSideConfiguration);

		// Create the metric capturing values.
		metrics = new PowerTransferMetrics();

		// Initialize the default data.
		recievedData = new DynamicGraphDataSet(new SDColor(0.1f, 1.0f, 0.2f, 0.75f));
		providedData = new DynamicGraphDataSet(new SDColor(1.0f, 0.1f, 0.2f, 0.75f));
	}

	@Override
	public void process() {

	}

	public PowerTransferMetrics getMetrics() {
		return metrics;
	}

	public void setMinimumPowerThreshold(int newThreshold) {
		minPowerThreshold = newThreshold;
	}

	public void setMaximumPowerThreshold(int newThreshold) {
		maxPowerThreshold = newThreshold;
	}

	public long getMinimumPowerThreshold() {
		return minPowerThreshold;
	}

	public long getMaximumPowerThreshold() {
		return maxPowerThreshold;
	}

	public long getInputLimit() {
		return inputRFTick;
	}

	public long getOutputLimit() {
		return outputRFTick;
	}

	public void setInputLimit(long newLimit) {
		inputRFTick = newLimit;
	}

	public void setOutputLimit(long newLimit) {
		outputRFTick = newLimit;
	}

	public void setMaximumPowerIO(long newMaxIO) {
		maxPowerIO = newMaxIO;
		setInputLimit(maxPowerIO);
		setOutputLimit(maxPowerIO);
	}

	public long getMaximumPowerIO() {
		return maxPowerIO;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPowerMonitor(windowId, inventory, this);
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		minPowerThreshold = nbt.getLong("min_power_threshold");
		maxPowerThreshold = nbt.getLong("max_power_threshold");

		inputRFTick = nbt.getLong("input_limit");
		outputRFTick = nbt.getLong("output_limit");
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		nbt.putLong("min_power_threshold", minPowerThreshold);
		nbt.putLong("max_power_threshold", maxPowerThreshold);

		nbt.putLong("input_limit", inputRFTick);
		nbt.putLong("output_limit", outputRFTick);
		return nbt;
	}

	@Override
	public void recieveMetrics(PowerTransferMetrics metrics) {
		this.metrics = metrics;
//		recievedData = new DynamicGraphDataSet(new SDColor(0.1f, 1.0f, 0.2f, 0.75f),
//				metrics.getData(MetricsTimeUnit.SECONDS).getInputValues());
//		providedData = new DynamicGraphDataSet(new SDColor(1.0f, 0.1f, 0.2f, 0.75f),
//				metrics.getData(MetricsTimeUnit.SECONDS).getOutputValues());
	}

	/**
	 * @return the recievedData
	 */
	public DynamicGraphDataSet getRecievedData() {
		return recievedData;
	}

	/**
	 * @return the providedData
	 */
	public DynamicGraphDataSet getProvidedData() {
		return providedData;
	}
}
