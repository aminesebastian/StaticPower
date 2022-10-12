package theking530.staticpower.data.crafting.wrappers.autosmith;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;
import theking530.api.attributes.modifiers.AbstractAttributeModifier;
import theking530.api.attributes.registration.AttributeModifierRegistry;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class AutoSmithRecipe extends AbstractMachineRecipe {
	public static final String ID = "auto_smith";
	public static final RecipeType<AutoSmithRecipe> RECIPE_TYPE = new StaticPowerRecipeType<AutoSmithRecipe>();

	@Nullable
	private final StaticPowerIngredient smithTarget;
	private final StaticPowerIngredient modifierMaterial;
	private final FluidStack modifierFluid;
	private final RecipeModifierWrapper[] modifiers;
	private final int repairAmount;

	public AutoSmithRecipe(ResourceLocation name, @Nullable StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidStack modifierFluid,
			RecipeModifierWrapper[] modifiers, int repairAmount, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.modifierMaterial = modifierMaterial;
		this.smithTarget = smithTarget;
		this.modifierFluid = modifierFluid;
		this.modifiers = modifiers;
		this.repairAmount = repairAmount;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.shouldVerifyItems()) {
			// Check if items are supplied.
			if (!matchParams.hasItems()) {
				return false;
			}

			if (matchParams.getItems().length != 2) {
				return false;
			}

			if (matchParams.getItems()[0].isEmpty() || matchParams.getItems()[1].isEmpty()) {
				return false;
			}

			// Check the smithing target if this recipes is restricted to specific items.
			if (!isWildcardRecipe() && !smithTarget.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}

			// Check the smithing material.
			if (!modifierMaterial.isEmpty()) {
				if (!modifierMaterial.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts())) {
					return false;
				}
			}

			// Check if the input is attributable and if any of the modifiers can be
			// applied.
			boolean appliedModifierIfRequested = false;
			if (hasModifiers()) {
				IAttributable attributable = matchParams.getItems()[0].getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
				if (attributable == null) {
					return false;
				}

				// Check if the item has any of the attributes.
				for (RecipeModifierWrapper wrapper : modifiers) {
					if (attributable.hasAttribute(wrapper.getAttributeId())) {
						AbstractAttributeDefenition attribute = attributable.getAttribute(wrapper.getAttributeId());
						if (attribute.canAcceptModifier(attributable, wrapper.getModifier())) {
							appliedModifierIfRequested = true;
							break;
						}
					}
				}

				// Check if an attribute modifier was acceptable.
				if (!appliedModifierIfRequested) {
					return false;
				}
			}

			// Check if this recipe performs a repair.
			if (performsRepair()) {
				// See if the item input is repairable and if it has any damage.
				boolean canRepair = matchParams.getItems()[0].getDamageValue() < matchParams.getItems()[0].getMaxDamage();

				// If it is not repairable AND no modifiers can be applied, then this recipe can
				// do nothing. Return false.
				if (!canRepair && !appliedModifierIfRequested) {
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

	public RecipeModifierWrapper[] getModifiers() {
		return modifiers;
	}

	public boolean hasModifiers() {
		return modifiers.length > 0;
	}

	public boolean performsRepair() {
		return repairAmount > 0;
	}

	public int getRepairAmount() {
		return repairAmount;
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
	public RecipeSerializer<AutoSmithRecipe> getSerializer() {
		return AutoSmithRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<AutoSmithRecipe> getType() {
		return RECIPE_TYPE;
	}

	public boolean canApplyToItemStack(ItemStack stack) {
		IAttributable attributable = stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
		if (attributable == null) {
			return false;
		}
		return canApplyToAttributable(attributable);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean applyToItemStack(ItemStack stack) {
		// Try to get the attributable. If the input has no attributable AND it does not
		// perform a repair, return false.
		IAttributable attributable = stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
		if (attributable == null && !performsRepair()) {
			return false;
		}

		// Flag to see if anything was modified or repaired.
		boolean applied = false;

		// Perform the attributable modification if applicable.
		if (attributable != null) {
			// Apply the modifiers and indicate that one was applied.
			for (RecipeModifierWrapper modifier : getModifiers()) {
				if (attributable.hasAttribute(modifier.getAttributeId())) {
					AbstractAttributeDefenition attribute = attributable.getAttribute(modifier.getAttributeId());
					if (attribute.canAcceptModifier(attributable, modifier.getModifier())) {
						attribute.addModifier(modifier.getModifier(), false);
						applied = true;
					}
				}
			}
		}

		// Attempt a repair if this recipe can perform one.
		if (performsRepair()) {
			if (stack.isRepairable() && stack.getDamageValue() > 0) {
				// NO need to zero check here, the #setDamage method already does so.
				stack.setDamageValue(stack.getDamageValue() - this.getRepairAmount());
				applied = true;
			}
		}

		return applied;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean canApplyToAttributable(IAttributable attributable) {
		for (RecipeModifierWrapper modifier : getModifiers()) {
			if (attributable.hasAttribute(modifier.getAttributeId())) {
				AbstractAttributeDefenition attribute = attributable.getAttribute(modifier.getAttributeId());
				if (attribute.canAcceptModifier(attributable, modifier.getModifier())) {
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

		public RecipeModifierWrapper(FriendlyByteBuf buffer) {
			CompoundTag data = buffer.readNbt();
			this.attributeId = new ResourceLocation(data.getString("attribute_id"));
			this.modifier = AttributeModifierRegistry.createInstance(data.getCompound("modifier"));
		}

		public CompoundTag serialize() {
			CompoundTag output = new CompoundTag();
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
