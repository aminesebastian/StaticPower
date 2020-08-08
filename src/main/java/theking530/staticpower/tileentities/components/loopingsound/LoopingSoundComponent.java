package theking530.staticpower.tileentities.components.loopingsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public class LoopingSoundComponent extends AbstractTileEntityComponent {
	private SimpleSound currentlyPlayingSound;
	private int soundReactionTime;

	private int soundStopCooldown;
	private boolean shouldBeStopping;
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
	}

	@Override
	public void postProcessUpdate() {
		if (!getWorld().isRemote) {
			// Tick down the start cooldown so we don't spawn with start messages.
			if (soundStartCooldown > 0) {
				soundStartCooldown--;
			}

			// Check the should stop to make sure we should be stopping.
			if (shouldBeStopping) {
				soundStopCooldown--;
				if (soundStopCooldown <= 0) {
					LoopingSoundPacketStop syncPacket = new LoopingSoundPacketStop(this);
					StaticPowerMessageHandler.sendToAllPlayersInDimension(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), syncPacket);
					shouldBeStopping = false;
					soundStopCooldown = 0;
				}
			}
		}
	}

	public void startPlayingSound(ResourceLocation soundIdIn, SoundCategory categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius) {
		if (getWorld().isRemote) {
			if (currentlyPlayingSound == null || !Minecraft.getInstance().getSoundHandler().isPlaying(currentlyPlayingSound)) {
				currentlyPlayingSound = new SimpleSound(soundIdIn, SoundCategory.BLOCKS, volumeIn, pitchIn, true, 0, ISound.AttenuationType.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
				Minecraft.getInstance().getSoundHandler().play(currentlyPlayingSound);
			}
		} else {
			if (soundStartCooldown == 0) {
				LoopingSoundPacketStart syncPacket = new LoopingSoundPacketStart(this, soundIdIn, categoryIn, volumeIn, pitchIn, pos);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), blockRadius, syncPacket);
				shouldBeStopping = false;
				soundStartCooldown = soundReactionTime;
			}
		}
	}

	public void stopPlayingSound() {
		if (getWorld().isRemote) {
			if (currentlyPlayingSound != null && Minecraft.getInstance().getSoundHandler().isPlaying(currentlyPlayingSound)) {
				Minecraft.getInstance().getSoundHandler().stop(currentlyPlayingSound);
			}
			currentlyPlayingSound = null;
		} else {
			if (!shouldBeStopping) {
				shouldBeStopping = true;
				soundStopCooldown = soundReactionTime;
			}
		}
	}

	@Override
	public void onOwningTileEntityRemoved() {
		stopPlayingSound();
	}
}
