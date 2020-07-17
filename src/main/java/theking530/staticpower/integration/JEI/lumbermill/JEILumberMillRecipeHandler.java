package theking530.staticpower.integration.JEI.lumbermill;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import theking530.staticpower.data.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;

public class JEILumberMillRecipeHandler {
	private static final Logger LOGGER = LogManager.getLogger(JEILumberMillRecipeHandler.class);

	public static List<LumberMillRecipe> getRecipes() {
		Stopwatch sw = Stopwatch.createStarted();
		List<LumberMillRecipe> recipes = new ArrayList<>();
		for (AbstractStaticPowerRecipe abstractRecipe : StaticPowerRecipeRegistry.RECIPES.get(LumberMillRecipe.RECIPE_TYPE)) {
			LumberMillRecipe lumberRecipe = (LumberMillRecipe) abstractRecipe;
			recipes.add(lumberRecipe);
		}
		sw.stop();
		LOGGER.debug("Registered {} lumber mill recipes in {}", recipes.size(), sw);
		return recipes;
	}
}
