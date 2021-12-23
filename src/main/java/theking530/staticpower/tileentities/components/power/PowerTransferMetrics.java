package theking530.staticpower.tileentities.components.power;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class PowerTransferMetrics implements INBTSerializable<CompoundTag> {
	public enum MetricCategory {
		TICKS, SECONDS, MINUTES, HOURS
	}

	public static final int MAX_METRIC_SAMPLES = 60;

	private final HashMap<MetricCategory, PowerTransferMetricWrapper> data;
	private byte currentTick;
	private byte currentSecond;
	private byte currentMinute;

	public PowerTransferMetrics() {
		this.data = new HashMap<MetricCategory, PowerTransferMetricWrapper>();

		// Pre-fill all the values.
		for (MetricCategory cat : MetricCategory.values()) {
			data.put(cat, new PowerTransferMetricWrapper(cat));
		}
	}

	public PowerTransferMetricWrapper getData(MetricCategory category) {
		return data.get(category);
	}

	public void addMetric(float input, float output) {
		data.get(MetricCategory.TICKS).addInputValue(input);
		data.get(MetricCategory.TICKS).addOutputValue(output);
		currentTick++;

		// Propagate the values forward to all additional time categories.
		if (currentTick >= 20) {
			currentTick = 0;
			currentSecond++;
			data.get(MetricCategory.SECONDS).addInputValue(input);
			data.get(MetricCategory.SECONDS).addOutputValue(output);
		}
		if (currentSecond >= 60) {
			currentSecond = 0;
			currentMinute++;
			data.get(MetricCategory.MINUTES).addInputValue(input);
			data.get(MetricCategory.MINUTES).addOutputValue(output);
		}
		if (currentMinute >= 60) {
			currentMinute = 0;
			data.get(MetricCategory.HOURS).addInputValue(input);
			data.get(MetricCategory.HOURS).addOutputValue(output);
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putByte("cTick", currentTick);
		output.putByte("cSec", currentSecond);
		output.putByte("cMin", currentMinute);

		// Serialize the data.
		for (MetricCategory cat : MetricCategory.values()) {
			output.put(cat.toString(), data.get(cat).serializeNBT());
		}

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		currentTick = nbt.getByte("cTick");
		currentSecond = nbt.getByte("cSec");
		currentMinute = nbt.getByte("cMin");

		// Deserialize the data.
		for (MetricCategory cat : MetricCategory.values()) {
			data.get(cat).deserializeNBT(nbt.getCompound(cat.toString()));
		}
	}

	public class PowerTransferMetricWrapper implements INBTSerializable<CompoundTag> {
		private MetricCategory category;
		private Deque<Float> inputValues;
		private Deque<Float> outputValues;

		public PowerTransferMetricWrapper() {
			inputValues = new LinkedList<Float>();
			outputValues = new LinkedList<Float>();

			// Pre-fill all the values.
			for (int i = 0; i < MAX_METRIC_SAMPLES; i++) {
				inputValues.add(0.0f);
				outputValues.add(0.0f);
			}
		}

		public PowerTransferMetricWrapper(MetricCategory category) {
			this();
			this.category = category;
		}

		public Deque<Float> getInputValues() {
			return inputValues;
		}

		public Deque<Float> getOutputValues() {
			return outputValues;
		}

		public void addInputValue(float value) {
			inputValues.poll();
			inputValues.add(value);
		}

		public void addOutputValue(float value) {
			outputValues.poll();
			outputValues.add(value);
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag output = new CompoundTag();

			// Serailize the category.
			output.putByte("cat", (byte) category.ordinal());

			// Serialize the input value list.
			ListTag inputValueList = new ListTag();
			inputValues.forEach(value -> {
				FloatTag valueTag = FloatTag.valueOf(value);
				inputValueList.add(valueTag);
			});
			output.put("input_values", inputValueList);

			// Serialize the output value list.
			ListTag outputValueList = new ListTag();
			outputValues.forEach(value -> {
				FloatTag valueTag = FloatTag.valueOf(value);
				outputValueList.add(valueTag);
			});
			output.put("output_values", outputValueList);

			return output;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			category = MetricCategory.values()[nbt.getByte("cat")];

			inputValues.clear();
			outputValues.clear();

			// Read the serialized lists.
			ListTag inputNBT = nbt.getList("input_values", Constants.NBT.TAG_FLOAT);
			ListTag outputNBT = nbt.getList("output_values", Constants.NBT.TAG_FLOAT);

			// Populate the arrays.
			for (Tag valueTag : inputNBT) {
				FloatTag value = (FloatTag) valueTag;
				inputValues.add(value.getAsFloat());
			}

			for (Tag valueTag : outputNBT) {
				FloatTag value = (FloatTag) valueTag;
				outputValues.add(value.getAsFloat());
			}
		}
	}

}
