package theking530.staticpower.tileentities.powered.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.volts.StaticVoltUtilities;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderBatteryBlock;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.OldPowerDistributionComponent;

public class TileEntityBattery extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.BASIC), ModBlocks.BatteryBasic);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.BatteryAdvanced);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.STATIC), ModBlocks.BatteryStatic);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.BatteryEnergized);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.LUMUM), ModBlocks.BatteryLumum);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.CREATIVE), ModBlocks.BatteryCreative);

	public static final DefaultSideConfiguration DEFAULT_SIDE_CONFIGURATION = new DefaultSideConfiguration();

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
		}
	}

	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent chargingInventory;

	private long minPowerThreshold;
	private long maxPowerThreshold;

	private long maxPowerIO;

	private long inputRFTick;
	private long outputRFTick;

	protected OldPowerDistributionComponent powerDistributor;

	public TileEntityBattery(BlockEntityTypeAllocator<TileEntityBattery> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state, tier);
		// Enable face interaction.
		enableFaceInteraction();
		this.ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.DEFAULT_SIDE_CONFIGURATION);

		// Add the power distributor.
		registerComponent(powerDistributor = new OldPowerDistributionComponent("PowerDistributor", energyStorage));

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

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		// Calculate the IO.
		maxPowerIO = tierObject.batteryMaxIO.get();
		inputRFTick = maxPowerIO / 2;
		outputRFTick = maxPowerIO / 2;

		// Set the capacities and IO.
		energyStorage.setCapacity(tierObject.batteryCapacity.get());
		energyStorage.setMaxReceive(inputRFTick);
		energyStorage.setMaxExtract(outputRFTick);

		// Add a battery input.
		registerComponent(chargingInventory = new InventoryComponent("ChargingInventorySlot", 1));

		// Add the charging input.
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide) {
			// If this is a creative battery, always keep the power at max.
			if (getTier() == StaticPowerTiers.CREATIVE) {
				this.energyStorage.addPowerIgnoreTransferRate(Long.MAX_VALUE);
			}

			// Charge up the item in the input slot.
			if (energyStorage.getStoredPower() > 0) {
				// Get the item to charge.
				ItemStack stack = chargingInventory.getStackInSlot(0);
				// If it's not empty and is an energy storing item.
				if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
					if (EnergyHandlerItemStackUtilities.getStoredPower(stack) < EnergyHandlerItemStackUtilities.getCapacity(stack)) {
						long charged = EnergyHandlerItemStackUtilities.receivePower(stack, StaticVoltUtilities.getCurrentMaximumPowerOutput(energyStorage), false);
						energyStorage.useBulkPower(charged);
					}
				}
			}
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
		energyStorage.setMaxReceive(newLimit);
	}

	public void setOutputLimit(long newLimit) {
		outputRFTick = newLimit;
		energyStorage.setMaxExtract(newLimit);
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
		return new ContainerBattery(windowId, inventory, this);
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

	// Tab Integration
	public boolean shouldOutputRedstoneSignal() {
		if (minPowerThreshold == 0 && maxPowerThreshold == 0) {
			return false;
		}
		float storedPercentage = StaticVoltUtilities.getStoredEnergyPercentScaled(energyStorage, 100.0f);
		if (storedPercentage >= this.minPowerThreshold && storedPercentage <= this.maxPowerThreshold) {
			return true;
		}
		return false;
	}
}
