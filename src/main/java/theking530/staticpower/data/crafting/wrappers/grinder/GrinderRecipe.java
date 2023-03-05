package theking530.staticpower.data.crafting.wrappers.grinder;

import java.util.List;

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

public class GrinderRecipe extends AbstractMachineRecipe {
	public static final String ID = "grinder";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<GrinderRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInputIngredient()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection()),
					StaticPowerOutputItem.CODEC.listOf().fieldOf("outputs").forGetter(recipe -> recipe.getOutputItems())).apply(instance, GrinderRecipe::new));

	private final StaticPowerIngredient inputItem;
	private final List<StaticPowerOutputItem> outputs;

	public GrinderRecipe(ResourceLocation id, StaticPowerIngredient input, MachineRecipeProcessingSection processing, List<StaticPowerOutputItem> outputs) {
		super(id, processing);
		this.inputItem = input;
		this.outputs = outputs;
	}

	public List<StaticPowerOutputItem> getOutputItems() {
		return outputs;
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputItem;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && inputItem.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && inputItem.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<GrinderRecipe> getSerializer() {
		return ModRecipeSerializers.GRINDER_SERIALIZER.get();
	}

	@Override
	public RecipeType<GrinderRecipe> getType() {
		return ModRecipeTypes.GRINDER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(() -> DEFAULT_PROCESSING_TIME, () -> DEFAULT_POWER_COST, () -> 0, () -> 0);
	}
}