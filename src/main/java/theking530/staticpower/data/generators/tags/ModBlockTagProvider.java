package theking530.staticpower.data.generators.tags;

import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.materials.Material;
import theking530.staticpower.data.materials.Material.MaterialClass;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModBlockTags;

public class ModBlockTagProvider extends BlockTagsProvider {

	public ModBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(Tags.Blocks.ORES).add(ModBlocks.OreRustyIron.get())
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.OVERWORLD_ORE))
						.map((bundle) -> bundle.get(MaterialTypes.OVERWORLD_ORE).get())))
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.DEEPSLATE_ORE))
						.map((bundle) -> bundle.get(MaterialTypes.DEEPSLATE_ORE).get())))
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.NETHER_ORE))
						.map((bundle) -> bundle.get(MaterialTypes.NETHER_ORE).get())));

		tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(ModBlocks.OreRustyIron.get()).add(toArray(ModMaterials.MATERIALS.values().stream()
				.filter((bundle) -> bundle.has(MaterialTypes.OVERWORLD_ORE)).map((bundle) -> bundle.get(MaterialTypes.OVERWORLD_ORE).get())));
		tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.DEEPSLATE_ORE))
				.map((bundle) -> bundle.get(MaterialTypes.DEEPSLATE_ORE).get())));
		tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK).add(toArray(
				ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.NETHER_ORE)).map((bundle) -> bundle.get(MaterialTypes.NETHER_ORE).get())));

		tag(BlockTags.PLANKS).add(ModBlocks.StaticPlanks.get(), ModBlocks.EnergizedPlanks.get(), ModBlocks.LumumPlanks.get(), ModBlocks.RubberTreePlanks.get());
		tag(BlockTags.LOGS).add(ModBlocks.StaticLog.get(), ModBlocks.EnergizedLog.get(), ModBlocks.LumumLog.get(), ModBlocks.RubberTreeWood.get(), ModBlocks.RubberTreeLog.get(),
				ModBlocks.RubberTreeStrippedLog.get(), ModBlocks.RubberTreeStrippedWood.get());
		tag(ModBlockTags.RUBBER_WOOD_LOGS).add(ModBlocks.RubberTreeWood.get(), ModBlocks.RubberTreeLog.get(), ModBlocks.RubberTreeStrippedLog.get(),
				ModBlocks.RubberTreeStrippedWood.get());
		tag(BlockTags.LOGS_THAT_BURN).add(ModBlocks.StaticLog.get(), ModBlocks.EnergizedLog.get(), ModBlocks.LumumLog.get(), ModBlocks.RubberTreeWood.get(),
				ModBlocks.RubberTreeLog.get(), ModBlocks.RubberTreeStrippedLog.get(), ModBlocks.RubberTreeStrippedWood.get());
		tag(BlockTags.LEAVES).add(ModBlocks.RubberTreeLeaves.get());
		tag(BlockTags.SAPLINGS).add(ModBlocks.RubberTreeSapling.get());
		tag(BlockTags.WOOL).add(ModBlocks.SmeepWool.get());

		tag(ModBlockTags.CABLES).add(toArray(ModBlocks.ItemCables)).add(toArray(ModBlocks.PowerCables)).add(toArray(ModBlocks.InsulatedPowerCables))
				.add(toArray(ModBlocks.IndustrialPowerCables)).add(toArray(ModBlocks.FluidCables)).add(toArray(ModBlocks.CapillaryFluidCables))
				.add(toArray(ModBlocks.IndustrialFluidCables)).add(toArray(ModBlocks.HeatCables)).add(toArray(ModBlocks.RedstoneCables))
				.add(ModBlocks.DigistoreCable.get(), ModBlocks.BasicRedstoneCableNaked.get(), ModBlocks.BundledRedstoneCable.get(), ModBlocks.ScaffoldCable.get());

		tag(ModBlockTags.PUMPS).add(ModBlocks.BasicPump.get(), ModBlocks.AdvancedPump.get(), ModBlocks.StaticPump.get(), ModBlocks.EnergizedPump.get(), ModBlocks.LumumPump.get(),
				ModBlocks.CreativePump.get());

		tag(ModBlockTags.MACHINES).add(ModBlocks.ChargingStation.get(), ModBlocks.PoweredFurnace.get(), ModBlocks.PoweredGrinder.get(), ModBlocks.LumberMill.get(),
				ModBlocks.BasicFarmer.get(), ModBlocks.TreeFarmer.get(), ModBlocks.Fermenter.get(), ModBlocks.Former.get(), ModBlocks.SolidGenerator.get(),
				ModBlocks.FluidGenerator.get(), ModBlocks.Crucible.get(), ModBlocks.Squeezer.get(), ModBlocks.Bottler.get(), ModBlocks.SolderingTable.get(),
				ModBlocks.AutoSolderingTable.get(), ModBlocks.AutoCraftingTable.get(), ModBlocks.FluidInfuser.get(), ModBlocks.Centrifuge.get(), ModBlocks.FusionFurnace.get(),
				ModBlocks.Miner.get(), ModBlocks.ElectricMiner.get(), ModBlocks.Evaporator.get(), ModBlocks.Condenser.get(), ModBlocks.Vulcanizer.get(), ModBlocks.AutoSmith.get(),
				ModBlocks.Carpenter.get(), ModBlocks.Mixer.get(), ModBlocks.Caster.get(), ModBlocks.Tumbler.get(), ModBlocks.Turbine.get(), ModBlocks.Packager.get(),
				ModBlocks.ExperienceHopper.get(), ModBlocks.RustyCauldron.get(), ModBlocks.CleanCauldron.get(), ModBlocks.DirectDropper.get(), ModBlocks.AutomaticPlacer.get(),
				ModBlocks.Enchanter.get(), ModBlocks.RandomItemGenerator.get(), ModBlocks.RandomOreGenerator.get(), ModBlocks.HydroponicFarmer.get(), ModBlocks.HydroponicPod.get(),
				ModBlocks.AlloyFurnace.get(), ModBlocks.RefineryController.get(), ModBlocks.RefineryPowerTap.get(), ModBlocks.RefineryFluidInput.get(),
				ModBlocks.RefineryFluidOutput.get(), ModBlocks.RefineryItemInput.get(), ModBlocks.RefineryHeatVent.get(), ModBlocks.RefineryTower.get(),
				ModBlocks.RefineryBoiler.get(), ModBlocks.Laboratory.get(), ModBlocks.ResearchCheater.get());

		tag(ModBlockTags.BATTERY_BLOCKS).add(ModBlocks.BatteryBasic.get(), ModBlocks.BatteryAdvanced.get(), ModBlocks.BatteryStatic.get(), ModBlocks.BatteryEnergized.get(),
				ModBlocks.BatteryLumum.get(), ModBlocks.BatteryCreative.get());

		tag(ModBlockTags.HEATSINKS).add(ModBlocks.AluminumHeatSink.get(), ModBlocks.CopperHeatSink.get(), ModBlocks.GoldHeatSink.get());

		tag(ModBlockTags.MACHINE_BLOCKS).add(ModBlocks.MachineBlockAluminum.get(), ModBlocks.MachineBlockBasic.get(), ModBlocks.MachineBlockAdvanced.get(),
				ModBlocks.MachineBlockStatic.get(), ModBlocks.MachineBlockEnergized.get(), ModBlocks.MachineBlockLumum.get(), ModBlocks.MachineBlockIndustrial.get());

		tag(ModBlockTags.SOLAR_PANELS).add(ModBlocks.SolarPanelBasic.get(), ModBlocks.SolarPanelAdvanced.get(), ModBlocks.SolarPanelStatic.get(),
				ModBlocks.SolarPanelEnergized.get(), ModBlocks.SolarPanelLumum.get(), ModBlocks.SolarPanelCreative.get());

		tag(ModBlockTags.TANKS).add(ModBlocks.IronTank.get(), ModBlocks.BasicTank.get(), ModBlocks.AdvancedTank.get(), ModBlocks.StaticTank.get(), ModBlocks.EnergizedTank.get(),
				ModBlocks.LumumTank.get(), ModBlocks.CreativeTank.get());

		tag(ModBlockTags.TRANSFORMERS).add(ModBlocks.TransformerBasic.get(), ModBlocks.TransformerAdvanced.get(), ModBlocks.TransformerStatic.get(),
				ModBlocks.TransformerEnergized.get(), ModBlocks.TransformerLumum.get());

		tag(ModBlockTags.POWER_MANAGEMENT).add(ModBlocks.Inverter.get(), ModBlocks.Rectifier.get());

		tag(ModBlockTags.CIRCUIT_BREAKERS).add(toArray(ModBlocks.CircuitBreakers));
		tag(ModBlockTags.RESISTORS).add(toArray(ModBlocks.Resistors));

		tag(ModBlockTags.FARMLAND).add(ModBlocks.StaticFarmland.get(), ModBlocks.EnergizedFarmland.get(), ModBlocks.LumumFarmland.get());
		tag(ModBlockTags.PUMP_TUBE).add(ModBlocks.PumpTube.get());
		tag(ModBlockTags.TILLABLE).addTag(BlockTags.DIRT);
		tag(ModBlockTags.REFINERY_BLOCK).add(ModBlocks.RefineryBoiler.get(), ModBlocks.RefineryController.get(), ModBlocks.RefineryFluidInput.get(),
				ModBlocks.RefineryFluidOutput.get(), ModBlocks.RefineryHeatVent.get(), ModBlocks.RefineryItemInput.get(), ModBlocks.RefineryPowerTap.get(),
				ModBlocks.RefineryTower.get());

		tag(ModBlockTags.CAULDRONS).add(ModBlocks.CleanCauldron.get(), ModBlocks.RustyCauldron.get());
		tag(ModBlockTags.CHESTS).add(ModBlocks.BasicChest.get(), ModBlocks.AdvancedChest.get(), ModBlocks.StaticChest.get(), ModBlocks.EnergizedChest.get(),
				ModBlocks.LumumChest.get());
		tag(ModBlockTags.CONVEYORS).add(toArray(ModBlocks.ConveyorsStraight)).add(toArray(ModBlocks.ConveyorsRampUp)).add(toArray(ModBlocks.ConveyorsRampDown))
				.add(toArray(ModBlocks.ConveyorsSupplier)).add(toArray(ModBlocks.ConveyorsExtractor)).add(toArray(ModBlocks.ConveyorsHopper))
				.add(toArray(ModBlocks.ConveyorsFilteredHopper));

		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.hasGeneratedOre()) {
				TagsProvider.TagAppender<Block> tag = tag(bundle.oreBlockTag());
				if (bundle.has(MaterialTypes.OVERWORLD_ORE)) {
					tag.add(bundle.get(MaterialTypes.OVERWORLD_ORE).get());
				}
				if (bundle.has(MaterialTypes.DEEPSLATE_ORE)) {
					tag.add(bundle.get(MaterialTypes.DEEPSLATE_ORE).get());
				}
				if (bundle.has(MaterialTypes.NETHER_ORE)) {
					tag.add(bundle.get(MaterialTypes.NETHER_ORE).get());
				}
			}

			for (Material<?> material : bundle.getMaterials()) {
				if (material.getMaterialClass() == MaterialClass.BLOCK && material.hasBlockTag()) {
					tag(material.getBlockTag()).add((Block) material.get());
				}
			}
		}

		tag(ModBlockTags.createForgeTag("rubber_wood_logs")).add(ModBlocks.RubberTreeLog.get(), ModBlocks.RubberTreeStrippedLog.get(), ModBlocks.RubberTreeWood.get(),
				ModBlocks.RubberTreeStrippedWood.get());
	}

	@SuppressWarnings("unchecked")
	private Block[] toArray(Map<?, ?> collection) {
		return collection.values().stream().map((regObject) -> ((RegistryObject<Block>) regObject).get()).toList().toArray(Block[]::new);
	}

	public static Block[] toArray(Stream<? extends Block> stream) {
		return stream.toList().toArray(Block[]::new);
	}
}
