package theking530.staticpower.tileentities.powered.autocrafter;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class PacketLockInventorySlot extends NetworkMessage {
	private BlockPos position;
	private String componentName;
	private int slot;
	private boolean locked;
	private ItemStack filteredIem;

	public PacketLockInventorySlot() {
	}

	public PacketLockInventorySlot(InventoryComponent inventoryComponent, int slot, boolean locked, ItemStack filteredIem) {
		this.position = inventoryComponent.getTileEntity().getPos();
		this.componentName = inventoryComponent.getComponentName();
		this.slot = slot;
		this.locked = locked;
		this.filteredIem = filteredIem;
	}

	@Override
	public void decode(PacketBuffer buf) {
		position = buf.readBlockPos();
		slot = buf.readInt();
		locked = buf.readBoolean();
		filteredIem = buf.readItemStack();
		componentName = buf.readString();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(position);
		buf.writeInt(slot);
		buf.writeBoolean(locked);
		buf.writeItemStack(filteredIem);
		buf.writeString(componentName);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().world.isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = context.get().getSender().world.getTileEntity(position);

				if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
					TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;

					// Ensure this tile entity is valid and has the requested component.
					if (tileEntity.hasComponentOfType(InventoryComponent.class)) {
						// Get a reference to the inventory component.
						InventoryComponent component = tileEntity.getComponent(InventoryComponent.class, componentName);

						// Update the locked state.
						if (locked) {
							component.lockSlot(slot, filteredIem);
						} else {
							component.unlockSlot(slot);
						}
					}
				}
			}
		});
	}
}
