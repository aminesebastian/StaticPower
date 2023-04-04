package theking530.staticpower.cables.heat;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.network.NetworkMessage;

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

	@SuppressWarnings({ "resource", "deprecation" })
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
