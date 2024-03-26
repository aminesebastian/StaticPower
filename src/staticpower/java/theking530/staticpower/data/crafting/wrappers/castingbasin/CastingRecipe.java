package theking530.staticpower.data.crafting.wrappers.castingbasin;

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
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CastingRecipe extends AbstractMachineRecipe {
	public static final String ID = "casting";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 2.0;

	public static final Codec<CastingRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							StaticPowerIngredient.CODEC.fieldOf("mold").forGetter(recipe -> recipe.getRequiredMold()),
							StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
							FluidIngredient.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.getInputFluid()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing")
									.forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, CastingRecipe::new));

	private FluidIngredient inputFluid;
	private StaticPowerIngredient requiredMold;
	private StaticPowerOutputItem outputItemStack;

	public CastingRecipe(ResourceLocation name, StaticPowerIngredient mold, StaticPowerOutputItem output,
			FluidIngredient inputFluid, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.inputFluid = inputFluid;
		requiredMold = mold;
		outputItemStack = output;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems() && matchParams.hasItems()) {
			matched &= requiredMold.test(matchParams.getItems()[0]);
		}

		// Check fluids.
		if (matchParams.shouldVerifyFluids() && matchParams.hasFluids()) {
			matched &= inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
		}

		return matched;

	}

	public FluidIngredient getInputFluid() {
		return inputFluid;
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
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(() -> DEFAULT_PROCESSING_TIME, () -> DEFAULT_POWER_COST,
				() -> 0.0f, () -> 0.0f);
	}

	@Override
	public RecipeSerializer<CastingRecipe> getSerializer() {
		return ModRecipeSerializers.CASTING_SERIALIZER.get();
	}

	@Override
	public RecipeType<CastingRecipe> getType() {
		return ModRecipeTypes.CASTING_RECIPE_TYPE.get();
	}
}
