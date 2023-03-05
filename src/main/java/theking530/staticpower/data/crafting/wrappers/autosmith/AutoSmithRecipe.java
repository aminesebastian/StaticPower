package theking530.staticpower.data.crafting.wrappers.autosmith;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;
import theking530.api.attributes.modifiers.AbstractAttributeModifier;
import theking530.api.attributes.registration.AttributeModifierRegistry;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class AutoSmithRecipe extends AbstractMachineRecipe {
	public static final String ID = "auto_smith";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<AutoSmithRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.optionalFieldOf("smith_target", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getSmithTarget()),
					StaticPowerIngredient.CODEC.fieldOf("modifier_item").forGetter(recipe -> recipe.getModifierMaterial()),
					FluidIngredient.CODEC.fieldOf("modifier_fluid").forGetter(recipe -> recipe.getModifierFluid()),
					RecipeModifierWrapper.CODEC.listOf().fieldOf("modifiers").forGetter(recipe -> recipe.getModifiers()),
					Codec.INT.fieldOf("repair_amount").forGetter(recipe -> recipe.getRepairAmount()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, AutoSmithRecipe::new));

	@Nullable
	private final StaticPowerIngredient smithTarget;
	private final StaticPowerIngredient modifierMaterial;
	private final FluidIngredient modifierFluid;
	private final List<RecipeModifierWrapper> modifiers;
	private final int repairAmount;

	public AutoSmithRecipe(ResourceLocation id, @Nullable StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidIngredient modifierFluid,
			List<RecipeModifierWrapper> modifiers, int repairAmount, MachineRecipeProcessingSection processing) {
		super(id, processing);
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

			if (!modifierFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
				return false;
			}
		}

		return true;
	}

	public List<RecipeModifierWrapper> getModifiers() {
		return modifiers;
	}

	public boolean hasModifiers() {
		return modifiers.size() > 0;
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

	public FluidIngredient getModifierFluid() {
		return modifierFluid;
	}

	@Override
	public RecipeSerializer<AutoSmithRecipe> getSerializer() {
		return ModRecipeSerializers.AUTO_SMITH_SERIALIZER.get();
	}

	@Override
	public RecipeType<AutoSmithRecipe> getType() {
		return ModRecipeTypes.AUTO_SMITH_RECIPE_TYPE.get();
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

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}

	public static class RecipeModifierWrapper {
		public static final Codec<RecipeModifierWrapper> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
			try {
				RecipeModifierWrapper ingredient = RecipeModifierWrapper.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue());
				return DataResult.success(ingredient);
			} catch (Exception e) {
				return DataResult.error(e.getMessage());
			}
		}, ingredient -> new Dynamic<JsonElement>(JsonOps.INSTANCE, ingredient.toJson()));

		private final ResourceLocation attributeId;
		private final AbstractAttributeModifier<?> modifier;

		public RecipeModifierWrapper(ResourceLocation attributeId, AbstractAttributeModifier<?> modifier) {
			this.attributeId = attributeId;
			this.modifier = modifier;
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

		public static RecipeModifierWrapper fromJson(JsonElement element) {
			if (!(element instanceof JsonObject)) {
				throw new RuntimeException(String.format("Unable to deserialize modifier from Json: %1$s. Expected an object.", element));
			}

			JsonObject json = (JsonObject) element;

			ResourceLocation attributeId = new ResourceLocation(json.get("id").getAsString());
			AbstractAttributeModifier<?> modifier = AttributeModifierRegistry.createInstance(json.get("modifier").getAsJsonObject());
			return new RecipeModifierWrapper(attributeId, modifier);
		}

		public JsonElement toJson() {
			JsonObject output = new JsonObject();
			output.addProperty("id", attributeId.toString());
			output.addProperty("modifier", modifier.getType());
			return output;
		}

		public static RecipeModifierWrapper readFromBuffer(FriendlyByteBuf buffer) {
			CompoundTag data = buffer.readNbt();
			ResourceLocation attributeId = new ResourceLocation(data.getString("attribute_id"));
			AbstractAttributeModifier<?> modifier = AttributeModifierRegistry.createInstance(data.getCompound("modifier"));
			return new RecipeModifierWrapper(attributeId, modifier);
		}

		public void writeToBuffer(FriendlyByteBuf buffer) {
			buffer.writeNbt(modifier.serialize());
		}

		public AbstractAttributeModifier<?> getModifier() {
			return modifier;
		}
	}

}
