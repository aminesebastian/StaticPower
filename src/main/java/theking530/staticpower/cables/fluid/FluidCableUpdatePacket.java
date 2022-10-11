package theking530.staticpower.cables.fluid;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.blockentities.components.ComponentUtilities;

public class FluidCableUpdatePacket extends NetworkMessage {
	private CompoundTag data;
	private BlockPos position;

	public FluidCableUpdatePacket(BlockPos position, CompoundTag data) {
		this.position = position;
		this.data = data;
	}

	public FluidCableUpdatePacket() {

	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(data);
		buffer.writeLong(position.asLong());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		data = buffer.readNbt();
		position = BlockPos.of(buffer.readLong());
	}

	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);
				ComponentUtilities.getComponent(FluidCableComponent.class, rawTileEntity).ifPresent(comp -> {
					comp.recieveUpdateRenderValues(data);
				});
			}
		});
	}
}
