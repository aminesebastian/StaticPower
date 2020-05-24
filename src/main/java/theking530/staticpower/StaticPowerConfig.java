package theking530.staticpower;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import theking530.staticpower.utilities.Reference;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerConfig {

	public static final StaticPowerClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	public static boolean COPPER_ORE_GEN;
	public static boolean TIN_ORE_GEN;
	public static boolean LEAD_ORE_GEN;
	public static boolean SILVER_ORE_GEN;
	public static boolean PLATINUM_ORE_GEN;
	public static boolean NICKEL_ORE_GEN;
	public static boolean ALUMINIUM_ORE_GEN;
	public static boolean SAPPHIRE_ORE_GEN;
	public static boolean RUBY_ORE_GEN;

	static {
		final Pair<StaticPowerClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(StaticPowerClientConfig::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	
	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == StaticPowerConfig.CLIENT_SPEC) {
			bakeConfig();
		}
	}
	
	public static void bakeConfig() {
		COPPER_ORE_GEN = CLIENT.COPPER_ORE_GEN.get();
		TIN_ORE_GEN = CLIENT.TIN_ORE_GEN.get();
		LEAD_ORE_GEN = CLIENT.LEAD_ORE_GEN.get();
		SILVER_ORE_GEN = CLIENT.SILVER_ORE_GEN.get();
		PLATINUM_ORE_GEN = CLIENT.PLATINUM_ORE_GEN.get();
		NICKEL_ORE_GEN = CLIENT.NICKEL_ORE_GEN.get();
		ALUMINIUM_ORE_GEN = CLIENT.ALUMINIUM_ORE_GEN.get();
		SAPPHIRE_ORE_GEN = CLIENT.SAPPHIRE_ORE_GEN.get();
		RUBY_ORE_GEN = CLIENT.RUBY_ORE_GEN.get();
	}

	public static class StaticPowerClientConfig {
		public final BooleanValue COPPER_ORE_GEN;
		public final BooleanValue TIN_ORE_GEN;
		public final BooleanValue LEAD_ORE_GEN;
		public final BooleanValue SILVER_ORE_GEN;
		public final BooleanValue PLATINUM_ORE_GEN;
		public final BooleanValue NICKEL_ORE_GEN;
		public final BooleanValue ALUMINIUM_ORE_GEN;
		public final BooleanValue SAPPHIRE_ORE_GEN;
		public final BooleanValue RUBY_ORE_GEN;

		public StaticPowerClientConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Ore Generation");
			COPPER_ORE_GEN = builder.comment("Disable or Enable Copper Ore Generation").translation(Reference.MOD_ID + ".config." + "copperore").define("CopperOre", true);
			TIN_ORE_GEN = builder.comment("Disable or Enable Tin Ore Generation").translation(Reference.MOD_ID + ".config." + "tinore").define("TinOre", true);
			LEAD_ORE_GEN = builder.comment("Disable or Enable Lead Ore Generation").translation(Reference.MOD_ID + ".config." + "leadore").define("LeadOre", true);
			SILVER_ORE_GEN = builder.comment("Disable or Enable Silver Ore Generation").translation(Reference.MOD_ID + ".config." + "silverore").define("SilverOre", true);
			PLATINUM_ORE_GEN = builder.comment("Disable or Enable Platinum Ore Generation").translation(Reference.MOD_ID + ".config." + "platinumore").define("PlatinumOre", true);
			NICKEL_ORE_GEN = builder.comment("Disable or Enable Nickel Ore Generation").translation(Reference.MOD_ID + ".config." + "nickelore").define("NickelOre", true);
			ALUMINIUM_ORE_GEN = builder.comment("Disable or Enable Aluminium Ore Generation").translation(Reference.MOD_ID + ".config." + "aluminiumore").define("AluminiumOre", true);
			SAPPHIRE_ORE_GEN = builder.comment("Disable or Enable Sapphire Ore Generation").translation(Reference.MOD_ID + ".config." + "sapphireore").define("SapphireOre", true);
			RUBY_ORE_GEN = builder.comment("Disable or Enable Ruby Ore Generation").translation(Reference.MOD_ID + ".config." + "rubyore").define("RubyOre", true);
			builder.pop();
		}
	}
}
