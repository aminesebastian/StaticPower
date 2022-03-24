package theking530.staticpower.tileentities.components.items;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;

public class PacketLockInventorySlot extends NetworkMessage {
	private BlockPos position;
	private String componentName;
	private int slot;
	private boolean locked;
	private ItemStack filteredIem;

	public PacketLockInventorySlot() {
	}

	public PacketLockInventorySlot(InventoryComponent inventoryComponent, int slot, boolean locked, ItemStack filteredIem) {
		this.position = inventoryComponent.getTileEntity().getBlockPos();
		this.componentName = inventoryComponent.getComponentName();
		this.slot = slot;
		this.locked = locked;
		this.filteredIem = filteredIem;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		position = buf.readBlockPos();
		slot = buf.readInt();
		locked = buf.readBoolean();
		filteredIem = buf.readItem();
		componentName = buf.readUtf();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(position);
		buf.writeInt(slot);
		buf.writeBoolean(locked);
		buf.writeItem(filteredIem);
		buf.writeUtf(componentName);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().level.isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = context.get().getSender().level.getBlockEntity(position);

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
