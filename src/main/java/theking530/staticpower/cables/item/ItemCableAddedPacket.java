package theking530.staticpower.cables.item;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;

public class ItemCableAddedPacket extends NetworkMessage {
	protected CompoundTag parcelNbt;
	protected BlockPos tileEntityPosition;

	public ItemCableAddedPacket() {

	}

	public ItemCableAddedPacket(ItemCableComponent component, ItemRoutingParcelClient parcel) {
		tileEntityPosition = component.getPos();
		parcelNbt = new CompoundTag();
		parcel.writeToNbt(parcelNbt);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeNbt(parcelNbt);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		tileEntityPosition = buffer.readBlockPos();
		parcelNbt = buffer.readNbt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(tileEntityPosition);
			if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
				TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
				ItemCableComponent cableComponent = tileEntity.getComponent(ItemCableComponent.class);
				cableComponent.addTransferingItem(ItemRoutingParcelClient.create(parcelNbt));
			}
		});
	}
}
