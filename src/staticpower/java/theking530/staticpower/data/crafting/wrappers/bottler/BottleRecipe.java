package theking530.staticpower.data.crafting.wrappers.bottler;

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
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class BottleRecipe extends AbstractMachineRecipe {
	public static final String ID = "bottler";
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<BottleRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							StaticPowerIngredient.CODEC.fieldOf("empty_bottle")
									.forGetter(recipe -> recipe.getEmptyBottle()),
							StaticPowerOutputItem.CODEC.fieldOf("filled_bottle")
									.forGetter(recipe -> recipe.getFilledBottle()),
							FluidIngredient.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.getFluid()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing")
									.forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, BottleRecipe::new));

	private final StaticPowerIngredient emptyBottle;
	private final StaticPowerOutputItem filledBottle;
	private final FluidIngredient fluid;

	public BottleRecipe(ResourceLocation id, StaticPowerIngredient emptyBottle, StaticPowerOutputItem filledBottle,
			FluidIngredient fluid, MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.emptyBottle = emptyBottle;
		this.filledBottle = filledBottle;
		this.fluid = fluid;
	}

	public StaticPowerIngredient getEmptyBottle() {
		return emptyBottle;
	}

	public StaticPowerOutputItem getFilledBottle() {
		return filledBottle;
	}

	public FluidIngredient getFluid() {
		return fluid;
	}

	@Override
	public RecipeSerializer<BottleRecipe> getSerializer() {
		return ModRecipeSerializers.BOTTLER_SERIALIZER.get();
	}

	@Override
	public RecipeType<BottleRecipe> getType() {
		return ModRecipeTypes.BOTTLER_RECIPE_TYPE.get();
	}

	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		// Check if the item and fluid match.
		if (matchParams.shouldVerifyItems()) {
			if (!emptyBottle.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}
		}
		if (matchParams.shouldVerifyFluids()) {
			if (!fluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
