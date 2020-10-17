package theking530.api.smithingattributes.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public class IntegerAttributeModifier extends AbstractAttributeModifier<Integer> {
	public boolean isAdditive;

	public IntegerAttributeModifier(String name, String type) {
		super(name, type);
	}

	@Override
	protected void read(JsonObject json) {
		value = json.get("amount").getAsInt();
		isAdditive = json.get("isAdditive").getAsBoolean();
	}

	@Override
	protected void read(CompoundNBT nbt) {
		value = nbt.getInt("amount");
		isAdditive = nbt.getBoolean("isAdditive");
	}

	@Override
	protected void write(CompoundNBT nbt) {
		nbt.putInt("amount", value);
		nbt.putBoolean("isAdditive", isAdditive);
	}
}
