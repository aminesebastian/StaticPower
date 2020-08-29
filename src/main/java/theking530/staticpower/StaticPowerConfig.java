package theking530.staticpower;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

@EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerConfig {

	public static final StaticPowerClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	public static boolean generateCopperOre;
	public static boolean generateTinOre;
	public static boolean generateLeadOre;
	public static boolean generateSilverOre;
	public static boolean generatePlatinumOre;
	public static boolean generateTungstenOre;
	public static boolean generateZincOre;
	public static boolean generateMagnesiumOre;
	public static boolean generateAluminiumOre;
	public static boolean generateSapphireOre;
	public static boolean generateRubyOre;

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
		generateCopperOre = CLIENT.generateCopperOre.get();
		generateTinOre = CLIENT.generateTinOre.get();
		generateLeadOre = CLIENT.generateLeadOre.get();
		generateSilverOre = CLIENT.generateSilverOre.get();
		generatePlatinumOre = CLIENT.generatePlatinumOre.get();
		generateTungstenOre = CLIENT.generateTungstenOre.get();
		generateZincOre = CLIENT.generateZincOre.get();
		generateMagnesiumOre = CLIENT.generateMagnesiumOre.get();
		generateAluminiumOre = CLIENT.generateAluminiumOre.get();
		generateSapphireOre = CLIENT.generateSapphireOre.get();
		generateRubyOre = CLIENT.generateRubyOre.get();
	}

	public static class StaticPowerClientConfig {
		public BooleanValue generateCopperOre;
		public BooleanValue generateTinOre;
		public BooleanValue generateLeadOre;
		public BooleanValue generateSilverOre;
		public BooleanValue generatePlatinumOre;
		public BooleanValue generateTungstenOre;
		public BooleanValue generateZincOre;
		public BooleanValue generateMagnesiumOre;
		public BooleanValue generateAluminiumOre;
		public BooleanValue generateSapphireOre;
		public BooleanValue generateRubyOre;

		public StaticPowerClientConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Ore Generation");
			generateZincOre = builder.comment("Disable or Enable Zinc Ore Generation").translation(StaticPower.MOD_ID + ".config." + "zincore").define("GenerateZincOre", true);
			generateMagnesiumOre = builder.comment("Disable or Enable Magnesium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "magnesiumore").define("GenerateMagnesiumOre", true);
			generateAluminiumOre = builder.comment("Disable or Enable Aluminium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "aluminiumore").define("GenerateAluminiumOre", true);
			generateCopperOre = builder.comment("Disable or Enable Copper Ore Generation").translation(StaticPower.MOD_ID + ".config." + "copperore").define("GenerateCopperOre", true);
			generateTinOre = builder.comment("Disable or Enable Tin Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tinore").define("GenerateTinOre", true);
			generateLeadOre = builder.comment("Disable or Enable Lead Ore Generation").translation(StaticPower.MOD_ID + ".config." + "leadore").define("GenerateLeadOre", true);
			generateSilverOre = builder.comment("Disable or Enable Silver Ore Generation").translation(StaticPower.MOD_ID + ".config." + "silverore").define("GenerateSilverOre", true);
			generatePlatinumOre = builder.comment("Disable or Enable Platinum Ore Generation").translation(StaticPower.MOD_ID + ".config." + "platinumore").define("GeneratePlatinumOre", true);
			generateTungstenOre = builder.comment("Disable or Enable Tunsgten Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tungstenore").define("GenerateTungstenOre", true);
			generateSapphireOre = builder.comment("Disable or Enable Sapphire Ore Generation").translation(StaticPower.MOD_ID + ".config." + "sapphireore").define("GenerateSapphireOre", true);
			generateRubyOre = builder.comment("Disable or Enable Ruby Ore Generation").translation(StaticPower.MOD_ID + ".config." + "rubyore").define("GenerateRubyOre", true);
			builder.pop();
		}
	}
}
