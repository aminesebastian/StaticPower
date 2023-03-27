package theking530.staticcore.gui.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.container.StaticPowerTileEntityContainer;

/**
 * Base GUI class for tile entities.
 * 
 * @author Amine Sebastian
 *
 * @param <T> The container type.
 */
public abstract class StaticCoreBlockEntityScreen<T extends StaticPowerTileEntityContainer<K>, K extends BlockEntityBase> extends StaticPowerContainerScreen<T> {
	private final K owningTileEntity;

	public StaticCoreBlockEntityScreen(T container, final Inventory playerInventory, Component title, int guiXSize, int guiYSize) {
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
