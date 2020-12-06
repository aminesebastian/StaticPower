package theking530.staticpower.init;

import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.fluid.StaticPowerFluidBundle.StaticPowerFluidBuilder;

public class ModFluids {
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
	public static StaticPowerFluidBundle Concrete;
	public static StaticPowerFluidBundle Latex;

	public static StaticPowerFluidBundle MoltenIron;
	public static StaticPowerFluidBundle MoltenGold;
	public static StaticPowerFluidBundle MoltenCopper;
	public static StaticPowerFluidBundle MoltenTin;
	public static StaticPowerFluidBundle MoltenZinc;
	public static StaticPowerFluidBundle MoltenMagnesium;
	public static StaticPowerFluidBundle MoltenTungsten;
	public static StaticPowerFluidBundle MoltenSilver;
	public static StaticPowerFluidBundle MoltenLead;
	public static StaticPowerFluidBundle MoltenAluminium;
	public static StaticPowerFluidBundle MoltenPlatinum;
	public static StaticPowerFluidBundle MoltenBrass;
	public static StaticPowerFluidBundle MoltenBronze;

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

		registerFluidBundle(Ethanol = new StaticPowerFluidBuilder("ethanol").addAutoBucket().build());

		registerFluidBundle(Mash = new StaticPowerFluidBuilder("mash").addAutoBucket().build());

		registerFluidBundle(EvaporatedMash = new StaticPowerFluidBuilder("evaporated_mash").addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-100);
		}).build());

		registerFluidBundle(LiquidExperience = new StaticPowerFluidBuilder("experience").addAutoBucket().build());

		registerFluidBundle(Steam = new StaticPowerFluidBuilder("steam").addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-100);
		}).build());

		registerFluidBundle(SeedOil = new StaticPowerFluidBuilder("oil_seed").addAutoBucket().build());
		registerFluidBundle(TreeOil = new StaticPowerFluidBuilder("oil_tree").addAutoBucket().build());
		registerFluidBundle(TreeSap = new StaticPowerFluidBuilder("tree_sap").addAutoBucket().build());
		registerFluidBundle(InfernalTreeSap = new StaticPowerFluidBuilder("infernal_tree_sap").addAutoBucket().build());

		registerFluidBundle(Milk = new StaticPowerFluidBuilder("milk").addBucketSupplier(() -> Items.MILK_BUCKET).setShouldRegisterBucket(false).build());

		registerFluidBundle(AppleJuice = new StaticPowerFluidBuilder("juice_apple").addAutoBucket().build());
		registerFluidBundle(BerryJuice = new StaticPowerFluidBuilder("juice_berry").addAutoBucket().build());
		registerFluidBundle(PumpkinJuice = new StaticPowerFluidBuilder("juice_pumpkin").addAutoBucket().build());
		registerFluidBundle(CarrotJuice = new StaticPowerFluidBuilder("juice_carrot").addAutoBucket().build());
		registerFluidBundle(WatermelonJuice = new StaticPowerFluidBuilder("juice_melon").addAutoBucket().build());
		registerFluidBundle(BeetJuice = new StaticPowerFluidBuilder("juice_beetroot").addAutoBucket().build());
		registerFluidBundle(Fertilizer = new StaticPowerFluidBuilder("fertilizer").addAutoBucket().build());

		registerFluidBundle(Honey = new StaticPowerFluidBuilder("honey").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(128).density(64).sound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
		}).build());

		registerFluidBundle(Concrete = new StaticPowerFluidBuilder("concrete").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(128).density(64).sound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
		}).build());

		registerFluidBundle(Latex = new StaticPowerFluidBuilder("latex").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).sound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
		}).build());

		registerFluidBundle(MoltenIron = new StaticPowerFluidBuilder("molten_iron").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(2800).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenGold = new StaticPowerFluidBuilder("molten_gold").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(1064).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenCopper = new StaticPowerFluidBuilder("molten_copper").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(1084).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenTin = new StaticPowerFluidBuilder("molten_tin").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(450).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenZinc = new StaticPowerFluidBuilder("molten_zinc").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(420).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenMagnesium = new StaticPowerFluidBuilder("molten_magnesium").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(1202).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenTungsten = new StaticPowerFluidBuilder("molten_tungsten").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(3400).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenSilver = new StaticPowerFluidBuilder("molten_silver").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(961).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenLead = new StaticPowerFluidBuilder("molten_lead").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(328).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenAluminium = new StaticPowerFluidBuilder("molten_aluminium").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(660).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenPlatinum = new StaticPowerFluidBuilder("molten_platinum").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(1770).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenBrass = new StaticPowerFluidBuilder("molten_brass").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(1710).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
		registerFluidBundle(MoltenBronze = new StaticPowerFluidBuilder("molten_bronze").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(80).density(32).temperature(950).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
		}).build());
	}

	public static void registerFluidBundle(StaticPowerFluidBundle bundle) {
		StaticPowerRegistry.preRegisterBlock(bundle.FluidBlock);
		StaticPowerRegistry.preRegisterFluid(bundle.Fluid);
		if (bundle.getSourceBuilder().getShouldRegisterBucket()) {
			StaticPowerRegistry.preRegisterItem(bundle.getBucket());
		}
	}
}
