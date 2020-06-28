package theking530.staticpower.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.client.container.StaticPowerItemContainer;

public abstract class StaticPowerItemStackGui<T extends StaticPowerItemContainer<K>, K extends Item> extends StaticPowerContainerGui<T> {
	private final ItemStack owningItemStack;

	public StaticPowerItemStackGui(T container, PlayerInventory playerInventory, ITextComponent title, int guiXSize, int guiYSize) {
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
