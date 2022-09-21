package theking530.staticpower.items.itemfilter;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.StaticPowerConfig;
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

	public ItemFilter(ResourceLocation tier) {
		super(new Properties().stacksTo(1));
		filterTier = tier;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		// Initialize the tag.
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
			stack.getTag().putBoolean(WHITE_LIST_MOD_KEY, false);
			stack.getTag().putBoolean(MATCH_NBT_KEY, false);
			stack.getTag().putBoolean(MATCH_TAGS_DICT_KEY, false);
			stack.getTag().putBoolean(MATCH_MOD_KEY, false);
		}

		// Add the inventory.
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory(
				"default", stack, StaticPowerConfig.getTier(filterTier).itemFilterSlots.get()));
	}

	/**
	 * When right clicked, open the filter UI.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player,
			InteractionHand hand, ItemStack item) {
		if (!world.isClientSide && !player.isShiftKeyDown()) {
			NetworkGUI.openGui((ServerPlayer) player, new ItemFilterContainerProvider(item), buff -> {
				buff.writeInt(player.getInventory().selected);
			});
			return InteractionResultHolder.success(item);
		}
		return InteractionResultHolder.pass(item);
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
		IItemHandler inv = filter.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
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
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			IItemHandler inv = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inv != null) {
				boolean empty = true;
				for (int i = 0; i < inv.getSlots(); i++) {
					if (!inv.getStackInSlot(i).isEmpty()) {
						tooltip.add(new TextComponent("Slot " + (i + 1) + ": ")
								.append(inv.getStackInSlot(i).getHoverName()));
						empty = false;
					}
				}
				if (empty) {
					tooltip.add(new TextComponent(ChatFormatting.ITALIC + "Empty"));
				}
			}
		}
	}

	public class ItemFilterContainerProvider implements MenuProvider {
		public ItemStack targetItemStack;

		public ItemFilterContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
			return new ContainerItemFilter(windowId, inventory, targetItemStack);
		}

		@Override
		public Component getDisplayName() {
			return targetItemStack.getHoverName();
		}
	}
}
