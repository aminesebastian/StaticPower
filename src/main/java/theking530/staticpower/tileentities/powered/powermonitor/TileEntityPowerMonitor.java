package theking530.staticpower.tileentities.powered.powermonitor;

import java.util.Collections;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.metrics.IPowerMetricsSyncConsumer;
import theking530.api.energy.metrics.MetricsTimeUnit;
import theking530.api.energy.metrics.PowerTransferMetrics;
import theking530.staticcore.gui.widgets.DataGraphWidget.FloatGraphDataSet;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderPowerMonitor;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;

public class TileEntityPowerMonitor extends TileEntityMachine implements IPowerMetricsSyncConsumer {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerMonitor> TYPE = new BlockEntityTypeAllocator<TileEntityPowerMonitor>(
			(allocator, pos, state) -> new TileEntityPowerMonitor(allocator, pos, state), ModBlocks.PowerMonitor);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderPowerMonitor::new);
		}
	}

	@SaveSerialize
	private PowerTransferMetrics metrics;
	private FloatGraphDataSet recievedData;
	private FloatGraphDataSet providedData;

	private long minPowerThreshold;
	private long maxPowerThreshold;

	private long maxPowerIO;

	private long inputRFTick;
	private long outputRFTick;

	protected PowerDistributionComponent powerDistributor;

	public TileEntityPowerMonitor(BlockEntityTypeAllocator<TileEntityPowerMonitor> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state, StaticPowerTiers.LUMUM);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", powerStorage));

		// Setup the energy storage component.
		powerStorage.setAutoSyncPacketsEnabled(true);
		powerStorage.setSideConfiguration(ioSideConfiguration);

		// Create the metric capturing values.
		metrics = new PowerTransferMetrics();

		// Initialize the default data.
		recievedData = new FloatGraphDataSet(new Color(0.1f, 1.0f, 0.2f, 0.75f), Collections.emptyList());
		providedData = new FloatGraphDataSet(new Color(1.0f, 0.1f, 0.2f, 0.75f), Collections.emptyList());
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
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
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

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return DEFAULT_NO_FACE_SIDE_CONFIGURATION.copy().setSide(BlockSide.TOP, false, MachineSideMode.Never).setSide(BlockSide.BOTTOM, false, MachineSideMode.Never)
				.setSide(BlockSide.BACK, false, MachineSideMode.Never);
	}

	@Override
	public void recieveMetrics(PowerTransferMetrics metrics) {
		this.metrics = metrics;
		recievedData = new FloatGraphDataSet(new Color(0.1f, 1.0f, 0.2f, 0.75f), metrics.getData(MetricsTimeUnit.SECONDS).getInputValues());
		providedData = new FloatGraphDataSet(new Color(1.0f, 0.1f, 0.2f, 0.75f), metrics.getData(MetricsTimeUnit.SECONDS).getOutputValues());
	}

	/**
	 * @return the recievedData
	 */
	public FloatGraphDataSet getRecievedData() {
		return recievedData;
	}

	/**
	 * @return the providedData
	 */
	public FloatGraphDataSet getProvidedData() {
		return providedData;
	}
}
