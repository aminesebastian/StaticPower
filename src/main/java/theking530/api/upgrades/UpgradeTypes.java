package theking530.api.upgrades;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;

public class UpgradeTypes {
	public static final UpgradeType SPEED = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "speed"));
	public static final UpgradeType POWER = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "power"));
	public static final UpgradeType TANK = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "tank"));
	public static final UpgradeType RANGE = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "range"));
	public static final UpgradeType OUTPUT_MULTIPLIER = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "output_multiplier"));
	public static final UpgradeType CENTRIFUGE = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "centrifuge"));
	public static final UpgradeType SPECIAL = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "special"));
	public static final UpgradeType DIGISTORE_ATTACHMENT = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "digistore_attachment"));
	public static final UpgradeType HEAT_CAPACITY = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "heat_capacity"));
	public static final UpgradeType HEAT = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "heat"));
	public static final UpgradeType POWER_TRANSFORMER = new UpgradeType(new ResourceLocation(StaticPower.MOD_ID, "power_transformer"));
}
