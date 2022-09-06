package theking530.staticpower.tileentities.testing.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.tileentities.components.energy.PowerStorageComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class BlockEntityTestBattery extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTestBattery> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntityTestBattery>(
			(allocator, pos, state) -> new BlockEntityTestBattery(allocator, pos, state, StaticPowerTiers.CREATIVE), ModBlocks.BatteryTest);

	public static final DefaultSideConfiguration DEFAULT_SIDE_CONFIGURATION = new DefaultSideConfiguration();

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
//			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
//			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
//			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
//			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
//			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
//			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
		}
	}

	public final InventoryComponent chargingInventory;
	public final PowerStorageComponent powerStorage;

	private long minPowerThreshold;
	private long maxPowerThreshold;

	private long maxPowerIO;

	private long inputRFTick;
	private long outputRFTick;

	protected PowerDistributionComponent powerDistributor;

	public BlockEntityTestBattery(BlockEntityTypeAllocator<BlockEntityTestBattery> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state);
		// Enable face interaction.
		enableFaceInteraction();
		this.ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.DEFAULT_SIDE_CONFIGURATION);

		// Setup the energy storage component.
		registerComponent(powerStorage = new PowerStorageComponent("powerStorage", 5, 12, 1000, 10).setSideConfiguration(ioSideConfiguration));

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", powerStorage));

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		// Calculate the IO.
		maxPowerIO = tierObject.batteryMaxIO.get();
		inputRFTick = maxPowerIO / 2;
		outputRFTick = maxPowerIO / 2;

		// Set the capacities and IO.
		powerStorage.setCapacity(5000);

		// Add a battery input.
		registerComponent(chargingInventory = new InventoryComponent("ChargingInventorySlot", 1));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide) {
			double voltage = 12;
			this.powerStorage.addPower(voltage, 31, false);
			this.powerStorage.setOutputVoltage(120);
		}
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
//		energyStorage.setMaxReceive(newLimit);
	}

	public void setOutputLimit(long newLimit) {
		outputRFTick = newLimit;
//		energyStorage.setMaxExtract(newLimit);
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
		return new ContainerTestBattery(windowId, inventory, this);
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
}
