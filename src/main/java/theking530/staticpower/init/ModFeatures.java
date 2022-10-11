package theking530.staticpower.init;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.world.OreRegistrationBuilder;
import theking530.staticcore.world.OreRegistrationBuilder.OreRegistrationResult;
import theking530.staticpower.StaticPower;
import theking530.staticpower.world.features.StaticPowerLakeFeature;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;
import theking530.staticpower.world.fluid.OilLakeRegistration;
import theking530.staticpower.world.trees.rubbertree.RubberTreeRegistration;

public class ModFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, StaticPower.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, StaticPower.MOD_ID);
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, StaticPower.MOD_ID);

	public static final RegistryObject<StaticPowerLakeFeature> STATIC_LAKE = FEATURES.register("static_lake",
			() -> new StaticPowerLakeFeature(StaticPowerLakeFeatureConfiguration.CODEC));

	public static final OreRegistrationResult ZINC = OreRegistrationBuilder.createOre("ore_zinc").blockSource(ModBlocks.OreZinc).levelRange(30, 100).veinSize(8).veinsPerChunk(14)
			.register();
	public static final OreRegistrationResult MAGENSIUM = OreRegistrationBuilder.createOre("ore_magnesium").blockSource(ModBlocks.OreMagnesium).levelRange(30, 100).veinSize(8)
			.veinsPerChunk(14).register();
	public static final OreRegistrationResult TIN = OreRegistrationBuilder.createOre("ore_tin").blockSource(ModBlocks.OreTin).levelRange(40, 128).veinSize(10).veinsPerChunk(20)
			.register();
	public static final OreRegistrationResult LEAD = OreRegistrationBuilder.createOre("ore_lead").blockSource(ModBlocks.OreLead).levelRange(0, 35).veinSize(4).veinsPerChunk(12)
			.register();
	public static final OreRegistrationResult SILVER = OreRegistrationBuilder.createOre("ore_silver").blockSource(ModBlocks.OreSilver).levelRange(0, 40).veinSize(4)
			.veinsPerChunk(12).register();
	public static final OreRegistrationResult TUNGSTEN = OreRegistrationBuilder.createOre("ore_tungsten").blockSource(ModBlocks.OreTungsten).levelRange(0, 20).veinSize(6)
			.veinsPerChunk(8).register();
	public static final OreRegistrationResult PLATINUM = OreRegistrationBuilder.createOre("ore_platinum").blockSource(ModBlocks.OrePlatinum).levelRange(0, 30).veinSize(6)
			.veinsPerChunk(8).register();
	public static final OreRegistrationResult ALUMINUM = OreRegistrationBuilder.createOre("ore_aluminum").blockSource(ModBlocks.OreAluminum).levelRange(50, 200).veinSize(8)
			.veinsPerChunk(18).register();
	public static final OreRegistrationResult SAPPHIRE = OreRegistrationBuilder.createOre("ore_sapphire").blockSource(ModBlocks.OreSapphire).levelRange(0, 20).veinSize(4)
			.veinsPerChunk(8).register();
	public static final OreRegistrationResult RUBY = OreRegistrationBuilder.createOre("ore_ruby").blockSource(ModBlocks.OreRuby).levelRange(0, 20).veinSize(4).veinsPerChunk(8)
			.register();
	public static final OreRegistrationResult RUSTY_IRON_ORE = OreRegistrationBuilder.createOre("ore_rusty_iron").blockSource(ModBlocks.OreRustyIron).levelRange(50, 150)
			.veinSize(4).veinsPerChunk(24).register();

	public static final OreRegistrationResult DEEPSLATE_ZINC = OreRegistrationBuilder.createOre("ore_deepslate_zinc")
			.blockSource(ModBlocks.OreDeepslateZinc, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-32, 10).veinSize(4).veinsPerChunk(8).register();
	public static final OreRegistrationResult DEEPSLATE_MAGENSIUM = OreRegistrationBuilder.createOre("ore_deepslate_magnesium")
			.blockSource(ModBlocks.OreDeepslateMagnesium, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-32, 10).veinSize(4).veinsPerChunk(8).register();
	public static final OreRegistrationResult DEEPSLATE_TIN = OreRegistrationBuilder.createOre("ore_deepslate_tin")
			.blockSource(ModBlocks.OreDeepslateTin, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-16, 30).veinSize(10).veinsPerChunk(18).register();
	public static final OreRegistrationResult DEEPSLATE_LEAD = OreRegistrationBuilder.createOre("ore_deepslate_lead")
			.blockSource(ModBlocks.OreDeepslateLead, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4).veinsPerChunk(10).register();
	public static final OreRegistrationResult DEEPSLATE_SILVER = OreRegistrationBuilder.createOre("ore_deepslate_silver")
			.blockSource(ModBlocks.OreDeepslateSilver, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4).veinsPerChunk(12).register();
	public static final OreRegistrationResult DEEPSLATE_TUNGSTEN = OreRegistrationBuilder.createOre("ore_deepslate_tungsten")
			.blockSource(ModBlocks.OreDeepslateTungsten, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4).veinsPerChunk(8).register();
	public static final OreRegistrationResult DEEPSLATE_PLATINUM = OreRegistrationBuilder.createOre("ore_deepslate_platinum")
			.blockSource(ModBlocks.OreDeepslatePlatinum, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4).veinsPerChunk(10).register();
	public static final OreRegistrationResult DEEPSLATE_ALUMINUM = OreRegistrationBuilder.createOre("ore_deepslate_aluminum")
			.blockSource(ModBlocks.OreDeepslateAluminum, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-12, 30).veinSize(4).veinsPerChunk(12).register();
	public static final OreRegistrationResult DEEPSLATE_SAPPHIRE = OreRegistrationBuilder.createOre("ore_deepslate_sapphire")
			.blockSource(ModBlocks.OreDeepslateSapphire, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4).veinsPerChunk(5).register();
	public static final OreRegistrationResult DEEPSLATE_RUBY = OreRegistrationBuilder.createOre("ore_deepslate_ruby")
			.blockSource(ModBlocks.OreDeepslateRuby, OreFeatures.DEEPSLATE_ORE_REPLACEABLES).levelRange(-64, 0).veinSize(4).veinsPerChunk(5).register();

	public static final OreRegistrationResult NETHER_SILVER = OreRegistrationBuilder.createOre("ore_nether_silver")
			.blockSource(ModBlocks.OreNetherSilver, OreFeatures.NETHER_ORE_REPLACEABLES).levelRange(0, 100).veinSize(5).veinsPerChunk(12).register();
	public static final OreRegistrationResult NETHER_PLATINUM = OreRegistrationBuilder.createOre("ore_nether_platinum")
			.blockSource(ModBlocks.OreNetherPlatinum, OreFeatures.NETHER_ORE_REPLACEABLES).levelRange(0, 100).veinSize(5).veinsPerChunk(12).register();
	public static final OreRegistrationResult NETHER_TUNGSTEN = OreRegistrationBuilder.createOre("ore_nether_tungsten")
			.blockSource(ModBlocks.OreNetherTungsten, OreFeatures.NETHER_ORE_REPLACEABLES).levelRange(0, 100).veinSize(5).veinsPerChunk(12).register();

	public static final RubberTreeRegistration RUBBER_TREE = new RubberTreeRegistration().register();

	public static final OilLakeRegistration OIL_DEPOSIT = new OilLakeRegistration().register();

	public static void init(IEventBus eventBus) {
		FEATURES.register(eventBus);
		CONFIGURED_FEATURES.register(eventBus);
		PLACED_FEATURES.register(eventBus);
	}
}
