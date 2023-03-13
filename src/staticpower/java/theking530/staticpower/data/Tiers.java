package theking530.staticpower.data;

import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;

public class Tiers {

	public record TierPair(ResourceLocation location, String name) {
	}

	private static final TierPair[] CABLE_TIERS = new TierPair[] { new TierPair(StaticPowerTiers.BASIC, "basic"), new TierPair(StaticPowerTiers.ADVANCED, "advanced"),
			new TierPair(StaticPowerTiers.STATIC, "static"), new TierPair(StaticPowerTiers.ENERGIZED, "energized"), new TierPair(StaticPowerTiers.LUMUM, "lumum"),
			new TierPair(StaticPowerTiers.CREATIVE, "creative") };

	private static final TierPair[] HEAT_CABLE_TIERS = new TierPair[] { new TierPair(StaticPowerTiers.ALUMINUM, "aluminum"), new TierPair(StaticPowerTiers.COPPER, "copper"),
			new TierPair(StaticPowerTiers.GOLD, "gold") };

	public record RedstoneCableTier(ResourceLocation location, MinecraftColor color) {
	}

	private static final RedstoneCableTier[] REDSTONE_CABLE_TIERS = new RedstoneCableTier[16];
	static {
		int index = 0;
		for (MinecraftColor color : MinecraftColor.values()) {
			REDSTONE_CABLE_TIERS[index] = new RedstoneCableTier(new ResourceLocation(StaticPower.MOD_ID, color.getName()), color);
			index++;
		}
	}

	private static final TierPair[] CONVEYOR_TIERS = new TierPair[] { new TierPair(StaticPowerTiers.BASIC, "basic"), new TierPair(StaticPowerTiers.ADVANCED, "advanced"),
			new TierPair(StaticPowerTiers.STATIC, "static"), new TierPair(StaticPowerTiers.ENERGIZED, "energized"), new TierPair(StaticPowerTiers.LUMUM, "lumum") };

	private static final int[] CIRCUIT_BREAKER_TIERS = new int[] { 2, 5, 10, 20, 50, 100 };

	public record ResistorTier(int value, String firstStripe, String secondStripe, String thirdStripe) {
	}

	private static final ResistorTier[] RESISTOR_TIERS = new ResistorTier[] { new ResistorTier(1, "0", "1", "0"), new ResistorTier(5, "0", "5", "0"),
			new ResistorTier(10, "1", "0", "0"), new ResistorTier(25, "2", "5", "0"), new ResistorTier(50, "5", "0", "0"), new ResistorTier(100, "1", "0", "1"),
			new ResistorTier(250, "2", "5", "1"), new ResistorTier(500, "5", "0", "1"), new ResistorTier(1000, "1", "1", "3") };

	public static TierPair[] getConveyorTiers() {
		return CONVEYOR_TIERS;
	}

	public static TierPair[] getCableTiers() {
		return CABLE_TIERS;
	}

	public static TierPair[] getHeat() {
		return HEAT_CABLE_TIERS;
	}

	public static RedstoneCableTier[] getRedstone() {
		return REDSTONE_CABLE_TIERS;
	}

	public static int[] getCircuitBrakerTiers() {
		return CIRCUIT_BREAKER_TIERS;
	}

	public static ResistorTier[] getResistorTiers() {
		return RESISTOR_TIERS;
	}
}
