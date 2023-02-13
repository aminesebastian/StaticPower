package theking530.staticpower.integration.JEI.ingredients.probabilitystack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.integration.JEI.JEIErrorUtilSnippet;
import theking530.staticpower.integration.JEI.PluginJEI;

public class ProbabilityItemStackHelper implements IIngredientHelper<ProbabilityItemStackOutput> {
	private final ISubtypeManager subtypeManager;

	public ProbabilityItemStackHelper(IModIngredientRegistration registration) {
		subtypeManager = registration.getSubtypeManager();
	}

	@Override
	public String getDisplayName(ProbabilityItemStackOutput ingredient) {
		Component displayNameTextComponent = ingredient.getItem().getHoverName();
		String displayName = displayNameTextComponent.getString();
		JEIErrorUtilSnippet.checkNotNull(displayName, "itemStack.getDisplayName()");
		return displayName;
	}

	@Override
	public String getDisplayModId(ProbabilityItemStackOutput ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItem());

		Item item = ingredient.getItem().getItem();
		String modId = item.getCreatorModId(ingredient.getItem());
		if (modId == null) {
			String stackInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("item.getCreatorModId() returned null for: " + stackInfo);
		}
		return modId;
	}

	@Override
	public Iterable<Integer> getColors(ProbabilityItemStackOutput ingredient) {
		return Collections.emptyList();
	}

	@Override
	public ResourceLocation getResourceLocation(ProbabilityItemStackOutput ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItem());

		Item item = ingredient.getItem().getItem();
		ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
		if (itemName == null) {
			String stackInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("item.getRegistryName() returned null for: " + stackInfo);
		}

		return itemName;
	}

	@Override
	public ItemStack getCheatItemStack(ProbabilityItemStackOutput ingredient) {
		return ingredient.getItem();
	}

	@Override
	public ProbabilityItemStackOutput copyIngredient(ProbabilityItemStackOutput ingredient) {
		return ingredient.copy();
	}

	@Override
	public ProbabilityItemStackOutput normalizeIngredient(ProbabilityItemStackOutput ingredient) {
		ProbabilityItemStackOutput copy = ingredient.copy();
		copy.setCount(1);
		return copy;
	}

	@Override
	public boolean isValidIngredient(ProbabilityItemStackOutput ingredient) {
		return !ingredient.isEmpty();
	}

	@Override
	public boolean isIngredientOnServer(ProbabilityItemStackOutput ingredient) {
		Item item = ingredient.getItem().getItem();
		return ForgeRegistries.ITEMS.containsValue(item);
	}

	@Override
	public Collection<ResourceLocation> getTags(ProbabilityItemStackOutput ingredient) {
		return ModItemTags.getTags(ingredient.getItem()).stream().map((key) -> key.location()).toList();
	}

	@Override
	public Collection<String> getCreativeTabNames(ProbabilityItemStackOutput ingredient) {
		Collection<String> creativeTabsStrings = new ArrayList<>();
		Item item = ingredient.getItem().getItem();
		for (CreativeModeTab itemGroup : item.getCreativeTabs()) {
			if (itemGroup != null) {
				String creativeTabName = itemGroup.getDisplayName().getString();
				creativeTabsStrings.add(creativeTabName);
			}
		}
		return creativeTabsStrings;
	}

	@Override
	public String getErrorInfo(@Nullable ProbabilityItemStackOutput ingredient) {
		return JEIErrorUtilSnippet.getItemStackInfo(ingredient.getItem());
	}

	@Override
	public IIngredientType<ProbabilityItemStackOutput> getIngredientType() {
		return PluginJEI.PROBABILITY_ITEM_STACK;
	}

	@Override
	public String getUniqueId(ProbabilityItemStackOutput ingredient, UidContext context) {
		String result = getResourceLocation(ingredient).toString();
		String subtypeInfo = subtypeManager.getSubtypeInfo(ingredient.getItem(), context);
		if (subtypeInfo != null && !subtypeInfo.isEmpty()) {
			result = result + ':' + subtypeInfo;
		}
		return result;
	}

}