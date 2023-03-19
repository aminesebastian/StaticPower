package theking530.staticcore.blockentity.components.loopingsound;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.network.NetworkMessage;

public class LoopingSoundPacketStart extends NetworkMessage {
	private String componentName;
	private ResourceLocation soundIdIn;
	private SoundSource categoryIn;
	private float volumeIn;
	private float pitchIn;
	private BlockPos position;
	private BlockPos soundPosition;

	public LoopingSoundPacketStart() {
	}

	public LoopingSoundPacketStart(LoopingSoundComponent component, ResourceLocation soundIdIn, SoundSource categoryIn, float volumeIn, float pitchIn, BlockPos pos) {
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
	public void decode(FriendlyByteBuf buf) {
		componentName = buf.readUtf();
		soundIdIn = new ResourceLocation(buf.readUtf());
		categoryIn = SoundSource.values()[buf.readInt()];
		volumeIn = buf.readFloat();
		pitchIn = buf.readFloat();
		position = buf.readBlockPos();
		soundPosition = buf.readBlockPos();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(componentName);
		buf.writeUtf(soundIdIn.toString());
		buf.writeInt(categoryIn.ordinal());
		buf.writeFloat(volumeIn);
		buf.writeFloat(pitchIn);
		buf.writeBlockPos(position);
		buf.writeBlockPos(soundPosition);
	}

	@Override
	@SuppressWarnings({ "resource", "deprecation" })
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			// Make sure the position is loaded.
			if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
				// Get the tile entity.
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);

				// If the component is found on the client, play the sound.
				ComponentUtilities.getComponent(LoopingSoundComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
					comp.startPlayingSound(ForgeRegistries.SOUND_EVENTS.getValue(soundIdIn), categoryIn, volumeIn, pitchIn, soundPosition, 0);
				});
			}
		});
	}
}
