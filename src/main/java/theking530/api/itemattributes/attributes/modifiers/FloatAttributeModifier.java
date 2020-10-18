package theking530.api.itemattributes.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public class FloatAttributeModifier extends AbstractAttributeModifier<Float> {
	public boolean isAdditive;

	public FloatAttributeModifier() {

	}

	public FloatAttributeModifier(float value, boolean isAdditive) {
		super(value);
		this.isAdditive = isAdditive;
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

	@Override
	public String getType() {
		return "float";
	}
}
