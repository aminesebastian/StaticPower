package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class FusionFurnaceRecipe extends AbstractMachineRecipe {
	public static final String ID = "fusion_furnace";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<FusionFurnaceRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							StaticPowerIngredient.CODEC.listOf().fieldOf("inputs").forGetter(recipe -> recipe.getInputs()),
							StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, FusionFurnaceRecipe::new));

	private final List<StaticPowerIngredient> inputs;
	private final StaticPowerOutputItem output;

	public FusionFurnaceRecipe(ResourceLocation name, List<StaticPowerIngredient> inputs, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.inputs = inputs;
		this.output = output;
	}

	public List<StaticPowerIngredient> getInputs() {
		return inputs;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public int getRequiredCountOfItem(ItemStack item) {
		for (StaticPowerIngredient input : inputs) {
			if (input.test(item)) {
				return input.getCount();
			}
		}
		return 0;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		// Check if the input counts catch.
		if (matchParams.shouldVerifyItems() && matchParams.getItems().length < inputs.size()) {
			return false;
		}

		// Copy the inputs.
		List<ItemStack> inputCopies = new ArrayList<ItemStack>();
		for (ItemStack input : matchParams.getItems()) {
			inputCopies.add(input.copy());
		}

		// Check each item, if any fails, return false.
		int matches = 0;
		for (StaticPowerIngredient ing : inputs) {
			for (int i = 0; i < inputCopies.size(); i++) {
				// Check the match.
				boolean itemMatched = false;
				if (matchParams.shouldVerifyItemCounts()) {
					itemMatched = ing.testWithCount(inputCopies.get(i));
				} else {
					itemMatched = ing.test(inputCopies.get(i));
				}

				// Check if there was a match.
				if (itemMatched) {
					inputCopies.set(i, ItemStack.EMPTY);
					matches++;
					break;
				}
			}
		}
		// Return true if we had the correct amount of matches.
		return matches == inputs.size();
	}

	@Override
	public RecipeSerializer<FusionFurnaceRecipe> getSerializer() {
		return ModRecipeSerializers.FUSION_FURANCE_SERIALIZER.get();
	}

	@Override
	public RecipeType<FusionFurnaceRecipe> getType() {
		return ModRecipeTypes.FUSION_FURNACE_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
