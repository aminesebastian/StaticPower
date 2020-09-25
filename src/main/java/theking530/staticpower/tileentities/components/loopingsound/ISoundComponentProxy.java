package theking530.staticpower.tileentities.components.loopingsound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISoundComponentProxy {
	void startPlayingSound(World world, ResourceLocation soundIdIn, SoundCategory categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius);

	void stopPlayingSound(World world);
}
