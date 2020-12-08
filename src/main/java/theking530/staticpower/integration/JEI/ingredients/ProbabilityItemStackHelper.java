package theking530.staticpower.integration.JEI.ingredients;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.color.ColorGetter;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;

public class ProbabilityItemStackHelper implements IIngredientHelper<ProbabilityItemStackOutput> {
	private final StackHelper stackHelper;

	public ProbabilityItemStackHelper(StackHelper stackHelper) {
		this.stackHelper = stackHelper;
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
			if (stackHelper.isEquivalent(toMatch.getItem(), stack.getItem(), context)) {
				return stack;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(ProbabilityItemStackOutput ingredient) {
		ITextComponent displayNameTextComponent = ingredient.getItem().getDisplayName();
		String displayName = displayNameTextComponent.getString();
		ErrorUtil.checkNotNull(displayName, "itemStack.getDisplayName()");
		return displayName;
	}

	@Override
	public String getUniqueId(ProbabilityItemStackOutput ingredient) {
		return getUniqueId(ingredient, UidContext.Ingredient);
	}

	@Override
	public String getUniqueId(ProbabilityItemStackOutput ingredient, UidContext context) {
		ErrorUtil.checkNotEmpty(ingredient.getItem());
		return stackHelper.getUniqueIdentifierForStack(ingredient.getItem(), context);
	}

	@Override
	public String getWildcardId(ProbabilityItemStackOutput ingredient) {
		ErrorUtil.checkNotEmpty(ingredient.getItem());
		return stackHelper.getRegistryNameForStack(ingredient.getItem());
	}

	@Override
	public String getModId(ProbabilityItemStackOutput ingredient) {
		ErrorUtil.checkNotEmpty(ingredient.getItem());

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
		ErrorUtil.checkNotEmpty(ingredient.getItem());

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
		return ColorGetter.getColors(ingredient.getItem(), 2);
	}

	@Override
	public String getResourceId(ProbabilityItemStackOutput ingredient) {
		ErrorUtil.checkNotEmpty(ingredient.getItem());

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
		for (ItemGroup itemGroup : item.getCreativeTabs()) {
			if (itemGroup != null) {
				String creativeTabName = itemGroup.getGroupName().getString();
				creativeTabsStrings.add(creativeTabName);
			}
		}
		return creativeTabsStrings;
	}

	@Override
	public String getErrorInfo(@Nullable ProbabilityItemStackOutput ingredient) {
		return ErrorUtil.getItemStackInfo(ingredient.getItem());
	}
}