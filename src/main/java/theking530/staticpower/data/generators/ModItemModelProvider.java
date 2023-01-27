package theking530.staticpower.data.generators;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.entities.AbstractEntityBuilder;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
	private final Set<Block> customModelBlockItems;

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
		customModelBlockItems = new HashSet<Block>();

		customModelBlockItems.add(ModBlocks.RefineryTower.get());
	}

	@Override
	protected void registerModels() {
		for (AbstractEntityBuilder<?> entity : ModEntities.ENTITIES) {
			if (entity instanceof AbstractSpawnableMobType) {
				spawnEgg(((AbstractSpawnableMobType<?>) entity).getEgg());
			}
		}

		simpleItem(ModItems.DistilleryGrain.get(), "misc/distillery_grain");
		simpleItem(ModItems.RustyIronScrap.get(), "materials/rusty_iron_scrap");

		simpleItem(ModItems.ApplePie.get(), "misc/pie_apple");
		simpleItem(ModItems.StaticPie.get(), "misc/pie_static");
		simpleItem(ModItems.EnergizedPie.get(), "misc/pie_energized");
		simpleItem(ModItems.LumumPie.get(), "misc/pie_lumum");
		simpleItem(ModItems.CookedEeef.get(), "misc/eeef_cooked");
		simpleItem(ModItems.CookedSmutton.get(), "misc/smutton_cooked");
		simpleItem(ModItems.Eather.get(), "misc/eather");
		simpleItem(ModItems.PotatoBread.get(), "misc/bread_potato");
		simpleItem(ModItems.PotatoFlour.get(), "misc/flour_potato");
		simpleItem(ModItems.RawEeef.get(), "misc/eeef_raw");
		simpleItem(ModItems.RawSmutton.get(), "misc/smutton_raw");
		simpleItem(ModItems.WheatFlour.get(), "misc/flour_wheat");

		simpleItem(ModItems.MoldBlade.get(), "molds/mold_blade");
		simpleItem(ModItems.MoldBlank.get(), "molds/mold_blank");
		simpleItem(ModItems.MoldBlock.get(), "molds/mold_block");
		simpleItem(ModItems.MoldDrillBit.get(), "molds/mold_drill_bit");
		simpleItem(ModItems.MoldGear.get(), "molds/mold_gear");
		simpleItem(ModItems.MoldIngot.get(), "molds/mold_ingot");
		simpleItem(ModItems.MoldNugget.get(), "molds/mold_nugget");
		simpleItem(ModItems.MoldPlate.get(), "molds/mold_plate");
		simpleItem(ModItems.MoldRod.get(), "molds/mold_rod");
		simpleItem(ModItems.MoldWire.get(), "molds/mold_wire");

		simpleItem(ModItems.RawAluminum.get(), "materials/raw_ores/raw_aluminum");
		simpleItem(ModItems.RawEnergized.get(), "materials/raw_ores/raw_energized");
		simpleItem(ModItems.RawLead.get(), "materials/raw_ores/raw_lead");
		simpleItem(ModItems.RawLumum.get(), "materials/raw_ores/raw_lumum");
		simpleItem(ModItems.RawMagnesium.get(), "materials/raw_ores/raw_magnesium");
		simpleItem(ModItems.RawPlatinum.get(), "materials/raw_ores/raw_platinum");
		simpleItem(ModItems.RawRustyIron.get(), "materials/raw_ores/raw_rusty_iron");
		simpleItem(ModItems.RawSilver.get(), "materials/raw_ores/raw_silver");
		simpleItem(ModItems.RawStatic.get(), "materials/raw_ores/raw_static");
		simpleItem(ModItems.RawTin.get(), "materials/raw_ores/raw_tin");
		simpleItem(ModItems.RawTungsten.get(), "materials/raw_ores/raw_tungsten");
		simpleItem(ModItems.RawZinc.get(), "materials/raw_ores/raw_zinc");
		simpleItem(ModItems.RawUranium.get(), "materials/raw_ores/raw_uranium");

		simpleItem(ModItems.IngotAluminum.get(), "materials/ingots/ingot_aluminum");
		simpleItem(ModItems.IngotBrass.get(), "materials/ingots/ingot_brass");
		simpleItem(ModItems.IngotBronze.get(), "materials/ingots/ingot_bronze");
		simpleItem(ModItems.IngotEnergized.get(), "materials/ingots/ingot_energized");
		simpleItem(ModItems.IngotInertInfusion.get(), "materials/ingots/ingot_inert_infusion");
		simpleItem(ModItems.IngotLead.get(), "materials/ingots/ingot_lead");
		simpleItem(ModItems.IngotLumum.get(), "materials/ingots/ingot_lumum");
		simpleItem(ModItems.IngotMagnesium.get(), "materials/ingots/ingot_magnesium");
		simpleItem(ModItems.IngotPlatinum.get(), "materials/ingots/ingot_platinum");
		simpleItem(ModItems.IngotRedstoneAlloy.get(), "materials/ingots/ingot_redstone_alloy");
		simpleItem(ModItems.IngotSilver.get(), "materials/ingots/ingot_silver");
		simpleItem(ModItems.IngotStatic.get(), "materials/ingots/ingot_static");
		simpleItem(ModItems.IngotTin.get(), "materials/ingots/ingot_tin");
		simpleItem(ModItems.IngotTungsten.get(), "materials/ingots/ingot_tungsten");
		simpleItem(ModItems.IngotZinc.get(), "materials/ingots/ingot_zinc");
		simpleItem(ModItems.IngotUranium.get(), "materials/ingots/ingot_uranium");

		simpleItem(ModItems.IngotAluminumHeated.get(), "materials/ingots/ingot_aluminum");
		simpleItem(ModItems.IngotBrassHeated.get(), "materials/ingots/ingot_brass");
		simpleItem(ModItems.IngotBronzeHeated.get(), "materials/ingots/ingot_bronze");
		simpleItem(ModItems.IngotEnergizedHeated.get(), "materials/ingots/ingot_energized");
		simpleItem(ModItems.IngotInertInfusionHeated.get(), "materials/ingots/ingot_inert_infusion");
		simpleItem(ModItems.IngotLeadHeated.get(), "materials/ingots/ingot_lead");
		simpleItem(ModItems.IngotLumumHeated.get(), "materials/ingots/ingot_lumum");
		simpleItem(ModItems.IngotMagnesiumHeated.get(), "materials/ingots/ingot_magnesium");
		simpleItem(ModItems.IngotPlatinumHeated.get(), "materials/ingots/ingot_platinum");
		simpleItem(ModItems.IngotRedstoneAlloyHeated.get(), "materials/ingots/ingot_redstone_alloy");
		simpleItem(ModItems.IngotSilverHeated.get(), "materials/ingots/ingot_silver");
		simpleItem(ModItems.IngotStaticHeated.get(), "materials/ingots/ingot_static");
		simpleItem(ModItems.IngotTinHeated.get(), "materials/ingots/ingot_tin");
		simpleItem(ModItems.IngotTungstenHeated.get(), "materials/ingots/ingot_tungsten");
		simpleItem(ModItems.IngotZincHeated.get(), "materials/ingots/ingot_zinc");

		simpleItemVanillaTexture(ModItems.IngotIronHeated.get(), "iron_ingot");
		simpleItemVanillaTexture(ModItems.IngotCopperHeated.get(), "copper_ingot");
		simpleItemVanillaTexture(ModItems.IngotGoldHeated.get(), "gold_ingot");

		simpleItem(ModItems.NuggetAluminum.get(), "materials/nuggets/nugget_aluminum");
		simpleItem(ModItems.NuggetBrass.get(), "materials/nuggets/nugget_brass");
		simpleItem(ModItems.NuggetBronze.get(), "materials/nuggets/nugget_bronze");
		simpleItem(ModItems.NuggetCopper.get(), "materials/nuggets/nugget_copper");
		simpleItem(ModItems.NuggetEnergized.get(), "materials/nuggets/nugget_energized");
		simpleItem(ModItems.NuggetInertInfusion.get(), "materials/nuggets/nugget_inert_infusion");
		simpleItem(ModItems.NuggetLead.get(), "materials/nuggets/nugget_lead");
		simpleItem(ModItems.NuggetLumum.get(), "materials/nuggets/nugget_lumum");
		simpleItem(ModItems.NuggetMagnesium.get(), "materials/nuggets/nugget_magnesium");
		simpleItem(ModItems.NuggetPlatinum.get(), "materials/nuggets/nugget_platinum");
		simpleItem(ModItems.NuggetRedstoneAlloy.get(), "materials/nuggets/nugget_redstone_alloy");
		simpleItem(ModItems.NuggetSilver.get(), "materials/nuggets/nugget_silver");
		simpleItem(ModItems.NuggetStatic.get(), "materials/nuggets/nugget_static");
		simpleItem(ModItems.NuggetTin.get(), "materials/nuggets/nugget_tin");
		simpleItem(ModItems.NuggetTungsten.get(), "materials/nuggets/nugget_tungsten");
		simpleItem(ModItems.NuggetZinc.get(), "materials/nuggets/nugget_zinc");

		simpleItem(ModItems.PlateAluminum.get(), "materials/plates/plate_aluminum");
		simpleItem(ModItems.PlateBrass.get(), "materials/plates/plate_brass");
		simpleItem(ModItems.PlateBronze.get(), "materials/plates/plate_bronze");
		simpleItem(ModItems.PlateCopper.get(), "materials/plates/plate_copper");
		simpleItem(ModItems.PlateEnergized.get(), "materials/plates/plate_energized");
		simpleItem(ModItems.PlateGold.get(), "materials/plates/plate_gold");
		simpleItem(ModItems.PlateInertInfusion.get(), "materials/plates/plate_inert_infusion");
		simpleItem(ModItems.PlateIron.get(), "materials/plates/plate_iron");
		simpleItem(ModItems.PlateLead.get(), "materials/plates/plate_lead");
		simpleItem(ModItems.PlateLumum.get(), "materials/plates/plate_lumum");
		simpleItem(ModItems.PlateMagnesium.get(), "materials/plates/plate_magnesium");
		simpleItem(ModItems.PlatePlatinum.get(), "materials/plates/plate_platinum");
		simpleItem(ModItems.PlateRedstoneAlloy.get(), "materials/plates/plate_redstone_alloy");
		simpleItem(ModItems.PlateSilver.get(), "materials/plates/plate_silver");
		simpleItem(ModItems.PlateStatic.get(), "materials/plates/plate_static");
		simpleItem(ModItems.PlateTin.get(), "materials/plates/plate_tin");
		simpleItem(ModItems.PlateTungsten.get(), "materials/plates/plate_tungsten");
		simpleItem(ModItems.PlateZinc.get(), "materials/plates/plate_zinc");

		simpleItem(ModItems.GearBrass.get(), "materials/gears/gear_brass");
		simpleItem(ModItems.GearBronze.get(), "materials/gears/gear_bronze");
		simpleItem(ModItems.GearCopper.get(), "materials/gears/gear_copper");
		simpleItem(ModItems.GearEnergized.get(), "materials/gears/gear_energized");
		simpleItem(ModItems.GearGold.get(), "materials/gears/gear_gold");
		simpleItem(ModItems.GearInertInfusion.get(), "materials/gears/gear_inert_infusion");
		simpleItem(ModItems.GearIron.get(), "materials/gears/gear_iron");
		simpleItem(ModItems.GearLumum.get(), "materials/gears/gear_lumum");
		simpleItem(ModItems.GearStatic.get(), "materials/gears/gear_static");
		simpleItem(ModItems.GearTin.get(), "materials/gears/gear_tin");

		simpleItem(ModItems.DustAluminum.get(), "materials/dusts/dust_aluminum");
		simpleItem(ModItems.DustBrass.get(), "materials/dusts/dust_brass");
		simpleItem(ModItems.DustBronze.get(), "materials/dusts/dust_bronze");
		simpleItem(ModItems.DustCharcoal.get(), "materials/dusts/dust_charcoal");
		simpleItem(ModItems.DustCoal.get(), "materials/dusts/dust_coal");
		simpleItem(ModItems.DustCopper.get(), "materials/dusts/dust_copper");
		simpleItem(ModItems.DustDiamond.get(), "materials/dusts/dust_diamond");
		simpleItem(ModItems.DustEmerald.get(), "materials/dusts/dust_emerald");
		simpleItem(ModItems.DustEnergized.get(), "materials/dusts/dust_energized");
		simpleItem(ModItems.DustGold.get(), "materials/dusts/dust_gold");
		simpleItem(ModItems.DustInertInfusion.get(), "materials/dusts/dust_inert_infusion");
		simpleItem(ModItems.DustIron.get(), "materials/dusts/dust_iron");
		simpleItem(ModItems.DustLead.get(), "materials/dusts/dust_lead");
		simpleItem(ModItems.DustLumum.get(), "materials/dusts/dust_lumum");
		simpleItem(ModItems.DustMagnesium.get(), "materials/dusts/dust_magnesium");
		simpleItem(ModItems.DustObsidian.get(), "materials/dusts/dust_obsidian");
		simpleItem(ModItems.DustPlatinum.get(), "materials/dusts/dust_platinum");
		simpleItem(ModItems.DustRedstoneAlloy.get(), "materials/dusts/dust_redstone_alloy");
		simpleItem(ModItems.DustRuby.get(), "materials/dusts/dust_ruby");
		simpleItem(ModItems.DustSaltpeter.get(), "materials/dusts/dust_saltpeter");
		simpleItem(ModItems.DustSapphire.get(), "materials/dusts/dust_sapphire");
		simpleItem(ModItems.DustSilver.get(), "materials/dusts/dust_silver");
		simpleItem(ModItems.DustStatic.get(), "materials/dusts/dust_static");
		simpleItem(ModItems.DustSulfur.get(), "materials/dusts/dust_sulfur");
		simpleItem(ModItems.DustTin.get(), "materials/dusts/dust_tin");
		simpleItem(ModItems.DustTungsten.get(), "materials/dusts/dust_tungsten");
		simpleItem(ModItems.DustZinc.get(), "materials/dusts/dust_zinc");
		simpleItem(ModItems.DustUrnaium.get(), "materials/dusts/dust_uranium");

		simpleItem(ModItems.DustCoalSmall.get(), "materials/dusts/dust_coal_small");
		simpleItem(ModItems.DustCharcoalSmall.get(), "materials/dusts/dust_charcoal_small");

		simpleItem(ModItems.ChunksAluminum.get(), "materials/chunks/chunks_aluminum");
		simpleItem(ModItems.ChunksCoal.get(), "materials/chunks/chunks_coal");
		simpleItem(ModItems.ChunksCopper.get(), "materials/chunks/chunks_copper");
		simpleItem(ModItems.ChunksDiamond.get(), "materials/chunks/chunks_diamond");
		simpleItem(ModItems.ChunksEmerald.get(), "materials/chunks/chunks_emerald");
		simpleItem(ModItems.ChunksGold.get(), "materials/chunks/chunks_gold");
		simpleItem(ModItems.ChunksIron.get(), "materials/chunks/chunks_iron");
		simpleItem(ModItems.ChunksLapis.get(), "materials/chunks/chunks_lapis");
		simpleItem(ModItems.ChunksLead.get(), "materials/chunks/chunks_lead");
		simpleItem(ModItems.ChunksMagnesium.get(), "materials/chunks/chunks_magnesium");
		simpleItem(ModItems.ChunksPlatinum.get(), "materials/chunks/chunks_platinum");
		simpleItem(ModItems.ChunksQuartz.get(), "materials/chunks/chunks_quartz");
		simpleItem(ModItems.ChunksRedstone.get(), "materials/chunks/chunks_redstone");
		simpleItem(ModItems.ChunksRuby.get(), "materials/chunks/chunks_ruby");
		simpleItem(ModItems.ChunksSapphire.get(), "materials/chunks/chunks_sapphire");
		simpleItem(ModItems.ChunksSilver.get(), "materials/chunks/chunks_silver");
		simpleItem(ModItems.ChunksTin.get(), "materials/chunks/chunks_tin");
		simpleItem(ModItems.ChunksTungsten.get(), "materials/chunks/chunks_tungsten");
		simpleItem(ModItems.ChunksZinc.get(), "materials/chunks/chunks_zinc");

		simpleItem(ModItems.RodBrass.get(), "materials/rods/rod_brass");
		simpleItem(ModItems.RodEnergized.get(), "materials/rods/rod_energized");
		simpleItem(ModItems.RodInertInfusion.get(), "materials/rods/rod_inert_infusion");
		simpleItem(ModItems.RodLumum.get(), "materials/rods/rod_lumum");
		simpleItem(ModItems.RodStatic.get(), "materials/rods/rod_static");
		simpleItem(ModItems.RodTin.get(), "materials/rods/rod_tin");

		simpleItem(ModItems.GemRuby.get(), "materials/gems/gem_ruby");
		simpleItem(ModItems.GemSapphire.get(), "materials/gems/gem_sapphire");

		simpleItem(ModItems.RawSilicon.get(), "materials/raw_silicon");
		simpleItem(ModItems.Silicon.get(), "materials/silicon");
		simpleItem(ModItems.StaticDopedSilicon.get(), "materials/silicon_doped_static");
		simpleItem(ModItems.EnergizedDopedSilicon.get(), "materials/silicon_doped_energized");
		simpleItem(ModItems.LumumDopedSilicon.get(), "materials/silicon_doped_lumum");
		simpleItem(ModItems.CrystalStatic.get(), "materials/crystal_static");
		simpleItem(ModItems.CrystalEnergized.get(), "materials/crystal_energized");
		simpleItem(ModItems.CrystalLumum.get(), "materials/crystal_lumum");
		simpleItem(ModItems.DustWood.get(), "materials/dusts/dust_wood");
		simpleItem(ModItems.LatexChunk.get(), "materials/latex_chunk");
		simpleItem(ModItems.RubberWoodBark.get(), "materials/rubber_wood_bark");
		simpleItem(ModItems.Slag.get(), "materials/slag");

		simpleItem(ModItems.PortableSmeltingCore.get(), "components/portable_smelting_core");
		simpleItem(ModItems.MemoryChip.get(), "components/memory_chip");
		simpleItem(ModItems.LogicGatePowerSync.get(), "components/logic_gate_power_sync");
		simpleItem(ModItems.InvertedLogicGatePowerSync.get(), "components/inverted_logic_gate_power_sync");
		simpleItem(ModItems.Servo.get(), "components/servo");
		simpleItem(ModItems.Diode.get(), "components/diode");
		simpleItem(ModItems.Transistor.get(), "components/transistor");
		simpleItem(ModItems.InternalClock.get(), "components/internal_clock");
		simpleItem(ModItems.IOPort.get(), "components/io_port");
		simpleItem(ModItems.Motor.get(), "components/motor");
		simpleItem(ModItems.Plug.get(), "components/plug");
		simpleItem(ModItems.DigistoreCore.get(), "components/digistore_core");

		simpleItem(ModItems.BasicCard.get(), "components/card_basic");
		simpleItem(ModItems.AdvancedCard.get(), "components/card_advanced");
		simpleItem(ModItems.StaticCard.get(), "components/card_static");
		simpleItem(ModItems.EnergizedCard.get(), "components/card_energized");
		simpleItem(ModItems.LumumCard.get(), "components/card_lumum");

		lightbulb(ModItems.LightBulbWhite.get(), "white");
		lightbulb(ModItems.LightBulbLightGray.get(), "light_gray");
		lightbulb(ModItems.LightBulbGray.get(), "gray");
		lightbulb(ModItems.LightBulbBlack.get(), "black");
		lightbulb(ModItems.LightBulbBrown.get(), "brown");
		lightbulb(ModItems.LightBulbRed.get(), "red");
		lightbulb(ModItems.LightBulbOrange.get(), "orange");
		lightbulb(ModItems.LightBulbYellow.get(), "yellow");
		lightbulb(ModItems.LightBulbLime.get(), "lime");
		lightbulb(ModItems.LightBulbGreem.get(), "green");
		lightbulb(ModItems.LightBulbCyan.get(), "cyan");
		lightbulb(ModItems.LightBulbLightBlue.get(), "light_blue");
		lightbulb(ModItems.LightBulbBlue.get(), "blue");
		lightbulb(ModItems.LightBulbPurple.get(), "purple");
		lightbulb(ModItems.LightBulbMagenta.get(), "magenta");
		lightbulb(ModItems.LightBulbPink.get(), "pink");

		simpleItem(ModItems.WoodTurbineBlades.get(), "components/turbine_blades_wood");
		simpleItem(ModItems.BasicTurbineBlades.get(), "components/turbine_blades_basic");
		simpleItem(ModItems.AdvancedTurbineBlades.get(), "components/turbine_blades_advanced");
		simpleItem(ModItems.StaticTurbineBlades.get(), "components/turbine_blades_static");
		simpleItem(ModItems.EnergizedTurbineBlades.get(), "components/turbine_blades_energized");
		simpleItem(ModItems.LumumTurbineBlades.get(), "components/turbine_blades_lumum");
		simpleItem(ModItems.CreativeTurbineBlades.get(), "components/turbine_blades_creative");

		simpleItem(ModItems.BasicProcessor.get(), "components/processor_basic");
		simpleItem(ModItems.AdvancedProcessor.get(), "components/processor_advanced");
		simpleItem(ModItems.StaticProcessor.get(), "components/processor_static");
		simpleItem(ModItems.EnergizedProcessor.get(), "components/processor_energized");
		simpleItem(ModItems.LumumProcessor.get(), "components/processor_lumum");

		simpleItem(ModItems.RubberBar.get(), "materials/rubber_bar");
		simpleItem(ModItems.RubberSheet.get(), "materials/rubber_sheet");

		simpleItem(ModItems.WireAluminum.get(), "components/wire_aluminum");
		simpleItem(ModItems.WireCopper.get(), "components/wire_brass");
		simpleItem(ModItems.WireBrass.get(), "components/wire_copper");
		simpleItem(ModItems.WireStatic.get(), "components/wire_static");
		simpleItem(ModItems.WireEnergized.get(), "components/wire_energized");
		simpleItem(ModItems.WireLumum.get(), "components/wire_lumum");
		simpleItem(ModItems.WireGold.get(), "components/wire_gold");

		simpleItem(ModItems.WireInsulatedCopper.get(), "components/wire_insulated_copper");
		simpleItem(ModItems.WireInsulatedBrass.get(), "components/wire_insulated_brass");
		simpleItem(ModItems.WireInsulatedStatic.get(), "components/wire_insulated_static");
		simpleItem(ModItems.WireInsulatedEnergized.get(), "components/wire_insulated_energized");
		simpleItem(ModItems.WireInsulatedLumum.get(), "components/wire_insulated_lumum");

		simpleItem(ModItems.WireCoilCopper.get(), "components/wire_coil_copper");
		simpleItem(ModItems.WireCoilBrass.get(), "components/wire_coil_brass");
		simpleItem(ModItems.WireCoilStatic.get(), "components/wire_coil_static");
		simpleItem(ModItems.WireCoilEnergized.get(), "components/wire_coil_energized");
		simpleItem(ModItems.WireCoilLumum.get(), "components/wire_coil_lumum");

		simpleItem(ModItems.WireCoilInsulatedCopper.get(), "components/wire_coil_insulated_copper");
		simpleItem(ModItems.WireCoilInsulatedBrass.get(), "components/wire_coil_insulated_brass");
		simpleItem(ModItems.WireCoilInsulatedStatic.get(), "components/wire_coil_insulated_static");
		simpleItem(ModItems.WireCoilInsulatedEnergized.get(), "components/wire_coil_insulated_energized");
		simpleItem(ModItems.WireCoilInsulatedLumum.get(), "components/wire_coil_insulated_lumum");

		simpleItem(ModItems.WireCoilDigistore.get(), "components/wire_coil_digistore");

		simpleItem(ModItems.IronDrillBit.get(), "components/drill_bits/drill_bit_iron");
		simpleItem(ModItems.BronzeDrillBit.get(), "components/drill_bits/drill_bit_bronze");
		simpleItem(ModItems.AdvancedDrillBit.get(), "components/drill_bits/drill_bit_advanced");
		simpleItem(ModItems.TungstenDrillBit.get(), "components/drill_bits/drill_bit_tungsten");
		simpleItem(ModItems.StaticDrillBit.get(), "components/drill_bits/drill_bit_static");
		simpleItem(ModItems.EnergizedDrillBit.get(), "components/drill_bits/drill_bit_energized");
		simpleItem(ModItems.LumumDrillBit.get(), "components/drill_bits/drill_bit_lumum");
		simpleItem(ModItems.CreativeDrillBit.get(), "components/drill_bits/drill_bit_creative");

		simpleItem(ModItems.IronBlade.get(), "components/blades/blade_iron");
		simpleItem(ModItems.BronzeBlade.get(), "components/blades/blade_bronze");
		simpleItem(ModItems.AdvancedBlade.get(), "components/blades/blade_advanced");
		simpleItem(ModItems.TungstenBlade.get(), "components/blades/blade_tungsten");
		simpleItem(ModItems.StaticBlade.get(), "components/blades/blade_static");
		simpleItem(ModItems.EnergizedBlade.get(), "components/blades/blade_energized");
		simpleItem(ModItems.LumumBlade.get(), "components/blades/blade_lumum");
		simpleItem(ModItems.CreativeBlade.get(), "components/blades/blade_creative");

		simpleItem(ModItems.IronChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_iron");
		simpleItem(ModItems.BronzeChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_bronze");
		simpleItem(ModItems.AdvancedChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_advanced");
		simpleItem(ModItems.TungstenChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_tungsten");
		simpleItem(ModItems.StaticChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_static");
		simpleItem(ModItems.EnergizedChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_energized");
		simpleItem(ModItems.LumumChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_lumum");
		simpleItem(ModItems.CreativeChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_creative");

		simpleItem(ModItems.IronFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_iron");
		simpleItem(ModItems.BasicFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_basic");
		simpleItem(ModItems.AdvancedFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_advanced");
		simpleItem(ModItems.StaticFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_static");
		simpleItem(ModItems.EnergizedFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_energized");
		simpleItem(ModItems.LumumFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_lumum");
		simpleItem(ModItems.CreativeFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_creative");

		simpleItem(ModItems.BasicUpgradePlate.get(), "components/upgrade_plate_basic");
		simpleItem(ModItems.AdvancedUpgradePlate.get(), "components/upgrade_plate_advanced");
		simpleItem(ModItems.StaticUpgradePlate.get(), "components/upgrade_plate_static");
		simpleItem(ModItems.EnergizedUpgradePlate.get(), "components/upgrade_plate_energized");
		simpleItem(ModItems.LumumUpgradePlate.get(), "components/upgrade_plate_lumum");

		simpleItem(ModItems.BasicPortableBattery.get(), "tools/batteries/portable_battery_basic");
		simpleItem(ModItems.AdvancedPortableBattery.get(), "tools/batteries/portable_battery_advanced");
		simpleItem(ModItems.StaticPortableBattery.get(), "tools/batteries/portable_battery_static");
		simpleItem(ModItems.EnergizedPortableBattery.get(), "tools/batteries/portable_battery_energized");
		simpleItem(ModItems.LumumPortableBattery.get(), "tools/batteries/portable_battery_lumum");
		simpleItem(ModItems.CreativePortableBattery.get(), "tools/batteries/portable_battery_creative");

		simpleItem(ModItems.Backpack.get(), "backpacks/generic");
		simpleItem(ModItems.BuildersBackPack.get(), "backpacks/builder");
		simpleItem(ModItems.DiggersBackPack.get(), "backpacks/digger");
		simpleItem(ModItems.HuntersPack.get(), "backpacks/hunter");
		simpleItem(ModItems.LumberjacksBackPack.get(), "backpacks/lumberjack");
		simpleItem(ModItems.MinersBackpack.get(), "backpacks/miner");
		simpleItem(ModItems.FarmersBackpack.get(), "backpacks/farmer");
		simpleItem(ModItems.EngineersBackpack.get(), "backpacks/engineer");
		simpleItem(ModItems.ToolsBackpack.get(), "backpacks/tool");

		simpleItem(ModItems.BasicBatteryPack.get(), "tools/batteries/battery_pack_basic");
		simpleItem(ModItems.AdvancedBatteryPack.get(), "tools/batteries/battery_pack_advanced");
		simpleItem(ModItems.StaticBatteryPack.get(), "tools/batteries/battery_pack_static");
		simpleItem(ModItems.EnergizedBatteryPack.get(), "tools/batteries/battery_pack_energized");
		simpleItem(ModItems.LumumBatteryPack.get(), "tools/batteries/battery_pack_lumum");
		simpleItem(ModItems.CreativeBatteryPack.get(), "tools/batteries/battery_pack_creative");

		simpleItem(ModItems.StaticSeeds.get(), "plants/seed_static");
		simpleItem(ModItems.EnergizedSeeds.get(), "plants/seed_energized");
		simpleItem(ModItems.LumumSeeds.get(), "plants/seed_lumum");

		simpleItem(ModItems.StaticFruit.get(), "plants/fruit_static");
		simpleItem(ModItems.EnergizedFruit.get(), "plants/fruit_energized");
		simpleItem(ModItems.LumumFruit.get(), "plants/fruit_lumum");
		simpleItem(ModItems.DepletedFruit.get(), "plants/fruit_depleted");

		simpleItem(ModItems.IronMetalHammer.get(), "tools/hammer_iron");
		simpleItem(ModItems.CopperMetalHammer.get(), "tools/hammer_copper");
		simpleItem(ModItems.TinMetalHammer.get(), "tools/hammer_tin");
		simpleItem(ModItems.ZincMetalHammer.get(), "tools/hammer_zinc");
		simpleItem(ModItems.BronzeMetalHammer.get(), "tools/hammer_bronze");
		simpleItem(ModItems.TungstenMetalHammer.get(), "tools/hammer_tungsten");
		simpleItem(ModItems.CreativeMetalHammer.get(), "tools/hammer_creative");

		simpleItem(ModItems.IronWireCutters.get(), "tools/wire_cutters_iron");
		simpleItem(ModItems.ZincWireCutters.get(), "tools/wire_cutters_zinc");
		simpleItem(ModItems.BronzeWireCutters.get(), "tools/wire_cutters_bronze");
		simpleItem(ModItems.TungstenWireCutters.get(), "tools/wire_cutters_tungsten");
		simpleItem(ModItems.CreativeWireCutters.get(), "tools/wire_cutters_creative");

		simpleItem(ModItems.SolderingIron.get(), "tools/soldering_iron");
		simpleItem(ModItems.ElectringSolderingIron.get(), "tools/soldering_iron_electric");

		simpleItem(ModItems.BasicMiningDrill.get(), "tools/mining_drill_basic");
		simpleItem(ModItems.AdvancedMiningDrill.get(), "tools/mining_drill_advanced");
		simpleItem(ModItems.StaticMiningDrill.get(), "tools/mining_drill_static");
		simpleItem(ModItems.EnergizedMiningDrill.get(), "tools/mining_drill_energized");
		simpleItem(ModItems.LumumMiningDrill.get(), "tools/mining_drill_lumum");

		simpleItem(ModItems.BasicChainsaw.get(), "tools/chainsaw_basic");
		simpleItem(ModItems.AdvancedChainsaw.get(), "tools/chainsaw_advanced");
		simpleItem(ModItems.StaticChainsaw.get(), "tools/chainsaw_static");
		simpleItem(ModItems.EnergizedChainsaw.get(), "tools/chainsaw_energized");
		simpleItem(ModItems.LumumChainsaw.get(), "tools/chainsaw_lumum");

		handheldItem(ModItems.Wrench.get(), "tools/wrench");
		handheldItem(ModItems.StaticWrench.get(), "tools/wrench_static");
		handheldItem(ModItems.EnergizedWrench.get(), "tools/wrench_energized");
		handheldItem(ModItems.LumumWrench.get(), "tools/wrench_lumum");

		simpleItem(ModItems.Thermometer.get(), "tools/thermometer");

		simpleItem(ModItems.WeakMagnet.get(), "tools/magnet_weak");
		simpleItem(ModItems.BasicMagnet.get(), "tools/magnet_basic");
		simpleItem(ModItems.AdvancedMagnet.get(), "tools/magnet_advanced");
		simpleItem(ModItems.StaticMagnet.get(), "tools/magnet_static");
		simpleItem(ModItems.EnergizedMagnet.get(), "tools/magnet_energized");
		simpleItem(ModItems.LumumMagnet.get(), "tools/magnet_lumum");

		simpleItem(ModItems.CableNetworkAnalyzer.get(), "tools/cable_network_analyzer");
		simpleItem(ModItems.Multimeter.get(), "tools/multimeter");

		simpleItem(ModItems.IronCoverSaw.get(), "tools/saw_iron");
		simpleItem(ModItems.TungstenCoverSaw.get(), "tools/saw_tungsten");
		simpleItem(ModItems.DiamondCoverSaw.get(), "tools/saw_diamond");
		simpleItem(ModItems.RubyCoverSaw.get(), "tools/saw_ruby");
		simpleItem(ModItems.SapphireCoverSaw.get(), "tools/saw_sapphire");

		simpleItem(ModItems.BasicFilter.get(), "filters/filter_item_basic");
		simpleItem(ModItems.AdvancedFilter.get(), "filters/filter_item_advanced");
		simpleItem(ModItems.StaticFilter.get(), "filters/filter_item_static");
		simpleItem(ModItems.EnergizedFilter.get(), "filters/filter_item_energized");
		simpleItem(ModItems.LumumFilter.get(), "filters/filter_item_lumum");

		regularSizeAttachment(ModItems.BasicExtractorAttachment.get(), "cable_basic_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.AdvancedExtractorAttachment.get(), "cable_advanced_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.StaticExtractorAttachment.get(), "cable_static_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.EnergizedExtractorAttachment.get(), "cable_energized_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.LumumExtractorAttachment.get(), "cable_lumum_attachment", "cable_attachment_extract_off");

		regularSizeAttachment(ModItems.BasicFilterAttachment.get(), "cable_basic_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.AdvancedFilterAttachment.get(), "cable_advanced_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.StaticFilterAttachment.get(), "cable_static_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.EnergizedFilterAttachment.get(), "cable_energized_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.LumumFilterAttachment.get(), "cable_lumum_attachment", "cable_attachment_filter");

		regularSizeAttachment(ModItems.BasicRetrieverAttachment.get(), "cable_basic_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.AdvancedRetrieverAttachment.get(), "cable_advanced_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.StaticRetrieverAttachment.get(), "cable_static_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.EnergizedRetrieverAttachment.get(), "cable_energized_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.LumumRetrieverAttachment.get(), "cable_lumum_attachment", "cable_attachment_retriever");

		digistoreAttachment(ModItems.ExporterAttachment.get(), "cable_digistore_exporter_attachment");
		digistoreAttachment(ModItems.ImporterAttachment.get(), "cable_digistore_importer_attachment");
		digistoreAttachment(ModItems.IOBusAttachment.get(), "cable_digistore_io_bus_attachment");
		digistoreAttachment(ModItems.RegulatorAttachment.get(), "cable_digistore_regulator_attachment");
		digistoreAttachment(ModItems.DigistoreCraftingInterfaceAttachment.get(), "cable_digistore_crafting_interface_attachment");

		digistoreFullBlockAttachment(ModItems.DigistoreTerminalAttachment.get(), "cable_digistore_base_full_attachment", "digistore_terminal");
		digistoreFullBlockAttachment(ModItems.DigistoreCraftingTerminalAttachment.get(), "cable_digistore_base_full_attachment", "digistore_crafting_terminal");
		digistoreFullBlockAttachment(ModItems.DigistorePatternEncoderAttachment.get(), "cable_digistore_base_full_attachment", "digistore_pattern_encoder");
		digistoreFullBlockAttachment(ModItems.DigistoreScreenAttachment.get(), "cable_digistore_base_full_attachment", "digistore_screen");
		digistoreFullBlockAttachment(ModItems.DigistoreLightAttachment.get(), "cable_digistore_base_full_attachment", "digistore_light");

		fromExistingModel(ModItems.SprinklerAttachment.get(), "item/sprinkler");
		fromExistingModel(ModItems.DrainAttachment.get(), "item/drain");
		fromExistingModel(ModBlocks.RefineryTower.get().asItem(), "block/refinery_tower/full");

		simpleItem(ModItems.PatternCard.get(), "digistore/digistore_pattern_card_encoded");
		namedGeneratedModel("digistore_pattern_card_encoded", "digistore/digistore_pattern_card_encoded");
		
		simpleItem(ModItems.PatternCard.get(), "digistore/digistore_pattern_card_empty");
		simpleItem(ModItems.DigistoreWirelessTerminal.get(), "tools/digistore_wireless_terminal");

		withExistingParent(name(ModItems.CableCover.get()), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/cable_cover"));

		simpleItem(ModItems.MilkBottle.get(), "bottles/bottle_milk");
		simpleItem(ModItems.AppleJuiceBottle.get(), "bottles/bottle_juice_apple");
		simpleItem(ModItems.CarrotJuiceBottle.get(), "bottles/bottle_juice_carrot");
		simpleItem(ModItems.PumpkinJuiceBottle.get(), "bottles/bottle_juice_pumpkin");
		simpleItem(ModItems.MelonJuiceBottle.get(), "bottles/bottle_juice_melon");
		simpleItem(ModItems.BeetJuiceBottle.get(), "bottles/bottle_juice_beetroot");
		simpleItem(ModItems.BerryJuiceBottle.get(), "bottles/bottle_juice_berry");

		simpleItem(ModItems.ResearchTier1.get(), "research/research_tier_1");
		simpleItem(ModItems.ResearchTier2.get(), "research/research_tier_2");
		simpleItem(ModItems.ResearchTier3.get(), "research/research_tier_3");
		simpleItem(ModItems.ResearchTier4.get(), "research/research_tier_4");
		simpleItem(ModItems.ResearchTier5.get(), "research/research_tier_5");
		simpleItem(ModItems.ResearchTier6.get(), "research/research_tier_6");
		simpleItem(ModItems.ResearchTier7.get(), "research/research_tier_7");

		simpleItem(ModItems.BasicDigistoreCard.get(), "digistore/digistore_card_basic");
		simpleItem(ModItems.AdvancedDigistoreCard.get(), "digistore/digistore_card_advanced");
		simpleItem(ModItems.StaticDigistoreCard.get(), "digistore/digistore_card_static");
		simpleItem(ModItems.EnergizedDigistoreCard.get(), "digistore/digistore_card_energized");
		simpleItem(ModItems.LumumDigistoreCard.get(), "digistore/digistore_card_lumum");
		simpleItem(ModItems.CreativeDigistoreCard.get(), "digistore/digistore_card_creative");

		simpleItem(ModItems.BasicStackedDigistoreCard.get(), "digistore/digistore_card_stacked_basic");
		simpleItem(ModItems.AdvancedStackedDigistoreCard.get(), "digistore/digistore_card_stacked_advanced");
		simpleItem(ModItems.StaticStackedDigistoreCard.get(), "digistore/digistore_card_stacked_static");
		simpleItem(ModItems.EnergizedStackedDigistoreCard.get(), "digistore/digistore_card_stacked_energized");
		simpleItem(ModItems.LumumStackedDigistoreCard.get(), "digistore/digistore_card_stacked_lumum");
		simpleItem(ModItems.CreativeStackedDigistoreCard.get(), "digistore/digistore_card_stacked_creative");

		simpleItem(ModItems.BasicSingularDigistoreCard.get(), "digistore/digistore_card_singular_basic");
		simpleItem(ModItems.AdvancedSingularDigistoreCard.get(), "digistore/digistore_card_singular_advanced");
		simpleItem(ModItems.StaticSingularDigistoreCard.get(), "digistore/digistore_card_singular_static");
		simpleItem(ModItems.EnergizedSingularDigistoreCard.get(), "digistore/digistore_card_singular_energized");
		simpleItem(ModItems.LumumSingularDigistoreCard.get(), "digistore/digistore_card_singular_lumum");
		simpleItem(ModItems.CreativeSingularDigistoreCard.get(), "digistore/digistore_card_singular_creative");

		simpleItem(ModItems.TeleportUpgrade.get(), "upgrades/upgrade_teleport");
		simpleItem(ModItems.ExperienceVacuumUpgrade.get(), "upgrades/upgrade_experience_vacuum");
		simpleItem(ModItems.VoidUpgrade.get(), "upgrades/upgrade_void");
		simpleItem(ModItems.StackUpgrade.get(), "upgrades/upgrade_stack");
		simpleItem(ModItems.AcceleratorUpgrade.get(), "upgrades/upgrade_accelerator");
		simpleItem(ModItems.CraftingUpgrade.get(), "upgrades/upgrade_crafting");

		upgrade(ModItems.TransformerUpgradeMV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_advanced");
		upgrade(ModItems.TransformerUpgradeHV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_static");
		upgrade(ModItems.TransformerUpgradeVHV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_energized");
		upgrade(ModItems.TransformerUpgradeEV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_basic");
		upgrade(ModItems.StaticPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_energized");
		upgrade(ModItems.LumumPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_basic");
		upgrade(ModItems.StaticOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_energized");
		upgrade(ModItems.LumumOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_basic");
		upgrade(ModItems.StaticSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_energized");
		upgrade(ModItems.LumumSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_basic");
		upgrade(ModItems.StaticRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_energized");
		upgrade(ModItems.LumumRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_basic");
		upgrade(ModItems.StaticTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_energized");
		upgrade(ModItems.LumumTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_basic");
		upgrade(ModItems.StaticCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_energized");
		upgrade(ModItems.LumumCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_basic");
		upgrade(ModItems.StaticHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_energized");
		upgrade(ModItems.LumumHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_basic");
		upgrade(ModItems.StaticHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_energized");
		upgrade(ModItems.LumumHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_lumum");

		cable1Inventory(ModBlocks.BasicRedstoneCableNaked.get(), "redstone/cable_basic_redstone_naked");
		cable2Inventory(ModBlocks.BasicRedstoneCableBlack.get(), "redstone/cable_basic_redstone_black");
		cable2Inventory(ModBlocks.BasicRedstoneCableDarkBlue.get(), "redstone/cable_basic_redstone_dark_blue");
		cable2Inventory(ModBlocks.BasicRedstoneCableDarkGreen.get(), "redstone/cable_basic_redstone_dark_green");
		cable2Inventory(ModBlocks.BasicRedstoneCableDarkAqua.get(), "redstone/cable_basic_redstone_dark_aqua");
		cable2Inventory(ModBlocks.BasicRedstoneCableDarkRed.get(), "redstone/cable_basic_redstone_dark_red");
		cable2Inventory(ModBlocks.BasicRedstoneCableDarkPurple.get(), "redstone/cable_basic_redstone_dark_purple");
		cable2Inventory(ModBlocks.BasicRedstoneCableGold.get(), "redstone/cable_basic_redstone_gold");
		cable2Inventory(ModBlocks.BasicRedstoneCableGray.get(), "redstone/cable_basic_redstone_gray");
		cable2Inventory(ModBlocks.BasicRedstoneCableDarkGray.get(), "redstone/cable_basic_redstone_dark_gray");
		cable2Inventory(ModBlocks.BasicRedstoneCableBlue.get(), "redstone/cable_basic_redstone_blue");
		cable2Inventory(ModBlocks.BasicRedstoneCableGreen.get(), "redstone/cable_basic_redstone_green");
		cable2Inventory(ModBlocks.BasicRedstoneCableAqua.get(), "redstone/cable_basic_redstone_aqua");
		cable2Inventory(ModBlocks.BasicRedstoneCableRed.get(), "redstone/cable_basic_redstone_red");
		cable2Inventory(ModBlocks.BasicRedstoneCableLightPurple.get(), "redstone/cable_basic_redstone_light_purple");
		cable2Inventory(ModBlocks.BasicRedstoneCableYellow.get(), "redstone/cable_basic_redstone_yellow");
		cable2Inventory(ModBlocks.BasicRedstoneCableWhite.get(), "redstone/cable_basic_redstone_white");
		cable5Inventory(ModBlocks.BundledRedstoneCable.get(), "redstone/cable_bundled_redstone");

		cable3Inventory(ModBlocks.DigistoreWire.get(), "cable_digistore");
		cable5Inventory(ModBlocks.ScaffoldCable.get(), "cable_scaffold");

		cable5Inventory(ModBlocks.AluminumHeatCable.get(), "cable_aluminum_heat");
		cable5Inventory(ModBlocks.CopperHeatCable.get(), "cable_copper_heat");
		cable5Inventory(ModBlocks.GoldHeatCable.get(), "cable_gold_heat");

		cable5Inventory(ModBlocks.PowerCableBasic.get(), "cable_power_basic");
		cable5Inventory(ModBlocks.PowerCableAdvanced.get(), "cable_power_advanced");
		cable5Inventory(ModBlocks.PowerCableStatic.get(), "cable_power_static");
		cable5Inventory(ModBlocks.PowerCableEnergized.get(), "cable_power_energized");
		cable5Inventory(ModBlocks.PowerCableLumum.get(), "cable_power_lumum");
		cable5Inventory(ModBlocks.PowerCableCreative.get(), "cable_power_creative");

		cable7Inventory(ModBlocks.IndustrialPowerCableBasic.get(), "cable_industrial_power_basic");
		cable7Inventory(ModBlocks.IndustrialPowerCableAdvanced.get(), "cable_industrial_power_advanced");
		cable7Inventory(ModBlocks.IndustrialPowerCableStatic.get(), "cable_industrial_power_static");
		cable7Inventory(ModBlocks.IndustrialPowerCableEnergized.get(), "cable_industrial_power_energized");
		cable7Inventory(ModBlocks.IndustrialPowerCableLumum.get(), "cable_industrial_power_lumum");
		cable7Inventory(ModBlocks.IndustrialPowerCableCreative.get(), "cable_industrial_power_creative");

		cable5Inventory(ModBlocks.InsulatedPowerCableBasic.get(), "cable_power_basic_insulated");
		cable5Inventory(ModBlocks.InsulatedPowerCableAdvanced.get(), "cable_power_advanced_insulated");
		cable5Inventory(ModBlocks.InsulatedPowerCableStatic.get(), "cable_power_static_insulated");
		cable5Inventory(ModBlocks.InsulatedPowerCableEnergized.get(), "cable_power_energized_insulated");
		cable5Inventory(ModBlocks.InsulatedPowerCableLumum.get(), "cable_power_lumum_insulated");
		cable5Inventory(ModBlocks.InsulatedPowerCableCreative.get(), "cable_power_creative_insulated");

		cable5Inventory(ModBlocks.ItemCableBasic.get(), "cable_item_basic");
		cable5Inventory(ModBlocks.ItemCableAdvanced.get(), "cable_item_advanced");
		cable5Inventory(ModBlocks.ItemCableStatic.get(), "cable_item_static");
		cable5Inventory(ModBlocks.ItemCableEnergized.get(), "cable_item_energized");
		cable5Inventory(ModBlocks.ItemCableLumum.get(), "cable_item_lumum");
		cable5Inventory(ModBlocks.ItemCableCreative.get(), "cable_item_creative");

		cable5Inventory(ModBlocks.FluidCableBasic.get(), "cable_fluid_basic");
		cable5Inventory(ModBlocks.FluidCableAdvanced.get(), "cable_fluid_advanced");
		cable5Inventory(ModBlocks.FluidCableStatic.get(), "cable_fluid_static");
		cable5Inventory(ModBlocks.FluidCableEnergized.get(), "cable_fluid_energized");
		cable5Inventory(ModBlocks.FluidCableLumum.get(), "cable_fluid_lumum");
		cable5Inventory(ModBlocks.FluidCableCreative.get(), "cable_fluid_creative");

		cable7Inventory(ModBlocks.IndustrialFluidCableBasic.get(), "cable_industrial_fluid_basic");
		cable7Inventory(ModBlocks.IndustrialFluidCableAdvanced.get(), "cable_industrial_fluid_advanced");
		cable7Inventory(ModBlocks.IndustrialFluidCableStatic.get(), "cable_industrial_fluid_static");
		cable7Inventory(ModBlocks.IndustrialFluidCableEnergized.get(), "cable_industrial_fluid_energized");
		cable7Inventory(ModBlocks.IndustrialFluidCableLumum.get(), "cable_industrial_fluid_lumum");
		cable7Inventory(ModBlocks.IndustrialFluidCableCreative.get(), "cable_industrial_fluid_creative");

		for (RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries()) {
			if (customModelBlockItems.contains(block.get())) {
				continue;
			}
			if (block.get().asItem() == Items.AIR) {
				continue;
			}
			basicItemBlock(block.get());
		}

		simpleItem(ModItems.StaticSeeds.get(), "plants/seed_static");
		simpleItem(ModItems.EnergizedSeeds.get(), "plants/seed_energized");
		simpleItem(ModItems.LumumSeeds.get(), "plants/seed_lumum");
	}

	private ItemModelBuilder lightbulb(Item item, String color) {
		ResourceLocation bulbGlass = new ResourceLocation(StaticPower.MOD_ID, "items/light_bulb/" + color + "_bulb");
		return withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/light_bulb")).texture("glass", bulbGlass);
	}

	private ItemModelBuilder spawnEgg(Item item) {
		return withExistingParent(name(item), new ResourceLocation("item/template_spawn_egg"));
	}

	private ItemModelBuilder namedGeneratedModel(String name, String texturePath) {
		return withExistingParent(name, new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	private ItemModelBuilder simpleItem(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	private ItemModelBuilder handheldItem(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/handheld")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	public ItemModelBuilder basicItemBlock(Block block) {
		ResourceLocation blockModelLocation = new ResourceLocation(StaticPower.MOD_ID, "block/" + block.asItem());
		ItemModelBuilder builder = withExistingParent(name(block.asItem()), blockModelLocation);
		return builder;
	}

	private ItemModelBuilder upgrade(Item item, String upgradeTexturePath, String plateTexturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + upgradeTexturePath))
				.texture("layer1", new ResourceLocation(StaticPower.MOD_ID, "items/upgrades/" + plateTexturePath));
	}

	private ItemModelBuilder simpleItemVanillaTexture(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation("item/" + texturePath));
	}

	private ItemModelBuilder regularSizeAttachment(Item item, String baseTexturePath, String overlayTexturePath) {
		return withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cables/cable_5_attachment"))
				.texture("cable_texture", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + baseTexturePath))
				.texture("overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + overlayTexturePath)).guiLight(GuiLight.FRONT);
	}

	private void digistoreAttachment(Item item, String texturePath) {
		withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/digistore_basic_attachment"))
				.texture("texture", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + texturePath)).guiLight(GuiLight.FRONT);
	}

	private void digistoreFullBlockAttachment(Item item, String texturePath, String overlayTexturePath) {
		withExistingParent(name(item) + "_off", new ResourceLocation(StaticPower.MOD_ID, "item/base_models/digistore_large_attachment"))
				.texture("base", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + texturePath))
				.texture("overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + overlayTexturePath)).guiLight(GuiLight.FRONT);

		withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/digistore_large_attachment"))
				.texture("base", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + texturePath))
				.texture("overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + overlayTexturePath + "_on")).guiLight(GuiLight.FRONT);
	}

	private ItemModelBuilder cable7Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_7_inventory", texturePath);
	}

	private ItemModelBuilder cable5Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_5_inventory", texturePath);
	}

	private ItemModelBuilder cable3Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_3_inventory", texturePath);
	}

	@SuppressWarnings("unused")
	private ItemModelBuilder cable2Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_2_inventory", texturePath);
	}

	@SuppressWarnings("unused")
	private ItemModelBuilder cable1Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_1_inventory", texturePath);
	}

	private ItemModelBuilder cableInventory(Block block, String modelPath, String texturePath) {
		Item item = block.asItem();
		customModelBlockItems.add(block);
		return withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/" + modelPath)).texture("cable_texture",
				new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/" + texturePath));
	}

	private void fromExistingModel(Item item, String modelPath) {
		ModelFile file = getExistingFile(new ResourceLocation(StaticPower.MOD_ID, modelPath));
		getBuilder(key(item).getPath()).parent(file);
	}

	private String name(Item item) {
		return key(item).getPath();
	}

	private ResourceLocation key(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}
}
