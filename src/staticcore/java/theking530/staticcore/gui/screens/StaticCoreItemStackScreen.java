package theking530.staticcore.gui.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.container.StaticPowerItemContainer;

public abstract class StaticCoreItemStackScreen<T extends StaticPowerItemContainer<K>, K extends Item> extends StaticPowerContainerScreen<T> {
	private final ItemStack owningItemStack;

	public StaticCoreItemStackScreen(T container, Inventory playerInventory, Component title, int guiXSize, int guiYSize) {
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
