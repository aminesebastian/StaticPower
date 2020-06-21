package theking530.staticpower.initialization;

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

	public static void init() {
		registerFluidBundle(StaticFluid = new StaticPowerFluidBundle("fluid_static"));
		registerFluidBundle(EnergizedFluid = new StaticPowerFluidBundle("fluid_energized"));
		registerFluidBundle(LumumFluid = new StaticPowerFluidBundle("fluid_lumum"));

		registerFluidBundle(Ethanol = new StaticPowerFluidBundle("ethanol"));
		registerFluidBundle(Mash = new StaticPowerFluidBundle("mash"));
		registerFluidBundle(EvaporatedMash = new StaticPowerFluidBundle("evaporated_mash"));
		registerFluidBundle(LiquidExperience = new StaticPowerFluidBundle("liquid_experience"));
		registerFluidBundle(Steam = new StaticPowerFluidBundle("steam"));
		registerFluidBundle(TreeOil = new StaticPowerFluidBundle("tree_oil"));
		registerFluidBundle(TreeSap = new StaticPowerFluidBundle("tree_sap"));
	}

	public static void registerFluidBundle(StaticPowerFluidBundle bundle) {
		StaticPowerRegistry.preRegisterBlock(bundle.FluidBlock);
		//StaticPowerRegistry.preRegisterFluid(bundle.FlowingFluid);
		StaticPowerRegistry.preRegisterFluid(bundle.Fluid);
		StaticPowerRegistry.preRegisterItem(bundle.Bucket);
	}
}
