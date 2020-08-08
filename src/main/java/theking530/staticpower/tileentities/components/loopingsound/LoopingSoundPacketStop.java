package theking530.staticpower.tileentities.components.loopingsound;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class LoopingSoundPacketStop extends NetworkMessage {
	private String componentName;
	private BlockPos position;

	public LoopingSoundPacketStop() {
	}

	public LoopingSoundPacketStop(LoopingSoundComponent component) {
		super();
		this.componentName = component.getComponentName();
		this.position = component.getPos();
	}

	@Override
	public void decode(PacketBuffer buf) {
		componentName = buf.readString();
		position = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeString(componentName);
		buf.writeBlockPos(position);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			// Make sure the position is loaded.
			if (Minecraft.getInstance().player.world.isAreaLoaded(position, 1)) {
				// Get the tile entity.
				TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);

				// If the component is found on the client, play the sound.
				ComponentUtilities.getComponent(LoopingSoundComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
					comp.stopPlayingSound();
				});
			}
		});
	}
}
