package theking530.staticpower.tileentities.cables.network.modules;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.item.ItemCableComponent;

public class ItemCableRemovedPacket extends NetworkMessage {
	protected long removedParcel;
	protected BlockPos tileEntityPosition;

	public ItemCableRemovedPacket() {

	}

	public ItemCableRemovedPacket(ItemCableComponent component, long removedParcel) {
		tileEntityPosition = component.getPos();
		this.removedParcel = removedParcel;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeLong(removedParcel);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		tileEntityPosition = buffer.readBlockPos();
		removedParcel = buffer.readLong();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(tileEntityPosition);
			if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
				TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
				ItemCableComponent cableComponent = tileEntity.getComponent(ItemCableComponent.class);
				cableComponent.removeTransferingItem(removedParcel);
			}
		});
	}
}
