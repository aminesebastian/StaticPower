package theking530.staticpower.tileentities.components.energy;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.energy.consumer.CapabilityStaticPower;
import theking530.api.energy.consumer.IStaticPowerStorage;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;

public class PowerDistributionComponent extends AbstractTileEntityComponent {

	private IStaticPowerStorage energyStorage;
	private MachineSideMode outputMode;

	public PowerDistributionComponent(String name, IStaticPowerStorage energyStorage) {
		this(name, energyStorage, MachineSideMode.Output);
	}

	public PowerDistributionComponent(String name, IStaticPowerStorage energyStorage, MachineSideMode mode) {
		super(name);
		this.energyStorage = energyStorage;
		this.outputMode = mode;
	}

	@Override
	public void preProcessUpdate() {
		if (isOnClientSide()) {
			return;
		}
		if (energyStorage != null && energyStorage.getStoredPower() > 0) {
			for (Direction facing : Direction.values()) {
				if (canOutputFromSide(facing)) {
					providePower(facing);
				}
			}
		}
	}

	public double providePower(Direction facing) {
		return providePower(getTileEntity().getBlockPos().relative(facing), facing);
	}

	public double providePower(BlockPos pos, Direction facing) {
		IStaticPowerStorage targetStorage = getPowerStorageAtLocation(pos, facing.getOpposite());

		if (targetStorage != null && energyStorage.getVoltageOutput() != 0) {
			double maxCurrentOutput = Math.min(energyStorage.getStoredPower() / energyStorage.getVoltageOutput(), 10);
			double maxPowerOutput = energyStorage.getVoltageOutput() * maxCurrentOutput;
			double usedPower = targetStorage.addPower(energyStorage.getVoltageOutput(), maxPowerOutput, false);
			energyStorage.usePower(usedPower, false);
			return usedPower;
		}
		return 0;
	}

	@Nullable
	public IStaticPowerStorage getPowerStorageAtLocation(BlockPos pos, Direction facing) {
		// Get the tile entity.
		BlockEntity te = getTileEntity().getLevel().getBlockEntity(pos);

		// If it does not exist, return null.
		if (te == null) {
			return null;
		}

		// Check if the tile entity is a static volt handler.
		return te.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, facing).orElse(null);
	}

	public boolean canOutputFromSide(Direction facing) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(facing) == outputMode;
		}
		return true;
	}
}
