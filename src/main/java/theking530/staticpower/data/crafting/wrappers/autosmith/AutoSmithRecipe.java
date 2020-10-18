package theking530.staticpower.data.crafting.wrappers.autosmith;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.itemattributes.attributes.AttributeModifierRegistry;
import theking530.api.itemattributes.attributes.modifiers.AbstractAttributeModifier;
import theking530.api.itemattributes.capability.CapabilityAttributable;
import theking530.api.itemattributes.capability.IAttributable;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class AutoSmithRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<AutoSmithRecipe> RECIPE_TYPE = IRecipeType.register("auto_smith");

	@Nullable
	private final StaticPowerIngredient smithTarget;
	private final StaticPowerIngredient modifierMaterial;
	private final FluidStack modifierFluid;
	private final RecipeModifierWrapper[] modifiers;

	public AutoSmithRecipe(ResourceLocation name, @Nullable StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidStack modifierFluid,
			RecipeModifierWrapper[] modifiers, int powerCost, int processingTime) {
		super(name, processingTime, powerCost);
		this.modifierMaterial = modifierMaterial;
		this.smithTarget = smithTarget;
		this.modifierFluid = modifierFluid;
		this.modifiers = modifiers;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.shouldVerifyItems()) {
			// Check if items are supplied.
			if (!matchParams.hasItems()) {
				return false;
			}

			// Check the smithing target if this recipes is restricted to specific items.
			if (isWildcardRecipe() && !smithTarget.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}

			// Check the smithing material.
			if (!modifierMaterial.isEmpty()) {
				if (matchParams.getItems().length < 2 || !modifierMaterial.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts())) {
					return false;
				}
			}

			// Check if the input is attributable.
			IAttributable attributable = matchParams.getItems()[0].getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
			if (attributable == null) {
				return false;
			}

			// Check if the item has any of the attributes.
			boolean wasApplied = false;
			for (RecipeModifierWrapper wrapper : modifiers) {
				if (attributable.hasAttribute(wrapper.getAttributeId())) {
					if (attributable.getAttribute(wrapper.getAttributeId()).canAcceptModifier(wrapper.getModifier())) {
						wasApplied = true;
						break;
					}
				}
			}

			// Check if an attribute modifier was acceptable.
			if (!wasApplied) {
				return false;
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

	public RecipeModifierWrapper[] getModifiers() {
		return modifiers;
	}

	public boolean isWildcardRecipe() {
		return smithTarget.isEmpty();
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

	public boolean applyToItemStack(ItemStack stack) {
		IAttributable attributable = stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
		if (attributable == null) {
			return false;
		}

		// Flag to see if anything was modified.
		boolean applied = false;

		// Apply the modifiers and indicate that one was applied.
		for (RecipeModifierWrapper modifier : getModifiers()) {
			if (attributable.hasAttribute(modifier.getAttributeId())) {
				if (attributable.getAttribute(modifier.getAttributeId()).canAcceptModifier(modifier.getModifier())) {
					attributable.getAttribute(modifier.getAttributeId()).addModifier(modifier.getModifier(), false);
					applied = true;
				}
			}
		}

		return applied;
	}

	public boolean canApplyToAttributable(IAttributable attributable) {
		for (RecipeModifierWrapper modifier : getModifiers()) {
			if (attributable.hasAttribute(modifier.getAttributeId())) {
				if (attributable.getAttribute(modifier.getAttributeId()).canAcceptModifier(modifier.getModifier())) {
					return true;
				}
			}
		}
		return false;
	}

	public static class RecipeModifierWrapper {
		private final ResourceLocation attributeId;
		private final AbstractAttributeModifier<?> modifier;

		public RecipeModifierWrapper(ResourceLocation attributeId, AbstractAttributeModifier<?> modifier) {
			this.attributeId = attributeId;
			this.modifier = modifier;
		}

		public RecipeModifierWrapper(JsonObject json) {
			this.attributeId = new ResourceLocation(json.get("id").getAsString());
			this.modifier = AttributeModifierRegistry.createInstance(json.get("modifier").getAsJsonObject());
		}

		public RecipeModifierWrapper(PacketBuffer buffer) {
			this.attributeId = new ResourceLocation(buffer.readString());
			this.modifier = AttributeModifierRegistry.createInstance(buffer.readCompoundTag());
		}

		public CompoundNBT serialize() {
			CompoundNBT output = new CompoundNBT();
			output.putString("attribute_id", attributeId.toString());
			output.put("modifier", modifier.serialize());
			return output;
		}

		public ResourceLocation getAttributeId() {
			return attributeId;
		}

		public AbstractAttributeModifier<?> getModifier() {
			return modifier;
		}
	}
}
