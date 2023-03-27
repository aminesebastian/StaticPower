package theking530.staticcore.data.tier.categories;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class TierToolConfiguration {
	/********
	 * Tools
	 ********/
	public final ConfigValue<Integer> drillBitUses;
	public final ConfigValue<Float> drillSpeedMultiplier;

	public final ConfigValue<Integer> chainsawBladeUses;
	public final ConfigValue<Float> chainsawSpeedMultiplier;

	public final ConfigValue<Double> hardenedDurabilityBoost;
	public final ConfigValue<Boolean> hardenedDurabilityBoostAdditive;

	public final ConfigValue<Integer> hammerUses;
	public final ConfigValue<Double> hammerSwingSpeed;
	public final ConfigValue<Double> hammerDamage;
	public final ConfigValue<Integer> hammerCooldown;

	public final ConfigValue<Integer> wireCutterUses;
	public final ConfigValue<Integer> magnetRadius;

	public TierToolConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		drillBitUses = builder.comment("The number of blocks that can be mined by a drill bit of this tier.").translation(modId + ".config." + "drillBitUses")
				.define("DrillBitUses", getDrillBitUses());
		drillSpeedMultiplier = builder.comment("The mining speed multiplier of drills of this tier.").translation(modId + ".config." + "drillSpeedMultiplier")
				.define("DrillSpeedMultiplier", getDrillSpeedMultiplier());

		chainsawBladeUses = builder.comment("The number of blocks that can be mined by a chainsaw blade of this tier.")
				.translation(modId + ".config." + "chainsawBladeUses").define("ChainsawBladeUses", this.getChainsawBladeUses());
		chainsawSpeedMultiplier = builder.comment("The mining speed multiplier of chainsaws of this tier.").translation(modId + ".config." + "chainsawSpeedMultiplier")
				.define("ChainsawSpeedMultiplier", getChainsawSpeedMultiplier());

		hardenedDurabilityBoost = builder.comment("The amount of durability gained when the diamond hardened modifier is added.")
				.translation(modId + ".config." + "diamondHardenedDurabilityBoost").define("DiamondHardenedDurabilityBoost", this.getHardenedDurabilityBoost());
		hardenedDurabilityBoostAdditive = builder.comment("Defines whether the hardened durability boost is additive or multaplicative.")
				.translation(modId + ".config." + "hardenedDurabilityBoostAdditive")
				.define("HardenedDurabilityBoostAdditive", this.isHardenedDurabilityBoostAdditive());

		hammerUses = builder.comment("The number of blocks/items that can be processed by a hammer of this tier.").translation(modId + ".config." + "hammerUses")
				.define("HammerUses", this.getHammerUses());
		hammerSwingSpeed = builder.comment("How fast a hammer of this tier swings.").translation(modId + ".config." + "hammerSwingSpeed").define("HammerSwingSpeed",
				this.getHammerSwingSpeed());
		hammerDamage = builder.comment("How much damage a hammer of this tier does.").translation(modId + ".config." + "hammerDamage").define("HammerDamage",
				this.getHammerDamage());
		hammerCooldown = builder.comment("How long the cooldown is (in ticks) between each anvil based crafting operation performed by a hammer of this tier..")
				.translation(modId + ".config." + "hammerCooldown").define("HammerCooldown", this.getHammerCooldown());

		wireCutterUses = builder.comment("The number of items that can be processed by a wire cutter of this tier.").translation(modId + ".config." + "wireCutterUses")
				.define("WireCutterUses", this.getWireCutterUses());

		magnetRadius = builder.comment("The number of blocks away from which items will be pulled towards the wielder.")
				.translation(modId + ".config." + "magnetRadius").define("MagnetRadius", this.getMagnetRadius());
	}

	protected int getMagnetRadius() {
		return 0;
	}

	protected int getHammerUses() {
		return 0;
	}

	protected double getHammerSwingSpeed() {
		return 0;
	}

	protected double getHammerDamage() {
		return 0;
	}

	protected int getHammerCooldown() {
		return 0;
	}

	protected int getWireCutterUses() {
		return 0;
	}

	protected double getHardenedDurabilityBoost() {
		return 0;
	}

	protected boolean isHardenedDurabilityBoostAdditive() {
		return false;
	}

	protected int getDrillBitUses() {
		return 0;
	}

	protected float getDrillSpeedMultiplier() {
		return 0;
	}

	protected int getChainsawBladeUses() {
		return 0;
	}

	protected float getChainsawSpeedMultiplier() {
		return 0;
	}
}
