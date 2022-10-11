package theking530.staticpower.data.crafting.wrappers.hammer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;
import theking530.staticpower.init.ModTags;

public class HammerRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "hammer";
	public static final RecipeType<HammerRecipe> RECIPE_TYPE = new StaticPowerRecipeType<HammerRecipe>();

	private final Ingredient hammer;
	private final StaticPowerIngredient inputItem;
	private final ResourceLocation block;
	private final TagKey<Block> blockTag;
	private final ProbabilityItemStackOutput outputItem;
	private final boolean isBlockType;

	public HammerRecipe(ResourceLocation name, Ingredient hammer, ResourceLocation block,
			ProbabilityItemStackOutput outputItem) {
		super(name);
		this.hammer = hammer;
		this.block = block;
		this.blockTag = ForgeRegistries.BLOCKS.tags().createTagKey(block);
		this.outputItem = outputItem;
		this.isBlockType = true;
		this.inputItem = null;
	}

	public HammerRecipe(ResourceLocation name, Ingredient hammer, StaticPowerIngredient inputItem,
			ProbabilityItemStackOutput outputItem) {
		super(name);
		this.hammer = hammer;
		this.inputItem = inputItem;
		this.block = null;
		this.blockTag = null;
		this.isBlockType = false;
		this.outputItem = outputItem;
	}

	public ProbabilityItemStackOutput getOutput() {
		return outputItem;
	}

	public TagKey<Block> getInputTag() {
		return blockTag;
	}

	public boolean isBlockType() {
		return isBlockType;
	}

	public StaticPowerIngredient getInputItem() {
		return inputItem;
	}

	public ResourceLocation getRawInputTag() {
		return block;
	}

	public Ingredient getHammer() {
		return hammer;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check blocks.
		if (isBlockType()) {
			if (matchParams.shouldVerifyBlocks() && matchParams.hasBlocks()) {
				return ModTags.tagContainsBlock(blockTag, matchParams.getBlocks()[0].getBlock());
			}
		} else {
			if (matchParams.shouldVerifyItems() && matchParams.hasItems()) {
				return inputItem.test(matchParams.getItems()[0]);
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HammerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}