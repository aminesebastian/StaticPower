package theking530.staticpower.client.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.tileentities.TileEntityBase;

/**
 * Base GUI class for tile entities.
 * 
 * @author Amine Sebastian
 *
 * @param <T> The container type.
 */
public abstract class StaticPowerTileEntityGui<T extends StaticPowerTileEntityContainer<K>, K extends TileEntityBase> extends StaticPowerContainerGui<T> {
	private final K owningTileEntity;

	public StaticPowerTileEntityGui(T container, final Inventory playerInventory, Component title, int guiXSize, int guiYSize) {
		super(container, playerInventory, title, guiXSize, guiYSize);
		owningTileEntity = container.getTileEntity();
		setOutputSlotSize(20);
	}

	/**
	 * Gets the owning tile entity for this container.
	 * 
	 * @return The owning tile entity for this container.
	 */
	protected K getTileEntity() {
		return owningTileEntity;
	}
}
