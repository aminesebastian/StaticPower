package theking530.staticpower.data.crafting.wrappers.vulcanizer;

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

public class VulcanizerRecipe extends AbstractMachineRecipe {
	public static final String ID = "vulcanizer";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<VulcanizerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.optionalFieldOf("input_item", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getInputItem()),
					FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getInputFluid()),
					StaticPowerOutputItem.CODEC.fieldOf("output_item").forGetter(recipe -> recipe.getOutput()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, VulcanizerRecipe::new));

	private final StaticPowerIngredient inputItem;
	private final FluidIngredient inputFluid;
	private final StaticPowerOutputItem output;

	public VulcanizerRecipe(ResourceLocation id, StaticPowerIngredient inputItem, FluidIngredient inputFluid, StaticPowerOutputItem output,
			MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.inputItem = inputItem;
		this.output = output;
		this.inputFluid = inputFluid;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public FluidIngredient getInputFluid() {
		return inputFluid;
	}

	public StaticPowerIngredient getInputItem() {
		return inputItem;
	}

	public boolean hasInputItem() {
		return !inputItem.isEmpty();
	}

	public ItemStack getRawOutputItem() {
		return output.getItemStack();
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {

		// Check fluid.
		if (matchParams.shouldVerifyFluids()) {
			if (!matchParams.hasFluids()) {
				return false;
			}
			if (!inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
				return false;
			}
		}

		if (hasInputItem() && matchParams.shouldVerifyItems()) {
			if (!matchParams.hasItems()) {
				return false;
			}

			if (!inputItem.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public RecipeSerializer<VulcanizerRecipe> getSerializer() {
		return ModRecipeSerializers.VULCANIZER_SERIALIZER.get();
	}

	@Override
	public RecipeType<VulcanizerRecipe> getType() {
		return ModRecipeTypes.VULCANIZER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}