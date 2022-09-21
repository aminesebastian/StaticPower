package theking530.api.energy.sided;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticVoltageRange;

/**
 * This is a utility wrapper that allows block entities to provide different
 * power functionality depending on the accessed face.
 * 
 * Power Storages that you want to be side accessible need to inherit from
 * {@link ISidedStaticPowerStorage} instead of {@link IStaticPowerStorage}.
 * 
 * @author amine
 *
 */
public class SidedStaticPowerCapabilityWrapper {
	private final Map<Direction, SidedStaticPowerCapability> sides;
	private final ISidedStaticPowerStorage owner;

	public SidedStaticPowerCapabilityWrapper(ISidedStaticPowerStorage proxy) {
		sides = new HashMap<>();
		this.owner = proxy;
		for (Direction dir : Direction.values()) {
			sides.put(dir, new SidedStaticPowerCapability(dir, proxy));
		}
	}

	public IStaticPowerStorage get(@Nullable Direction side) {
		if (side == null) {
			return owner;
		}
		return sides.get(side);
	}

	public class SidedStaticPowerCapability implements IStaticPowerStorage {
		private final Direction representedSide;
		private final ISidedStaticPowerStorage proxy;

		public SidedStaticPowerCapability(Direction representedSide, ISidedStaticPowerStorage proxy) {
			this.representedSide = representedSide;
			this.proxy = proxy;
		}

		@Override
		public final StaticVoltageRange getInputVoltageRange() {
			return proxy.getInputVoltageRange(representedSide);
		}

		@Override
		public double getMaximumPowerInput() {
			return proxy.getMaximumCurrentInput(representedSide);
		}

		@Override
		public boolean canAcceptCurrentType(CurrentType type) {
			return proxy.canAcceptCurrentType(representedSide, type);
		}

		@Override
		public final double getOutputVoltage() {
			return proxy.getOutputVoltage(representedSide);
		}

		@Override
		public final double getMaximumPowerOutput() {
			return proxy.getMaximumCurrentOutput(representedSide);
		}

		@Override
		public CurrentType getOutputCurrentType() {
			return proxy.getOutputCurrentType(representedSide);
		}

		@Override
		public final double getStoredPower() {
			return proxy.getStoredPower(representedSide);
		}

		@Override
		public final double getCapacity() {
			return proxy.getCapacity(representedSide);
		}

		@Override
		public final double addPower(PowerStack power, boolean simulate) {
			return proxy.addPower(representedSide, power, simulate);
		}

		@Override
		public final PowerStack drainPower(double power, boolean simulate) {
			return proxy.drainPower(representedSide, power, simulate);
		}
	}
}
