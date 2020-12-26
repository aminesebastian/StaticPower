package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
			ThermalConductivityJEIRecipeWrapper jeiRecipe = new ThermalConductivityJEIRecipeWrapper(recipe);

			// Add blocks.
			if (recipe.getBlockTags().length > 0) {
				RECIPES.add(jeiRecipe);

				// Add all the potential inputs.
				for (Entry<RegistryKey<Block>, Block> block : GameRegistry.findRegistry(Block.class).getEntries()) {
					RecipeMatchParameters matchParams = new RecipeMatchParameters(block.getValue().getDefaultState());
					if (recipe.isValid(matchParams)) {
						jeiRecipe.addInput(new ItemStack(block.getValue()));
					}
				}
				// Finalize the recipe.
				jeiRecipe.finalize();
			} else if (recipe.getFluidTags().length > 0) {
				RECIPES.add(jeiRecipe);
				// Add all the potential inputs.
				for (Entry<RegistryKey<Fluid>, Fluid> fluid : GameRegistry.findRegistry(Fluid.class).getEntries()) {
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

	public static class ThermalConductivityJEIRecipeWrapper implements IRecipe<IInventory> {
		public static final IRecipeType<ThermalConductivityJEIRecipeWrapper> RECIPE_TYPE = IRecipeType.register("thermal_conductivity_jei");
		private final ResourceLocation id;
		private final ThermalConductivityRecipe recipe;
		private final ItemStack blockOutput;
		private final FluidStack fluidOutput;

		private List<ItemStack> rawInputs;
		private Ingredient input;
		private FluidStack fluid;

		public ThermalConductivityJEIRecipeWrapper(ThermalConductivityRecipe recipe) {
			super();
			this.recipe = recipe;
			this.blockOutput = generateOutputIngredient(recipe);
			this.id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_jei");
			this.rawInputs = new ArrayList<ItemStack>();
			this.fluid = FluidStack.EMPTY;

			if (recipe.hasOverheatedBlock()) {
				if (recipe.getOverheatedBlock().getFluidState().getFluid() != Fluids.EMPTY) {
					fluidOutput = new FluidStack(recipe.getOverheatedBlock().getFluidState().getFluid(), 1000);
				} else {
					fluidOutput = FluidStack.EMPTY;
				}
			} else {
				fluidOutput = FluidStack.EMPTY;
			}
		}

		public void addInput(ItemStack input) {
			rawInputs.add(input);
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
			input = Ingredient.fromStacks(rawInputs.stream());
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
		public boolean matches(IInventory inv, World worldIn) {
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
		public ItemStack getCraftingResult(IInventory inv) {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean canFit(int width, int height) {
			return false;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return ItemStack.EMPTY;
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return null;
		}

		@Override
		public IRecipeType<?> getType() {
			return RECIPE_TYPE;
		}
	}
}