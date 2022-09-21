package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;

public class ThermalConductivityJEIRecipeWrapper implements Recipe<Container> {
	public static final RecipeType<ThermalConductivityJEIRecipeWrapper> RECIPE_TYPE = new StaticPowerRecipeType<ThermalConductivityJEIRecipeWrapper>();
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