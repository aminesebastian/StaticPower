package theking530.staticpower.data.crafting.wrappers.enchanter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.EnchantmentRecipeWrapper;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipeSerializer;

public class EnchanterRecipe extends AbstractMachineRecipe {
	public static final String ID = "esoteric_enchanter";
	public static final RecipeType<EnchanterRecipe> RECIPE_TYPE = new StaticPowerRecipeType<EnchanterRecipe>();

	private final List<StaticPowerIngredient> inputIngredients;
	private final FluidStack inputFluidStack;
	private final List<EnchantmentRecipeWrapper> enchantments;

	public EnchanterRecipe(ResourceLocation name, List<StaticPowerIngredient> inputs, FluidStack inputFluid, List<EnchantmentRecipeWrapper> enchantments, MachineRecipeProcessingSection processing) {
		super(name, processing);
		inputIngredients = inputs;
		inputFluidStack = inputFluid;
		this.enchantments = enchantments;
	}

	public List<StaticPowerIngredient> getInputIngredients() {
		return inputIngredients;
	}

	public FluidStack getInputFluidStack() {
		return inputFluidStack;
	}

	public List<EnchantmentRecipeWrapper> getEnchantments() {
		return enchantments;
	}

	public ItemStack getEnchantedVersion(ItemStack input) {
		ItemStack toEnchant = input.copy();
		Map<Enchantment, Integer> toPut = new HashMap<Enchantment, Integer>();

		// Go through all the enchantments and see if they can be applied to the item.
		// If they can be, add them to the toPut list.
		for (EnchantmentRecipeWrapper wrapper : enchantments) {
			if (wrapper.getEnchantment().canEnchant(toEnchant)) {
				boolean isCompatible = true;
				for (Enchantment queued : toPut.keySet()) {
					if (!wrapper.getEnchantment().isCompatibleWith(queued)) {
						isCompatible = false;
						break;
					}
				}

				if (isCompatible) {
					toPut.put(wrapper.getEnchantment(), wrapper.getLevel());
				}
			}
		}

		// Perform the enchantment.
		EnchantmentHelper.setEnchantments(toPut, toEnchant);
		return toEnchant;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check if the input counts catch. We check +1 because the match params items
		// should be the inputs PLUS the item to enchant.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.getItems().length != inputIngredients.size() + 1) {
				return false;
			}

			// Copy the inputs.
			List<ItemStack> inputCopies = new ArrayList<ItemStack>();
			for (ItemStack input : matchParams.getItems()) {
				inputCopies.add(input.copy());
			}

			// Check each item, if any fails, return false.
			int matches = 0;
			for (StaticPowerIngredient ing : inputIngredients) {
				for (int i = 0; i < inputCopies.size(); i++) {
					// Check the match.
					boolean itemMatched = false;
					if (matchParams.shouldVerifyItemCounts()) {
						itemMatched = ing.testWithCount(inputCopies.get(i));
					} else {
						itemMatched = ing.test(inputCopies.get(i));
					}

					// Check if there was a match.
					if (itemMatched) {
						inputCopies.set(i, ItemStack.EMPTY);
						matches++;
						break;
					}
				}
			}

			// Return true if we had the correct amount of matches.
			if (matches != inputIngredients.size()) {
				return false;
			}

			// Finally, check for enchantability.
			ItemStack toEnchantCopy = matchParams.getItems()[3];
			for (EnchantmentRecipeWrapper wrapper : enchantments) {
				if (!wrapper.getEnchantment().canEnchant(toEnchantCopy)) {
					return false;
				}
			}
		}

		// Now check fluids.
		if (matchParams.shouldVerifyFluids()) {
			if (!inputFluidStack.isEmpty() && !matchParams.getFluids()[0].isFluidEqual(inputFluidStack)) {
				return false;
			}
			if (matchParams.shouldVerifyFluidAmounts()) {
				if (!inputFluidStack.isEmpty() && matchParams.getFluids()[0].getAmount() < inputFluidStack.getAmount()) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FermenterRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
