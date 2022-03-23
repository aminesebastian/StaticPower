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
		registerFluidBundle(StaticFluid = new StaticPowerFluidBuilder("fluid_static", new Color(0.2f, 0.9f, 0.2f)).addAutoBucket().addAttributes(builder -> {
			builder.luminosity(15);
		}).build());
		registerFluidBundle(EnergizedFluid = new StaticPowerFluidBuilder("fluid_energized", new Color(0.1f, 0.6f, 0.9f)).addAutoBucket().addAttributes(builder -> {
			builder.luminosity(15);
		}).build());
		registerFluidBundle(LumumFluid = new StaticPowerFluidBuilder("fluid_lumum", new Color(0.5f, 0.1f, 0.9f)).addAutoBucket().addAttributes(builder -> {
			builder.luminosity(15);
		}).build());

		registerFluidBundle(Ethanol = new StaticPowerFluidBuilder("ethanol", new Color(0.4f, 0.75f, 0.9f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500);
		}).build());

		registerFluidBundle(Mash = new StaticPowerFluidBuilder("mash", new Color(0.3f, 0.2f, 0.2f)).addAttributes(builder -> {
			builder.viscosity(2000);
		}).addAutoBucket().build());

		registerFluidBundle(EvaporatedMash = new StaticPowerFluidBuilder("evaporated_mash", new Color(0.4f, 0.3f, 0.3f)).addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-500).viscosity(500);
		}).build());

		registerFluidBundle(LiquidExperience = new StaticPowerFluidBuilder("experience", new Color(0.6f, 0.8f, 0.3f)).addAutoBucket().build());

		registerFluidBundle(Steam = new StaticPowerFluidBuilder("steam", new Color(0.8f, 0.8f, 0.8f)).setOpacity(0.5f).addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-500).viscosity(500);
		}).build());

		registerFluidBundle(SeedOil = new StaticPowerFluidBuilder("oil_seed", new Color(0.6f, 0.55f, 0.4f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500);
		}).build());
		registerFluidBundle(TreeOil = new StaticPowerFluidBuilder("oil_tree", new Color(0.6f, 0.6f, 0.4f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500);
		}).build());
		registerFluidBundle(TreeSap = new StaticPowerFluidBuilder("tree_sap", new Color(0.3f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(2000);
		}).build());
		registerFluidBundle(InfernalTreeSap = new StaticPowerFluidBuilder("infernal_tree_sap", new Color(0.5f, 0.25f, 0.1f)).addAttributes(builder -> {
			builder.viscosity(2000);
		}).addAutoBucket().build());

		registerFluidBundle(AppleJuice = new StaticPowerFluidBuilder("juice_apple", new Color(0.5f, 0.45f, 0.3f)).addAutoBucket().build());
		registerFluidBundle(BerryJuice = new StaticPowerFluidBuilder("juice_berry", new Color(0.7f, 0.2f, 0.3f)).addAutoBucket().build());
		registerFluidBundle(PumpkinJuice = new StaticPowerFluidBuilder("juice_pumpkin", new Color(0.7f, 0.4f, 0.25f)).addAutoBucket().build());
		registerFluidBundle(CarrotJuice = new StaticPowerFluidBuilder("juice_carrot", new Color(0.9f, 0.6f, 0.2f)).addAutoBucket().build());
		registerFluidBundle(WatermelonJuice = new StaticPowerFluidBuilder("juice_melon", new Color(0.9f, 0.2f, 0.3f)).addAutoBucket().build());
		registerFluidBundle(BeetJuice = new StaticPowerFluidBuilder("juice_beetroot", new Color(0.45f, 0.05f, 0.175f)).addAutoBucket().build());
		registerFluidBundle(Fertilizer = new StaticPowerFluidBuilder("fertilizer", new Color(0.2f, 0.25f, 0.6f)).addAutoBucket().build());

		registerFluidBundle(Honey = new StaticPowerFluidBuilder("honey", new Color(0.9f, 0.7f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(2000).density(64).sound(SoundEvents.HONEY_BLOCK_STEP);
		}).build());

		// Create all colors of concrete.
		ColoredConrete = new HashMap<>();
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle;
			registerFluidBundle(bundle = new StaticPowerFluidBuilder("concrete_" + color.getId(), color.getColor()).setTextureName("concrete")
					.addAutoBucket(true, StaticPowerSprites.CONCRETE_BUCKET_FLUID_MASK).addAttributes(builder -> {
						builder.viscosity(2500).density(64).sound(SoundEvents.HONEY_BLOCK_STEP).color(color.getColor().fromFloatToEightBit().encodeInInteger());
					}).build());
			ColoredConrete.put(color, bundle);
		}

		// Create all colors of dye.
		Dyes = new HashMap<>();
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle;
			registerFluidBundle(bundle = new StaticPowerFluidBuilder("dye_" + color.getId(), color.getColor()).setTextureName("dye").addAutoBucket(true, StaticPowerSprites.DYE_BUCKET_FLUID_MASK)
					.addAttributes(builder -> {
						builder.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).color(color.getColor().fromFloatToEightBit().encodeInInteger());
					}).build());
			Dyes.put(color, bundle);
		}

		registerFluidBundle(Latex = new StaticPowerFluidBuilder("latex", new Color(1.0f, 1.0f, 1.0f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).sound(SoundEvents.HONEY_BLOCK_STEP);
		}).build());

		registerFluidBundle(MoltenIron = new StaticPowerFluidBuilder("molten_iron", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(2800).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenGold = new StaticPowerFluidBuilder("molten_gold", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(1064).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenCopper = new StaticPowerFluidBuilder("molten_copper", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(1084).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenTin = new StaticPowerFluidBuilder("molten_tin", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(450).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenZinc = new StaticPowerFluidBuilder("molten_zinc", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(420).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenMagnesium = new StaticPowerFluidBuilder("molten_magnesium", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1250).density(32).temperature(1202).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenTungsten = new StaticPowerFluidBuilder("molten_tungsten", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1000).density(32).temperature(3400).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenSilver = new StaticPowerFluidBuilder("molten_silver", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1000).density(32).temperature(961).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenLead = new StaticPowerFluidBuilder("molten_lead", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(750).density(32).temperature(328).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenAluminum = new StaticPowerFluidBuilder("molten_aluminum", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(500).density(32).temperature(660).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenPlatinum = new StaticPowerFluidBuilder("molten_platinum", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(1770).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenBrass = new StaticPowerFluidBuilder("molten_brass", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(750).density(32).temperature(1710).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenBronze = new StaticPowerFluidBuilder("molten_bronze", new Color(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
			builder.viscosity(1500).density(32).temperature(950).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
		}).build());

		registerFluidBundle(CrudeOil = new StaticPowerFluidBuilder("oil_crude", new Color(0.1f, 0.1f, 0.1f)).setOpacity(1.0f).addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK)
				.addAttributes(builder -> {
					builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
				}).build());
		registerFluidBundle(
				LightOil = new StaticPowerFluidBuilder("oil_light", new Color(0.25f, 0.225f, 0.15f)).addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
					builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
				}).build());
		registerFluidBundle(
				HeavyOil = new StaticPowerFluidBuilder("oil_heavy", new Color(0.35f, 0.225f, 0.15f)).addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
					builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
				}).build());
		registerFluidBundle(Fuel = new StaticPowerFluidBuilder("fuel", new Color(0.35f, 0.375f, 0.15f)).addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
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
