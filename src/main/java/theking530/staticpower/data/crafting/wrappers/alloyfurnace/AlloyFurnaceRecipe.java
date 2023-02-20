package theking530.staticpower.data.crafting.wrappers.alloyfurnace;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class AlloyFurnaceRecipe extends AbstractMachineRecipe {
	public static final String ID = "alloy_furnace";

	public static final Codec<AlloyFurnaceRecipe> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input1").forGetter(recipe -> recipe.getInput1()),
					StaticPowerIngredient.CODEC.fieldOf("input2").forGetter(recipe -> recipe.getInput2()),
					StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
					Codec.INT.optionalFieldOf("experience", 0).forGetter(recipe -> recipe.getExperience()),
					MachineRecipeProcessingSection.CODEC.optionalFieldOf("processing", null).forGetter(recipe -> recipe.getProcessingSection()))
			.apply(instance, AlloyFurnaceRecipe::new));

	private final StaticPowerIngredient input1;
	private final StaticPowerIngredient input2;
	private final StaticPowerOutputItem output;
	private final int experience;

	public AlloyFurnaceRecipe(ResourceLocation id, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output, int experience,
			MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.experience = experience;
		this.input1 = input1;
		this.input2 = input2;
		this.output = output;
	}

	public StaticPowerIngredient getInput1() {
		return input1;
	}

	public StaticPowerIngredient getInput2() {
		return input2;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public int getExperience() {
		return experience;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check if the input counts catch.
		if (matchParams.shouldVerifyItems() && matchParams.getItems().length != 2) {
			return false;
		}

		if (matchParams.shouldVerifyItemCounts()) {
			if (input1.testWithCount(matchParams.getItems()[0]) && input2.testWithCount(matchParams.getItems()[1])) {
				return true;
			}
			if (input1.testWithCount(matchParams.getItems()[1]) && input2.testWithCount(matchParams.getItems()[0])) {
				return true;
			}
		} else {
			if (input1.test(matchParams.getItems()[0]) && input2.test(matchParams.getItems()[1])) {
				return true;
			}
			if (input1.test(matchParams.getItems()[1]) && input2.test(matchParams.getItems()[0])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<AlloyFurnaceRecipe> getSerializer() {
		return ModRecipeSerializers.ALLOY_FURNACE_SERIALIZER.get();
	}

	@Override
	public RecipeType<AlloyFurnaceRecipe> getType() {
		return ModRecipeTypes.ALLOY_FURNACE_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(() -> 400, () -> 0.0, () -> 0, () -> 0);
	}
}
