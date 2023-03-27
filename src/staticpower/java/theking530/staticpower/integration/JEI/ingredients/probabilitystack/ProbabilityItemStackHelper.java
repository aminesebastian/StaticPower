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
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.integration.JEI.JEIErrorUtilSnippet;
import theking530.staticpower.integration.JEI.PluginJEI;

public class ProbabilityItemStackHelper implements IIngredientHelper<StaticPowerOutputItem> {
	private final ISubtypeManager subtypeManager;

	public ProbabilityItemStackHelper(IModIngredientRegistration registration) {
		subtypeManager = registration.getSubtypeManager();
	}

	@Override
	public String getDisplayName(StaticPowerOutputItem ingredient) {
		Component displayNameTextComponent = ingredient.getItemStack().getHoverName();
		String displayName = displayNameTextComponent.getString();
		JEIErrorUtilSnippet.checkNotNull(displayName, "itemStack.getDisplayName()");
		return displayName;
	}

	@Override
	public String getDisplayModId(StaticPowerOutputItem ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItemStack());

		Item item = ingredient.getItemStack().getItem();
		String modId = item.getCreatorModId(ingredient.getItemStack());
		if (modId == null) {
			String stackInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("item.getCreatorModId() returned null for: " + stackInfo);
		}
		return modId;
	}

	@Override
	public Iterable<Integer> getColors(StaticPowerOutputItem ingredient) {
		return Collections.emptyList();
	}

	@Override
	public ResourceLocation getResourceLocation(StaticPowerOutputItem ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItemStack());

		Item item = ingredient.getItemStack().getItem();
		ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
		if (itemName == null) {
			String stackInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("item.getRegistryName() returned null for: " + stackInfo);
		}

		return itemName;
	}

	@Override
	public ItemStack getCheatItemStack(StaticPowerOutputItem ingredient) {
		return ingredient.getItemStack();
	}

	@Override
	public StaticPowerOutputItem copyIngredient(StaticPowerOutputItem ingredient) {
		return ingredient.copy();
	}

	@Override
	public StaticPowerOutputItem normalizeIngredient(StaticPowerOutputItem ingredient) {
		StaticPowerOutputItem copy = ingredient.copy();
		copy.setCount(1);
		return copy;
	}

	@Override
	public boolean isValidIngredient(StaticPowerOutputItem ingredient) {
		return !ingredient.isEmpty();
	}

	@Override
	public boolean isIngredientOnServer(StaticPowerOutputItem ingredient) {
		Item item = ingredient.getItemStack().getItem();
		return ForgeRegistries.ITEMS.containsValue(item);
	}

	@Override
	public Collection<ResourceLocation> getTags(StaticPowerOutputItem ingredient) {
		return ModItemTags.getTags(ingredient.getItemStack()).stream().map((key) -> key.location()).toList();
	}

	@Override
	public Collection<String> getCreativeTabNames(StaticPowerOutputItem ingredient) {
		Collection<String> creativeTabsStrings = new ArrayList<>();
		Item item = ingredient.getItemStack().getItem();
		for (CreativeModeTab itemGroup : item.getCreativeTabs()) {
			if (itemGroup != null) {
				String creativeTabName = itemGroup.getDisplayName().getString();
				creativeTabsStrings.add(creativeTabName);
			}
		}
		return creativeTabsStrings;
	}

	@Override
	public String getErrorInfo(@Nullable StaticPowerOutputItem ingredient) {
		return JEIErrorUtilSnippet.getItemStackInfo(ingredient.getItemStack());
	}

	@Override
	public IIngredientType<StaticPowerOutputItem> getIngredientType() {
		return PluginJEI.PROBABILITY_ITEM_STACK;
	}

	@Override
	public String getUniqueId(StaticPowerOutputItem ingredient, UidContext context) {
		String result = getResourceLocation(ingredient).toString();
		String subtypeInfo = subtypeManager.getSubtypeInfo(ingredient.getItemStack(), context);
		if (subtypeInfo != null && !subtypeInfo.isEmpty()) {
			result = result + ':' + subtypeInfo;
		}
		return result;
	}

}