package theking530.api.digistore;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class DigistoreInventorySyncPacket extends NetworkMessage {
	protected CompoundTag inventoryUpdateTag;
	protected BlockPos capabilityPosition;

	public DigistoreInventorySyncPacket(BlockPos inventoryTEPosition, IDigistoreInventory inventory) {
		capabilityPosition = inventoryTEPosition;
		inventoryUpdateTag = inventory.serializeNBT();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(capabilityPosition);
		buffer.writeNbt(inventoryUpdateTag);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		capabilityPosition = buffer.readBlockPos();
		inventoryUpdateTag = buffer.readNbt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.level.isLoaded(capabilityPosition)) {
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(capabilityPosition);
				if (rawTileEntity != null) {
					rawTileEntity.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).ifPresent(inv -> {
						inv.deserializeNBT(inventoryUpdateTag);
					});
				}
			}
		});
	}

}
