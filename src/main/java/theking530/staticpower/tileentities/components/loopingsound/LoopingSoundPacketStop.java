package theking530.staticpower.tileentities.components.loopingsound;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
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
	public void decode(FriendlyByteBuf buf) {
		componentName =buf.readUtf();
		position = buf.readBlockPos();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(componentName);
		buf.writeBlockPos(position);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			// Make sure the position is loaded.
			if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
				// Get the tile entity.
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);

				// If the component is found on the client, play the sound.
				ComponentUtilities.getComponent(LoopingSoundComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
					comp.stopPlayingSound();
				});
			}
		});
	}
}
