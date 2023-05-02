package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.world.OreRegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.world.features.StaticPowerLakeFeature;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;
import theking530.staticpower.world.fluid.OilLakeRegistration;
import theking530.staticpower.world.trees.rubbertree.RubberTreeRegistration;

public class ModFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY,
			StaticPower.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister
			.create(Registry.PLACED_FEATURE_REGISTRY, StaticPower.MOD_ID);
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister
			.create(Registry.CONFIGURED_FEATURE_REGISTRY, StaticPower.MOD_ID);
	private static final Map<String, OreRegistryObject> REGISTERED_ORES = new HashMap<>();

	public static final RegistryObject<StaticPowerLakeFeature> STATIC_LAKE = FEATURES.register("static_lake",
			() -> new StaticPowerLakeFeature(StaticPowerLakeFeatureConfiguration.CODEC));

	public static final OreRegistryObject ZINC = createOre("ore_zinc")
			.blockSource(ModMaterials.ZINC.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(30, 100)
			.veinSize(8).veinsPerChunk(14);
	public static final OreRegistryObject MAGENSIUM = createOre("ore_magnesium")
			.blockSource(ModMaterials.MAGNESIUM.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(30, 100)
			.veinSize(8).veinsPerChunk(14);
	public static final OreRegistryObject TIN = createOre("ore_tin")
			.blockSource(ModMaterials.TIN.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(40, 128)
			.veinSize(10).veinsPerChunk(20);
	public static final OreRegistryObject LEAD = createOre("ore_lead")
			.blockSource(ModMaterials.LEAD.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(0, 35).veinSize(4)
			.veinsPerChunk(12);
	public static final OreRegistryObject SILVER = createOre("ore_silver")
			.blockSource(ModMaterials.SILVER.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(0, 40)
			.veinSize(4).veinsPerChunk(12);
	public static final OreRegistryObject TUNGSTEN = createOre("ore_tungsten")
			.blockSource(ModMaterials.TUNGSTEN.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(0, 20)
			.veinSize(6).veinsPerChunk(8);
	public static final OreRegistryObject PLATINUM = createOre("ore_platinum")
			.blockSource(ModMaterials.PLATINUM.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(0, 30)
			.veinSize(6).veinsPerChunk(8);
	public static final OreRegistryObject ALUMINUM = createOre("ore_aluminum")
			.blockSource(ModMaterials.ALUMINUM.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(50, 200)
			.veinSize(8).veinsPerChunk(18);
	public static final OreRegistryObject SAPPHIRE = createOre("ore_sapphire")
			.blockSource(ModMaterials.SAPPHIRE.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(0, 20)
			.veinSize(4).veinsPerChunk(8);
	public static final OreRegistryObject RUBY = createOre("ore_ruby")
			.blockSource(ModMaterials.RUBY.get(MaterialTypes.OVERWORLD_ORE).getSupplier()).levelRange(0, 20).veinSize(4)
			.veinsPerChunk(8);
	public static final OreRegistryObject RUSTY_IRON_ORE = createOre("ore_rusty_iron")
			.blockSource(ModBlocks.OreRustyIron).levelRange(50, 150).veinSize(4).veinsPerChunk(24);

	public static final OreRegistryObject DEEPSLATE_ZINC = createOre("ore_deepslate_zinc")
			.blockSource(ModMaterials.ZINC.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-32, 10).veinSize(4).veinsPerChunk(8);
	public static final OreRegistryObject DEEPSLATE_MAGENSIUM = createOre("ore_deepslate_magnesium")
			.blockSource(ModMaterials.MAGNESIUM.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-32, 10).veinSize(4).veinsPerChunk(8);
	public static final OreRegistryObject DEEPSLATE_TIN = createOre("ore_deepslate_tin")
			.blockSource(ModMaterials.TIN.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-16, 30).veinSize(10).veinsPerChunk(18);
	public static final OreRegistryObject DEEPSLATE_LEAD = createOre("ore_deepslate_lead")
			.blockSource(ModMaterials.LEAD.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-64, 0).veinSize(4).veinsPerChunk(10);
	public static final OreRegistryObject DEEPSLATE_SILVER = createOre("ore_deepslate_silver")
			.blockSource(ModMaterials.SILVER.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-64, 0).veinSize(4).veinsPerChunk(12);
	public static final OreRegistryObject DEEPSLATE_TUNGSTEN = createOre("ore_deepslate_tungsten")
			.blockSource(ModMaterials.TUNGSTEN.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-64, 0).veinSize(4).veinsPerChunk(8);
	public static final OreRegistryObject DEEPSLATE_PLATINUM = createOre("ore_deepslate_platinum")
			.blockSource(ModMaterials.PLATINUM.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-64, 0).veinSize(4).veinsPerChunk(10);
	public static final OreRegistryObject DEEPSLATE_ALUMINUM = createOre("ore_deepslate_aluminum")
			.blockSource(ModMaterials.ALUMINUM.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-12, 30).veinSize(4).veinsPerChunk(12);
	public static final OreRegistryObject DEEPSLATE_SAPPHIRE = createOre("ore_deepslate_sapphire")
			.blockSource(ModMaterials.SAPPHIRE.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-64, 0).veinSize(4).veinsPerChunk(5);
	public static final OreRegistryObject DEEPSLATE_RUBY = createOre("ore_deepslate_ruby")
			.blockSource(ModMaterials.RUBY.get(MaterialTypes.DEEPSLATE_ORE).getSupplier(),
					OreFeatures.DEEPSLATE_ORE_REPLACEABLES)
			.levelRange(-64, 0).veinSize(4).veinsPerChunk(5);

	public static final OreRegistryObject NETHER_SILVER = createOre("ore_nether_silver")
			.blockSource(ModMaterials.SILVER.get(MaterialTypes.NETHER_ORE).getSupplier(),
					OreFeatures.NETHER_ORE_REPLACEABLES)
			.levelRange(0, 100).veinSize(5).veinsPerChunk(12);
	public static final OreRegistryObject NETHER_PLATINUM = createOre("ore_nether_platinum")
			.blockSource(ModMaterials.PLATINUM.get(MaterialTypes.NETHER_ORE).getSupplier(),
					OreFeatures.NETHER_ORE_REPLACEABLES)
			.levelRange(0, 100).veinSize(5).veinsPerChunk(12);
	public static final OreRegistryObject NETHER_TUNGSTEN = createOre("ore_nether_tungsten")
			.blockSource(ModMaterials.TUNGSTEN.get(MaterialTypes.NETHER_ORE).getSupplier(),
					OreFeatures.NETHER_ORE_REPLACEABLES)
			.levelRange(0, 100).veinSize(5).veinsPerChunk(12);

	public static final RubberTreeRegistration RUBBER_TREE = new RubberTreeRegistration().register();

	public static final OilLakeRegistration OIL_DEPOSIT = new OilLakeRegistration().register();

	private static OreRegistryObject createOre(String name) {
		OreRegistryObject output = OreRegistryObject.createOre(name);
		REGISTERED_ORES.put(name, output);
		return output;
	}

	public static void init(IEventBus eventBus) {
		for (OreRegistryObject ore : REGISTERED_ORES.values()) {
			ore.register(CONFIGURED_FEATURES, PLACED_FEATURES);
		}
		FEATURES.register(eventBus);
		CONFIGURED_FEATURES.register(eventBus);
		PLACED_FEATURES.register(eventBus);
	}
}
