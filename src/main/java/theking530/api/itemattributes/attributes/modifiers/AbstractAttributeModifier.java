package theking530.api.itemattributes.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public abstract class AbstractAttributeModifier<T> {
	protected T value;

	public AbstractAttributeModifier() {
		this(null);
	}

	public AbstractAttributeModifier(T modifierValue) {
		this.value = modifierValue;
	}

	public abstract String getType();

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void deserialize(JsonObject json) {
		read(json);
	}

	public void deserialize(CompoundNBT nbt) {
		read(nbt);
	}

	public CompoundNBT serialize() {
		CompoundNBT output = new CompoundNBT();
		output.putString("type", getType());
		write(output);
		return output;
	}

	protected abstract void read(JsonObject json);

	protected abstract void read(CompoundNBT nbt);

	protected abstract void write(CompoundNBT nbt);
}
