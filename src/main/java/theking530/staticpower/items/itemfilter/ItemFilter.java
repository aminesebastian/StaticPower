package theking530.staticpower.items.itemfilter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemFilter extends StaticPowerItem {
	public FilterTier filterTier;

	public ItemFilter(String name, FilterTier tier) {
		super(name, new Properties().maxStackSize(1));
		filterTier = tier;
	}

	/**
	 * Add the energy storage capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilityProvider() {
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
				if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
					return net.minecraftforge.common.util.LazyOptional.of(() -> {
						return new InventoryItemFilter(stack, filterTier);
					}).cast();
				}
				return LazyOptional.empty();
			}
		};
	}

	/**
	 * When right clicked, open the filter UI.
	 */
	@Override
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (!world.isRemote && !player.isSneaking()) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ItemFilterContainerProvider(item), buff -> {
				buff.writeInt(player.inventory.getSlotFor(item));
			});
			return ActionResult.resultSuccess(item);
		}
		return ActionResult.resultPass(item);
	}

	/**
	 * Evaluates the provided {@link ItemStack} against the filter.
	 * 
	 * @param filter    The filter's ItemStack.
	 * @param itemstack The {@link ItemStack} to check against the filter.
	 * @return True if the itemstack passs the filter.
	 */
	public boolean evaluateItemStackAgainstFilter(ItemStack filter, ItemStack itemstack) {
		List<ItemStack> slots = new ArrayList<ItemStack>();

		if (filter.hasTag()) {
			InventoryItemFilter tempHandler = new InventoryItemFilter(filter, filterTier);
			tempHandler.deserializeNBT(filter.getTag());

			for (int i = 0; i < tempHandler.getSlots(); i++) {
				if (!tempHandler.getStackInSlot(i).isEmpty()) {
					slots.add(tempHandler.getStackInSlot(i).copy());
				}
			}

			boolean whitelist = true;
			boolean matchNBT = false;
			boolean matchMetadata = true;
			boolean matchOreDict = false;

			if (filter.getTag().contains("WHITE_LIST_MODE")) {
				whitelist = filter.getTag().getBoolean("WHITE_LIST_MODE");
			}
			if (filter.getTag().contains("MATCH_NBT")) {
				matchNBT = filter.getTag().getBoolean("MATCH_NBT");
			}
			if (filter.getTag().contains("MATCH_METADATA")) {
				matchMetadata = filter.getTag().getBoolean("MATCH_METADATA");
			}
			if (filter.getTag().contains("MATCH_ORE_DICT")) {
				matchOreDict = filter.getTag().getBoolean("MATCH_ORE_DICT");
			}
			return ItemUtilities.filterItems(slots, itemstack, whitelist, matchMetadata, matchNBT, matchOreDict, false);
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
					tooltip.add(new StringTextComponent("Slot " + (i + 1) + ": " + tempHandler.getStackInSlot(i).getDisplayName()));
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
