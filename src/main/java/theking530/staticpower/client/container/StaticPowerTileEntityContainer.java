package theking530.staticpower.client.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.TileEntityBase;

public abstract class StaticPowerTileEntityContainer<T extends TileEntityBase> extends StaticPowerContainer {
	private final T owningTileEntity;

	public StaticPowerTileEntityContainer(ContainerType<?> containerType, int windowId, PlayerInventory inv, T owner) {
		super(containerType, windowId, inv);
		owningTileEntity = owner;
		initializeContainer(); // This has to be called here and not in the super.
	}

	/**
	 * Gets the owning tile entity for this container.
	 * 
	 * @return The owning tile entity for this container.
	 */
	public T getTileEntity() {
		return owningTileEntity;
	}

	/**
	 * Gets the tile entity from the provided data packet.
	 * 
	 * @param playerInventory The player's inventory (used to get the world
	 *                        instance).
	 * @param data            The data packet.
	 * @return The {@link TileEntityBase} for this container or null if none was
	 *         encountered.
	 */
	protected static TileEntityBase resolveTileEntityFromDataPacket(final PlayerInventory playerInventory, final PacketBuffer data) {
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof TileEntityBase) {
			return (TileEntityBase) tileAtPos;
		} else {
			StaticPower.LOGGER.error(String.format("Encountered invalid tile entity: %1$s at position: %2$s.", tileAtPos, data.readBlockPos()));
			return null;
		}
	}
}
