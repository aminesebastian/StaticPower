package theking530.staticpower.blockentities.components.loopingsound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public interface ISoundComponentProxy {
	void startPlayingSound(Level world, ResourceLocation soundIdIn, SoundSource categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius);

	void stopPlayingSound(Level world);
}
