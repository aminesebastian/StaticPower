package theking530.staticpower.data.crafting.wrappers.lumbermill;

import java.util.LinkedList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class LumberMillRecipe extends AbstractMachineRecipe {
	public static final String ID = "lumber_mill";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<LumberMillRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("primary_output_item", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getPrimaryOutput()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("secondary_output_item", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getSecondaryOutput()),
					JsonUtilities.FLUIDSTACK_CODEC.optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(recipe -> recipe.getOutputFluid()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, LumberMillRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem primaryOutput;
	private final StaticPowerOutputItem secondaryOutput;
	private final FluidStack outputFluid;

	public LumberMillRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput, FluidStack outputFluid,
			MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getPrimaryOutput() {
		return primaryOutput;
	}

	public StaticPowerOutputItem getSecondaryOutput() {
		return secondaryOutput;
	}

	public List<ItemStack> getRawOutputItems() {
		List<ItemStack> output = new LinkedList<ItemStack>();
		output.add(getPrimaryOutput().getItemStack());
		if (hasSecondaryOutput()) {
			output.add(getSecondaryOutput().getItemStack());
		}
		return output;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public boolean hasSecondaryOutput() {
		return !secondaryOutput.isEmpty();
	}

	public boolean hasOutputFluid() {
		return !outputFluid.isEmpty();
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
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
	public RecipeSerializer<LumberMillRecipe> getSerializer() {
		return ModRecipeSerializers.LUMBER_MILL_SERIALIZER.get();
	}

	@Override
	public RecipeType<LumberMillRecipe> getType() {
		return ModRecipeTypes.LUMBER_MILL_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
