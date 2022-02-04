package theking530.staticpower.cables.heat;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class HeatCableUpdatePacket extends NetworkMessage {
	private BlockPos position;
	private float currentHeat;
	private float capacity;

	public HeatCableUpdatePacket(BlockPos position, double currentHeat, double capacity) {
		this.position = position;
		this.currentHeat = (float) currentHeat;
		this.capacity = (float) capacity;
	}

	public HeatCableUpdatePacket() {

	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(position);
		buffer.writeFloat(currentHeat);
		buffer.writeFloat(capacity);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		position = buffer.readBlockPos();
		currentHeat = buffer.readFloat();
		capacity = buffer.readFloat();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);
				ComponentUtilities.getComponent(HeatCableComponent.class, rawTileEntity).ifPresent(comp -> {
					comp.updateFromNetworkUpdatePacket(currentHeat, capacity);
				});
			}
		});
	}
}
