package theking530.staticpower.tileentities.components.power;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.api.power.PowerEnergyInterface;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;

public class PowerDistributionComponent extends AbstractTileEntityComponent {

	private IStaticVoltHandler energyStorage;
	private MachineSideMode outputMode;

	public PowerDistributionComponent(String name, IStaticVoltHandler energyStorage) {
		this(name, energyStorage, MachineSideMode.Output);
	}

	public PowerDistributionComponent(String name, IStaticVoltHandler energyStorage, MachineSideMode mode) {
		super(name);
		this.energyStorage = energyStorage;
		this.outputMode = mode;
	}

	@SuppressWarnings("resource")
	@Override
	public void preProcessUpdate() {
		if (getLevel().isClientSide) {
			return;
		}
		if (energyStorage != null && getTileEntity() != null) {
			if (energyStorage.getStoredPower() > 0) {
				for (Direction facing : Direction.values()) {
					if (canOutputFromSide(facing)) {
						long maxExtract = energyStorage.drainPower(Integer.MAX_VALUE, true);
						providePower(facing, Math.min(maxExtract, energyStorage.getStoredPower()));
					}
				}
			}
		}
	}

	public long providePower(Direction facing, long amount) {
		return providePower(getTileEntity().getBlockPos().relative(facing), facing, amount);
	}

	/**
	 * @param pos    - The position of the block to send energy to.
	 * @param facing - The side of the block we are sending energy to.
	 * @param amount - The amount of energy to send.
	 * @return - The actual amount of energy that was sent.
	 */
	public long providePower(BlockPos pos, Direction facing, long amount) {
		PowerEnergyInterface powerInterface = getInterfaceForDesination(pos, facing.getOpposite());

		if (powerInterface != null) {
			long provided = powerInterface.receivePower(amount, true);
			long drained = energyStorage.drainPower(provided, false);
			powerInterface.receivePower(drained, false);
			return provided;
		}
		return 0;
	}

	@Nullable
	public PowerEnergyInterface getInterfaceForDesination(BlockPos pos, Direction facing) {
		// Get the tile entity.
		BlockEntity te = getTileEntity().getLevel().getBlockEntity(pos);

		// If it does not exist, return null.
		if (te == null) {
			return null;
		}

		// Check if the tile entity is a static volt handler.
		LazyOptional<IStaticVoltHandler> powerStorageOptional = te.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY, facing);
		// If it is, return an interface based on that. Otherwise, check the same thing
		// with energy storage instead.
		if (powerStorageOptional.isPresent()) {
			IStaticVoltHandler powerStorage = powerStorageOptional.orElse(null);
			if (powerStorage != null && powerStorage.receivePower(Integer.MAX_VALUE, true) > 0) {
				return new PowerEnergyInterface(powerStorage);
			}
		} else {
			LazyOptional<IEnergyStorage> energyStorageOptional = te.getCapability(CapabilityEnergy.ENERGY, facing);
			if (energyStorageOptional.isPresent()) {
				IEnergyStorage energyStorage = energyStorageOptional.orElse(null);
				if (energyStorage != null && energyStorage.receiveEnergy(Integer.MAX_VALUE, true) > 0) {
					return new PowerEnergyInterface(energyStorage);
				}
			}
		}
		return null;
	}

	public boolean canOutputFromSide(Direction facing) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(facing) == outputMode;
		}
		return true;
	}
}
