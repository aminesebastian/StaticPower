package theking530.staticpower.data.crafting.wrappers.cauldron;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CauldronRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "cauldron";
	public static final Codec<CauldronRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.optionalFieldOf("input_item", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getInput()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("output_item", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getOutput()),
					FluidIngredient.CODEC.optionalFieldOf("input_fluid", FluidIngredient.EMPTY).forGetter(recipe -> recipe.getRequiredFluid()),
					JsonUtilities.FLUIDSTACK_CODEC.optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(recipe -> recipe.getOutputFluid()),
					Codec.BOOL.fieldOf("drain_after_craft").forGetter(recipe -> recipe.shouldDrainAfterCraft()),
					Codec.INT.fieldOf("time").forGetter(recipe -> recipe.getRequiredTimeInCauldron())).apply(instance, CauldronRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidIngredient inputFluid;
	private final FluidStack outputFluid;
	private final int timeInCauldron;
	private final boolean drainAfterCraft;

	public CauldronRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output, FluidIngredient inputFluid, FluidStack outputFluid,
			boolean drainAfterCraft, int timeInCauldron) {
		super(name);
		this.input = input;
		this.output = output;
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
		this.timeInCauldron = timeInCauldron;
		this.drainAfterCraft = drainAfterCraft;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public boolean hasItemOutput() {
		return !output.isEmpty();
	}

	public FluidIngredient getRequiredFluid() {
		return inputFluid;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public int getRequiredTimeInCauldron() {
		return timeInCauldron;
	}

	public boolean shouldDrainAfterCraft() {
		return drainAfterCraft;
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids() && !inputFluid.isEmpty()) {
			if (!inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
				return false;
			}
		}

		// Check items.
		if (matched && matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<CauldronRecipe> getSerializer() {
		return ModRecipeSerializers.CAULDRON_SERIALIZER.get();
	}

	@Override
	public RecipeType<CauldronRecipe> getType() {
		return ModRecipeTypes.CAULDRON_RECIPE_TYPE.get();
	}
}
