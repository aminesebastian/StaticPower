package theking530.api.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import theking530.api.attributes.registration.AttributeModifierRegistration;

@AttributeModifierRegistration("float")
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
	protected void read(CompoundTag nbt) {
		value = nbt.getFloat("amount");
		isAdditive = nbt.getBoolean("isAdditive");
	}

	@Override
	protected void write(CompoundTag nbt) {
		nbt.putFloat("amount", value);
		nbt.putBoolean("isAdditive", isAdditive);
	}

	@Override
	public String getType() {
		return "float";
	}
}
