package theking530.staticpower.data.crafting.wrappers.packager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class PackagerRecipe extends AbstractMachineRecipe {
	public static final String ID = "packager";
	public static final RecipeType<PackagerRecipe> RECIPE_TYPE = new StaticPowerRecipeType<PackagerRecipe>();

	private final StaticPowerIngredient inputItem;
	private final StaticPowerOutputItem outputItem;
	private final int size;

	public PackagerRecipe(ResourceLocation name, int size, StaticPowerIngredient input, StaticPowerOutputItem outputItem, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.size = size;
		this.inputItem = input;
		this.outputItem = outputItem;
	}

	public StaticPowerOutputItem getOutput() {
		return outputItem;
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputItem;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check the size param.
		if (matchParams.hasCustomParameter("size")) {
			int paramSize = matchParams.getCustomParameterContainer().getInt("size");
			if (paramSize != size) {
				return false;
			}
		} else {
			return false;
		}

		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			matched &= matchParams.hasItems() && inputItem.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
		}

		return matched;
	}

	@Override
	public RecipeSerializer<PackagerRecipe> getSerializer() {
		return PackagerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<PackagerRecipe> getType() {
		return RECIPE_TYPE;
	}
}