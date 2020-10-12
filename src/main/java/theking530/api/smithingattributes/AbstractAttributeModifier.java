package theking530.api.smithingattributes;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public abstract class AbstractAttributeModifier {
	public String name;
	public String type;

	public AbstractAttributeModifier(String name, String type) {
		this.name = name;
		this.type = type;
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

	public CompoundNBT write() {
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
