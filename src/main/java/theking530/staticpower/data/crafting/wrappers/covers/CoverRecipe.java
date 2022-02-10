package theking530.staticpower.data.crafting.wrappers.covers;

import javax.annotation.Nonnull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.tools.CoverSaw;

public class CoverRecipe extends CustomRecipe {
	public static final SimpleRecipeSerializer<CoverRecipe> SERIALIZER_INSTANCE;

	static {
		SERIALIZER_INSTANCE = new SimpleRecipeSerializer<>(id -> new CoverRecipe(id));
		SERIALIZER_INSTANCE.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "cover_recipe"));
	}

	public CoverRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull final CraftingContainer inv, @Nonnull final Level w) {
		return !this.getOutput(inv, false).isEmpty();
	}

	@Nonnull
	private ItemStack getOutput(final Container inv, final boolean createFacade) {
		int uniqueStacks = 0;
		Item target = null;
		boolean sawFound = false;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			// Get the stack we're checking.
			ItemStack invStack = inv.getItem(i);

			// Skip empty slots.
			if (invStack.isEmpty()) {
				continue;
			}

			// Increment the unique stack size.
			uniqueStacks++;

			// If we found a cover saw, mark the flag as true. If not, we assume this is the
			// target block.
			if (invStack.getItem() instanceof CoverSaw) {
				sawFound = true;
			} else {
				target = invStack.getItem();
			}

			// Immediatley return if there are more than two items in the crafting grid.
			if (uniqueStacks > 2) {
				return ItemStack.EMPTY;
			}
		}

		// If there is no saw, or if the target item is not for a block, return empty.
		if (!sawFound || !(target instanceof BlockItem)) {
			return ItemStack.EMPTY;
		}

		// If we can make a cover for this block, return that cover. Otherwise, return
		// an empty itemstack.
		if (CableCover.isValidForCover(((BlockItem) target).getBlock())) {
			ItemStack output = ModItems.CableCover.makeCoverForBlock(((BlockItem) target).getBlock().defaultBlockState());
			output.setCount(8);
			return output;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack assemble(@Nonnull final CraftingContainer inv) {
		return this.getOutput(inv, true);
	}

	@Override
	public boolean canCraftInDimensions(int i, int i1) {
		return false;
	}

	@Nonnull
	@Override
	public RecipeSerializer<CoverRecipe> getSerializer() {
		return SERIALIZER_INSTANCE;
	}
}