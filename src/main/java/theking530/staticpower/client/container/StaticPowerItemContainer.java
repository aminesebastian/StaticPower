package theking530.staticpower.client.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public abstract class StaticPowerItemContainer<T extends Item> extends StaticPowerContainer {
	private final ItemStack heldItemstack;

	protected StaticPowerItemContainer(ContainerType<?> type, int id, PlayerInventory inv, ItemStack itemStack) {
		super(type, id, inv);
		heldItemstack = itemStack;
		initializeContainer(); // This has to be called here and not in the super.
	}

	/**
	 * Gets the {@link ItemStack} that triggered the opening of this container.
	 * 
	 * @return The {@link ItemStack} that opened this container.
	 */
	public ItemStack getItemStack() {
		return heldItemstack;
	}

	/**
	 * Gets the {@link ItemStack} from the provided data packet.
	 * 
	 * @param inv  The player's inventory.
	 * @param data The buffer including the index of the itemstack in the player's
	 *             inventory.
	 * @return The {@link ItemStack} that triggered the opening of this container.
	 */
	protected static ItemStack resolveHeldItemStackFromBuffer(PlayerInventory inv, PacketBuffer data) {
		return inv.getStackInSlot(data.readInt());
	}
}
