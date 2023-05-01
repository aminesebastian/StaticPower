package theking530.api.energy;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

public class PowerStack {
	public static final PowerStack EMPTY = new EmptyPowerStack(0, StaticPowerVoltage.ZERO);

	private double power;
	private StaticPowerVoltage voltage;
	private double current;
	private CurrentType type;

	public PowerStack(double power, StaticPowerVoltage voltage) {
		this(power, voltage, CurrentType.DIRECT);
	}

	private PowerStack(PowerStack otherStack) {
		this(otherStack, otherStack.power);
	}

	private PowerStack(PowerStack otherStack, double power) {
		this(power, otherStack.voltage, otherStack.type);
	}

	public PowerStack(double power, StaticPowerVoltage voltage, CurrentType type) {
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

	public StaticPowerVoltage getVoltage() {
		return voltage;
	}

	public void setVoltage(StaticPowerVoltage voltage) {
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
		return new PowerStack(this);
	}

	public PowerStack copyWithPower(double power) {
		return new PowerStack(this, power);
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putDouble("p", power);
		output.putDouble("v", voltage.ordinal());
		output.putByte("t", (byte) type.ordinal());
		return output;
	}

	public static PowerStack deserialize(CompoundTag tag) {
		return new PowerStack(tag.getDouble("p"), StaticPowerVoltage.values()[tag.getByte("v")],
				CurrentType.values()[tag.getByte("t")]);
	}

	protected void updateCurrent() {
		if (voltage.getValue() > 0) {
			current = power / voltage.getValue();
		} else {
			current = 0;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(current, power, type, voltage);
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
		return Double.doubleToLongBits(current) == Double.doubleToLongBits(other.current)
				&& Double.doubleToLongBits(power) == Double.doubleToLongBits(other.power) && type == other.type
				&& voltage == other.voltage;
	}

	@Override
	public String toString() {
		return "PowerStack [power=" + power + ", voltage=" + voltage + ", current=" + current + ", type=" + type + "]";
	}

	/**
	 * This is purely so that we make sure no one tries to modify the empty power
	 * stack.
	 * 
	 * @author amine
	 *
	 */
	private static class EmptyPowerStack extends PowerStack {
		public EmptyPowerStack(double power, StaticPowerVoltage voltage) {
			super(power, voltage);
		}

		@Override
		public void setPower(double power) {
			throw new RuntimeException(
					"Someone tried to modify the empty power stack! Make a copy if you need to do so!");
		}

		@Override
		public void setVoltage(StaticPowerVoltage voltage) {
			throw new RuntimeException(
					"Someone tried to modify the empty power stack! Make a copy if you need to do so!");
		}
	}
}
