package theking530.staticpower.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.container.StaticPowerItemContainer;

public abstract class StaticPowerItemStackGui<T extends StaticPowerItemContainer<K>, K extends Item> extends StaticPowerContainerGui<T> {
	private final ItemStack owningItemStack;

	public StaticPowerItemStackGui(T container, Inventory playerInventory, Component title, int guiXSize, int guiYSize) {
		super(container, playerInventory, title, guiXSize, guiYSize);
		owningItemStack = container.getItemStack();
	}

	/**
	 * Gets the {@link ItemStack} that is responsible for opening this Gui.
	 * 
	 * @return The {@link ItemStack} that is responsible for opening this Gui.
	 */
	protected ItemStack getItemStack() {
		return owningItemStack;
	}
}
