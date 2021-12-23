package theking530.staticpower.integration.JEI.ingredients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.integration.JEI.JEIErrorUtilSnippet;

public class ProbabilityItemStackHelper implements IIngredientHelper<ProbabilityItemStackOutput> {
	private final ISubtypeManager subtypeManager;

	public ProbabilityItemStackHelper(IModIngredientRegistration registration) {
		subtypeManager = registration.getSubtypeManager();
	}

	@Override
	public IFocus<?> translateFocus(IFocus<ProbabilityItemStackOutput> focus, IFocusFactory focusFactory) {
		ItemStack itemStack = focus.getValue().getItem();
		Item item = itemStack.getItem();
		// Special case for ItemBlocks containing fluid blocks.
		// Nothing crafts those, the player probably wants to look up fluids.
		if (item instanceof BlockItem) {
			Block block = ((BlockItem) item).getBlock();
			if (block instanceof IFluidBlock) {
				IFluidBlock fluidBlock = (IFluidBlock) block;
				Fluid fluid = fluidBlock.getFluid();
				if (fluid != null) {
					FluidStack fluidStack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
					return focusFactory.createFocus(focus.getMode(), fluidStack);
				}
			}
		}
		return focus;
	}

	@Override
	@Nullable
	public ProbabilityItemStackOutput getMatch(Iterable<ProbabilityItemStackOutput> ingredients, ProbabilityItemStackOutput toMatch) {
		return getMatch(ingredients, toMatch, UidContext.Ingredient);
	}

	@Nullable
	@Override
	public ProbabilityItemStackOutput getMatch(Iterable<ProbabilityItemStackOutput> ingredients, ProbabilityItemStackOutput toMatch, UidContext context) {
		for (ProbabilityItemStackOutput stack : ingredients) {
			if (isEquivalent(toMatch.getItem(), stack.getItem(), context)) {
				return stack;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(ProbabilityItemStackOutput ingredient) {
		Component displayNameTextComponent = ingredient.getItem().getHoverName();
		String displayName = displayNameTextComponent.getString();
		JEIErrorUtilSnippet.checkNotNull(displayName, "itemStack.getDisplayName()");
		return displayName;
	}

	@Override
	public String getUniqueId(ProbabilityItemStackOutput ingredient) {
		return getUniqueId(ingredient, UidContext.Ingredient);
	}

	@Override
	public String getUniqueId(ProbabilityItemStackOutput ingredient, UidContext context) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItem());
		return getUniqueIdentifierForStack(ingredient.getItem(), context);
	}

	@Override
	public String getWildcardId(ProbabilityItemStackOutput ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItem());
		return getRegistryNameForStack(ingredient.getItem());
	}

	@Override
	public String getModId(ProbabilityItemStackOutput ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItem());

		Item item = ingredient.getItem().getItem();
		ResourceLocation itemName = item.getRegistryName();
		if (itemName == null) {
			String stackInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("item.getRegistryName() returned null for: " + stackInfo);
		}

		return itemName.getNamespace();
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
	public String getResourceId(ProbabilityItemStackOutput ingredient) {
		JEIErrorUtilSnippet.checkNotEmpty(ingredient.getItem());

		Item item = ingredient.getItem().getItem();
		ResourceLocation itemName = item.getRegistryName();
		if (itemName == null) {
			String stackInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("item.getRegistryName() returned null for: " + stackInfo);
		}

		return itemName.getPath();
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
		return ingredient.getItem().getItem().getTags();
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

	public boolean isEquivalent(@Nullable ItemStack lhs, @Nullable ItemStack rhs, UidContext context) {
		JEIErrorUtilSnippet.checkNotNull(context, "context");
		if (lhs == rhs) {
			return true;
		}

		if (lhs == null || rhs == null) {
			return false;
		}

		if (lhs.getItem() != rhs.getItem()) {
			return false;
		}

		String keyLhs = getUniqueIdentifierForStack(lhs, context);
		String keyRhs = getUniqueIdentifierForStack(rhs, context);
		return keyLhs.equals(keyRhs);
	}

	public String getUniqueIdentifierForStack(ItemStack stack, UidContext context) {
		String result = getRegistryNameForStack(stack);
		String subtypeInfo = subtypeManager.getSubtypeInfo(stack, context);
		if (subtypeInfo != null && !subtypeInfo.isEmpty()) {
			result = result + ':' + subtypeInfo;
		}
		return result;
	}

	public String getRegistryNameForStack(ItemStack stack) {
		JEIErrorUtilSnippet.checkNotEmpty(stack, "stack");

		Item item = stack.getItem();
		ResourceLocation registryName = item.getRegistryName();
		if (registryName == null) {
			String stackInfo = JEIErrorUtilSnippet.getItemStackInfo(stack);
			throw new IllegalStateException("Item has no registry name: " + stackInfo);
		}

		return registryName.toString();
	}

	public enum UidMode {
		NORMAL, WILDCARD
	}
}