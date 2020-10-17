package theking530.api.smithingattributes.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public abstract class AbstractAttributeModifier<T> {
	protected String name;
	protected String type;
	protected T value;

	public AbstractAttributeModifier(String name, String type) {
		this.name = name;
		this.type = type;
		this.value = null;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void deserialize(JsonObject json) {
		name = json.get("name").getAsString();
		type = json.get("type").getAsString();
		read(json);
	}

	public void deserialize(CompoundNBT nbt) {
		name = nbt.getString("name");
		type = nbt.getString("type");
		read(nbt);
	}

	public CompoundNBT serialize() {
		CompoundNBT output = new CompoundNBT();
		output.putString("name", name);
		output.putString("type", type);
		write(output);
		return output;
	}

	protected abstract void read(JsonObject json);

	protected abstract void read(CompoundNBT nbt);

	protected abstract void write(CompoundNBT nbt);
}
