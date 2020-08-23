package theking530.staticpower.cables.heat;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class HeatCableUpdatePacket extends NetworkMessage {
	private BlockPos position;
	private float currentHeat;
	private float capacity;

	public HeatCableUpdatePacket(BlockPos position, float currentHeat, float capacity) {
		this.position = position;
		this.currentHeat = currentHeat;
		this.capacity = capacity;
	}

	public HeatCableUpdatePacket() {

	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(position);
		buffer.writeFloat(currentHeat);
		buffer.writeFloat(capacity);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		position = buffer.readBlockPos();
		currentHeat = buffer.readFloat();
		capacity = buffer.readFloat();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.world.isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);
				ComponentUtilities.getComponent(HeatCableComponent.class, rawTileEntity).ifPresent(comp -> {
					comp.updateFromNetworkUpdatePacket(currentHeat, capacity);
				});
			}
		});
	}
}
