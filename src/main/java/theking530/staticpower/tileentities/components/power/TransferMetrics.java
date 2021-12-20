package theking530.staticpower.tileentities.components.power;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class TransferMetrics implements INBTSerializable<CompoundNBT> {
	public static final int MAX_METRIC_SAMPLES = 60;

	private final Queue<Float> received;
	private final Queue<Float> provided;

	/**
	 * @param received
	 * @param provided
	 */
	public TransferMetrics() {
		this.received = new LinkedList<Float>();
		this.provided = new LinkedList<Float>();
	}

	protected TransferMetrics(Queue<Float> received, Queue<Float> provided) {
		this.received = received;
		this.provided = provided;
	}

	public boolean isEmpty() {
		return received.isEmpty() || provided.isEmpty();
	}

	public void addMetrics(float received, float provided) {
		// Add the values.
		this.received.add(received);
		this.provided.add(provided);

		// Make sure our queues only keep the correct values.
		if (this.received.size() > MAX_METRIC_SAMPLES) {
			this.received.poll();
		}

		if (this.provided.size() > MAX_METRIC_SAMPLES) {
			this.provided.poll();
		}
	}

	public List<Float> getReceivedData() {
		return new ArrayList<Float>(received);
	}

	public List<Float> getProvidedData() {
		return new ArrayList<Float>(provided);
	}

	@Override
	public String toString() {
		return "TransferMetrics [received=" + received + ", provided=" + provided + "]";
	}

	@Override
	public CompoundNBT serializeNBT() {
		// Allocate the output.
		CompoundNBT output = new CompoundNBT();

		// Convert the queues to lists.
		List<Float> receivedList = new ArrayList<Float>(received);
		List<Float> providedList = new ArrayList<Float>(provided);

		// Serialize the recieved list.
		ListNBT receivedNBTList = new ListNBT();
		receivedList.forEach(value -> {
			FloatNBT recievedTag = FloatNBT.valueOf(value);
			receivedNBTList.add(recievedTag);
		});
		output.put("received", receivedNBTList);

		// Serialize the provided list.
		ListNBT providedNBTList = new ListNBT();
		providedList.forEach(value -> {
			FloatNBT providedTag = FloatNBT.valueOf(value);
			providedNBTList.add(providedTag);
		});
		output.put("provided", providedNBTList);

		// Return the outputs.
		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		// Clear existing data.
		received.clear();
		provided.clear();

		// Read the serialized lists.
		ListNBT receivedNBT = nbt.getList("received", Constants.NBT.TAG_FLOAT);
		ListNBT providedNBT = nbt.getList("provided", Constants.NBT.TAG_FLOAT);

		// Populate the arrays.
		for (INBT receivedTag : receivedNBT) {
			FloatNBT receivedValue = (FloatNBT) receivedTag;
			received.add(receivedValue.getFloat());
		}
		for (INBT providedTag : providedNBT) {
			FloatNBT providedValue = (FloatNBT) providedTag;
			provided.add(providedValue.getFloat());
		}
	}
}