package theking530.staticpower.cables.item;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;

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
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeLong(removedParcel);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		tileEntityPosition = buffer.readBlockPos();
		removedParcel = buffer.readLong();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(tileEntityPosition);
			if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
				TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
				ItemCableComponent cableComponent = tileEntity.getComponent(ItemCableComponent.class);
				cableComponent.removeTransferingItem(removedParcel);
			}
		});
	}
}
