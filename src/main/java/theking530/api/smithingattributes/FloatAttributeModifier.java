package theking530.api.smithingattributes;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public class FloatAttributeModifier extends AbstractAttributeModifier {
	public float amount;
	public boolean isAdditive;

	public FloatAttributeModifier(String name, String type) {
		super(name, type);
	}

	@Override
	protected void read(JsonObject json) {
		amount = json.get("amount").getAsFloat();
		isAdditive = json.get("isAdditive").getAsBoolean();
	}

	@Override
	protected void read(CompoundNBT nbt) {
		amount = nbt.getFloat("amount");
		isAdditive = nbt.getBoolean("isAdditive");
	}

	@Override
	protected void write(CompoundNBT nbt) {
		nbt.putFloat("amount", amount);
		nbt.putBoolean("isAdditive", isAdditive);
	}
}
