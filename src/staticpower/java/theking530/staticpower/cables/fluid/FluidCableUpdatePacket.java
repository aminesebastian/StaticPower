package theking530.staticpower.cables.fluid;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.world.WorldUtilities;

public class FluidCableUpdatePacket extends NetworkMessage {
	private BlockPos position;
	private FluidStack fluid;
	private float pressure;

	public FluidCableUpdatePacket(BlockPos position, FluidStack fluid, float pressure) {
		this.position = position;
		this.fluid = fluid;
		this.pressure = pressure;
	}

	public FluidCableUpdatePacket() {

	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(position.asLong());
		fluid.writeToPacket(buffer);
		buffer.writeFloat(pressure);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		position = BlockPos.of(buffer.readLong());
		fluid = FluidStack.readFromPacket(buffer);
		pressure = buffer.readFloat();
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = Minecraft.getInstance().player;
			if (WorldUtilities.isBlockPosInLoadedChunk(player.level, position)) {
				BlockEntity rawTileEntity = player.getLevel().getBlockEntity(position);
				ComponentUtilities.getComponent(FluidCableComponent.class, rawTileEntity).ifPresent(comp -> {
					comp.recieveUpdateRenderValues(fluid, pressure);
				});
			}
		});
	}
}
