package theking530.staticpower.tileentities.components.loopingsound;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class LoopingSoundPacketStart extends NetworkMessage {
	private String componentName;
	private ResourceLocation soundIdIn;
	private SoundCategory categoryIn;
	private float volumeIn;
	private float pitchIn;
	private BlockPos position;
	private BlockPos soundPosition;

	public LoopingSoundPacketStart() {
	}

	public LoopingSoundPacketStart(LoopingSoundComponent component, ResourceLocation soundIdIn, SoundCategory categoryIn, float volumeIn, float pitchIn, BlockPos pos) {
		super();
		this.componentName = component.getComponentName();
		this.soundIdIn = soundIdIn;
		this.categoryIn = categoryIn;
		this.volumeIn = volumeIn;
		this.pitchIn = pitchIn;
		this.position = component.getPos();
		this.soundPosition = pos;
	}

	@Override
	public void decode(PacketBuffer buf) {
		componentName = buf.readString();
		soundIdIn = new ResourceLocation(buf.readString());
		categoryIn = SoundCategory.values()[buf.readInt()];
		volumeIn = buf.readFloat();
		pitchIn = buf.readFloat();
		position = buf.readBlockPos();
		soundPosition = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeString(componentName);
		buf.writeString(soundIdIn.toString());
		buf.writeInt(categoryIn.ordinal());
		buf.writeFloat(volumeIn);
		buf.writeFloat(pitchIn);
		buf.writeBlockPos(position);
		buf.writeBlockPos(soundPosition);
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
					comp.startPlayingSound(soundIdIn, categoryIn, volumeIn, pitchIn, soundPosition, 0);
				});
			}
		});
	}
}
