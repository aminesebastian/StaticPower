package theking530.staticpower.data.crafting.wrappers.autosmith;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.smithingattributes.AbstractAttributeModifier;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class AutoSmithRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<AutoSmithRecipe> RECIPE_TYPE = IRecipeType.register("auto_smith");

	private final StaticPowerIngredient smithTarget;
	private final StaticPowerIngredient modifierMaterial;
	private final FluidStack modifierFluid;
	private final AbstractAttributeModifier[] modifiers;

	public AutoSmithRecipe(ResourceLocation name, StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidStack modifierFluid, AbstractAttributeModifier[] modifiers, int powerCost,
			int processingTime) {
		super(name, processingTime, powerCost);
		this.modifierMaterial = modifierMaterial;
		this.smithTarget = smithTarget;
		this.modifierFluid = modifierFluid;
		this.modifiers = modifiers;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.shouldVerifyItems()) {
			if (!matchParams.hasItems()) {
				return false;
			}

			if (!smithTarget.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}

			if (matchParams.getItems().length == 2) {
				if (!modifierMaterial.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts())) {
					return false;
				}
			}
		}

		if (matchParams.shouldVerifyFluids()) {
			if (!matchParams.hasFluids()) {
				return false;
			}
			if (!matchParams.getFluids()[0].isFluidEqual(matchParams.getFluids()[0])) {
				return false;
			}
			if (matchParams.shouldVerifyFluidAmounts()) {
				if (matchParams.getFluids()[0].getAmount() < modifierFluid.getAmount()) {
					return false;
				}
			}
		}

		return true;
	}

	public AbstractAttributeModifier[] getModifiers() {
		return modifiers;
	}

	public StaticPowerIngredient getSmithTarget() {
		return smithTarget;
	}

	public StaticPowerIngredient getModifierMaterial() {
		return modifierMaterial;
	}

	public FluidStack getModifierFluid() {
		return modifierFluid;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return AutoSmithRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
