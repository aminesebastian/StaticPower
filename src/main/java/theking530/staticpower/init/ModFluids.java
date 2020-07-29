package theking530.staticpower.init;

import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.fluids.StaticPowerFluidBundle;
import theking530.staticpower.fluids.StaticPowerFluidBundle.StaticPowerFluidBuilder;

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
		registerFluidBundle(EvaporatedMash = new StaticPowerFluidBuilder("evaporated_mash").addAutoBucket().build());
		registerFluidBundle(LiquidExperience = new StaticPowerFluidBuilder("liquid_experience").addAutoBucket().build());
		registerFluidBundle(Steam = new StaticPowerFluidBuilder("steam").addAutoBucket().addAttributes(builder -> {
			builder.gaseous().density(-64);
		}).build());
		registerFluidBundle(SeedOil = new StaticPowerFluidBuilder("seed_oil").addAutoBucket().build());
		registerFluidBundle(TreeOil = new StaticPowerFluidBuilder("tree_oil").addAutoBucket().build());
		registerFluidBundle(TreeSap = new StaticPowerFluidBuilder("tree_sap").addAutoBucket().build());

		registerFluidBundle(Milk = new StaticPowerFluidBuilder("milk").addBucketSupplier(() -> Items.MILK_BUCKET).setShouldRegisterBucket(false).build());

		registerFluidBundle(AppleJuice = new StaticPowerFluidBuilder("juice_apple").addAutoBucket().build());
		registerFluidBundle(BerryJuice = new StaticPowerFluidBuilder("juice_berry").addAutoBucket().build());
		registerFluidBundle(PumpkinJuice = new StaticPowerFluidBuilder("juice_pumpkin").addAutoBucket().build());
		registerFluidBundle(CarrotJuice = new StaticPowerFluidBuilder("juice_carrot").addAutoBucket().build());
		registerFluidBundle(WatermelonJuice = new StaticPowerFluidBuilder("juice_melon").addAutoBucket().build());
		registerFluidBundle(BeetJuice = new StaticPowerFluidBuilder("juice_beetroot").addAutoBucket().build());
		registerFluidBundle(Fertilizer = new StaticPowerFluidBuilder("liquid_fertilizer").addAutoBucket().build());

		registerFluidBundle(Honey = new StaticPowerFluidBuilder("honey").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(128).density(64).sound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
		}).build());
		registerFluidBundle(Concrete = new StaticPowerFluidBuilder("concrete").addAutoBucket().addAttributes(builder -> {
			builder.viscosity(128).density(64).sound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
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
