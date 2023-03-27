package theking530.staticpower.integration.JEI.ingredients.power;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.StaticPower;
import theking530.staticpower.integration.JEI.PluginJEI;

public class PowerIngredientHelper implements IIngredientHelper<MachineRecipeProcessingSection> {
	private static final ResourceLocation REGISTRY_NAME = new ResourceLocation(StaticPower.MOD_ID, "power");

	public PowerIngredientHelper(IModIngredientRegistration registration) {
	}

	@Override
	public String getDisplayName(MachineRecipeProcessingSection ingredient) {
		Component displayNameTextComponent = Component.translatable("gui.staticcore.power");
		return displayNameTextComponent.getString();
	}

	@Override
	public String getDisplayModId(MachineRecipeProcessingSection ingredient) {
		return StaticPower.MOD_ID;
	}

	@Override
	public ResourceLocation getResourceLocation(MachineRecipeProcessingSection ingredient) {
		return REGISTRY_NAME;
	}

	@Override
	public MachineRecipeProcessingSection copyIngredient(MachineRecipeProcessingSection ingredient) {
		return ingredient.copy();
	}

	@Override
	public String getErrorInfo(@Nullable MachineRecipeProcessingSection ingredient) {
		if (ingredient == null) {
			return "null";
		}
		return "";
	}

	@Override
	public IIngredientType<MachineRecipeProcessingSection> getIngredientType() {
		return PluginJEI.POWER_INGREDIENT;
	}

	@Override
	public String getUniqueId(MachineRecipeProcessingSection ingredient, UidContext context) {
		return "Power";
	}
}