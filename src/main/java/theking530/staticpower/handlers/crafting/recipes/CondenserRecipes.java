package theking530.staticpower.handlers.crafting.recipes;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.utilities.RegisterHelper;

public class CondenserRecipes {

	public static void registerCondenserRecipes() {
		RegisterHelper.registerCondenserRecipe(new FluidStack(ModFluids.EvaporatedMash, 10), new FluidStack(ModFluids.Ethanol, 1), 3);
	}	
}
