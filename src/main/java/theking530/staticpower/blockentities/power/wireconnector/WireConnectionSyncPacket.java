package theking530.staticpower.blockentities.power.wireconnector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.SparseCableLink;

public class WireConnectionSyncPacket extends NetworkMessage {
	protected Collection<SparseCableLink> links;
	protected BlockPos cablePos;

	public WireConnectionSyncPacket() {

	}

	public WireConnectionSyncPacket(BlockPos cablePos, Collection<SparseCableLink> links) {
		this.cablePos = cablePos;
		this.links = links;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(cablePos);
		buffer.writeShort(links.size());
		for (SparseCableLink link : links) {
			buffer.writeNbt(link.serialize());
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		cablePos = buffer.readBlockPos();
		links = new ArrayList<SparseCableLink>();
		short count = buffer.readShort();
		for (short i = 0; i < count; i++) {
			links.add(SparseCableLink.fromTag(buffer.readNbt()));
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockEntityBase tileEntity = (BlockEntityBase) Minecraft.getInstance().player.level.getBlockEntity(cablePos);
			if (tileEntity != null) {
				WirePowerCableComponent cableComponent = tileEntity.getComponent(WirePowerCableComponent.class);
				cableComponent.syncConnectionStateFromServer(links);
			}
		});
	}
}
