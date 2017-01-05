package theking530.staticpower.handlers.crafting.recipes;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;

public class FluidGeneratorRecipes {

	public static void registerFluidGeneratorRecipes() {
		registerRecipe(ModFluids.StaticFluid, 128);
		registerRecipe(ModFluids.EnergizedFluid, 256);
		registerRecipe(ModFluids.LumumFluid, 512);
		registerRecipe(ModFluids.Mash, 20);
		registerRecipe(ModFluids.EvaporatedMash, 40);
		registerRecipe(ModFluids.Ethanol, 240);
	}
	
	public static void registerRecipe(Fluid fluid, int output) {
		RegisterHelper.registerFluidGeneratorRecipe(new FluidStack(fluid, 1),  output);
	}
}
