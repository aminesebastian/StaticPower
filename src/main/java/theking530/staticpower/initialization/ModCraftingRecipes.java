package theking530.staticpower.initialization;

import theking530.staticpower.handlers.crafting.recipes.CentrifugeRecipes;
import theking530.staticpower.handlers.crafting.recipes.CondenserRecipes;
import theking530.staticpower.handlers.crafting.recipes.DistilleryRecipes;
import theking530.staticpower.handlers.crafting.recipes.EsotericEnchanterRecipes;
import theking530.staticpower.handlers.crafting.recipes.FarmerRecipes;
import theking530.staticpower.handlers.crafting.recipes.FermenterRecipes;
import theking530.staticpower.handlers.crafting.recipes.FluidGeneratorRecipes;
import theking530.staticpower.handlers.crafting.recipes.FormerRecipes;
import theking530.staticpower.handlers.crafting.recipes.FusionRecipes;
import theking530.staticpower.handlers.crafting.recipes.GrinderRecipes;
import theking530.staticpower.handlers.crafting.recipes.InfuserRecipes;
import theking530.staticpower.handlers.crafting.recipes.LumberMillRecipes;
import theking530.staticpower.handlers.crafting.recipes.ShapedRecipes;
import theking530.staticpower.handlers.crafting.recipes.ShaplessRecipes;
import theking530.staticpower.handlers.crafting.recipes.SmeltingRecipes;
import theking530.staticpower.handlers.crafting.recipes.SolderingRecipes;
import theking530.staticpower.handlers.crafting.recipes.SqueezerRecipes;
import theking530.staticpower.handlers.crafting.recipes.ToolRecipes;

public class ModCraftingRecipes {
	public static void init() {
		ShapedRecipes.registerFullRecipes();   
		ShaplessRecipes.registerShapelessRecipes(); 
		SmeltingRecipes.registerFullRecipes();
		FusionRecipes.registerFusionRecipes();
		SolderingRecipes.registerSolderingRecipes();
		GrinderRecipes.registerGrinderRecipes();
		InfuserRecipes.registerInfusionRecipes();
		SqueezerRecipes.registerSqueezingRecipes();
		FermenterRecipes.registerFermenterRecipes();
		FluidGeneratorRecipes.registerFluidGeneratorRecipes();
		CondenserRecipes.registerCondenserRecipes();
		DistilleryRecipes.registerDistilleryRecipes();
		FormerRecipes.registerFusionRecipes();
		EsotericEnchanterRecipes.registerEsotericEnchanterRecipes();
		ToolRecipes.registerToolRecipes();
		FarmerRecipes.registerFarmerRecipes();
		CentrifugeRecipes.registerCentrigureRecipes();
		LumberMillRecipes.registerLumberMillRecipes();
	}
}
