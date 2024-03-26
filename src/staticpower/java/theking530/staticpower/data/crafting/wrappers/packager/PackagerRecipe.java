package theking530.staticpower.data.crafting.wrappers.packager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeType;

public class PackagerRecipe extends AbstractMachineRecipe {
	public static final String ID = "packager";
	public static final RecipeType<PackagerRecipe> RECIPE_TYPE = new StaticPowerRecipeType<PackagerRecipe>();
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<PackagerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					Codec.INT.fieldOf("size").forGetter(recipe -> recipe.getSize()), StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInputIngredient()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("output", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getOutput()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, PackagerRecipe::new));

	private final StaticPowerIngredient inputItem;
	private final StaticPowerOutputItem outputItem;
	private final int size;

	public PackagerRecipe(ResourceLocation name, int size, StaticPowerIngredient input, StaticPowerOutputItem outputItem, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.size = size;
		this.inputItem = input;
		this.outputItem = outputItem;
	}

	public StaticPowerOutputItem getOutput() {
		return outputItem;
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputItem;
	}

	public int getSize() {
		return size;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		// Check the size param.
		if (matchParams.hasCustomParameter("size")) {
			int paramSize = matchParams.getCustomParameterContainer().getInt("size");
			if (paramSize != size) {
				return false;
			}
		} else {
			return false;
		}

		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			matched &= matchParams.hasItems() && inputItem.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
		}

		return matched;
	}

	@Override
	public RecipeSerializer<PackagerRecipe> getSerializer() {
		return PackagerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<PackagerRecipe> getType() {
		return RECIPE_TYPE;
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}