package theking530.staticpower.blockentities.components.loopingsound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.blockentities.components.AbstractBlockEntityComponent;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class LoopingSoundComponent extends AbstractBlockEntityComponent {
	private final ISoundComponentProxy proxy;
	private int soundReactionTime;

	private int soundStopCooldown;
	private boolean shouldBeStopping;
	private boolean isPlaying;
	private int soundStartCooldown;

	/**
	 * Creates a looping sound component.
	 * 
	 * @param name         The name of the component (must be unique withint the
	 *                     owning tile entity).
	 * @param reactionTime How reactive this component should be. Meaning, if this
	 *                     value is 20, then the fastest this component can switch
	 *                     between on and off is 20 ticks. The higher this value,
	 *                     the more performant the component. But too high will
	 *                     introduce lag between when you want the sound to start
	 *                     and when it plays.
	 */
	public LoopingSoundComponent(String name, int reactionTime) {
		super(name);
		soundReactionTime = reactionTime;
		if (FMLEnvironment.dist == Dist.CLIENT) {
			proxy = new ClientSoundComponentProxy();
		} else {
			proxy = new ServerSoundComponentProxy();
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!getLevel().isClientSide()) {
			// Tick down the start cooldown so we don't spawn with start messages.
			if (soundStartCooldown > 0) {
				soundStartCooldown--;
			}

			// Check the should stop to make sure we should be stopping.
			if (shouldBeStopping) {
				soundStopCooldown--;
				if (soundStopCooldown <= 0) {
					LoopingSoundPacketStop syncPacket = new LoopingSoundPacketStop(this);
					StaticPowerMessageHandler.sendToAllPlayersInDimension(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), syncPacket);
					shouldBeStopping = false;
					soundStopCooldown = 0;
					isPlaying = false;
				}
			}
		}
	}

	public void startPlayingSound(SoundEvent sound, SoundSource categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius) {
		if (isPlaying) {
			return;
		}

		isPlaying = true;
		ResourceLocation soundId = ForgeRegistries.SOUND_EVENTS.getKey(sound);
		if (getLevel().isClientSide()) {
			proxy.startPlayingSound(getLevel(), soundId, categoryIn, volumeIn, pitchIn, pos, blockRadius);
		} else {
			if (soundStartCooldown == 0) {
				LoopingSoundPacketStart syncPacket = new LoopingSoundPacketStart(this, soundId, categoryIn, volumeIn, pitchIn, pos);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), blockRadius, syncPacket);
				shouldBeStopping = false;
				soundStartCooldown = soundReactionTime;
			}
		}
	}

	public void stopPlayingSound() {
		if (!isPlaying) {
			return;
		}

		if (getLevel().isClientSide()) {
			proxy.stopPlayingSound(getLevel());
		} else {
			if (!shouldBeStopping) {
				shouldBeStopping = true;
				soundStopCooldown = soundReactionTime;
			}
		}
	}

	@Override
	public void onOwningBlockEntityUnloaded() {
		stopPlayingSound();
	}
}
