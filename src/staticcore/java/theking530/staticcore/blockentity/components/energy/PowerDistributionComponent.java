package theking530.staticcore.blockentity.components.energy;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;

public class PowerDistributionComponent extends AbstractBlockEntityComponent {

	@Nullable
	private IStaticPowerStorage autoDistributionSource;
	private MachineSideMode outputMode;
	private boolean provideAlternatingCurrent;

	public PowerDistributionComponent(String name) {
		this(name, null, MachineSideMode.Output);
	}

	public PowerDistributionComponent(String name, IStaticPowerStorage energyStorage) {
		this(name, energyStorage, MachineSideMode.Output);
	}

	public PowerDistributionComponent(String name, IStaticPowerStorage energyStorage, MachineSideMode mode) {
		super(name);
		this.autoDistributionSource = energyStorage;
		this.outputMode = mode;
		provideAlternatingCurrent = false;
	}

	@Override
	public void preProcessUpdate() {
		if (isClientSide()) {
			return;
		}

		if (autoDistributionSource != null && autoDistributionSource.getStoredPower() > 0) {
			for (Direction facing : Direction.values()) {
				if (canOutputFromSide(facing)) {
					PowerStack maxDrain = autoDistributionSource.drainPower(StaticPowerEnergyUtilities.getMaximumPower(), true);
					distributeOnSide(autoDistributionSource, facing, maxDrain, false);
				}
			}
		}
	}

	public double manuallyDistributePower(Direction[] sides, IStaticPowerStorage source, PowerStack stack, boolean simulate) {
		double providedTotal = 0;
		for (Direction facing : sides) {
			if (canOutputFromSide(facing)) {
				double provided = distributeOnSide(source, facing, stack, simulate);
				providedTotal += provided;
				if (!simulate) {
					source.drainPower(provided, simulate);
				}
			}
		}
		return providedTotal;
	}

	public double manuallyDistributePower(List<Direction> sides, IStaticPowerStorage source, PowerStack stack, boolean simulate) {
		Direction[] sidesArray = new Direction[sides.size()];
		sides.toArray(sidesArray);
		return manuallyDistributePower(sidesArray, source, stack, simulate);
	}

	public double manuallyDistributePower(IStaticPowerStorage source, PowerStack stack, boolean simulate) {
		return manuallyDistributePower(Direction.values(), source, stack, simulate);
	}

	protected double distributeOnSide(IStaticPowerStorage source, Direction facing, PowerStack stack, boolean simulate) {
		IStaticPowerStorage targetStorage = getPowerStorageOnSide(facing.getOpposite());

		if (source != null && targetStorage != null) {
			double provided = targetStorage.addPower(stack, simulate);
			source.drainPower(provided, simulate);
			return provided;
		}
		return 0;
	}

	@Nullable
	protected IStaticPowerStorage getPowerStorageOnSide(Direction facing) {
		// Get the tile entity.
		BlockEntity te = getBlockEntity().getLevel().getBlockEntity(getBlockEntity().getBlockPos().relative(facing.getOpposite()));

		// If it does not exist, return null.
		if (te == null) {
			return null;
		}

		// Check if the tile entity is a static volt handler.
		return te.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, facing).orElse(null);
	}

	protected boolean canOutputFromSide(Direction facing) {
		if (getBlockEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getBlockEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(facing) == outputMode;
		}
		return true;
	}

	public boolean isProvideAlternatingCurrent() {
		return provideAlternatingCurrent;
	}

	public PowerDistributionComponent setProvideAlternatingCurrent(boolean provideAlternatingCurrent) {
		this.provideAlternatingCurrent = provideAlternatingCurrent;
		return this;
	}
}
