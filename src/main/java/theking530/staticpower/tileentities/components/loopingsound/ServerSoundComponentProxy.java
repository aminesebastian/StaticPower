package theking530.staticpower.tileentities.components.loopingsound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class ServerSoundComponentProxy implements ISoundComponentProxy {
	@Override
	public void startPlayingSound(Level world, ResourceLocation soundIdIn, SoundSource categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopPlayingSound(Level world) {
		// TODO Auto-generated method stub

	}
}
