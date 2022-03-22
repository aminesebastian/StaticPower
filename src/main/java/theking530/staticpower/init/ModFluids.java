package theking530.staticpower.init;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.fluid.StaticPowerFluidBundle.StaticPowerFluidBuilder;

public class ModFluids {
	public static final Set<StaticPowerFluidBundle> FLUID_BUNDLES = new LinkedHashSet<>();
	public static StaticPowerFluidBundle StaticFluid;
	public static StaticPowerFluidBundle EnergizedFluid;
	public static StaticPowerFluidBundle LumumFluid;

	public static StaticPowerFluidBundle Ethanol;
	public static StaticPowerFluidBundle Mash;
	public static StaticPowerFluidBundle EvaporatedMash;
	public static StaticPowerFluidBundle LiquidExperience;
	public static StaticPowerFluidBundle Steam;
	public static StaticPowerFluidBundle TreeOil;
	public static StaticPowerFluidBundle TreeSap;
	public static StaticPowerFluidBundle InfernalTreeSap;
	public static StaticPowerFluidBundle SeedOil;

	public static StaticPowerFluidBundle Milk;

	public static StaticPowerFluidBundle AppleJuice;
	public static StaticPowerFluidBundle BerryJuice;
	public static StaticPowerFluidBundle PumpkinJuice;
	public static StaticPowerFluidBundle CarrotJuice;
	public static StaticPowerFluidBundle WatermelonJuice;
	public static StaticPowerFluidBundle BeetJuice;
	public static StaticPowerFluidBundle Fertilizer;
	public static StaticPowerFluidBundle Honey;
	public static StaticPowerFluidBundle Latex;

	public static Map<MinecraftColor, StaticPowerFluidBundle> ColoredConrete;
	public static Map<MinecraftColor, StaticPowerFluidBundle> Dyes;

	public static StaticPowerFluidBundle MoltenIron;
	public static StaticPowerFluidBundle MoltenGold;
	public static StaticPowerFluidBundle MoltenCopper;
	public static StaticPowerFluidBundle MoltenTin;
	public static StaticPowerFluidBundle MoltenZinc;
	public static StaticPowerFluidBundle MoltenMagnesium;
	public static StaticPowerFluidBundle MoltenTungsten;
	public static StaticPowerFluidBundle MoltenSilver;
	public static StaticPowerFluidBundle MoltenLead;
	public static StaticPowerFluidBundle MoltenAluminum;
	public static StaticPowerFluidBundle MoltenPlatinum;
	public static StaticPowerFluidBundle MoltenBrass;
	public static StaticPowerFluidBundle MoltenBronze;

	public static StaticPowerFluidBundle CrudeOil;
	public static StaticPowerFluidBundle LightOil;
	public static StaticPowerFluidBundle HeavyOil;
	public static StaticPowerFluidBundle Fuel;

	public static void init() {
		registerFluidBundle(StaticFluid = new StaticPowerFluidBuilder("fluid_static").addAutoBucket().addAttributes(builder -> {
			builder.luminosity(15);
		}).build());
		registerFluidBundle(EnergizedFluid = new StaticPowerFluidBuilder("fluid_energized").addAutoBucket().addAttributes(builder -> {
			builder.luminosity(15);
		}).build());
		registerFluidBundle(LumumFluid = new StaticPowerFluidBuilder("fluid_lumum").addAutoBucket().addAttributes(builder -> {
			builder.luminosity(15);
		}).build());

		registerFluidBundle(Ethanol = new StaticPowerFluidBuilder("ethanol").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500);
		}).build());

		registerFluidBundle(Mash = new StaticPowerFluidBuilder("mash").addAttributes(builder -> {
			builder.viscosity(2000);
		}).addAutoBucket().build());

		registerFluidBundle(EvaporatedMash = new StaticPowerFluidBuilder("evaporated_mash").addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-500).viscosity(500);
		}).build());

		registerFluidBundle(LiquidExperience = new StaticPowerFluidBuilder("experience").addAutoBucket().build());

		registerFluidBundle(Steam = new StaticPowerFluidBuilder("steam").addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-500).viscosity(500).color(new Color(255f, 255f, 255f, 128f).encodeInInteger());
		}).build());

		registerFluidBundle(SeedOil = new StaticPowerFluidBuilder("oil_seed").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500);
		}).build());
		registerFluidBundle(TreeOil = new StaticPowerFluidBuilder("oil_tree").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500);
		}).build());
		registerFluidBundle(TreeSap = new StaticPowerFluidBuilder("tree_sap").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(2000);
		}).build());
		registerFluidBundle(InfernalTreeSap = new StaticPowerFluidBuilder("infernal_tree_sap").addAttributes(builder -> {
			builder.viscosity(2000);
		}).addAutoBucket().build());

		registerFluidBundle(Milk = new StaticPowerFluidBuilder("milk").addBucketSupplier(() -> Items.MILK_BUCKET).build());

		registerFluidBundle(AppleJuice = new StaticPowerFluidBuilder("juice_apple").addAutoBucket().build());
		registerFluidBundle(BerryJuice = new StaticPowerFluidBuilder("juice_berry").addAutoBucket().build());
		registerFluidBundle(PumpkinJuice = new StaticPowerFluidBuilder("juice_pumpkin").addAutoBucket().build());
		registerFluidBundle(CarrotJuice = new StaticPowerFluidBuilder("juice_carrot").addAutoBucket().build());
		registerFluidBundle(WatermelonJuice = new StaticPowerFluidBuilder("juice_melon").addAutoBucket().build());
		registerFluidBundle(BeetJuice = new StaticPowerFluidBuilder("juice_beetroot").addAutoBucket().build());
		registerFluidBundle(Fertilizer = new StaticPowerFluidBuilder("fertilizer").addAutoBucket().build());

		registerFluidBundle(Honey = new StaticPowerFluidBuilder("honey").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(2000).density(64).sound(SoundEvents.HONEY_BLOCK_STEP);
		}).build());

		// Create all colors of concrete.
		ColoredConrete = new HashMap<>();
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle;
			registerFluidBundle(bundle = new StaticPowerFluidBuilder("concrete_" + color.getId()).setTextureName("concrete").addAutoBucket(true, StaticPowerSprites.CONCRETE_BUCKET_FLUID_MASK)
					.addAttributes(builder -> {
						builder.viscosity(2500).density(64).sound(SoundEvents.HONEY_BLOCK_STEP).color(color.getColor().fromFloatToEightBit().encodeInInteger());
					}).build());
			ColoredConrete.put(color, bundle);
		}

		// Create all colors of dye.
		Dyes = new HashMap<>();
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle;
			registerFluidBundle(
					bundle = new StaticPowerFluidBuilder("dye_" + color.getId()).setTextureName("dye").addAutoBucket(true, StaticPowerSprites.DYE_BUCKET_FLUID_MASK).addAttributes(builder -> {
						builder.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).color(color.getColor().fromFloatToEightBit().encodeInInteger());
					}).build());
			Dyes.put(color, bundle);
		}

		registerFluidBundle(Latex = new StaticPowerFluidBuilder("latex").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).sound(SoundEvents.HONEY_BLOCK_STEP);
		}).build());

		registerFluidBundle(MoltenIron = new StaticPowerFluidBuilder("molten_iron").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(2800).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenGold = new StaticPowerFluidBuilder("molten_gold").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(1064).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenCopper = new StaticPowerFluidBuilder("molten_copper").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(1084).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenTin = new StaticPowerFluidBuilder("molten_tin").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(450).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenZinc = new StaticPowerFluidBuilder("molten_zinc").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(420).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenMagnesium = new StaticPowerFluidBuilder("molten_magnesium").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1250).density(32).temperature(1202).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenTungsten = new StaticPowerFluidBuilder("molten_tungsten").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1000).density(32).temperature(3400).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenSilver = new StaticPowerFluidBuilder("molten_silver").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1000).density(32).temperature(961).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenLead = new StaticPowerFluidBuilder("molten_lead").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(750).density(32).temperature(328).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenAluminum = new StaticPowerFluidBuilder("molten_aluminum").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500).density(32).temperature(660).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenPlatinum = new StaticPowerFluidBuilder("molten_platinum").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(1770).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenBrass = new StaticPowerFluidBuilder("molten_brass").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(750).density(32).temperature(1710).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenBronze = new StaticPowerFluidBuilder("molten_bronze").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(950).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());

		registerFluidBundle(CrudeOil = new StaticPowerFluidBuilder("oil_crude").addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
			builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(LightOil = new StaticPowerFluidBuilder("oil_light").addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
			builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(HeavyOil = new StaticPowerFluidBuilder("oil_heavy").addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
			builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(Fuel = new StaticPowerFluidBuilder("fuel").addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
			builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
	}

	public static void registerFluidBundle(StaticPowerFluidBundle bundle) {
		StaticPowerRegistry.preRegisterBlock(bundle.FluidBlock);
		StaticPowerRegistry.preRegisterFluid(bundle.Fluid);
		StaticPowerRegistry.preRegisterFluid(bundle.FlowingFluid);
		if (bundle.getSourceBuilder().getShouldRegisterBucket()) {
			StaticPowerRegistry.preRegisterItem(bundle.getBucket());
		}
		FLUID_BUNDLES.add(bundle);
	}
}
