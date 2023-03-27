package theking530.staticcore.data.tier.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class TierItemCableConfiguration {
	public final ConfigValue<Double> itemCableAcceleration;
	public final ConfigValue<Double> itemCableFriction;
	public final ConfigValue<Double> itemCableMaxSpeed;

	public TierItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		builder.push("Item");
		itemCableAcceleration = builder.comment("The rate (as a percentage 1.0+) at which items in this tube will accelerate up to the max speed.")
				.translation(modId + ".config." + "itemCableAcceleration").define("ItemCableAcceleration", this.getItemCableAcceleration());

		itemCableFriction = builder.comment("The rate (as a percentage 1.0+) at which items in this tube will decelerate down to the max speed.")
				.translation(modId + ".config." + "itemCableFriction").define("ItemCableFriction", this.getItemCableFriction());

		itemCableMaxSpeed = builder.comment(
				"The max speed (in blocks traveled per second) of an item tube of this tier. Faster items will slow down to match this rate, and slower items will speed up to match this rate. This value cannot be higher than 19, otherwise items will cease to transfer.")
				.translation(modId + ".config." + "itemCableMaxSpeed").define("ItemCableMaxSpeed", this.getItemCableMaxSpeed());
		builder.pop();
	}

	protected abstract double getItemCableAcceleration();

	protected abstract double getItemCableMaxSpeed();

	protected abstract double getItemCableFriction();
}
