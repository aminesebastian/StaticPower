package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class VulcanizerRecipe extends AbstractMachineRecipe {
	public static final String ID = "vulcanizer";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<VulcanizerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getInputFluid()),
					StaticPowerOutputItem.CODEC.fieldOf("output_item").forGetter(recipe -> recipe.getOutput()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, VulcanizerRecipe::new));

	private final FluidIngredient inputFluid;
	private final StaticPowerOutputItem output;

	public VulcanizerRecipe(ResourceLocation id, FluidIngredient inputFluid, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.output = output;
		this.inputFluid = inputFluid;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public FluidIngredient getInputFluid() {
		return inputFluid;
	}

	public ItemStack getRawOutputItem() {
		return output.getItemStack();
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check fluid.
		if (matchParams.shouldVerifyFluids()) {
			matched &= matchParams.hasFluids();
			matched &= inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
		}

		return matched;
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