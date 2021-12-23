package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fmllegacy.common.registry.GameRegistry;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.utilities.ItemUtilities;

public class ThermalConductivityRecipeProvider implements IRecipeManagerPlugin {
	private static List<ThermalConductivityJEIRecipeWrapper> RECIPES;

	public ThermalConductivityRecipeProvider() {
	}

	public static List<ThermalConductivityJEIRecipeWrapper> getRecipes() {
		// Create the recipes array.
		RECIPES = new ArrayList<ThermalConductivityJEIRecipeWrapper>();

		// Get all thermal conductivity recipes.
		List<ThermalConductivityRecipe> originalRecipes = StaticPowerRecipeRegistry.getRecipesOfType(ThermalConductivityRecipe.RECIPE_TYPE);

		// Iterate through all the recipes.
		for (ThermalConductivityRecipe recipe : originalRecipes) {
			// Skip air recipe.
			if (recipe.isAirRecipe()) {
				continue;
			}

			// Create the recipe (may be dropped).
			ThermalConductivityJEIRecipeWrapper jeiRecipe;

			// If this is a fire input recipe, mark it.
			if (recipe.getBlockTags().length > 0 && recipe.getBlockTags()[0].toString().equals("minecraft:fire")) {
				jeiRecipe = new ThermalConductivityJEIRecipeWrapper(recipe, true);
			} else {
				jeiRecipe = new ThermalConductivityJEIRecipeWrapper(recipe);
			}

			// Add blocks.
			if (recipe.getBlockTags().length > 0) {
				RECIPES.add(jeiRecipe);

				// Add all the potential inputs.
				for (Entry<ResourceKey<Block>, Block> block : GameRegistry.findRegistry(Block.class).getEntries()) {
					RecipeMatchParameters matchParams = new RecipeMatchParameters(block.getValue().defaultBlockState());
					if (recipe.isValid(matchParams)) {
						jeiRecipe.addInput(new ItemStack(block.getValue()));
					}
				}
				// Finalize the recipe.
				jeiRecipe.finalize();
			} else if (recipe.getFluidTags().length > 0) {
				RECIPES.add(jeiRecipe);
				// Add all the potential inputs.
				for (Entry<ResourceKey<Fluid>, Fluid> fluid : GameRegistry.findRegistry(Fluid.class).getEntries()) {
					RecipeMatchParameters matchParams = new RecipeMatchParameters(new FluidStack(fluid.getValue(), 1));
					if (recipe.isValid(matchParams)) {
						jeiRecipe.setFluidStack(new FluidStack(fluid.getValue(), 1000));
					}
				}

				// Finalize the recipe.
				jeiRecipe.finalize();
			}
		}
		return RECIPES;
	}

	@Override
	public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
		if (focus.getValue() instanceof ItemStack) {
			ItemStack itemStack = (ItemStack) focus.getValue();
			if (focus.getMode() == IFocus.Mode.OUTPUT) {
				if (isValidOverheatingOutput(itemStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.UID);
				}
			} else if (focus.getMode() == IFocus.Mode.INPUT) {
				if (hasThermalConductivity(itemStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.UID);
				}
			}
		} else if (focus.getValue() instanceof FluidStack) {
			if (focus.getMode() == IFocus.Mode.INPUT) {
				if (hasThermalConductivity((FluidStack) focus.getValue())) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.UID);
				}
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		// Check the category.
		if (recipeCategory != null && !ThermalConductivityRecipeCategory.UID.equals(recipeCategory.getUid())) {
			return Collections.emptyList();
		}

		// Return the recipes.
		return (List<T>) getRecipes();
	}

	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		return Collections.emptyList();
	}

	private static boolean hasThermalConductivity(ItemStack stack) {
		// Check to see if this itemstack represents a block.
		if (stack.getItem() instanceof BlockItem) {
			// Get block item.
			BlockItem blockItem = (BlockItem) stack.getItem();

			// Create the match params.
			ItemStack input = new ItemStack(blockItem);

			// Get the recipes.
			for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
				if (recipe.getInput().test(input)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean hasThermalConductivity(FluidStack stack) {
		// Get the recipes.
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (recipe.fluid.isFluidEqual(stack)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isValidOverheatingOutput(ItemStack stack) {
		// Iterate through all the recipes and add the applicable ones.
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (ItemUtilities.areItemStacksStackable(recipe.getOutputBlock(), stack) || ItemUtilities.areItemStacksStackable(recipe.getOutputItem().getItem(), stack)) {
				return true;
			}
		}
		return false;
	}

	public static class ThermalConductivityJEIRecipeWrapper implements Recipe<Container> {
		public static final RecipeType<ThermalConductivityJEIRecipeWrapper> RECIPE_TYPE = RecipeType.register("thermal_conductivity_jei");
		private final boolean isFireInput;
		private final boolean hasFireOutput;
		private final ResourceLocation id;
		private final ThermalConductivityRecipe recipe;
		private final ItemStack blockOutput;
		private final FluidStack fluidOutput;

		private List<ItemStack> rawInputs;
		private Ingredient input;
		private FluidStack fluid;

		public ThermalConductivityJEIRecipeWrapper(ThermalConductivityRecipe recipe) {
			this(recipe, false);
		}

		public ThermalConductivityJEIRecipeWrapper(ThermalConductivityRecipe recipe, boolean isFireInput) {
			super();
			this.isFireInput = isFireInput;
			this.recipe = recipe;
			this.blockOutput = generateOutputIngredient(recipe);
			this.id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_jei");
			this.rawInputs = new ArrayList<ItemStack>();
			this.fluid = FluidStack.EMPTY;

			if (recipe.hasOverheatedBlock()) {
				if (recipe.getOverheatedBlock().getFluidState().getType() != Fluids.EMPTY) {
					fluidOutput = new FluidStack(recipe.getOverheatedBlock().getFluidState().getType(), 1000);
				} else {
					fluidOutput = FluidStack.EMPTY;
				}
			} else {
				fluidOutput = FluidStack.EMPTY;
			}

			this.hasFireOutput = recipe.getOverheatedBlock() != null ? recipe.getOverheatedBlock().getBlock().getRegistryName().toString().equals("minecraft:fire") : false;
		}

		public void addInput(ItemStack input) {
			rawInputs.add(input);
		}

		public boolean getIsFireInput() {
			return isFireInput;
		}

		public boolean getHasFireOutput() {
			return hasFireOutput;
		}

		public FluidStack getFluidInput() {
			return this.fluid;
		}

		public void setFluidStack(FluidStack fluid) {
			this.fluid = fluid;
		}

		public FluidStack getOutputFluid() {
			return this.fluidOutput;
		}

		public void finalize() {
			// Make the ingredient.
			input = Ingredient.of(rawInputs.stream());
		}

		private ItemStack generateOutputIngredient(ThermalConductivityRecipe recipe) {
			if (recipe.hasOverheatedBlock()) {
				return new ItemStack(recipe.getOverheatedBlock().getBlock());
			} else {
				return ItemStack.EMPTY;
			}
		}

		public ThermalConductivityRecipe getRecipe() {
			return recipe;
		}

		@Override
		public boolean matches(Container inv, Level worldIn) {
			return false;
		}

		public ItemStack getOutputBlock() {
			return blockOutput;
		}

		public ProbabilityItemStackOutput getOutputItem() {
			return recipe.getOverheatedItem();
		}

		public Ingredient getInput() {
			return input;
		}

		@Override
		public ItemStack assemble(Container inv) {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean canCraftInDimensions(int width, int height) {
			return false;
		}

		@Override
		public ItemStack getResultItem() {
			return ItemStack.EMPTY;
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return null;
		}

		@Override
		public RecipeType<?> getType() {
			return RECIPE_TYPE;
		}
	}
}