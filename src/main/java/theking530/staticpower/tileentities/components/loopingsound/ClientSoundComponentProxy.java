package theking530.staticpower.tileentities.components.loopingsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSoundComponentProxy implements ISoundComponentProxy {
	private SimpleSoundInstance currentlyPlayingSound;

	@Override
	public void startPlayingSound(Level world, ResourceLocation soundIdIn, SoundSource categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius) {
		if (world.isClientSide) {
			if (currentlyPlayingSound == null || !Minecraft.getInstance().getSoundManager().isActive(currentlyPlayingSound)) {
				currentlyPlayingSound = new SimpleSoundInstance(soundIdIn, SoundSource.BLOCKS, volumeIn, pitchIn, true, 0, SoundInstance.Attenuation.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
				Minecraft.getInstance().getSoundManager().play(currentlyPlayingSound);
			}
		}
	}

	@Override
	public void stopPlayingSound(Level world) {
		if (world.isClientSide) {
			if (currentlyPlayingSound != null && Minecraft.getInstance().getSoundManager().isActive(currentlyPlayingSound)) {
				Minecraft.getInstance().getSoundManager().stop(currentlyPlayingSound);
			}
			currentlyPlayingSound = null;
		}
	}

}
