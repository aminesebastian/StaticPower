package theking530.staticpower;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;

@EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerConfig {

	public static final StaticPowerCommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final StaticPowerServerConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		final Pair<StaticPowerServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder()
				.configure(StaticPowerServerConfig::new);
		SERVER_SPEC = serverPair.getRight();
		SERVER = serverPair.getLeft();

		final Pair<StaticPowerCommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder()
				.configure(StaticPowerCommonConfig::new);
		COMMON_SPEC = commonPair.getRight();
		COMMON = commonPair.getLeft();
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
		private final static double BASIC_MACHINE_POWER_USAGE_PER_S = 100;
		private final static double ADVANCED_MACHINE_POWER_USAGE_PER_S = 200;
		private final static double STATIC_MACHINE_POWER_USAGE_PER_S = 500;
		private final static double ENERGIZED_MACHINE_POWER_USAGE_PER_S = 1000;
		private final static double LUMUM_MACHINE_POWER_USAGE_PER_S = 5000;

		public final ConfigValue<Boolean> generateRubberTrees;
		public final ConfigValue<Integer> minRubberTreeCount;
		public final ConfigValue<Integer> maxRubberTreeCount;
		public final ConfigValue<Float> rubberTreeSpawnChance;
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

		public final ConfigValue<Double> digistoreWirelessTerminalPowerCapacity;
		public final ConfigValue<Double> digistoreWirelessTerminalPowerUsage;

		public final ConfigValue<Float> minerHeatGeneration;
		public final ConfigValue<Integer> minerFuelUsage;
		public final ConfigValue<Integer> minerRadius;
		public final ConfigValue<Integer> minerProcessingTime;

		public final ConfigValue<Float> electricMinerHeatGeneration;
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
		public final ConfigValue<Float> crucibleHeatGenerationPerTick;
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

		public final ConfigValue<Integer> alloyFurnaceProcessingTime;

		public final ConfigValue<Double> lathePowerUsage;
		public final ConfigValue<Integer> latheProcessingTime;

		public final ConfigValue<Double> lumberMillPowerUsage;
		public final ConfigValue<Integer> lumberMillProcessingTime;

		public final ConfigValue<Double> mixerPowerUsage;
		public final ConfigValue<Integer> mixerProcessingTime;

		public final ConfigValue<Double> packagerPowerUsage;
		public final ConfigValue<Integer> packagerProcessingTime;

		public final ConfigValue<Double> pumpPowerUsage;
		public final ConfigValue<Integer> pumpTankCapacity;

		public final ConfigValue<Double> squeezerPowerUsage;
		public final ConfigValue<Integer> squeezerProcessingTime;

		public final ConfigValue<Double> hydroponicFarmerPowerUsage;
		public final ConfigValue<Integer> hydroponicFarmerProcessingTime;

		public final ConfigValue<Double> vulcanizerPowerUsage;
		public final ConfigValue<Integer> vulcanizerProcessingTime;

		public final ConfigValue<Double> enchanterPowerUsage;
		public final ConfigValue<Integer> enchanterProcessingTime;

		public final ConfigValue<Integer> refineryProcessingTime;
		public final ConfigValue<Float> refineryMinimumHeat;
		public final ConfigValue<Double> refineryHeatUse;
		public final ConfigValue<Integer> refineryOverheatCooldownTime;

		public final ConfigValue<Double> laboratoryPowerUsage;

		public final ConfigValue<Double> drillPowerUsePerBlock;
		public final ConfigValue<Double> chainsawPowerUsePerBlock;

		public final ConfigValue<Integer> coalCoakBurnTime;

		public StaticPowerServerConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Generation");
			{
				builder.push("Ore Generation");
				{
					builder.push("Overworld");
					{
						generateZincOre = builder.comment("Disable or Enable Zinc Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateZincOre")
								.define("GenerateZincOre", true);
						generateMagnesiumOre = builder.comment("Disable or Enable Magnesium Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateMagnesiumOre")
								.define("GenerateMagnesiumOre", true);
						generateAluminumOre = builder.comment("Disable or Enable Aluminum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateAluminumOre")
								.define("GenerateAluminumOre", true);
						generateTinOre = builder.comment("Disable or Enable Tin Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "tinore").define("generateTinOre", true);
						generateLeadOre = builder.comment("Disable or Enable Lead Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateLeadOre")
								.define("GenerateLeadOre", true);
						generateSilverOre = builder.comment("Disable or Enable Silver Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateSilverOre")
								.define("GenerateSilverOre", true);
						generatePlatinumOre = builder.comment("Disable or Enable Platinum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generatePlatinumOre")
								.define("GeneratePlatinumOre", true);
						generateTungstenOre = builder.comment("Disable or Enable Tunsgten Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateTungstenOre")
								.define("GenerateTungstenOre", true);
						generateSapphireOre = builder.comment("Disable or Enable Sapphire Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateSapphireOre")
								.define("GenerateSapphireOre", true);
						generateRubyOre = builder.comment("Disable or Enable Ruby Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateRubyOre")
								.define("GenerateRubyOre", true);
						generateRustyIronOre = builder.comment("Disable or Enable Rusty Iron Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateRustyIronOre")
								.define("GenerateRustyIronOre", true);
					}
					builder.pop();
					builder.push("Deep");
					{
						generateDeepslateZincOre = builder.comment("Disable or Enable Deepslate Zinc Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateZincOre")
								.define("GenerateDeepslateZincOre", true);
						generateDeepslateMagnesiumOre = builder
								.comment("Disable or Enable Deepslate Magnesium Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateMagnesiumOre")
								.define("GenerateDeepslateMagnesiumOre", true);
						generateDeepslateAluminumOre = builder
								.comment("Disable or Enable Deepslate Aluminum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateAluminumOre")
								.define("GenerateDeepslateAluminumOre", true);
						generateDeepslateTinOre = builder.comment("Disable or Enable Deepslate Tin Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateTinOre")
								.define("GenerateDeepslateTinOre", true);
						generateDeepslateLeadOre = builder.comment("Disable or Enable Deepslate Lead Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateLeadOre")
								.define("GenerateDeepslateLeadOre", true);
						generateDeepslateSilverOre = builder
								.comment("Disable or Enable Deepslate Silver Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateSilverOre")
								.define("GenerateDeepslateSilverOre", true);
						generateDeepslatePlatinumOre = builder
								.comment("Disable or Enable Deepslate Platinum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslatePlatinumOre")
								.define("GenerateDeepslatePlatinumOre", true);
						generateDeepslateTungstenOre = builder
								.comment("Disable or Enable Deepslate Tunsgten Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateTungstenOre")
								.define("GenerateDeepslateTungstenOre", true);
						generateDeepslateSapphireOre = builder
								.comment("Disable or Enable Deepslate Sapphire Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateSapphireOre")
								.define("GenerateDeepslateSapphireOre", true);
						generateDeepslateRubyOre = builder.comment("Disable or Enable Deepslate Ruby Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateDeepslateRubyOre")
								.define("GenerateDeepslateRubyOre", true);
					}
					builder.pop();
					builder.push("Nether");
					{
						generateNetherSilverOre = builder.comment("Disable or Enable Nether Silver Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateNetherSilverOre")
								.define("GenerateNetherSilverOre", true);
						generateNetherPlatinumOre = builder.comment("Disable or Enable Nether Platinum Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateNetherPlatinumOre")
								.define("GenerateNetherPlatinumOre", true);
						generateNetherTungstenOre = builder.comment("Disable or Enable Nether Tungsten Ore Generation")
								.translation(StaticPower.MOD_ID + ".config." + "generateNetherTungstenOre")
								.define("GenerateNetherTungstenOre", true);
					}
					builder.pop();
				}
				builder.pop();
				builder.push("Tree Generation");
				{
					generateRubberTrees = builder.comment("Disable or Enable Rubber Tree Generation.")
							.translation(StaticPower.MOD_ID + ".config." + "generateRubberTrees")
							.define("GenerateRubberTrees", true);
					disableRubberTreesInSnowyBiomes = builder
							.comment("Disables rubber tress from spawning in snowy biomes.")
							.translation(StaticPower.MOD_ID + ".config." + "disableRubberTreesInSnowyBiomes")
							.define("DisableRubberTreesInSnowyBiomes", true);
					minRubberTreeCount = builder.comment(
							"Indicates the number of GUARANTEED trees per biome. The default values allows for some biomes to not have a single tree spanwed.")
							.translation(StaticPower.MOD_ID + ".config." + "minRubberTreeCount")
							.define("MinRubberTreeCount", 0);
					maxRubberTreeCount = builder
							.comment("Controls the max number of trees that can be grown in a biome.")
							.translation(StaticPower.MOD_ID + ".config." + "maxRubberTreeCount")
							.define("MaxRubberTreeCount", 4);
					rubberTreeSpawnChance = builder.comment(
							"When a biome is created, the MinRuberTreeCount amount of trees is allocated. This value represents the chance the number of trees between min and max tree counts will be added in addition. Setting this value to 0 would force all biomes to only contain the MinRubberTreeCount amount of trees, and setting it to 1 will force all biomes to contain MaxRubberTreeCount + RandomNumberBetween(MinRubberTreeCount, MaxRubberTreeCount) trees.")
							.translation(StaticPower.MOD_ID + ".config." + "rubberTreeSpawnChance")
							.define("RubberTreeSpawnChance", 0.15f);

				}
				builder.pop();
			}
			builder.pop();

			builder.push("Tools");
			{
				builder.push("Axe");
				minRubberWoodBarkPerStrip = builder.comment(
						"Controls the minimum number of strips of bark are removed from a rubber wood log when stripped with an axe.")
						.translation(StaticPower.MOD_ID + ".config." + "minRubberWoodBarkPerStrip")
						.define("MinRubberWoodBarkPerStrip", 1);
				maxRubberWoodBarkPerStrip = builder.comment(
						"Controls the maximum number of strips of bark are removed from a rubber wood log when stripped with an axe.")
						.translation(StaticPower.MOD_ID + ".config." + "maxRubberWoodBarkPerStrip")
						.define("MaxRubberWoodBarkPerStrip", 4);
				builder.pop();

				builder.push("WirelessTerminal");
				digistoreWirelessTerminalPowerCapacity = builder
						.comment("Sets the power capacity of the Digistore Wireless Terminal (in SW).")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreWirelessTerminalPowerCapacity")
						.define("DigistoreWirelessTerminalPowerCapacity", StaticPowerEnergyUtilities.toKW(1));
				digistoreWirelessTerminalPowerUsage = builder.comment(
						"Sets the power used every time the digistore network is accessed by the wireless terminal (in SW).")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreWirelessTerminalPowerUsage")
						.define("DigistoreWirelessTerminalPowerUsage", 10.0);
				builder.pop();
			}
			builder.pop();

			builder.push("Digistore");
			{
				{
					builder.push("Cards");
					digistoreCardUniqueTypes = builder.comment(
							"The number of unique types that can be contained in a stacked digistore card of this tier.")
							.translation(StaticPower.MOD_ID + ".config." + "digistoreCardUniqueTypes")
							.define("DigistoreCardUniqueTypes", 64);
					digistoreStackedCardUniqueTypes = builder.comment(
							"The number of unique types that can be contained in a stacked digistore card of this tier.")
							.translation(StaticPower.MOD_ID + ".config." + "digistoreStackedCardUniqueTypes")
							.define("DigistoreStackedCardUniqueTypes", 256);
					builder.pop();
				}
				builder.push("Regulator");
				digistoreRegulatorRate = builder.comment(
						"Controls how many ticks between each digistore regulator operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorRate")
						.define("DigistoreRegulatorRate", 50);
				digistoreRegulatorSlots = builder.comment("Controls how many slots the regulator has.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorSlots")
						.define("DigistoreRegulatorSlots", 8);
				digistoreRegulatorStackSize = builder
						.comment("Controls how many items can be transfered for each item type during a regulation.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorStackSize")
						.define("DigistoreRegulatorStackSize", 8);
				builder.pop();
			}
			{
				builder.push("I/O Bus");
				digistoreIOBusRate = builder.comment(
						"Controls how many ticks between each digistore I/O bus operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusRate")
						.define("DigistoreIOBusRate", 40);
				digistoreIOBusSlots = builder.comment(
						"Controls how many slots each the import and output rows of the digistore I/O bus have.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusSlots")
						.define("DigistoreIOBusSlots", 8);
				digistoreIOBusStackSize = builder.comment(
						"Controls how many items the digistore I/O will try to import per operation. This count is separate for the import and the export.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusStackSize")
						.define("DigistoreIOBusStackSize", 8);
				builder.pop();
			}
			{
				builder.push("Importer");
				digistoreImporterRate = builder.comment(
						"Controls how many ticks between each digistore importer operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterRate")
						.define("DigistoreImporterRate", 40);
				digistoreImporterSlots = builder.comment("Controls how many slots the digistore importer has.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterSlots")
						.define("DigistoreImporterSlots", 8);
				digistoreImporterStackSize = builder
						.comment("Controls how many items the importer will try to import per operation.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterStackSize")
						.define("DigistoreImporterStackSize", 8);
				builder.pop();
			}
			{
				builder.push("Exporter");
				digistoreExporterRate = builder.comment(
						"Controls how many ticks between each digistore exporter operation. The higher, the faster the operations, but the stronger hit to performance.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterRate")
						.define("DigistoreExporterRate", 40);
				digistoreExporterSlots = builder.comment("Controls how many slots the digistore exporter has.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterSlots")
						.define("DigistoreExporterSlots", 8);
				digistoreExporterStackSize = builder
						.comment("Controls how many items the exporter will try to export per operation.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterStackSize")
						.define("DigistoreExporterstackSize", 8);
				builder.pop();
			}
			{
				builder.push("Crafting Interface");

				digistoreCraftingInterfaceSlots = builder
						.comment("Controls how many slots the crafting interface attachment gets.")
						.translation(StaticPower.MOD_ID + ".config." + "digistoreCraftingInterfaceSlots")
						.define("DigistoreCraftingInterfaceSlots", 9);
				builder.pop();
			}
			builder.pop();

			builder.push("Upgrades");
			acceleratorCardImprovment = builder
					.comment("Defines the effect a max sized stack of accelerator upgrades will have.")
					.translation(StaticPower.MOD_ID + ".config." + "acceleratorCardImprovment")
					.define("AcceleratorCardImprovment", 4.0);
			builder.pop();

			builder.push("Machines");
			{
				builder.push("Miner");
				{
					minerHeatGeneration = builder
							.comment("Defines how much heat is produced when a block is broken by a regular miner.")
							.translation(StaticPower.MOD_ID + ".config." + "minerHeatGeneration")
							.define("MinerHeatGeneration", 100.0f);
					minerFuelUsage = builder.comment("Defines how much fuel value is used per tick by a regular miner.")
							.translation(StaticPower.MOD_ID + ".config." + "minerFuelUsage")
							.define("MinerFuelUsage", 1);
					minerRadius = builder.comment("Defines the base radius of the regular miner.")
							.translation(StaticPower.MOD_ID + ".config." + "minerRadius").define("MinerRadius", 3);
					builder.pop();
					minerProcessingTime = builder.comment(
							"Defines the amount of ticks a regular miner takes to break a block [1 Second = 20 Ticks].")
							.translation(StaticPower.MOD_ID + ".config." + "minerProcessingTime")
							.define("MinerProcessingTime", 70);
				}
				{
					builder.push("Electric Miner");
					electricMinerHeatGeneration = builder
							.comment("Defines how much heat is produced when a block is broken by an electric miner.")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerHeatGeneration")
							.define("ElectricMinerHeatGeneration", 100.0f);
					electricMinerRadius = builder.comment("Defines the base radius of the electric miner.")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerRadius")
							.define("ElectricMinerRadius", 3);
					electricMinerPowerUsage = builder
							.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerPowerUsage")
							.define("ElectricMinerPowerUsage",
									convertPowerUsageFromSecondsToTicks(ENERGIZED_MACHINE_POWER_USAGE_PER_S));
					electricMinerProcessingTime = builder.comment(
							"Defines the amount of ticks an electric miner takes to break a block [1 Second = 20 Ticks].")
							.translation(StaticPower.MOD_ID + ".config." + "electricMinerProcessingTime")
							.define("ElectricMinerProcessingTime", 40);
					builder.pop();
				}
				{
					builder.push("Solid Generator");
					solidFuelGenerationPerTick = builder.comment(
							"Defines the amount of power that is generated per tick in a solid fueled generator (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "solidFuelGenerationPerTick")
							.define("SolidFuelGenerationPerTick",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S * 2));
					builder.pop();
				}

				{
					builder.push("Auto Crafter");
					autoCrafterPowerUsage = builder
							.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "autoCrafterPowerUsage")
							.define("AutoCrafterPowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					autoCrafterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "autoCrafterProcessingTime")
							.define("AutoCrafterProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Auto Smith");
					autoSmithPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "autoSmithPowerUsage")
							.define("AutoSmithPowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					autoSmithProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "autoSmithProcessingTime")
							.define("AutoSmithProcessingTime", 75);
					builder.pop();
				}
				{
					builder.push("Auto Solderer");
					autoSolderingTablePowerUsage = builder
							.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "autoSolderingTablePowerUsage")
							.define("AutoSolderingTablePowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					autoSolderingTableProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "autoSolderingTableProcessingTime")
							.define("AutoSolderingTableProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Basic Farmer");
					basicFarmerPowerUsage = builder
							.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerPowerUsage")
							.define("BasicFarmerPowerUsage",
									convertPowerUsageFromSecondsToTicks(STATIC_MACHINE_POWER_USAGE_PER_S));
					basicFarmerHarvestPowerUsage = builder
							.comment("Controls how much power is used per harvest in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerHarvestPowerUsage")
							.define("BasicFarmerHarvestPowerUsage", STATIC_MACHINE_POWER_USAGE_PER_S);
					basicFarmerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerProcessingTime")
							.define("BasicFarmerProcessingTime", 20);
					basicFarmerFluidUsage = builder
							.comment("Controls how many mB of fluid is consumed per tick in the Basic Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerFluidUsage")
							.define("BasicFarmerFluidUsage", 1);

					basicFarmerDefaultRange = builder.comment("Controls the default radius for the Basic Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerDefaultRange")
							.define("basicFarmerDefaultRange", 2);

					basicFarmerToolUsage = builder
							.comment("Controls the amount of durability tools take per pick in the Basic Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "basicFarmerToolUsage")
							.define("BasicFarmerToolUsage", 1);
					builder.pop();
				}
				{
					builder.push("Bottler");

					bottlerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "bottlerPowerUsage")
							.define("BottlerPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					bottlerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "bottlerProcessingTime")
							.define("BottlerProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Caster");

					casterPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "casterPowerUsage")
							.define("CasterPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					casterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "casterProcessingTime")
							.define("CasterProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Centrifuge");
					centrifugePowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugePowerUsage")
							.define("CentrifugePowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					centrifugeMotorPowerUsage = builder
							.comment("Controls how much power is used per tick to maintain the motor speed (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugeMotorPowerUsage")
							.define("CentrifugeMotorPowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					centrifugeProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugeProcessingTime")
							.define("CentrifugeProcessingTime", 100);
					centrifugeInitialMaxSpeed = builder
							.comment("Controls the default max RPM that an un-upgraded centrifuge will spin up to.")
							.translation(StaticPower.MOD_ID + ".config." + "centrifugeInitialMaxSpeed")
							.define("CentrifugeInitialMaxSpeed", 500);
					builder.pop();
				}
				{
					builder.push("Crucible");
					cruciblePowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "cruciblePowerUsage")
							.define("CruciblePowerUsage",
									convertPowerUsageFromSecondsToTicks(ENERGIZED_MACHINE_POWER_USAGE_PER_S));
					crucibleProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "crucibleProcessingTime")
							.define("CrucibleProcessingTime", 100);
					crucibleHeatPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine to maintain the heat level (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "crucibleHeatPowerUsage")
							.define("CrucibleHeatPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					crucibleHeatGenerationPerTick = builder
							.comment("Controls the amount of heat that is generated per tick for in the Crucible.")
							.translation(StaticPower.MOD_ID + ".config." + "crucibleHeatGenerationPerTick")
							.define("CrucibleHeatGenerationPerTick", 1.0f);
					builder.pop();
				}
				{
					builder.push("Fermenter");
					fermenterPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fermenterPowerUsage")
							.define("FermenterPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					fermenterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fermenterProcessingTime")
							.define("FermenterProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Fluid Infuser");
					fluidInfuserPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fluidInfuserPowerUsage")
							.define("FluidInfuserPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					fluidInfuserProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fluidInfuserProcessingTime")
							.define("FluidInfuserProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Former");

					formerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "formerPowerUsage")
							.define("FormerPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					formerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "formerProcessingTime")
							.define("FormerProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Fusion Furnace");

					fusionFurnacePowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fusionFurnacePowerUsage")
							.define("FusionFurnacePowerUsage",
									convertPowerUsageFromSecondsToTicks(ENERGIZED_MACHINE_POWER_USAGE_PER_S));
					fusionFurnaceProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "fusionFurnaceProcessingTime")
							.define("FusionFurnaceProcessingTime", 250);
					builder.pop();
				}
				{
					builder.push("Alloy Furnace");
					alloyFurnaceProcessingTime = builder.comment(
							"Controls how much time it takes to processing an alloy furnace (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "alloyFurnaceProcessingTime")
							.define("AlloyFurnaceProcessingTime", 400);
					builder.pop();
				}
				{
					builder.push("Heatsink");
					heatSinkTemperatureDamageThreshold = builder.comment(
							"When a heatsink is hotter than this value, it will damage entities that stand on it.")
							.translation(StaticPower.MOD_ID + ".config." + "heatSinkTemperatureDamageThreshold")
							.define("HeatSinkTemperatureDamageThreshold", 100);
					builder.pop();
				}
				{
					builder.push("Lathe");
					lathePowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "lathePowerUsage").define("LathePowerUsage",
									convertPowerUsageFromSecondsToTicks(STATIC_MACHINE_POWER_USAGE_PER_S));
					latheProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "latheProcessingTime")
							.define("LatheProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Lumber Mill");
					lumberMillPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "lumberMillPowerUsage")
							.define("LumberMillPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					lumberMillProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "lumberMillProcessingTime")
							.define("LumberMillProcessingTime", 150);
					builder.pop();
				}
				{
					builder.push("Mixer");
					mixerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "mixerPowerUsage").define("MixerPowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					mixerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "mixerProcessingTime")
							.define("MixerProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Packager");
					packagerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "packagerPowerUsage")
							.define("PackagerPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					packagerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "packagerProcessingTime")
							.define("PackagerProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Powered Furnace");
					poweredFurnacePowerUsage = builder
							.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "poweredFurnacePowerUsage")
							.define("PoweredFurnacePowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					builder.pop();
				}
				{
					builder.push("Powered Grinder");
					poweredGrinderOutputBonusChance = builder
							.comment("Controls the default Powered Grinder output bonus chance.")
							.translation(StaticPower.MOD_ID + ".config." + "poweredGrinderOutputBonusChance")
							.define("PoweredGrinderOutputBonusChance", 1.0);
					poweredGrinderPowerUsage = builder
							.comment("Controls how much power is used per tick in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "poweredFurnacePowerUsage")
							.define("PoweredFurnacePowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					poweredGrinderProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "poweredGrinderProcessingTime")
							.define("PoweredGrinderProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Pump");
					pumpPowerUsage = builder
							.comment("Controls how much power is used per pump action in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "pumpPowerUsage").define("PumpPowerUsage",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					pumpTankCapacity = builder.comment("How many mB of fluid a pump can contain.")
							.translation(StaticPower.MOD_ID + ".config." + "pumpTankCapacity")
							.define("PumpTankCapacity", 5000);
					builder.pop();
				}
				{
					builder.push("Squeezer");
					squeezerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "squeezerPowerUsage")
							.define("SqueezerPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					squeezerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "squeezerProcessingTime")
							.define("SqueezerProcessingTime", 100);
					builder.pop();
				}
				{
					builder.push("Hydroponic Farmer");
					hydroponicFarmerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "hydroponicFarmerPowerUsage")
							.define("HydroponicFarmerPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					hydroponicFarmerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "hydroponicFarmerProcessingTime")
							.define("HydroponicFarmerProcessingTime", 12000);
					builder.pop();
				}
				{
					builder.push("Tree Farmer");
					treeFarmerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerPowerUsage")
							.define("TreeFarmerPowerUsage",
									convertPowerUsageFromSecondsToTicks(STATIC_MACHINE_POWER_USAGE_PER_S));
					treeFarmerHarvestPowerUsage = builder
							.comment("Controls how much power is used per harvest in this machine (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerHarvestPowerUsage")
							.define("TreeFarmerHarvestPowerUsage",
									convertPowerUsageFromSecondsToTicks(STATIC_MACHINE_POWER_USAGE_PER_S));
					treeFarmerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerProcessingTime")
							.define("TreeFarmerProcessingTime", 20);
					treeFarmerFluidUsage = builder
							.comment("Controls how many mB of fluid is consumed per tick in the Tree Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerFluidUsage")
							.define("TreeFarmerFluidUsage", 1);
					treeFarmerDefaultRange = builder.comment("Controls the default radius for the Tree Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerDefaultRange")
							.define("TreeFarmerDefaultRange", 2);
					treeFarmerToolUsage = builder
							.comment("Controls the amount of durability tools take per pick in the Tree Farmer.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerToolUsage")
							.define("TreeFarmerToolUsage", 1);
					treeFarmerMaxTreeRecursion = builder.comment(
							"Controls the maximum amount of blocks a Tree Farmer will consider as being part of a tree. The higher this value, the higher the impact to performance.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerMaxTreeRecursion")
							.define("TreeFarmerMaxTreeRecursion", 100);
					treeFarmerSaplingSpacing = builder.comment("Controls the spacing between saplings when planted.")
							.translation(StaticPower.MOD_ID + ".config." + "treeFarmerSaplingSpacing")
							.define("TreeFarmerSaplingSpacing", 2);
					builder.pop();
				}
				{
					builder.push("Tumbler");
					tumblerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerPowerUsage")
							.define("TumblerPowerUsage",
									convertPowerUsageFromSecondsToTicks(ENERGIZED_MACHINE_POWER_USAGE_PER_S));
					tumblerMotorPowerUsage = builder
							.comment("Controls how much power is used per tick to maintain the motor speed (in SW).")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerMotorPowerUsage")
							.define("TumblerMotorPowerUsage",
									convertPowerUsageFromSecondsToTicks(STATIC_MACHINE_POWER_USAGE_PER_S));
					tumblerOutputBonusChance = builder.comment("Controls the default Tumbler output bonus chance.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerOutputBonusChance")
							.define("TumblerOutputBonusChance", 1.0);
					tumblerRequiredSpeed = builder
							.comment("Controls the speed required in the Tumbler before it starts processing.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerRequiredSpeed")
							.define("TumblerRequiredSpeed", 1000);
					tumblerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "tumblerProcessingTime")
							.define("TumblerProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Vulcanizer");
					vulcanizerPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "vulcanizerPowerUsage")
							.define("VulcanizerPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					vulcanizerProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "vulcanizerProcessingTime")
							.define("VulcanizerProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Enchanter");
					enchanterPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "enchanterPowerUsage")
							.define("EnchanterPowerUsage",
									convertPowerUsageFromSecondsToTicks(LUMUM_MACHINE_POWER_USAGE_PER_S));
					enchanterProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "enchanterProcessingTime")
							.define("EnchanterProcessingTime", 200);
					builder.pop();
				}
				{
					builder.push("Refinery");
					refineryProcessingTime = builder.comment(
							"Controls how much time it takes to processing a recipe in this machine (in ticks [1 Second = 20 Ticks]). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryProcessingTime")
							.define("RefineryProcessingTime", 20);
					refineryMinimumHeat = builder.comment(
							"Defines how much heat (in mC [1C = 1000mC]) is required by the refinery before it begins processing.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryMinimumHeat")
							.define("RefineryMinimumHeat", 300.0f);
					refineryHeatUse = builder.comment(
							"Defines how much heat (in mC [1C = 1000mC]) is used by the refinery per tick ([1 Second = 20 Ticks]).")
							.translation(StaticPower.MOD_ID + ".config." + "refineryHeatUse").define("RefineryHeatUse",
									convertPowerUsageFromSecondsToTicks(ADVANCED_MACHINE_POWER_USAGE_PER_S));
					refineryOverheatCooldownTime = builder.comment(
							"Defines how long it takes (in ticks [1 Second = 20 Ticks]) after a refinery has overheated to start processing again.")
							.translation(StaticPower.MOD_ID + ".config." + "refineryOverheatCooldownTime")
							.define("RefineryOverheatCooldownTime", 100);
					builder.pop();
				}
				{
					builder.push("Laboratory");
					laboratoryPowerUsage = builder.comment(
							"Controls how much power is used per tick in this machine (in SW). Individual recipes can override this value.")
							.translation(StaticPower.MOD_ID + ".config." + "laboratoryPowerUsage")
							.define("LaboratoryPowerUsage",
									convertPowerUsageFromSecondsToTicks(BASIC_MACHINE_POWER_USAGE_PER_S));
					builder.pop();
				}

			}
			builder.pop();

			builder.push("Items");
			drillPowerUsePerBlock = builder.comment("The amount of power used to mine a single block by a drill.")
					.translation(StaticPower.MOD_ID + ".config." + "drillPowerUsePerBlock")
					.define("DrillPowerUsePerBlock", 10.0);

			chainsawPowerUsePerBlock = builder.comment("The amount of power used to mine a single block by a chainsaw.")
					.translation(StaticPower.MOD_ID + ".config." + "chainsawPowerUsePerBlock")
					.define("ChainsawPowerUsePerBlock", 10.0);

			coalCoakBurnTime = builder.comment("The number of ticks coal coke burns in a furnace.")
					.translation(StaticPower.MOD_ID + ".config." + "coalCoakBurnTime").define("CoalCoakBurnTime", 160);
			builder.pop();
		}
	}

	public static void preInitialize() {
		// Verify the config sub-folder exists.
		checkOrCreateFolder(StaticPower.MOD_ID);

		// Verify the tiers folder exists.
		checkOrCreateFolder(StaticPower.MOD_ID + "\\tiers");

		// Add the server and common configs.
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, StaticPowerConfig.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticPowerConfig.COMMON_SPEC,
				StaticPower.MOD_ID + "\\" + StaticPower.MOD_ID + "-common.toml");
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

	private static double convertPowerUsageFromSecondsToTicks(double watts) {
		return watts / 20;
	}
}
