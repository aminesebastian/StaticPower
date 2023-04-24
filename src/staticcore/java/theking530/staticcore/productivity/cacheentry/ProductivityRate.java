package theking530.staticcore.productivity.cacheentry;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.utilities.math.SDMath;

public class ProductivityRate {
	private double currentValue;
	private double idealValue;

	public ProductivityRate(double currentValue, double idealValue) {
		this.currentValue = currentValue;
		this.idealValue = idealValue;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public double getIdealValue() {
		return idealValue;
	}

	public void setIdealValue(double idealValue) {
		this.idealValue = idealValue;
	}

	public boolean isZero() {
		return currentValue == 0 && idealValue == 0;
	}

	public ProductivityRate copy() {
		return new ProductivityRate(currentValue, idealValue);
	}

	public void setValues(double currentValue, double idealValue) {
		this.currentValue = currentValue;
		this.idealValue = idealValue;
	}

	public void interpolateTowards(double totalCurrentValue, double totalIdealValue, double smoothingFactor) {
		double delta = Math.abs(totalCurrentValue - totalIdealValue);
		double relativeSmoothingFactor = smoothingFactor / Math.sqrt(delta);
		relativeSmoothingFactor = SDMath.clamp(relativeSmoothingFactor, 1, smoothingFactor);

		currentValue = (totalCurrentValue + (currentValue * relativeSmoothingFactor)) / (relativeSmoothingFactor + 1);
		if (currentValue < 0.001) {
			if (totalCurrentValue > 0) {
				currentValue = totalCurrentValue;
			} else {
				currentValue = 0;
			}
		}

		idealValue = (totalIdealValue + (idealValue * relativeSmoothingFactor)) / (relativeSmoothingFactor + 1);
		if (idealValue < 0.001) {
			if (totalIdealValue > 0) {
				idealValue = totalIdealValue;
			} else {
				idealValue = 0;
			}
		}

	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putDouble("c", currentValue);
		output.putDouble("i", idealValue);
		return output;
	}

	public static ProductivityRate deserialize(CompoundTag tag) {
		return new ProductivityRate(tag.getDouble("c"), tag.getDouble("i"));
	}

	@Override
	public String toString() {
		return "ProductivityRate [currentValue=" + currentValue + ", idealValue=" + idealValue + "]";
	}
}
