package theking530.api.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;

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

	public void deserialize(CompoundTag nbt) {
		read(nbt);
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("type", getType());
		write(output);
		return output;
	}

	protected abstract void read(JsonObject json);

	protected abstract void read(CompoundTag nbt);

	protected abstract void write(CompoundTag nbt);
}
