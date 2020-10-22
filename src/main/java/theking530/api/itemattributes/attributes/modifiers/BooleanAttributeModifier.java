package theking530.api.itemattributes.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public class BooleanAttributeModifier extends AbstractAttributeModifier<Boolean> {
	public BooleanAttributeModifier() {

	}

	public BooleanAttributeModifier(boolean value) {
		super(value);
	}

	@Override
	protected void read(JsonObject json) {
		value = json.get("value").getAsBoolean();
	}

	@Override
	protected void read(CompoundNBT nbt) {
		value = nbt.getBoolean("value");
	}

	@Override
	protected void write(CompoundNBT nbt) {
		nbt.putBoolean("value", value);
	}

	@Override
	public String getType() {
		return "boolean";
	}
}
