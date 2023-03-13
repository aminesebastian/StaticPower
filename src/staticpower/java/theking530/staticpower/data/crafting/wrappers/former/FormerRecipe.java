package theking530.staticpower.data.crafting.wrappers.former;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class FormerRecipe extends AbstractMachineRecipe {
	public static final String ID = "former";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<FormerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInputIngredient()),
					StaticPowerIngredient.CODEC.fieldOf("mold").forGetter(recipe -> recipe.getRequiredMold()),
					StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, FormerRecipe::new));

	private StaticPowerIngredient inputIngredient;
	private StaticPowerIngredient requiredMold;
	private StaticPowerOutputItem outputItemStack;

	public FormerRecipe(ResourceLocation id, StaticPowerIngredient input, StaticPowerIngredient mold, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		super(id, processing);
		inputIngredient = input;
		requiredMold = mold;
		outputItemStack = output;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			// Check to make sure we got two items.
			if (matchParams.getItems().length != 2) {
				return false;
			}

			matched &= matchParams.hasItems() && inputIngredient.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
			matched &= matchParams.hasItems() && requiredMold.test(matchParams.getItems()[1]);
		}

		return matched;
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputIngredient;
	}

	public ItemStack getRawRecipeOutput() {
		return outputItemStack.getItemStack();
	}

	public StaticPowerOutputItem getOutput() {
		return outputItemStack;
	}

	public StaticPowerIngredient getRequiredMold() {
		return requiredMold;
	}

	@Override
	public RecipeSerializer<FormerRecipe> getSerializer() {
		return ModRecipeSerializers.FORMER_SERIALIZER.get();
	}

	@Override
	public RecipeType<FormerRecipe> getType() {
		return ModRecipeTypes.FORMER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
