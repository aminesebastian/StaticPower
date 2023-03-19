package theking530.staticcore.blockentity.components.loopingsound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class ServerSoundComponentProxy implements ISoundComponentProxy {
	@Override
	public void startPlayingSound(Level world, ResourceLocation soundIdIn, SoundSource categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius) {
	}

	@Override
	public void stopPlayingSound(Level world) {
	}
}
