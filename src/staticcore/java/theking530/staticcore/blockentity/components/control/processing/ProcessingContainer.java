package theking530.staticcore.blockentity.components.control.processing;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class ProcessingContainer implements INBTSerializable<CompoundTag> {
	public enum CaptureType {
		NONE, RATE_ONLY, COUNT_ONLY, BOTH
	}

	private final ConcretizedProductContainer inputs;
	private final ConcretizedProductContainer outputs;
	private CompoundTag customParameters;

	public ProcessingContainer() {
		inputs = new ConcretizedProductContainer();
		outputs = new ConcretizedProductContainer();
		customParameters = new CompoundTag();
	}

	public ConcretizedProductContainer getInputs() {
		return inputs;
	}

	public ConcretizedProductContainer getOutputs() {
		return outputs;
	}

	public void absorbIntoInputs(ConcretizedProductContainer other) {
		inputs.mergeOther(other);
	}

	public void absorbIntoOutputs(ConcretizedProductContainer other) {
		outputs.mergeOther(other);
	}

	public CompoundTag getCustomParameterContainer() {
		return customParameters;
	}

	public void clear() {
		inputs.clear();
		outputs.clear();
		customParameters = new CompoundTag();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("custom_data", customParameters);
		output.put("inputs", inputs.serializeNBT());
		output.put("outputs", outputs.serializeNBT());

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		customParameters = nbt.getCompound("custom_data");
		inputs.deserializeNBT(nbt.getCompound("inputs"));
		outputs.deserializeNBT(nbt.getCompound("outputs"));
	}
}
