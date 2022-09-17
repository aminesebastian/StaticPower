package theking530.staticpower;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.data.StaticPowerTier;

@EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerConfig {

	public static final StaticPowerCommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final StaticPowerServerConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	public static final Map<String, ConfigPair> TIERS;

	static {
		final Pair<StaticPowerServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(StaticPowerServerConfig::new);
		SERVER_SPEC = serverPair.getRight();
		SERVER = serverPair.getLeft();

		final Pair<StaticPowerCommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(StaticPowerCommonConfig::new);
		COMMON_SPEC = commonPair.getRight();
		COMMON = commonPair.getLeft();

		TIERS = new HashMap<>();
	}

	/**
	 * This config contains data that needs to be loaded before the world is
	 * created. This data is NOT synced to the client.
	 * 
	 */
	public static class StaticPowerCommonConfig {

		public StaticPowerCommonConfig(ForgeConfigSpec.Builder builder) {

		}
	}

	/**
	 * This config contains data that is loaded once the world is created and it is
	 * synced to the client.
	 * 
	 * @author amine
	 *
	 */
	public static class StaticPowerServerConfig {
		public final ConfigValue<Boolean> generateRubberTrees;
		public final ConfigValue<Integer> minRubberTreeCount;
		public final ConfigValue<Integer> maxRubberTreeCount;
		public final ConfigValue<Double> rubberTreeSpawnChance;
		public final ConfigValue<Boolean> disableRubberTreesInSnowyBiomes;

		public final BooleanValue generateTinOre;
		public final BooleanValue generateLeadOre;
		public final BooleanValue generateSilverOre;
		public final BooleanValue generatePlatinumOre;
		public final BooleanValue generateTungstenOre;
		public final BooleanValue generateZincOre;
		public final BooleanValue generateMagnesiumOre;
		public final BooleanValue generateAluminumOre;
		public final BooleanValue generateSapphireOre;
		public final BooleanValue generateRubyOre;
		public final BooleanValue generateRustyIronOre;

		public final BooleanValue generateDeepslateTinOre;
		public final BooleanValue generateDeepslateLeadOre;
		public final BooleanValue generateDeepslateSilverOre;
		public final BooleanValue generateDeepslatePlatinumOre;
		public final BooleanValue generateDeepslateTungstenOre;
		public final BooleanValue generateDeepslateZincOre;
		public final BooleanValue generateDeepslateMagnesiumOre;
		public final BooleanValue generateDeepslateAluminumOre;
		public final BooleanValue generateDeepslateSapphireOre;
		public final BooleanValue generateDeepslateRubyOre;

		public final BooleanValue generateNetherSilverOre;
		public final BooleanValue generateNetherPlatinumOre;
		public final BooleanValue generateNetherTungstenOre;

		public final ConfigValue<Integer> smeepSpawnWeight;
		public final ConfigValue<Integer> smeepMinCount;
		public final ConfigValue<Integer> smeepMaxCount;

		public final ConfigValue<Integer> minRubberWoodBarkPerStrip;
		public final ConfigValue<Integer> maxRubberWoodBarkPerStrip;

		public final ConfigValue<Integer> digistoreCardUniqueTypes;
		public final ConfigValue<Integer> digistoreStackedCardUniqueTypes;

		public final ConfigValue<Integer> digistoreRegulatorRate;
		public final ConfigValue<Integer> digistoreRegulatorStackSize;
		public final ConfigValue<Integer> digistoreRegulatorSlots;

		public final ConfigValue<Integer> digistoreIOBusRate;
		public final ConfigValue<Integer> digistoreIOBusSlots;
		public final ConfigValue<Integer> digistoreIOBusStackSize;

		public final ConfigValue<Integer> digistoreImporterRate;
		public final ConfigValue<Integer> digistoreImporterSlots;
		public final ConfigValue<Integer> digistoreImporterStackSize;

		public final ConfigValue<Integer> digistoreExporterRate;
		public final ConfigValue<Integer> digistoreExporterSlots;
		public final ConfigValue<Integer> digistoreExporterStackSize;

		public final ConfigValue<Integer> digistoreCraftingInterfaceSlots;

		public final ConfigValue<Double> acceleratorCardImprovment;

		public final ConfigValue<Integer> digistoreWirelessTerminalPowerCapacity;
		public final ConfigValue<Double> digistoreWirelessTerminalPowerUsage;

		public final ConfigValue<Integer> minerHeatGeneration;
		public final ConfigValue<Integer> minerFuelUsage;
		public final ConfigValue<Integer> minerRadius;
		public final ConfigValue<Integer> minerProcessingTime;

		public final ConfigValue<Integer> electricMinerHeatGeneration;
		public final ConfigValue<Integer> electricMinerRadius;
		public final ConfigValue<Integer> electricMinerProcessingTime;
		public final ConfigValue<Double> electricMinerPowerUsage;

		public final ConfigValue<Double> solidFuelGenerationPerTick;

		public final ConfigValue<Integer> centrifugeInitialMaxSpeed;
		public final ConfigValue<Double> centrifugePowerUsage;
		public final ConfigValue<Double> centrifugeMotorPowerUsage;
		public final ConfigValue<Integer> centrifugeProcessingTime;

		public final ConfigValue<Double> cruciblePowerUsage;
		public final ConfigValue<Double> crucibleHeatPowerUsage;
		public final ConfigValue<Integer> crucibleHeatGenerationPerTick;
		public final ConfigValue<Integer> crucibleProcessingTime;

		public final ConfigValue<Integer> basicFarmerFluidUsage;
		public final ConfigValue<Integer> basicFarmerDefaultRange;
		public final ConfigValue<Integer> basicFarmerToolUsage;
		public final ConfigValue<Double> basicFarmerPowerUsage;
		public final ConfigValue<Double> basicFarmerHarvestPowerUsage;
		public final ConfigValue<Integer> basicFarmerProcessingTime;

		public final ConfigValue<Integer> heatSinkTemperatureDamageThreshold;

		public final ConfigValue<Double> poweredGrinderOutputBonusChance;
		public final ConfigValue<Double> poweredGrinderPowerUsage;
		public final ConfigValue<Integer> poweredGrinderProcessingTime;

		public final ConfigValue<Double> poweredFurnacePowerUsage;

		public final ConfigValue<Double> tumblerOutputBonusChance;
		public final ConfigValue<Integer> tumblerRequiredSpeed;
		public final ConfigValue<Double> tumblerPowerUsage;
		public final ConfigValue<Double> tumblerMotorPowerUsage;
		public final ConfigValue<Integer> tumblerProcessingTime;

		public final ConfigValue<Integer> treeFarmerFluidUsage;
		public final ConfigValue<Integer> treeFarmerDefaultRange;
		public final ConfigValue<Integer> treeFarmerToolUsage;
		public final ConfigValue<Integer> treeFarmerMaxTreeRecursion;
		public final ConfigValue<Integer> treeFarmerSaplingSpacing;
		public final ConfigValue<Integer> treeFarmerProcessingTime;
		public final ConfigValue<Double> treeFarmerPowerUsage;
		public final ConfigValue<Double> treeFarmerHarvestPowerUsage;

		public final ConfigValue<Double> autoCrafterPowerUsage;
		public final ConfigValue<Integer> autoCrafterProcessingTime;

		public final ConfigValue<Double> autoSmithPowerUsage;
		public final ConfigValue<Integer> autoSmithProcessingTime;

		public final ConfigValue<Double> autoSolderingTablePowerUsage;
		public final ConfigValue<Integer> autoSolderingTableProcessingTime;

		public final ConfigValue<Double> bottlerPowerUsage;
		public final ConfigValue<Integer> bottlerProcessingTime;

		public final ConfigValue<Double> casterPowerUsage;
		public final ConfigValue<Integer> casterProcessingTime;

		public final ConfigValue<Double> fermenterPowerUsage;
		public final ConfigValue<Integer> fermenterProcessingTime;

		public final ConfigValue<Double> fluidInfuserPowerUsage;
		public final ConfigValue<Integer> fluidInfuserProcessingTime;

		public final ConfigValue<Double> formerPowerUsage;
		public final ConfigValue<Integer> formerProcessingTime;

		public final ConfigValue<Double> fusionFurnacePowerUsage;
		public final ConfigValue<Integer> fusionFurnaceProcessingTime;

		public final ConfigValue<Double> lathePowerUsage;
		public final ConfigValue<Integer> latheProcessingTime;

		public final ConfigValue<Double> lumberMillPowerUsage;
		public final ConfigValue<Integer> lumberMillProcessingTime;

		public final ConfigValue<Double> mixerPowerUsage;
		public final ConfigValue<Integer> mixerProcessingTime;

		public final ConfigValue<Double> packagerPowerUsage;
		public final ConfigValue<Integer> packagerProcessingTime;

		public final ConfigValue<Double> pumpPowerUsage;

		public final ConfigValue<Double> squeezerPowerUsage;
		public final ConfigValue<Integer> squeezerProcessingTime;

		public final ConfigValue<Double> vulcanizerPowerUsage;
		public final ConfigValue<Integer> vulcanizerProcessingTime;

		public final ConfigValue<Double> enchanterPowerUsage;
		public final ConfigValue<Integer> enchanterProcessingTime;

		public final ConfigValue<Double> refineryPowerUsage;
		public final ConfigValue<Integer> refineryProcessingTime;
		public final ConfigValue<Integer> refineryPerBoilerHeatGeneration;
		public final ConfigValue<Integer> refineryMinimumHeat;
		public final ConfigValue<Integer> refineryHeatUse;
		public final ConfigValue<Integer> refineryOverheatCooldownTime;

		public final ConfigValue<Double> laboratoryPowerUsage;

		public final ConfigValue<Double> lowVoltage;
		public final ConfigValue<Double> mediumVoltage;
		public final ConfigValue<Double> highVoltage;
		public final ConfigValue<Double> extremeVoltage;
		public final ConfigValue<Double> bonkersVoltage;

		public StaticPowerServerConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Generation");
			{
				builder.push("Ore Generation");
				{
					builder.push("Overworld");
					{
						generateZincOre = builder.comment("Disable or Enable Zinc Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateZincOre")
								.define("GenerateZincOre", true);
						generateMagnesiumOre = builder.comment("Disable or Enable Magnesium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateMagnesiumOre")
								.define("GenerateMagnesiumOre", true);
						generateAluminumOre = builder.comment("Disable or Enable Aluminum Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateAluminumOre")
								.define("GenerateAluminumOre", true);
						generateTinOre = builder.comment("Disable or Enable Tin Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tinore").define("generateTinOre",
								true);
						generateLeadOre = builder.comment("Disable or Enable Lead Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateLeadOre")
								.define("GenerateLeadOre", true);
						generateSilverOre = builder.comment("Disable or Enable Silver Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateSilverOre")
								.define("GenerateSilverOre", true);
						generatePlatinumOre = builder.comment("Disable or Enable Platinum Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generatePlatinumOre")
								.define("GeneratePlatinumOre", true);
						generateTungstenOre = builder.comment("Disable or Enable Tunsgten Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateTungstenOre")
								.define("GenerateTungstenOre", true);
						generateSapphireOre = builder.comment("Disable or Enable Sapphire Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateSapphireOre")
								.define("GenerateSapphireOre", true);
						generateRubyOre = builder.comment("Disable or Enable Ruby Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateRubyOre")
								.define("GenerateRubyOre", true);
						generateRustyIronOre = builder.comment("Disable or Enable Rusty Iron Ore Generation").translation(StaticPower.MOD_ID + ".config." + "generateRustyIronOre")
								.define("GenerateRustyIronOre", true);
					}
					builder.pop();
					builder.push("Deep");
					{
						generateDeepslateZincOre = builder.comment("Disable or Enable Deepslate Zinc Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateZincOre").define("GenerateDeepslateZincOre", true);
						generateDeepslateMagnesiumOre = builder.comment("Disable or Enable Deepslate Magnesium Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateMagnesiumOre").define("GenerateDeepslateMagnesiumOre", true);
						generateDeepslateAluminumOre = builder.comment("Disable or Enable Deepslate Aluminum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateAluminumOre").define("GenerateDeepslateAluminumOre", true);
						generateDeepslateTinOre = builder.comment("Disable or Enable Deepslate Tin Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateTinOre").define("GenerateDeepslateTinOre", true);
						generateDeepslateLeadOre = builder.comment("Disable or Enable Deepslate Lead Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateLeadOre").define("GenerateDeepslateLeadOre", true);
						generateDeepslateSilverOre = builder.comment("Disable or Enable Deepslate Silver Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateSilverOre").define("GenerateDeepslateSilverOre", true);
						generateDeepslatePlatinumOre = builder.comment("Disable or Enable Deepslate Platinum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslatePlatinumOre").define("GenerateDeepslatePlatinumOre", true);
						generateDeepslateTungstenOre = builder.comment("Disable or Enable Deepslate Tunsgten Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateTungstenOre").define("GenerateDeepslateTungstenOre", true);
						generateDeepslateSapphireOre = builder.comment("Disable or Enable Deepslate Sapphire Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateSapphireOre").define("GenerateDeepslateSapphireOre", true);
						generateDeepslateRubyOre = builder.comment("Disable or Enable Deepslate Ruby Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateRubyOre").define("GenerateDeepslateRubyOre", true);
					}
					builder.pop();
					builder.push("Nether");
					{
						generateNetherSilverOre = builder.comment("Disable or Enable Nether Silver Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateNetherSilverOre").define("GenerateNetherSilverOre", true);
						generateNetherPlatinumOre = builder.comment("Disable or Enable Nether Platinum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateNetherPlatinumOre").define("GenerateNetherPlatinumOre", true);
						generateNetherTungstenOre = builder.comment("Disable or Enable Nether Tungsten Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateNetherTungstenOre").define("GenerateNetherTungstenOre", true);
					}
					builder.pop();
				}
				builder.pop();
				builder.push("Tree Generation");
				{
					generateRubberTrees = builder.comment("Disable or Enable Rubber Tree Generation.").translation(StaticPower.MOD_ID + ".config." + "generateRubberTrees")
							.define("GenerateRubberTrees", true);
					disableRubberTreesInSnowyBiomes = builder.comment("Disables rubber tress from spawning in snowy biomes.")
							.translation(StaticPower.MOD_ID + ".config." + "disableRubberTreesInSnowyBiomes").define("DisableRubberTreesInSnowyBiomes", true);
					minRubberTreeCount = builder
							.comment("Indicates the number of GUARANTEED trees per biome. The default values allows for some biomes to not have a single tree spanwed.")
							.translation(StaticPower.MOD_ID + ".config." + "minRubberTreeCount").define("MinRubberTreeCount", 0);
					maxRubberTreeCount = builder.comment("Controls the max number of trees that can be grown in a biome.")
							.translation(StaticPower.MOD_ID + ".config." + "maxRubberTreeCount").define("MaxRubberTreeCount", 4);
					rubberTreeSpawnChance = builder.comment(
							"When a biome is created, the MinRuberTreeCount amount of trees is allocated. This value represents the chance the number of trees between min and max tree counts will be added in addition. Setting this value to 0 would force all biomes to only contain the MinRubberTreeCount amount of trees, and setting it to 1 will force all biomes to contain MaxRubberTreeCount + RandomNumberBetween(MinRubberTreeCount, MaxRubberTreeCount) trees.")
							.translation(StaticPower.MOD_ID + ".config." + "rubberTreeSpawnChance").define("RubberTreeSpawnChance", 0.15);

				}
				builder.pop();
				builder.push("Mobs");
				{
					builder.push("Smeep");
					{
						smeepSpawnWeight = builder.comment(
								"Controls how many ticks between each digistore regulator operation. The higher, the faster the operations, but the stronger hit to performance.")
								.translation(StaticPower.MOD_ID + ".config." + "smeepSpawnWeight").define("SmeepSpawnWeight", 4);
						smeepMinCount = builder.comment("Controls how many slots the regulator has.").translation(StaticPower.MOD_ID + ".config." + "SmeepMinCount")
								.define("smeepMinCount", 2);
						smeepMaxCount = builder.comment("Controls how many items can be transfered for each item type during a regulation.")
								.translation(StaticPower.MOD_ID + ".config." + "smeepMaxCount").define("SmeepMaxCount", 5);
					}
					builder.pop();
				}
				builder.pop();
			}
			builder.pop();

			builder.push("Power");
			{
				lowVoltage = builder.comment("The voltage level associated with low voltage power.").translation(StaticPower.MOD_ID + ".config." + "lowVoltage")
						.define("LowVoltage", 10.0);
				mediumVoltage = builder.comment("The voltage level associated with medium voltage power.").translation(StaticPower.MOD_ID + ".config." + "mediumVoltage")
						.define("MediumVoltage", 100.0);
				highVoltage = builder.comment("The voltage level associated with high voltage power.").translation(StaticPower.MOD_ID + ".config." + "highVoltage")
						.define("HighVoltage", 1000.0);
				extremeVoltage = builder.comment("The voltage level associated with extreme voltage power.").translation(StaticPower.MOD_ID + ".config." + "extremeVoltage")
						.define("ExtremeVoltage", 10000.0);
				bonkersVoltage = builder.comment("The voltage level associated with bonkers voltage power.").translation(StaticPower.MOD_ID + ".config." + "bonkersVoltage")
						.define("BonkersVoltage", 100000.0);
			}
			builder.pop();

			builder.push("Tools");
			{
				builder.push("Axe");
				minRubberWoodBarkPerStrip = builder.comment("Controls the minimum number of strips of bark are removed from a rubber wood log when stripped with an axe.")
						.translation(StaticPower.MOD_ID + ".config." + "minRubberWoodBarkPerStrip").define("MinRubberWoodBarkPerStrip", 1);
				maxRubberWoodBarkPerStrip = builder.comment("Controls the maximum number of strips of bark are removed from a rubber wood log when stripped with an axe.")
						.translation(StaticPower.MOD_ID + ".config." + "maxRubberWoodBarkPerStrip").define("MaxRubberWoodBarkPerStrip", 4);
				builder.pop();

				builder.push("WirelessTerminal");
				digistoreWirelessTerminalPowerCapacity = builder.comment("Sets the power capacity of the Digistore Wireless Terminal (in SW).")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreWirelessTerminalPowerCapacity").define("DigistoreWirelessTerminalPowerCapacity", 10000000);
				digistoreWirelessTerminalPowerUsage = builder.comment("Sets the power used every time the digistore network is accessed by the wireless terminal (in SW).")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreWirelessTerminalPowerUsage").define("DigistoreWirelessTerminalPowerUsage", 50.0);
				builder.pop();
			}
			builder.pop();

			builder.push("Digistore");
			{
				{
					builder.push("Cards");
					digistoreCardUniqueTypes = builder.comment("The number of unique types that can be contained in a stacked digistore card of this tier.")
							.translation(StaticPower.MOD_ID + ".config." + "digistoreCardUniqueTypes").define("DigistoreCardUniqueTypes", 64);
					digistoreStackedCardUniqueTypes = builder.comment("The number of unique types that can be contained in a stacked digistore card of this tier.")
							.translation(StaticPower.MOD_ID + ".config." + "digistoreStackedCardUniqueTypes").define("DigistoreStackedCardUniqueTypes", 256);
					builder.pop();
				}
				builder.push("Regulator");
				digistoreRegulatorRate = builder
						.comment("Controls how many ticks between each digistore regulator operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorRate").define("DigistoreRegulatorRate", 50);
				digistoreRegulatorSlots = builder.comment("Controls how many slots the regulator has.").translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorSlots")
						.define("DigistoreRegulatorSlots", 8);
				digistoreRegulatorStackSize = builder.comment("Controls how many items can be transfered for each item type during a regulation.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorStackSize").define("DigistoreRegulatorStackSize", 8);
				builder.pop();
			}
			{
				builder.push("I/O Bus");
				digistoreIOBusRate = builder
						.comment("Controls how many ticks between each digistore I/O bus operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusRate").define("DigistoreIOBusRate", 40);
				digistoreIOBusSlots = builder.comment("Controls how many slots each the import and output rows of the digistore I/O bus have.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusSlots").define("DigistoreIOBusSlots", 8);
				digistoreIOBusStackSize = builder
						.comment("Controls how many items the digistore I/O will try to import per operation. This count is separate for the import and the export.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusStackSize").define("DigistoreIOBusStackSize", 8);
				builder.pop();
			}
			{
				builder.push("Importer");
				digistoreImporterRate = builder
						.comment("Controls how many ticks between each digistore importer operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterRate").define("DigistoreImporterRate", 40);
				digistoreImporterSlots = builder.comment("Controls how many slots the digistore importer has.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterSlots").define("DigistoreImporterSlots", 8);
				digistoreImporterStackSize = builder.comment("Controls how many items the importer will try to import per operation.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterStackSize").define("DigistoreImporterStackSize", 8);
				builder.pop();
			}
			{
				builder.push("Exporter");
				digistoreExporterRate = builder
						.comment("Controls how many ticks between each digistore exporter operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterRate").define("DigistoreExporterRate", 40);
				digistoreExporterSlots = builder.comment("Controls how many slots the digistore exporter has.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterSlots").define("DigistoreExporterSlots", 8);
				digistoreExporterStackSize = builder.comment("Controls how many items the exporter will try to export per operation.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterStackSize").define("DigistoreExporterstackSize", 8);
				builder.pop();
			}
			{
				builder.push("Crafting Interface");

				digistoreCraftingInterfaceSlots = builder.comment("Controls how many slots the crafting interface attachment gets.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreCraftingInterfaceSlots").define("DigistoreCraftingInterfaceSlots", 9);
				builder.pop();
			}
			builder.pop();

			builder.push("Upgrades");
			acceleratorCardImprovment = builder.comment("Defines the effect a max sized stack of accelerator upgrades will have.")
					.translation(StaticPower.MOD_ID + ".config." + "acceleratorCardImprovment").define("AcceleratorCardImprovment", 4.0);
			builder.pop();

			builder.push("Machines");
			{
				builder.push("Miner");
				{
					minerHeatGeneration = builder.comment("Defines how much heat is produced when a block is broken by a regular miner.")
							.translation(StaticPower.MOD_ID + ".config." + "minerHeatGeneration").define("MinerHeatGeneration", CapabilityHeatable.convertHeatToMilliHeat(100));
					minerFuelUsage = builder.comment("Defines how much fuel value is used per tick by a regular miner.")
							.translation(StaticPower.MOD_ID + ".config." + "minerFuelUsage").define("MinerFuelUsage", 1);
					minerRadius = builder.comment("Defines the base radius of the regular miner.").translation(StaticPower.MOD_ID + ".config." + "minerRadius")
							.define("MinerRadius", 3);
					builder.pop();
					minerProcessingTime = builder.comment("Defines the amount of ticks a regular miner takes to break a block [1 Second = 20 Ticks].")
							.translation(StaticPower.MOD_ID + ".config." + "minerProcessingTime").define("MinerProcessingTime", 70);
				}
				{
					builder.push("Electric Miner");
					electricMinerHeatGeneration = builder.comment("Defines how much heat is produced when a block is broken by an electric miner.")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerHeatGeneration")
							.define("ElectricMinerHeatGeneration", CapabilityHeatable.convertHeatToMilliHeat(100));
					electricMinerRadius = builder.comment("Defines the base radius of the electric miner.").translation(StaticPower.MOD_ID + ".config." + "electricMinerRadius")
							.define("ElectricMinerRadius", 3);
					electricMinerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerPowerUsage").define("ElectricMinerPowerUsage", 50.0);
					electricMinerProcessingTime = builder.comment("Defines the amount of ticks an electric miner takes to break a block [1 Second = 20 Ticks].")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerProcessingTime").define("ElectricMinerProcessingTime", 40);
					builder.pop();
				}
				{
					builder.push("Solid Generator");
					solidFuelGenerationPerTick = builder.comment("Defines the amount of power that is generated per tick in a solid fueled generator (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "solidFuelGenerationPerTick").define("SolidFuelGenerationPerTick", 5.0);
					builder.pop();
				}

				{
					builder.push("Auto Crafter");
					autoCrafterPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "autoCrafterPowerUsage").define("AutoCrafterPowerUsage", 5.0);
					autoCrafterProcessingTime = builder.comment("Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "autoCrafterProcessingTime").define("AutoCrafterProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Auto Smith");
					autoSmithPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "autoSmithPowerUsage").define("AutoSmithPowerUsage", 14.0);
					autoSmithProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "autoSmithProcessingTime").define("AutoSmithProcessingTime", 75);
					builder.pop();
				}
				{
					builder.push("Auto Solderer");
					autoSolderingTablePowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "autoSolderingTablePowerUsage").define("AutoSolderingTablePowerUsage", 5.0);
					autoSolderingTableProcessingTime = builder.comment("Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "autoSolderingTableProcessingTime").define("AutoSolderingTableProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Basic Farmer");
					basicFarmerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerPowerUsage").define("BasicFarmerPowerUsage", 10.0);
					basicFarmerHarvestPowerUsage = builder.comment("Controls how much power is used per harvest in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerHarvestPowerUsage").define("BasicFarmerHarvestPowerUsage", 100.0);
					basicFarmerProcessingTime = builder.comment("Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerProcessingTime").define("BasicFarmerProcessingTime", 20);
					basicFarmerFluidUsage = builder.comment("Controls how many mB of fluid is consumed per tick in the Basic Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerFluidUsage").define("BasicFarmerFluidUsage", 1);

					basicFarmerDefaultRange = builder.comment("Controls the default radius for the Basic Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerDefaultRange").define("basicFarmerDefaultRange", 2);

					basicFarmerToolUsage = builder.comment("Controls the amount of durability tools take per pick in the Basic Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerToolUsage").define("BasicFarmerToolUsage", 1);
					builder.pop();
				}
				{
					builder.push("Bottler");

					bottlerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "bottlerPowerUsage").define("BottlerPowerUsage", 5.0);
					bottlerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "bottlerProcessingTime").define("BottlerProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Caster");

					casterPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "casterPowerUsage").define("CasterPowerUsage", 15.0);
					casterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "casterProcessingTime").define("CasterProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Centrifuge");
					centrifugePowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugePowerUsage").define("CentrifugePowerUsage", 10.0);
					centrifugeMotorPowerUsage = builder.comment("Controls how much power is used per tick to maintain the motor speed (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugeMotorPowerUsage").define("CentrifugeMotorPowerUsage", 5.0);
					centrifugeProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugeProcessingTime").define("CentrifugeProcessingTime", 100);
					centrifugeInitialMaxSpeed = builder.comment("Controls the default max RPM that an un-upgraded centrifuge will spin up to.")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugeInitialMaxSpeed").define("CentrifugeInitialMaxSpeed", 500);
					builder.pop();
				}
				{
					builder.push("Crucible");
					cruciblePowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "cruciblePowerUsage").define("CruciblePowerUsage", 20.0);
					crucibleProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "crucibleProcessingTime").define("CrucibleProcessingTime", 100);
					crucibleHeatPowerUsage = builder.comment("Controls how much power is used per tick in this machine to maintain the heat level (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "crucibleHeatPowerUsage").define("CrucibleHeatPowerUsage", 10.0);
					crucibleHeatGenerationPerTick = builder.comment("Controls the amount of heat that is generated per tick for in the Crucible.")
							.translation(StaticPower.MOD_ID + ".config." + "crucibleHeatGenerationPerTick")
							.define("CrucibleHeatGenerationPerTick", CapabilityHeatable.convertHeatToMilliHeat(5));
					builder.pop();
				}
				{
					builder.push("Fermenter");
					fermenterPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fermenterPowerUsage").define("FermenterPowerUsage", 5.0);
					fermenterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fermenterProcessingTime").define("FermenterProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Fluid Infuser");
					fluidInfuserPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fluidInfuserPowerUsage").define("FluidInfuserPowerUsage", 5.0);
					fluidInfuserProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fluidInfuserProcessingTime").define("FluidInfuserProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Former");

					formerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "formerPowerUsage").define("FormerPowerUsage", 5.0);
					formerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "formerProcessingTime").define("FormerProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Fusion Furnace");

					fusionFurnacePowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fusionFurnacePowerUsage").define("FusionFurnacePowerUsage", 25.0);
					fusionFurnaceProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fusionFurnaceProcessingTime").define("FusionFurnaceProcessingTime", 250);
					builder.pop();
				}
				{
					builder.push("Heatsink");
					heatSinkTemperatureDamageThreshold = builder.comment("When a heatsink is hotter than this value, it will damage entities that stand on it.")
							.translation(StaticPower.MOD_ID + ".config." + "heatSinkTemperatureDamageThreshold").define("HeatSinkTemperatureDamageThreshold", 100);
					builder.pop();
				}
				{
					builder.push("Lathe");
					lathePowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "lathePowerUsage").define("LathePowerUsage", 10.0);
					latheProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "latheProcessingTime").define("LatheProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Lumber Mill");
					lumberMillPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "lumberMillPowerUsage").define("LumberMillPowerUsage", 5.0);
					lumberMillProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "lumberMillProcessingTime").define("LumberMillProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Mixer");
					mixerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "mixerPowerUsage").define("MixerPowerUsage", 10.0);
					mixerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "mixerProcessingTime").define("MixerProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Packager");
					packagerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "packagerPowerUsage").define("PackagerPowerUsage", 5.0);
					packagerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "packagerProcessingTime").define("PackagerProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Powered Furnace");
					poweredFurnacePowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "poweredFurnacePowerUsage").define("PoweredFurnacePowerUsage", 5.0);
					builder.pop();
				}
				{
					builder.push("Powered Grinder");
					poweredGrinderOutputBonusChance = builder.comment("Controls the default Powered Grinder output bonus chance.")
							.translation(StaticPower.MOD_ID + ".config." + "poweredGrinderOutputBonusChance").define("PoweredGrinderOutputBonusChance", 1.0);
					poweredGrinderPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "poweredFurnacePowerUsage").define("PoweredFurnacePowerUsage", 5.0);
					poweredGrinderProcessingTime = builder.comment("Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "poweredGrinderProcessingTime").define("PoweredGrinderProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Pump");
					pumpPowerUsage = builder.comment("Controls how much power is used per pump action in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "pumpPowerUsage").define("PumpPowerUsage", 200.0);
					builder.pop();
				}
				{
					builder.push("Squeezer");
					squeezerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "squeezerPowerUsage").define("SqueezerPowerUsage", 5.0);
					squeezerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "squeezerProcessingTime").define("SqueezerProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Tree Farmer");
					treeFarmerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerPowerUsage").define("TreeFarmerPowerUsage", 10.0);
					treeFarmerHarvestPowerUsage = builder.comment("Controls how much power is used per harvest in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerHarvestPowerUsage").define("TreeFarmerHarvestPowerUsage", 100.0);
					treeFarmerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerProcessingTime").define("TreeFarmerProcessingTime", 20);
					treeFarmerFluidUsage = builder.comment("Controls how many mB of fluid is consumed per tick in the Tree Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerFluidUsage").define("TreeFarmerFluidUsage", 1);
					treeFarmerDefaultRange = builder.comment("Controls the default radius for the Tree Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerDefaultRange").define("TreeFarmerDefaultRange", 2);
					treeFarmerToolUsage = builder.comment("Controls the amount of durability tools take per pick in the Tree Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerToolUsage").define("TreeFarmerToolUsage", 1);
					treeFarmerMaxTreeRecursion = builder.comment(
							"Controls the maximum amount of blocks a Tree Farmer will consider as being part of a tree. The higher this value, the higher the impact to performance.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerMaxTreeRecursion").define("TreeFarmerMaxTreeRecursion", 100);
					treeFarmerSaplingSpacing = builder.comment("Controls the spacing between saplings when planted.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerSaplingSpacing").define("TreeFarmerSaplingSpacing", 2);
					builder.pop();
				}
				{
					builder.push("Tumbler");
					tumblerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerPowerUsage").define("TumblerPowerUsage", 20.0);
					tumblerMotorPowerUsage = builder.comment("Controls how much power is used per tick to maintain the motor speed (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerMotorPowerUsage").define("TumblerMotorPowerUsage", 10.0);
					tumblerOutputBonusChance = builder.comment("Controls the default Tumbler output bonus chance.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerOutputBonusChance").define("TumblerOutputBonusChance", 1.0);
					tumblerRequiredSpeed = builder.comment("Controls the speed required in the Tumbler before it starts processing.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerRequiredSpeed").define("TumblerRequiredSpeed", 1000);
					tumblerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerProcessingTime").define("TumblerProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Vulcanizer");
					vulcanizerPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "vulcanizerPowerUsage").define("VulcanizerPowerUsage", 5.0);
					vulcanizerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "vulcanizerProcessingTime").define("VulcanizerProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Enchanter");
					enchanterPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "enchanterPowerUsage").define("EnchanterPowerUsage", 5.0);
					enchanterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "enchanterProcessingTime").define("EnchanterProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Refinery");
					refineryPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryPowerUsage").define("RefineryPowerUsage", 20.0);
					refineryProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryProcessingTime").define("RefineryProcessingTime", 20);
					refineryPerBoilerHeatGeneration = builder.comment("Defines how much heat (in mC [1C = 1000mC]) is produced by the refinery per tick ([1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "refineryHeatGeneration")
							.define("RefineryHeatGeneration", CapabilityHeatable.convertHeatToMilliHeat(10));
					refineryMinimumHeat = builder.comment("Defines how much heat (in mC [1C = 1000mC]) is required by the refinery before it begins processing.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryMinimumHeat").define("RefineryMinimumHeat", CapabilityHeatable.convertHeatToMilliHeat(300));
					refineryHeatUse = builder.comment("Defines how much heat (in mC [1C = 1000mC]) is used by the refinery per tick ([1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "refineryHeatUse").define("RefineryHeatUse", CapabilityHeatable.convertHeatToMilliHeat(10));
					refineryOverheatCooldownTime = builder
							.comment("Defines how long it takes (in ticks [1 Second = 20 Ticks]) after a refinery has overheated to start processing again.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryOverheatCooldownTime").define("RefineryOverheatCooldownTime", 100);
					builder.pop();
				}
				{
					builder.push("Laboratory");
					laboratoryPowerUsage = builder.comment("Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "laboratoryPowerUsage").define("LaboratoryPowerUsage", 5.0);
					builder.pop();
				}

			}
			builder.pop();
		}
	}

	public static void registerTier(ResourceLocation tierId, Function<Builder, StaticPowerTier> tierConstructor) {
		Pair<StaticPowerTier, ForgeConfigSpec> basicPair = new ForgeConfigSpec.Builder().configure(tierConstructor);
		TIERS.put(tierId.toString(), new ConfigPair(basicPair.getRight(), basicPair.getLeft()));
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticPowerConfig.TIERS.get(tierId.toString()).spec,
				StaticPower.MOD_ID + "\\tiers\\" + tierId.getPath() + ".toml");
	}

	public static StaticPowerTier getTier(ResourceLocation tierId) {
		return TIERS.get(tierId.toString()).tier;
	}

	public static void preInitialize() {
		// Verify the config sub-folder exists.
		checkOrCreateFolder(StaticPower.MOD_ID);

		// Verify the tiers folder exists.
		checkOrCreateFolder(StaticPower.MOD_ID + "\\tiers");

		// Add the server and common configs.
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, StaticPowerConfig.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticPowerConfig.COMMON_SPEC, StaticPower.MOD_ID + "\\" + StaticPower.MOD_ID + "-common.toml");
	}

	private static void checkOrCreateFolder(String path) {
		// Verify or create the folder.
		File subFolder = new File(FMLPaths.CONFIGDIR.get().toFile(), path);
		if (!subFolder.exists()) {
			try {
				if (!subFolder.mkdir()) {
					throw new RuntimeException("Could not create config directory " + subFolder);
				}
			} catch (SecurityException e) {
				throw new RuntimeException("Could not create config directory " + subFolder, e);
			}
		}
	}

	public static class ConfigPair {
		public final StaticPowerTier tier;
		public final ForgeConfigSpec spec;

		public ConfigPair(ForgeConfigSpec spec, StaticPowerTier tier) {
			this.tier = tier;
			this.spec = spec;
		}
	}
}
