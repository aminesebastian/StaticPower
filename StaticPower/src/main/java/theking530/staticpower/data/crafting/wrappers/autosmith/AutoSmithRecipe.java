package theking530.staticpower.data.crafting.wrappers.autosmith;

import java.util.Collections;
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
import net.minecraft.world.level.Level;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class AutoSmithRecipe extends AbstractMachineRecipe {
	public static final String ID = "auto_smith";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<AutoSmithRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
			StaticPowerIngredient.CODEC.optionalFieldOf("target", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getSmithTarget()),
			StaticPowerIngredient.CODEC.optionalFieldOf("input_item", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getModifierMaterial()),
			FluidIngredient.CODEC.optionalFieldOf("input_fluid", FluidIngredient.EMPTY).forGetter(recipe -> recipe.getModifierFluid()),
			RecipeAttributeWrapper.CODEC.listOf().optionalFieldOf("attributes", Collections.emptyList()).forGetter(recipe -> recipe.getModifiers()),
			Codec.INT.optionalFieldOf("repair_amount", 0).forGetter(recipe -> recipe.getRepairAmount()),
			MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, AutoSmithRecipe::new));

	@Nullable
	private final StaticPowerIngredient smithTarget;
	private final StaticPowerIngredient modifierMaterial;
	private final FluidIngredient modifierFluid;
	private final List<RecipeAttributeWrapper<?>> attributeModifiers;
	private final int repairAmount;

	public AutoSmithRecipe(ResourceLocation id, @Nullable StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidIngredient modifierFluid,
			List<RecipeAttributeWrapper<?>> attributeModifiers, int repairAmount, MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.modifierMaterial = modifierMaterial;
		this.smithTarget = smithTarget;
		this.modifierFluid = modifierFluid;
		this.attributeModifiers = attributeModifiers;
		this.repairAmount = repairAmount;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
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
				for (RecipeAttributeWrapper wrapper : attributeModifiers) {
					if (attributable.hasAttribute(wrapper.getAttributeType())) {
						AttributeInstance attribute = attributable.getAttribute(wrapper.getAttributeType());
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

	public List<RecipeAttributeWrapper<?>> getModifiers() {
		return attributeModifiers;
	}

	public boolean hasModifiers() {
		return attributeModifiers.size() > 0;
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
			for (RecipeAttributeWrapper modifier : getModifiers()) {
				if (attributable.hasAttribute(modifier.getAttributeType())) {
					AttributeInstance<?> attribute = attributable.getAttribute(modifier.getAttributeType());
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
		for (RecipeAttributeWrapper modifier : getModifiers()) {
			if (attributable.hasAttribute(modifier.getAttributeType())) {
				AttributeInstance attribute = attributable.getAttribute(modifier.getAttributeType());
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

	public static class RecipeAttributeWrapper<T> {
		public static final Codec<RecipeAttributeWrapper<?>> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
			try {
				RecipeAttributeWrapper<?> ingredient = RecipeAttributeWrapper.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue());
				return DataResult.success(ingredient);
			} catch (Exception e) {
				return DataResult.error(e.getMessage());
			}
		}, ingredient -> new Dynamic<JsonElement>(JsonOps.INSTANCE, ingredient.toJson()));

		private final AttributeType<T> attributeType;
		private final AttributeModifierInstance<T> modifier;

		private RecipeAttributeWrapper(AttributeType<T> attributeType, AttributeModifierInstance<T> modifier) {
			this.attributeType = attributeType;
			this.modifier = modifier;
		}

		public static <T> RecipeAttributeWrapper<T> create(AttributeType<T> type, AttributeModifierType<T> modifierType, T value) {
			return new RecipeAttributeWrapper<T>(type, modifierType.create(value));
		}

		public AttributeType<?> getAttributeType() {
			return attributeType;
		}

		@SuppressWarnings("unchecked")
		public static <T> RecipeAttributeWrapper<T> fromJson(JsonElement element) {
			if (!(element instanceof JsonObject)) {
				throw new RuntimeException(String.format("Unable to deserialize modifier from Json: %1$s. Expected an object.", element));
			}

			JsonObject json = (JsonObject) element;
			ResourceLocation attributeType = new ResourceLocation(json.get("type").getAsString());
			AttributeType<T> attribute = (AttributeType<T>) StaticCoreRegistries.ATTRIBUTE_TYPE.getValue(attributeType);

			AttributeModifierInstance<T> modifierInstance = AttributeModifierInstance.deserializeFromJson(json.get("modifier").getAsJsonObject());
			return new RecipeAttributeWrapper<T>(attribute, modifierInstance);
		}

		public JsonElement toJson() {
			JsonObject output = new JsonObject();

			output.addProperty("type", StaticCoreRegistries.ATTRIBUTE_TYPE.getKey(attributeType).toString());
			output.add("modifier", getModifier().serializeToJson());

			return output;
		}

		@SuppressWarnings("unchecked")
		public static <T> RecipeAttributeWrapper<T> readFromBuffer(FriendlyByteBuf buffer) {
			ResourceLocation attributeKey = new ResourceLocation(buffer.readUtf());
			AttributeType<T> type = (AttributeType<T>) StaticCoreRegistries.ATTRIBUTE_TYPE.getValue(attributeKey);

			CompoundTag data = buffer.readNbt();
			AttributeModifierInstance<T> modifier = AttributeModifierInstance.deserialize(data);
			return new RecipeAttributeWrapper<T>(type, modifier);
		}

		public void writeToBuffer(FriendlyByteBuf buffer) {
			buffer.writeUtf(StaticCoreRegistries.ATTRIBUTE_TYPE.getKey(attributeType).toString());

			CompoundTag modifierData = new CompoundTag();
			modifier.serialize(modifierData);
			buffer.writeNbt(modifierData);
		}

		public AttributeModifierInstance<?> getModifier() {
			return modifier;
		}
	}

}
