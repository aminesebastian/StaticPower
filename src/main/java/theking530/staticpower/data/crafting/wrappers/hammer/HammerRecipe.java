package theking530.staticpower.data.crafting.wrappers.hammer;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class HammerRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<HammerRecipe> RECIPE_TYPE = IRecipeType.register("hammer");

	private final Ingredient hammer;
	private final ResourceLocation block;
	private final IOptionalNamedTag<Block> blockTag;
	private final ProbabilityItemStackOutput outputItem;

	public HammerRecipe(ResourceLocation name, Ingredient hammer, ResourceLocation block, ProbabilityItemStackOutput outputItem) {
		super(name);
		this.hammer = hammer;
		this.block = block;
		this.blockTag = BlockTags.createOptional(block);
		this.outputItem = outputItem;
	}

	public ProbabilityItemStackOutput getOutput() {
		return outputItem;
	}

	public IOptionalNamedTag<Block> getInputTag() {
		return blockTag;
	}

	public ResourceLocation getRawInputTag() {
		return block;
	}

	public Ingredient getHammer() {
		return hammer;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check items.
		if (matchParams.shouldVerifyBlocks() && matchParams.hasBlocks()) {
			return blockTag.contains(matchParams.getBlocks()[0].getBlock());
		}

		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return HammerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}