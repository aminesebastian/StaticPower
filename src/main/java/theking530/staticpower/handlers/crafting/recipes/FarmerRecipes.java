package theking530.staticpower.handlers.crafting.recipes;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;

public class FarmerRecipes {

	public static void register() {
		RegisterHelper.registerFarmingRecipe(createFluidStack(FluidRegistry.WATER), 1.0f);
		RegisterHelper.registerFarmingRecipe(createFluidStack(ModFluids.StaticFluid), 1.5f);
		RegisterHelper.registerFarmingRecipe(createFluidStack(ModFluids.EnergizedFluid), 2.0f);
		RegisterHelper.registerFarmingRecipe(createFluidStack(ModFluids.LumumFluid), 2.5f);
		
		RegisterHelper.registerFarmingRecipe(createFluidStack(ModFluids.Mash), 3.0f);
		RegisterHelper.registerFarmingRecipe(createFluidStack(ModFluids.EvaporatedMash), 3.5f);
		RegisterHelper.registerFarmingRecipe(createFluidStack(ModFluids.Ethanol), 4.0f);
	}
	public static FluidStack createFluidStack(Fluid fluid) {
		return new FluidStack(fluid, 1000); //Amount doesnt matter.
	}
}
