package theking530.staticpower.tileentities.powered.powermonitor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.gui.widgets.DataGraphWidget.FloatGraphDataSet;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderPowerMonitor;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.IPowerMetricsSyncConsumer;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics.MetricCategory;
import theking530.staticpower.tileentities.components.power.TileEntityPowerMetricsSyncPacket;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;

public class TileEntityPowerMonitor extends TileEntityMachine implements IPowerMetricsSyncConsumer {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerMonitor> TYPE = new TileEntityTypeAllocator<TileEntityPowerMonitor>((allocator) -> new TileEntityPowerMonitor(allocator),
			ModBlocks.PowerMonitor);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderPowerMonitor::new);
		}
	}

	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent chargingInventory;

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

	public TileEntityPowerMonitor(TileEntityTypeAllocator<TileEntityPowerMonitor> allocator) {
		super(allocator);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", energyStorage.getStorage()));

		// Setup the energy storage component.
		energyStorage.setAutoSyncPacketsEnabled(true);
		energyStorage.setCapabiltiyFilter((amount, direction, action) -> {
			if (direction == null) {
				return false;
			}
			if (action == EnergyManipulationAction.PROVIDE && this.ioSideConfiguration.getWorldSpaceDirectionConfiguration(direction) != MachineSideMode.Output) {
				return false;
			}
			if (action == EnergyManipulationAction.RECIEVE && this.ioSideConfiguration.getWorldSpaceDirectionConfiguration(direction) != MachineSideMode.Input) {
				return false;
			}
			if (this.ioSideConfiguration.getWorldSpaceDirectionConfiguration(direction) == MachineSideMode.Disabled) {
				return false;
			}
			return true;
		});

		// Get the maximum tier possible.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(StaticPowerTiers.LUMUM);

		// Calculate the IO.
		maxPowerIO = tierObject.batteryMaxIO.get();
		inputRFTick = maxPowerIO / 2;
		outputRFTick = maxPowerIO / 2;

		// Set the capacities and IO.
		energyStorage.getStorage().setCapacity(maxPowerIO);
		energyStorage.getStorage().setMaxReceive(inputRFTick);
		energyStorage.getStorage().setMaxExtract(outputRFTick);

		// Add a battery input.
		registerComponent(chargingInventory = new InventoryComponent("ChargingInventorySlot", 1));

		// Add the charging input.
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));

		// Create the metric capturing values.
		metrics = new PowerTransferMetrics();
	}

	@Override
	public void process() {
		if (!getWorld().isRemote) {
			// If this is a creative battery, always keep the power at max.
			if (getTier() == StaticPowerTiers.CREATIVE) {
				this.energyStorage.getStorage().addPowerIgnoreTransferRate(Long.MAX_VALUE);
			}

			// Charge up the item in the input slot.
			if (energyStorage.getStorage().getStoredPower() > 0) {
				// Get the item to charge.
				ItemStack stack = chargingInventory.getStackInSlot(0);
				// If it's not empty and is an energy storing item.
				if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
					if (EnergyHandlerItemStackUtilities.getStoredPower(stack) < EnergyHandlerItemStackUtilities.getCapacity(stack)) {
						long charged = EnergyHandlerItemStackUtilities.receivePower(stack, energyStorage.getStorage().getCurrentMaximumPowerOutput(), false);
						energyStorage.useBulkPower(charged);
					}
				}
			}

			// Capture metrics.
			metrics.addMetric(energyStorage.getStorage().getReceivedPerTick(), energyStorage.getStorage().getExtractedPerTick());

			if (!getWorld().isRemote() && getWorld().getGameTime() % 20 == 0) {
				TileEntityPowerMetricsSyncPacket msg = new TileEntityPowerMetricsSyncPacket(getPos(), metrics);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 20, msg);
			}
		}
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
		energyStorage.getStorage().setMaxReceive(newLimit);
	}

	public void setOutputLimit(long newLimit) {
		outputRFTick = newLimit;
		energyStorage.getStorage().setMaxExtract(newLimit);
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
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPowerMonitor(windowId, inventory, this);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		minPowerThreshold = nbt.getLong("min_power_threshold");
		maxPowerThreshold = nbt.getLong("max_power_threshold");

		inputRFTick = nbt.getLong("input_limit");
		outputRFTick = nbt.getLong("output_limit");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		nbt.putLong("min_power_threshold", minPowerThreshold);
		nbt.putLong("max_power_threshold", maxPowerThreshold);

		nbt.putLong("input_limit", inputRFTick);
		nbt.putLong("output_limit", outputRFTick);
		return nbt;
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return DEFAULT_NO_FACE_SIDE_CONFIGURATION.copy().setSide(BlockSide.TOP, false, MachineSideMode.Never).setSide(BlockSide.BOTTOM, false, MachineSideMode.Never).setSide(BlockSide.BACK,
				false, MachineSideMode.Never);
	}

	// Tab Integration
	public boolean shouldOutputRedstoneSignal() {
		if (minPowerThreshold == 0 && maxPowerThreshold == 0) {
			return false;
		}
		float storedPercentage = energyStorage.getStorage().getStoredEnergyPercentScaled(100.0f);
		if (storedPercentage >= this.minPowerThreshold && storedPercentage <= this.maxPowerThreshold) {
			return true;
		}
		return false;
	}

	@Override
	public void recieveMetrics(PowerTransferMetrics metrics) {
		this.metrics = metrics;
		recievedData = new FloatGraphDataSet(new Color(0.1f, 1.0f, 0.2f, 0.75f), metrics.getData(MetricCategory.SECONDS).getInputValues());
		providedData = new FloatGraphDataSet(new Color(1.0f, 0.1f, 0.2f, 0.75f), metrics.getData(MetricCategory.SECONDS).getOutputValues());
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
