package theking530.staticpower.tileentities.powered.battery;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderBatteryBlock;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;

public class TileEntityBattery extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBattery> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityBattery>((allocator) -> new TileEntityBattery(allocator, StaticPowerTiers.BASIC),
			ModBlocks.BatteryBasic);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBattery> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityBattery>(
			(allocator) -> new TileEntityBattery(allocator, StaticPowerTiers.ADVANCED), ModBlocks.BatteryAdvanced);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBattery> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityBattery>(
			(allocator) -> new TileEntityBattery(allocator, StaticPowerTiers.STATIC), ModBlocks.BatteryStatic);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBattery> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityBattery>(
			(allocator) -> new TileEntityBattery(allocator, StaticPowerTiers.ENERGIZED), ModBlocks.BatteryEnergized);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBattery> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityBattery>((allocator) -> new TileEntityBattery(allocator, StaticPowerTiers.LUMUM),
			ModBlocks.BatteryLumum);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBattery> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityBattery>(
			(allocator) -> new TileEntityBattery(allocator, StaticPowerTiers.CREATIVE), ModBlocks.BatteryCreative);

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

	private int minPowerThreshold;
	private int maxPowerThreshold;

	private int maxPowerIO;

	private int inputRFTick;
	private int outputRFTick;

	protected PowerDistributionComponent powerDistributor;

	public TileEntityBattery(TileEntityTypeAllocator<TileEntityBattery> allocator, ResourceLocation tier) {
		super(allocator);
		DisableFaceInteraction = false;

		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", energyStorage.getStorage()));
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

		energyStorage.getStorage().setCapacity(TierReloadListener.getTier(tier).getBatteryCapacity());
		inputRFTick = TierReloadListener.getTier(tier).getDefaultMachinePowerOutput() * 2;
		outputRFTick = TierReloadListener.getTier(tier).getDefaultMachinePowerOutput() * 2;
		maxPowerIO = inputRFTick * 2;

		energyStorage.getStorage().setMaxReceive(inputRFTick);
		energyStorage.getStorage().setMaxExtract(outputRFTick);
	}

	public void process() {
		super.process();
	}

	public void setMinimumPowerThreshold(int newThreshold) {
		minPowerThreshold = newThreshold;
	}

	public void setMaximumPowerThreshold(int newThreshold) {
		maxPowerThreshold = newThreshold;
	}

	public int getMinimumPowerThreshold() {
		return minPowerThreshold;
	}

	public int getMaximumPowerThreshold() {
		return maxPowerThreshold;
	}

	public int getInputLimit() {
		return inputRFTick;
	}

	public int getOutputLimit() {
		return outputRFTick;
	}

	public void setInputLimit(int newLimit) {
		inputRFTick = newLimit;
		energyStorage.getStorage().setMaxReceive(newLimit);
	}

	public void setOutputLimit(int newLimit) {
		outputRFTick = newLimit;
		energyStorage.getStorage().setMaxExtract(newLimit);
	}

	public void setMaximumPowerIO(int newMaxIO) {
		maxPowerIO = newMaxIO;
		setInputLimit(maxPowerIO);
		setOutputLimit(maxPowerIO);
	}

	public int getMaximumPowerIO() {
		return maxPowerIO;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerBattery(windowId, inventory, this);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		minPowerThreshold = nbt.getInt("min_power_threshold");
		maxPowerThreshold = nbt.getInt("max_power_threshold");

		inputRFTick = nbt.getInt("input_limit");
		outputRFTick = nbt.getInt("output_limit");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		nbt.putInt("min_power_threshold", minPowerThreshold);
		nbt.putInt("max_power_threshold", maxPowerThreshold);

		nbt.putInt("input_limit", inputRFTick);
		nbt.putInt("output_limit", outputRFTick);
		return nbt;
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
}
