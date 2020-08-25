package theking530.staticpower.tileentities.components.power;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.energy.CapabilityStaticVolt;
import theking530.staticpower.energy.IStaticVoltHandler;
import theking530.staticpower.energy.PowerEnergyInterface;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

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

	@Override
	public void preProcessUpdate() {
		if (getWorld().isRemote) {
			return;
		}
		if (energyStorage != null && getTileEntity() != null) {
			if (energyStorage.getStoredPower() > 0) {
				for (Direction facing : Direction.values()) {
					if (canOutputFromSide(facing)) {
						int maxExtract = energyStorage.drainPower(Integer.MAX_VALUE, true);
						providePower(facing, Math.min(maxExtract, energyStorage.getStoredPower()));
					}
				}
			}
		}
	}

	public int providePower(Direction facing, int amount) {
		return providePower(getTileEntity().getPos().offset(facing), facing, amount);
	}

	/**
	 * @param pos    - The position of the block to send energy to.
	 * @param facing - The side of the block we are sending energy to.
	 * @param amount - The amount of energy to send.
	 * @return - The actual amount of energy that was sent.
	 */
	public int providePower(BlockPos pos, Direction facing, int amount) {
		PowerEnergyInterface powerInterface = getInterfaceForDesination(pos, facing.getOpposite());

		if (powerInterface != null) {
			if (powerInterface.getStoredPower() < powerInterface.getCapacity() && powerInterface.canRecievePower()) {
				int provided = powerInterface.receivePower(amount, false);
				energyStorage.drainPower(provided, false);
				return provided;
			}
		}
		return 0;
	}

	@Nullable
	public PowerEnergyInterface getInterfaceForDesination(BlockPos pos, Direction facing) {
		// Get the tile entity.
		TileEntity te = getTileEntity().getWorld().getTileEntity(pos);

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
			if (powerStorage != null && powerStorage.receivePower(1, true) > 0) {
				return new PowerEnergyInterface(powerStorage);
			}
		} else {
			LazyOptional<IEnergyStorage> energyStorageOptional = te.getCapability(CapabilityEnergy.ENERGY, facing);
			if (energyStorageOptional.isPresent()) {
				IEnergyStorage energyStorage = energyStorageOptional.orElse(null);
				if (energyStorage != null && energyStorage.receiveEnergy(1, true) > 0) {
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
