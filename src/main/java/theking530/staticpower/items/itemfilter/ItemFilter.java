package theking530.staticpower.items.itemfilter;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.items.ItemStackInventoryCapabilityProvider;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemFilter extends StaticPowerItem {
	/** Indicates if we are in whitelist or blacklist modes. */
	public static final String WHITE_LIST_MOD_KEY = "WhitelistMode";
	/** Indicates if this filter should match NBT. */
	public static final String MATCH_NBT_KEY = "MatchNBT";
	/** Indicates if this filter should match item tags. */
	public static final String MATCH_TAGS_DICT_KEY = "MatchTags";
	/** Indicates if this filter should match namespaces. */
	public static final String MATCH_MOD_KEY = "MatchMod";

	public ResourceLocation filterTier;

	public ItemFilter(String name, ResourceLocation tier) {
		super(name, new Properties().maxStackSize(1));
		filterTier = tier;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		// Initialize the tag.
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}

		// Initialize the filter flags.
		stack.getTag().putBoolean(WHITE_LIST_MOD_KEY, false);
		stack.getTag().putBoolean(MATCH_NBT_KEY, false);
		stack.getTag().putBoolean(MATCH_TAGS_DICT_KEY, false);
		stack.getTag().putBoolean(MATCH_MOD_KEY, false);

		// Add the inventory.
		return new ItemStackInventoryCapabilityProvider(stack, TierReloadListener.getTier(filterTier).getItemFilterSlots(), nbt);
	}

	/**
	 * When right clicked, open the filter UI.
	 */
	@Override
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (!world.isRemote && !player.isSneaking()) {
			NetworkGUI.openGui((ServerPlayerEntity) player, new ItemFilterContainerProvider(item), buff -> {
				buff.writeInt(player.inventory.getSlotFor(item));
			});
			return ActionResult.resultSuccess(item);
		}
		return ActionResult.resultPass(item);
	}

	public boolean isWhiteListMode(ItemStack filter) {
		return filter.getTag().getBoolean(WHITE_LIST_MOD_KEY);
	}

	public boolean filterForNBT(ItemStack filter) {
		return filter.getTag().getBoolean(MATCH_NBT_KEY);
	}

	public boolean filterForTag(ItemStack filter) {
		return filter.getTag().getBoolean(MATCH_TAGS_DICT_KEY);
	}

	public boolean filterForMod(ItemStack filter) {
		return filter.getTag().getBoolean(MATCH_MOD_KEY);
	}

	public ItemFilter setWhitelistMode(ItemStack filter, boolean whitelist) {
		filter.getTag().putBoolean(WHITE_LIST_MOD_KEY, whitelist);
		return this;
	}

	public ItemFilter setFilterForNBT(ItemStack filter, boolean shouldFilter) {
		filter.getTag().putBoolean(MATCH_NBT_KEY, shouldFilter);
		return this;
	}

	public ItemFilter setFilterForTag(ItemStack filter, boolean shouldFilter) {
		filter.getTag().putBoolean(MATCH_TAGS_DICT_KEY, shouldFilter);
		return this;
	}

	public ItemFilter setFilterForMod(ItemStack filter, boolean shouldFilter) {
		filter.getTag().putBoolean(MATCH_MOD_KEY, shouldFilter);
		return this;
	}

	/**
	 * Evaluates the provided {@link ItemStack} against the filter.
	 * 
	 * @param filter    The filter's ItemStack.
	 * @param itemstack The {@link ItemStack} to check against the filter.
	 * @return True if the itemstack passs the filter.
	 */
	public boolean evaluateItemStackAgainstFilter(ItemStack filter, ItemStack itemstack) {
		IItemHandler inv = itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		if (inv != null) {
			boolean whitelist = true;
			boolean matchNBT = false;
			boolean matchOreDict = false;
			boolean matchMod = false;

			if (filter.getTag().contains(WHITE_LIST_MOD_KEY)) {
				whitelist = filter.getTag().getBoolean(WHITE_LIST_MOD_KEY);
			}
			if (filter.getTag().contains(MATCH_NBT_KEY)) {
				matchNBT = filter.getTag().getBoolean(MATCH_NBT_KEY);
			}
			if (filter.getTag().contains(MATCH_TAGS_DICT_KEY)) {
				matchOreDict = filter.getTag().getBoolean(MATCH_TAGS_DICT_KEY);
			}
			if (filter.getTag().contains(MATCH_MOD_KEY)) {
				matchMod = filter.getTag().getBoolean(MATCH_MOD_KEY);
			}
			return ItemUtilities.filterItems(inv, itemstack, whitelist, matchNBT, matchOreDict, matchMod);
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		boolean empty = true;
		if (stack.hasTag() && stack.getTag().contains("Items")) {
			ItemStackHandler tempHandler = new ItemStackHandler();
			tempHandler.deserializeNBT(stack.getTag());

			for (int i = 0; i < tempHandler.getSlots(); i++) {
				if (!tempHandler.getStackInSlot(i).isEmpty()) {
					tooltip.add(new StringTextComponent("Slot " + (i + 1) + ": ").append(tempHandler.getStackInSlot(i).getDisplayName()));
					empty = false;
				}
			}
		}
		if (empty) {
			tooltip.add(new StringTextComponent(TextFormatting.ITALIC + "Empty"));
		}
	}

	public class ItemFilterContainerProvider implements INamedContainerProvider {
		public ItemStack targetItemStack;

		public ItemFilterContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
			return new ContainerItemFilter(windowId, inventory, targetItemStack);
		}

		@Override
		public ITextComponent getDisplayName() {
			return targetItemStack.getDisplayName();
		}
	}
}
