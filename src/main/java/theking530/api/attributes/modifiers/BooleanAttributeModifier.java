package theking530.api.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import theking530.api.attributes.registration.AttributeModifierRegistration;

@AttributeModifierRegistration("boolean")
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
	protected void read(CompoundTag nbt) {
		value = nbt.getBoolean("value");
	}

	@Override
	protected void write(CompoundTag nbt) {
		nbt.putBoolean("value", value);
	}

	@Override
	public String getType() {
		return "boolean";
	}
}
