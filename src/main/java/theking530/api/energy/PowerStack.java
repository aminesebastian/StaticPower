package theking530.api.energy;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

public class PowerStack {
	public static final PowerStack EMPTY = new EmptyPowerStack(0, 0);
	private double power;
	private double voltage;
	private double current;
	private CurrentType type;

	public PowerStack(double power, double voltage) {
		this(power, voltage, CurrentType.DIRECT);
	}

	public PowerStack(double power, double voltage, CurrentType type) {
		this.power = power;
		this.voltage = voltage;
		this.type = type;
		updateCurrent();
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
		updateCurrent();
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
		updateCurrent();
	}

	public CurrentType getCurrentType() {
		return type;
	}

	public double getCurrent() {
		return current;
	}

	public void setType(CurrentType type) {
		this.type = type;
	}

	public boolean isEmpty() {
		return power == 0;
	}

	public PowerStack copy() {
		return new PowerStack(power, voltage, type);
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putDouble("p", power);
		output.putDouble("v", voltage);
		output.putByte("t", (byte) type.ordinal());
		return output;
	}

	public static PowerStack deserialize(CompoundTag tag) {
		return new PowerStack(tag.getDouble("p"), tag.getDouble("v"), CurrentType.values()[tag.getByte("t")]);
	}

	protected void updateCurrent() {
		if (voltage != 0) {
			current = power / Math.abs(voltage);
		} else {
			current = 0;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(power, type, voltage);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PowerStack other = (PowerStack) obj;
		return Double.doubleToLongBits(power) == Double.doubleToLongBits(other.power) && type == other.type
				&& Double.doubleToLongBits(voltage) == Double.doubleToLongBits(other.voltage);
	}

	@Override
	public String toString() {
		return "PowerStack [power=" + power + ", voltage=" + voltage + ", type=" + type + "]";
	}

	/**
	 * This is purely so that we make sure no one tries to modify the empty power
	 * stack.
	 * 
	 * @author amine
	 *
	 */
	private static class EmptyPowerStack extends PowerStack {
		public EmptyPowerStack(double power, double voltage) {
			super(power, voltage);
		}

		@Override
		public void setPower(double power) {
			throw new RuntimeException("Someone tried to modify the empty power stack! Make a copy if you need to do so!");
		}

		@Override
		public void setVoltage(double voltage) {
			throw new RuntimeException("Someone tried to modify the empty power stack! Make a copy if you need to do so!");
		}
	}
}
