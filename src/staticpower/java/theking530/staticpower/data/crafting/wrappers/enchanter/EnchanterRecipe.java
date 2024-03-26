package theking530.staticpower.data.crafting.wrappers.enchanter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.EnchantmentRecipeWrapper;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class EnchanterRecipe extends AbstractMachineRecipe {
	public static final String ID = "esoteric_enchanter";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<EnchanterRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.listOf().fieldOf("input_items").forGetter(recipe -> recipe.getInputIngredients()),
					FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getInputFluidStack()),
					EnchantmentRecipeWrapper.CODEC.listOf().fieldOf("enchantments").forGetter(recipe -> recipe.getEnchantments()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, EnchanterRecipe::new));

	private final List<StaticPowerIngredient> inputIngredients;
	private final FluidIngredient inputFluidStack;
	private final List<EnchantmentRecipeWrapper> enchantments;

	public EnchanterRecipe(ResourceLocation name, List<StaticPowerIngredient> inputs, FluidIngredient inputFluid, List<EnchantmentRecipeWrapper> enchantments,
			MachineRecipeProcessingSection processing) {
		super(name, processing);
		inputIngredients = inputs;
		inputFluidStack = inputFluid;
		this.enchantments = enchantments;
	}

	public List<StaticPowerIngredient> getInputIngredients() {
		return inputIngredients;
	}

	public FluidIngredient getInputFluidStack() {
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
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
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
			if (!inputFluidStack.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public RecipeSerializer<EnchanterRecipe> getSerializer() {
		return ModRecipeSerializers.ENCHANTER_SERIALIZER.get();
	}

	@Override
	public RecipeType<EnchanterRecipe> getType() {
		return ModRecipeTypes.ENCHANTER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
