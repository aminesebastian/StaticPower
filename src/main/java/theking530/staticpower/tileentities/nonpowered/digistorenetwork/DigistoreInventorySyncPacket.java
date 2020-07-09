package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class DigistoreInventorySyncPacket extends NetworkMessage {
	protected CompoundNBT inventoryUpdateTag;
	protected BlockPos capabilityPosition;

	public DigistoreInventorySyncPacket(BlockPos inventoryTEPosition, IDigistoreInventory inventory) {
		capabilityPosition = inventoryTEPosition;
		inventoryUpdateTag = inventory.serializeNBT();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(capabilityPosition);
		buffer.writeCompoundTag(inventoryUpdateTag);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		capabilityPosition = buffer.readBlockPos();
		inventoryUpdateTag = buffer.readCompoundTag();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.world.isAreaLoaded(capabilityPosition, 1)) {
				TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(capabilityPosition);
				if (rawTileEntity != null) {
					rawTileEntity.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).ifPresent(inv -> {
						inv.deserializeNBT(inventoryUpdateTag);
					});
				}
			}
		});
	}

}
