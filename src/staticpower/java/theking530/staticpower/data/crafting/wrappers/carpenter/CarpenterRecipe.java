package theking530.staticpower.data.crafting.wrappers.carpenter;

import java.util.LinkedList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.ShapedRecipePattern;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CarpenterRecipe extends AbstractMachineRecipe {
	public static final String ID = "carpenter";

	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<CarpenterRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					ShapedRecipePattern.CODEC.fieldOf("input_items").forGetter(recipe -> recipe.getPattern()),
					StaticPowerOutputItem.CODEC.fieldOf("primary_output_item").forGetter(recipe -> recipe.getPrimaryOutput()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("secondary_output_item", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getSecondaryOutput()),
					JsonUtilities.FLUIDSTACK_CODEC.optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(recipe -> recipe.getOutputFluid()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, CarpenterRecipe::new));

	private final ShapedRecipePattern pattern;
	private final StaticPowerOutputItem primaryOutput;
	private final StaticPowerOutputItem secondaryOutput;
	private final FluidStack outputFluid;

	public CarpenterRecipe(ResourceLocation name, ShapedRecipePattern pattern, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput, FluidStack outputFluid,
			MachineRecipeProcessingSection processing) {
		super(name, processing);

		this.pattern = pattern;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
	}

	public ShapedRecipePattern getPattern() {
		return pattern;
	}

	public NonNullList<StaticPowerIngredient> getInputs() {
		return pattern.getIngredients();
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
	public boolean isValid(RecipeMatchParameters matchParams) {
		// If there aren't enough items, just return false.
		if (matchParams.getItems().length != 9) {
			return false;
		}

		for (int i = 0; i < pattern.getRecipeWidth(); i++) {
			for (int j = 0; j < pattern.getRecipeHeight(); j++) {
				if (!pattern.getIngredients().get(i + j * pattern.getRecipeWidth()).testWithCount(matchParams.getItems()[i + j * pattern.getRecipeWidth()])) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public RecipeSerializer<CarpenterRecipe> getSerializer() {
		return ModRecipeSerializers.LATHE_SERIALIZER.get();
	}

	@Override
	public RecipeType<CarpenterRecipe> getType() {
		return ModRecipeTypes.LATHE_RECIPE_TYPE.get();
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(IItemHandler inv, Level worldIn) {
		for (int i = 0; i <= pattern.getRecipeWidth(); ++i) {
			for (int j = 0; j <= pattern.getRecipeHeight(); ++j) {
				if (this.checkMatch(inv, i, j, true)) {
					return true;
				}

				if (this.checkMatch(inv, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	private boolean checkMatch(IItemHandler craftingInventory, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
		for (int i = 0; i < pattern.getRecipeWidth(); ++i) {
			for (int j = 0; j < pattern.getRecipeHeight(); ++j) {
				int k = i - p_77573_2_;
				int l = j - p_77573_3_;
				StaticPowerIngredient ingredient = StaticPowerIngredient.EMPTY;
				if (k >= 0 && l >= 0 && k < this.pattern.getRecipeWidth() && l < pattern.getRecipeHeight()) {
					if (p_77573_4_) {
						ingredient = this.pattern.getIngredients().get(pattern.getRecipeWidth() - k - 1 + l * pattern.getRecipeWidth());
					} else {
						ingredient = this.pattern.getIngredients().get(k + l * pattern.getRecipeWidth());
					}
				}

				if (!ingredient.testWithCount(craftingInventory.getStackInSlot(i + j * pattern.getRecipeWidth()))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}

}
