package theking530.api.smithingattributes.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public class FloatAttributeModifier extends AbstractAttributeModifier<Float> {
	public boolean isAdditive;

	public FloatAttributeModifier(String name, String type) {
		super(name, type);
	}

	@Override
	protected void read(JsonObject json) {
		value = json.get("amount").getAsFloat();
		isAdditive = json.get("isAdditive").getAsBoolean();
	}

	@Override
	protected void read(CompoundNBT nbt) {
		value = nbt.getFloat("amount");
		isAdditive = nbt.getBoolean("isAdditive");
	}

	@Override
	protected void write(CompoundNBT nbt) {
		nbt.putFloat("amount", value);
		nbt.putBoolean("isAdditive", isAdditive);
	}
}
