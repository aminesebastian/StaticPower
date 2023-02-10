package theking530.staticpower.blockentities.components.items;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.blockentities.BlockEntityBase;

public class PacketLockInventorySlot extends NetworkMessage {
	private BlockPos position;
	private String componentName;
	private int slot;
	private boolean locked;
	private ItemStack filteredIem;

	public PacketLockInventorySlot() {
	}

	public PacketLockInventorySlot(InventoryComponent inventoryComponent, int slot, boolean locked, ItemStack filteredIem) {
		this.position = inventoryComponent.getBlockEntity().getBlockPos();
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

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().level.isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = context.get().getSender().level.getBlockEntity(position);

				if (rawTileEntity != null && rawTileEntity instanceof BlockEntityBase) {
					BlockEntityBase tileEntity = (BlockEntityBase) rawTileEntity;

					// Ensure this tile entity is valid and has the requested component.
					if (tileEntity.hasComponentOfType(InventoryComponent.class)) {
						// Get a reference to the inventory component.
						InventoryComponent component = tileEntity.getComponent(InventoryComponent.class, componentName);

						// Update the locked state.
						if (locked) {
							tileEntity.getLevel().playSound(null, tileEntity.getBlockPos(), SoundEvents.SPYGLASS_USE, SoundSource.BLOCKS, 1, 1.25f);
							tileEntity.getLevel().playSound(null, tileEntity.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.15f, 1.25f);
							component.lockSlot(slot, filteredIem);
						} else {
							tileEntity.getLevel().playSound(null, tileEntity.getBlockPos(), SoundEvents.SPYGLASS_USE, SoundSource.BLOCKS, 1, 1);
							tileEntity.getLevel().playSound(null, tileEntity.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.15f, 1f);
							component.unlockSlot(slot);
						}
					}
				}
			}
		});
	}
}
