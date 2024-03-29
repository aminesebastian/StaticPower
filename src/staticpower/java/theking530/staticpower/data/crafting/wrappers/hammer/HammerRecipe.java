package theking530.staticpower.data.crafting.wrappers.hammer;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;

public class HammerRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "hammer";
	public static final Ingredient DEFAULT_HAMMERS = Ingredient.of(ModItemTags.HAMMER);

	public static final Codec<HammerRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()), Codec.FLOAT.fieldOf("experience").forGetter(recipe -> recipe.getExperience()),
			JsonUtilities.INGREDIENT_CODEC.optionalFieldOf("hammer", DEFAULT_HAMMERS).forGetter(recipe -> recipe.getHammer()),
			StaticPowerIngredient.CODEC.optionalFieldOf("item").forGetter(recipe -> Optional.ofNullable(recipe.getInputItem())),
			TagKey.codec(ForgeRegistries.BLOCKS.getRegistryKey()).optionalFieldOf("block").forGetter(recipe -> Optional.ofNullable(recipe.getBlock())),
			StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()), Codec.BOOL.fieldOf("requires_anvil").forGetter(recipe -> recipe.requiresAnvil()))
			.apply(instance, (id, experience, hammer, item, block, output, requiresAnvil) -> {
				return new HammerRecipe(id, experience, hammer, item.orElse(null), block.orElse(null), output, requiresAnvil);
			}));

	private final Ingredient hammer;
	private final StaticPowerIngredient inputItem;
	private final TagKey<Block> blockTag;
	private final StaticPowerOutputItem outputItem;
	private final boolean isBlockType;
	private final boolean requiresAnvil;

	public HammerRecipe(ResourceLocation id, float experience, Ingredient hammer, TagKey<Block> block, StaticPowerOutputItem outputItem) {
		this(id, experience, hammer, null, block, outputItem, false);
	}

	public HammerRecipe(ResourceLocation id, float experience, Ingredient hammer, StaticPowerIngredient inputItem, StaticPowerOutputItem outputItem, boolean requiresAnvil) {
		this(id, experience, hammer, inputItem, null, outputItem, requiresAnvil);
	}

	public HammerRecipe(ResourceLocation id, float experience, Ingredient hammer, StaticPowerIngredient inputItem, TagKey<Block> block, StaticPowerOutputItem outputItem,
			boolean requiresAnvil) {
		super(id, experience);
		this.hammer = hammer;
		this.inputItem = inputItem;
		this.blockTag = block;
		this.isBlockType = block != null;
		this.outputItem = outputItem;
		this.requiresAnvil = requiresAnvil;
	}

	public boolean requiresAnvil() {
		return requiresAnvil;
	}

	public StaticPowerOutputItem getOutput() {
		return outputItem;
	}

	public TagKey<Block> getBlock() {
		return blockTag;
	}

	public String getEncodedBlockTag() {
		return TagKey.codec(ForgeRegistries.BLOCKS.getRegistryKey()).encodeStart(JsonOps.INSTANCE, getBlock()).result().get().toString();
	}

	public boolean isBlockType() {
		return isBlockType;
	}

	public StaticPowerIngredient getInputItem() {
		return inputItem;
	}

	public Ingredient getHammer() {
		return hammer;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		// Check blocks.
		if (isBlockType()) {
			if (matchParams.shouldVerifyBlocks() && matchParams.hasBlocks()) {
				return ModBlockTags.matches(blockTag, matchParams.getBlocks()[0].getBlock());
			}
		} else {
			if (matchParams.shouldVerifyItems() && matchParams.hasItems()) {
				return inputItem.test(matchParams.getItems()[0]);
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<HammerRecipe> getSerializer() {
		return ModRecipeSerializers.HAMMER_SERIALIZER.get();
	}

	@Override
	public RecipeType<HammerRecipe> getType() {
		return ModRecipeTypes.HAMMER_RECIPE_TYPE.get();
	}
}