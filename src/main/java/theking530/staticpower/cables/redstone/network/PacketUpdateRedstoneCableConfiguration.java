package theking530.staticpower.cables.redstone.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
import theking530.staticpower.cables.redstone.basic.RedstoneCableComponent;
import theking530.staticpower.network.NetworkMessage;

public class PacketUpdateRedstoneCableConfiguration extends NetworkMessage {
	private BlockPos cablePos;
	private RedstoneCableConfiguration configuration;

	public PacketUpdateRedstoneCableConfiguration() {

	}

	public PacketUpdateRedstoneCableConfiguration(BlockPos cablePos, RedstoneCableConfiguration configuration) {
		this.cablePos = cablePos;
		this.configuration = configuration;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(cablePos);
		buffer.writeNbt(configuration.serializeNBT());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		cablePos = buffer.readBlockPos();
		configuration = new RedstoneCableConfiguration();
		configuration.deserializeNBT(buffer.readNbt());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			// Get the world.
			ServerLevel world = ctx.get().getSender().getLevel();

			// Get the component at the location.
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, cablePos);

			// Update the configuration.
			if (component instanceof RedstoneCableComponent) {
				((RedstoneCableComponent) component).updateConfiguration(configuration);
			}
		});
	}
}
