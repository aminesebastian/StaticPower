package theking530.staticpower.teams.production;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;

public abstract class ProductionEntry<T> {
	private final List<Integer> insertedPerSecond;
	private final List<Integer> extractedPerSecond;
	protected T product;
	protected int currentSecondInserted;
	protected int currentSecondExtracted;

	public ProductionEntry(T product) {
		this();
		this.product = product;
	}

	public ProductionEntry(CompoundTag tag) {
		this();
		deserialize(tag);
	}

	protected ProductionEntry() {
		this.currentSecondInserted = 0;
		this.currentSecondExtracted = 0;
		this.insertedPerSecond = new LinkedList<>();
		this.extractedPerSecond = new LinkedList<>();
	}

	public void tick(long gameTime) {
		if (gameTime % 20 == 0) {
			capture(gameTime);
		}
	}

	private void capture(long gameTime) {
		insertedPerSecond.add(currentSecondInserted);
		extractedPerSecond.add(currentSecondExtracted);
		currentSecondInserted = 0;
		currentSecondExtracted = 0;
	}

	public T getProduct() {
		return product;
	}

	public void inserted(int amount) {
		currentSecondInserted += Math.max(0, amount);
	}

	public void extracted(int amount) {
		currentSecondExtracted += Math.max(0, amount);
	}

	@Override
	public abstract int hashCode();

	@Override
	public String toString() {
		return "ProductionEntry [product=" + product + ", inserted=" + currentSecondInserted + ", extracted=" + currentSecondExtracted + "]";
	}

	public abstract CompoundTag serialize();

	public abstract void deserialize(CompoundTag tag);
}
