package theking530.staticcore.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;

public abstract class StaticPowerItemContainer<T extends Item> extends StaticCoreContainerMenu {
	private final ItemStack itemstack;

	protected StaticPowerItemContainer(ContainerTypeAllocator<? extends StaticCoreContainerMenu, ?> allocator, int id, Inventory inv, ItemStack itemStack) {
		super(allocator, id, inv);
		itemstack = itemStack;
		initializeContainer(); // This has to be called here and not in the super.
	}

	/**
	 * Gets the {@link ItemStack} that triggered the opening of this container.
	 * 
	 * @return The {@link ItemStack} that opened this container.
	 */
	public ItemStack getItemStack() {
		return itemstack;
	}

	/**
	 * Gets the {@link ItemStack} from the provided data packet.
	 * 
	 * @param inv  The player's inventory.
	 * @param data The buffer including the index of the itemstack in the player's
	 *             inventory.
	 * @return The {@link ItemStack} that triggered the opening of this container.
	 */
	protected static ItemStack getHeldItemstack(Inventory inv, FriendlyByteBuf data) {
		return inv.getItem(data.readInt());
	}

	@Override
	public void clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
		// Prevent us from moving the item that we're currnetly looking at.
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem() == player.getMainHandItem()) {
			return;
		}
		super.clicked(slot, dragType, clickTypeIn, player);
	}
}
