package theking530.staticpower.initialization;

import net.minecraft.util.SoundEvents;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.fluids.StaticPowerFluidBundle;

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

	public static StaticPowerFluidBundle AppleJuice;
	public static StaticPowerFluidBundle BerryJuice;
	public static StaticPowerFluidBundle PumpkinJuice;
	public static StaticPowerFluidBundle CarrotJuice;
	public static StaticPowerFluidBundle WatermelonJuice;
	public static StaticPowerFluidBundle BeetJuice;
	public static StaticPowerFluidBundle Fertilizer;
	public static StaticPowerFluidBundle Honey;

	public static void init() {
		registerFluidBundle(StaticFluid = new StaticPowerFluidBundle("fluid_static", builder -> {
			builder.luminosity(15);
		}));
		registerFluidBundle(EnergizedFluid = new StaticPowerFluidBundle("fluid_energized", builder -> {
			builder.luminosity(15);
		}));
		registerFluidBundle(LumumFluid = new StaticPowerFluidBundle("fluid_lumum", builder -> {
			builder.luminosity(15);
		}));

		registerFluidBundle(Ethanol = new StaticPowerFluidBundle("ethanol"));
		registerFluidBundle(Mash = new StaticPowerFluidBundle("mash"));
		registerFluidBundle(EvaporatedMash = new StaticPowerFluidBundle("evaporated_mash"));
		registerFluidBundle(LiquidExperience = new StaticPowerFluidBundle("liquid_experience"));
		registerFluidBundle(Steam = new StaticPowerFluidBundle("steam", builder -> {
			builder.gaseous().density(-64);
		}));
		registerFluidBundle(SeedOil = new StaticPowerFluidBundle("seed_oil"));
		registerFluidBundle(TreeOil = new StaticPowerFluidBundle("tree_oil"));
		registerFluidBundle(TreeSap = new StaticPowerFluidBundle("tree_sap"));

		registerFluidBundle(AppleJuice = new StaticPowerFluidBundle("juice_apple"));
		registerFluidBundle(BerryJuice = new StaticPowerFluidBundle("juice_berry"));
		registerFluidBundle(PumpkinJuice = new StaticPowerFluidBundle("juice_pumpkin"));
		registerFluidBundle(CarrotJuice = new StaticPowerFluidBundle("juice_carrot"));
		registerFluidBundle(WatermelonJuice = new StaticPowerFluidBundle("juice_watermelon"));
		registerFluidBundle(BeetJuice = new StaticPowerFluidBundle("juice_beet"));

		registerFluidBundle(Fertilizer = new StaticPowerFluidBundle("liquid_fertilizer"));
		registerFluidBundle(Honey = new StaticPowerFluidBundle("honey", builder -> {
			builder.viscosity(128).density(64).sound(SoundEvents.BLOCK_HONEY_BLOCK_STEP);
		}));
	}

	public static void registerFluidBundle(StaticPowerFluidBundle bundle) {
		StaticPowerRegistry.preRegisterBlock(bundle.FluidBlock);
		StaticPowerRegistry.preRegisterFluid(bundle.Fluid);
		StaticPowerRegistry.preRegisterItem(bundle.Bucket);
	}
}
