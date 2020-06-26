package theking530.staticpower.cables.item;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;

public class ItemCableAddedPacket extends NetworkMessage {
	protected CompoundNBT parcelNbt;
	protected BlockPos tileEntityPosition;

	public ItemCableAddedPacket() {

	}

	public ItemCableAddedPacket(ItemCableComponent component, ItemRoutingParcelClient parcel) {
		tileEntityPosition = component.getPos();
		parcelNbt = new CompoundNBT();
		parcel.writeToNbt(parcelNbt);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeCompoundTag(parcelNbt);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		tileEntityPosition = buffer.readBlockPos();
		parcelNbt = buffer.readCompoundTag();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(tileEntityPosition);
			if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
				TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
				ItemCableComponent cableComponent = tileEntity.getComponent(ItemCableComponent.class);
				cableComponent.addTransferingItem(ItemRoutingParcelClient.create(parcelNbt));
			}
		});
	}
}
