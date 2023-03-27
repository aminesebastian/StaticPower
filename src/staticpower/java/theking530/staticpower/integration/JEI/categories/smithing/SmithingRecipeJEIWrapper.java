package theking530.staticpower.integration.JEI.categories.smithing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.crafting.StaticPowerRecipeType;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;

public class SmithingRecipeJEIWrapper implements Recipe<Container> {
	public static final RecipeType<SmithingRecipeJEIWrapper> RECIPE_TYPE = new StaticPowerRecipeType<SmithingRecipeJEIWrapper>();
	public final ResourceLocation id;
	public final AutoSmithRecipe recipe;
	public final Ingredient inputIng;
	final ItemStack input;
	public final ItemStack output;

	public SmithingRecipeJEIWrapper(AutoSmithRecipe recipe, ItemStack input, ItemStack output) {
		super();
		this.recipe = recipe;
		this.input = input;
		this.inputIng = Ingredient.of(input);
		this.output = output;
		this.id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + ForgeRegistries.ITEMS.getKey(output.getItem()).getPath().replace(":", "/"));
	}

	public AutoSmithRecipe getRecipe() {
		return recipe;
	}

	@Override
	public boolean matches(Container inv, Level worldIn) {
		return false;
	}

	@Override
	public ItemStack assemble(Container inv) {
		return output;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return output;
	}

	public ItemStack getInputItem() {
		return input;
	}

	public Ingredient getInput() {
		return inputIng;
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