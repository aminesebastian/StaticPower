package theking530.staticpower.data.crafting.wrappers.hydroponicfarming;

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
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class HydroponicFarmingRecipe extends AbstractMachineRecipe {
	public static final String ID = "hydroponic_farming";
	public static final int DEFAULT_PROCESSING_TIME = 12000;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<HydroponicFarmingRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							StaticPowerIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getInput()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, HydroponicFarmingRecipe::new));

	private final StaticPowerIngredient input;

	public HydroponicFarmingRecipe(ResourceLocation id, StaticPowerIngredient input, MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.input = input;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<HydroponicFarmingRecipe> getSerializer() {
		return ModRecipeSerializers.HYDROPONIC_FARMER_SERIALIZER.get();
	}

	@Override
	public RecipeType<HydroponicFarmingRecipe> getType() {
		return ModRecipeTypes.HYDROPONIC_FARMING_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
