package theking530.api.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import theking530.api.attributes.registration.AttributeModifierRegistration;

@AttributeModifierRegistration("integer")
public class IntegerAttributeModifier extends AbstractAttributeModifier<Integer> {
	public boolean isAdditive;

	public IntegerAttributeModifier() {

	}

	public IntegerAttributeModifier(int value, boolean isAdditive) {
		super(value);
		this.isAdditive = isAdditive;
	}

	@Override
	protected void read(JsonObject json) {
		value = json.get("amount").getAsInt();
		isAdditive = json.get("isAdditive").getAsBoolean();
	}

	@Override
	protected void read(CompoundTag nbt) {
		value = nbt.getInt("amount");
		isAdditive = nbt.getBoolean("isAdditive");
	}

	@Override
	protected void write(CompoundTag nbt) {
		nbt.putInt("amount", value);
		nbt.putBoolean("isAdditive", isAdditive);
	}

	@Override
	public String getType() {
		return "integer";
	}
}
