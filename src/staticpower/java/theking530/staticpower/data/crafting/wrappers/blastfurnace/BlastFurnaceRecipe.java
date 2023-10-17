package theking530.staticpower.data.crafting.wrappers.blastfurnace;

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
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class BlastFurnaceRecipe extends AbstractMachineRecipe {
	public static final String ID = "blast_furnace";
	public static final int DEFAULT_PROCESSING_TIME = 200;

	public static final Codec<BlastFurnaceRecipe> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput()),
					StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
					StaticPowerOutputItem.CODEC.fieldOf("slag_output").forGetter(recipe -> recipe.getSlagOutput()),
					Codec.FLOAT.fieldOf("experience").forGetter(recipe -> recipe.getExperience()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing")
							.forGetter(recipe -> recipe.getProcessingSection()))
			.apply(instance, BlastFurnaceRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final StaticPowerOutputItem slagOutput;

	public BlastFurnaceRecipe(ResourceLocation id, StaticPowerIngredient input, StaticPowerOutputItem output,
			StaticPowerOutputItem slagOutput, float experience, MachineRecipeProcessingSection processing) {
		super(id, experience, processing);
		this.input = input;
		this.output = output;
		this.slagOutput = slagOutput;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public StaticPowerOutputItem getSlagOutput() {
		return slagOutput;
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		// Check if the input counts catch.
		if (matchParams.shouldVerifyItems() && matchParams.getItems().length != 1) {
			return false;
		}

		return input.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
	}

	@Override
	public RecipeSerializer<BlastFurnaceRecipe> getSerializer() {
		return ModRecipeSerializers.BLAST_FURNACE_SERIALIZER.get();
	}

	@Override
	public RecipeType<BlastFurnaceRecipe> getType() {
		return ModRecipeTypes.BLAST_FURNACE_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(() -> DEFAULT_PROCESSING_TIME, () -> 0.0, () -> 0.0f,
				() -> 0.0f);
	}
}
