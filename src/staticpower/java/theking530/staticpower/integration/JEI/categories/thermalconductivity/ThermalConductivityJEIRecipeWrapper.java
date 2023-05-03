package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.block.BlockStateIngredient;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerRecipeType;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.fluid.FluidIngredient;

public class ThermalConductivityJEIRecipeWrapper implements Recipe<Container> {

	public static final RecipeType<ThermalConductivityJEIRecipeWrapper> RECIPE_TYPE = new StaticPowerRecipeType<ThermalConductivityJEIRecipeWrapper>();
	private final boolean isFireInput;
	private final boolean hasFireOutput;
	private final ResourceLocation id;
	private final ThermalConductivityRecipe recipe;

	private final ConcretizedThermalConductivityBehaviour concretizedOverheat;
	private final ConcretizedThermalConductivityBehaviour concretizedFreeze;

	private List<ItemStack> rawInputs;
	private BlockStateIngredient blocks;
	private FluidIngredient fluids;

	public ThermalConductivityJEIRecipeWrapper(ThermalConductivityRecipe recipe) {
		this(recipe, false);
	}

	public ThermalConductivityJEIRecipeWrapper(ThermalConductivityRecipe recipe, boolean isFireInput) {
		super();
		this.isFireInput = isFireInput;
		this.recipe = recipe;
		this.id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_jei");
		this.rawInputs = new ArrayList<ItemStack>();
		this.blocks = recipe.getBlocks();
		this.fluids = recipe.getFluids();

		if (recipe.hasOverheatingBehaviour()) {
			concretizedOverheat = ConcretizedThermalConductivityBehaviour.from(recipe.getOverheatingBehaviour());
		} else {
			concretizedOverheat = null;
		}

		if (recipe.hasOverheatingBehaviour()) {
			concretizedFreeze = ConcretizedThermalConductivityBehaviour.from(recipe.getFreezingBehaviour());
		} else {
			concretizedFreeze = null;
		}

		this.hasFireOutput = recipe.hasOverheatingBehaviour() && recipe.getOverheatingBehaviour().hasBlock()
				? recipe.getOverheatingBehaviour().getBlockState().getBlock() == Blocks.FIRE
				: false;

		if (!recipe.getBlocks().isEmpty()) {
			for (Entry<ResourceKey<Block>, Block> block : ForgeRegistries.BLOCKS.getEntries()) {
				RecipeMatchParameters matchParams = new RecipeMatchParameters(block.getValue().defaultBlockState());
				if (recipe.matches(matchParams, null)) {
					rawInputs.add(new ItemStack(block.getValue()));
				}
			}
		}
	}

	public boolean getIsFireInput() {
		return isFireInput;
	}

	public boolean getHasFireOutput() {
		return hasFireOutput;
	}

	public FluidIngredient getFluidInput() {
		return this.fluids;
	}

	public BlockStateIngredient getBlocks() {
		return this.blocks;
	}

	public ConcretizedThermalConductivityBehaviour getConcretizedOverheat() {
		return concretizedOverheat;
	}

	public ConcretizedThermalConductivityBehaviour getConcretizedFreeze() {
		return concretizedFreeze;
	}

	public ThermalConductivityRecipe getRecipe() {
		return recipe;
	}

	@Override
	public boolean matches(Container inv, Level worldIn) {
		return false;
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