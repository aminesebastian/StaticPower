package theking530.staticpower.cables.redstone.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(cablePos);
		buffer.writeCompoundTag(configuration.serializeNBT());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		cablePos = buffer.readBlockPos();
		configuration = new RedstoneCableConfiguration();
		configuration.deserializeNBT(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			// Get the world.
			ServerWorld world = ctx.get().getSender().getServerWorld();

			// Get the component at the location.
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, cablePos);

			// Update the configuration.
			if (component instanceof RedstoneCableComponent) {
				((RedstoneCableComponent) component).updateConfiguration(configuration);
			}
		});
	}
}
