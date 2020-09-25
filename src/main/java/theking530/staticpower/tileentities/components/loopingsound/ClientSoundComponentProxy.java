package theking530.staticpower.tileentities.components.loopingsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSoundComponentProxy implements ISoundComponentProxy {
	private SimpleSound currentlyPlayingSound;

	@Override
	public void startPlayingSound(World world, ResourceLocation soundIdIn, SoundCategory categoryIn, float volumeIn, float pitchIn, BlockPos pos, int blockRadius) {
		if (world.isRemote) {
			if (currentlyPlayingSound == null || !Minecraft.getInstance().getSoundHandler().isPlaying(currentlyPlayingSound)) {
				currentlyPlayingSound = new SimpleSound(soundIdIn, SoundCategory.BLOCKS, volumeIn, pitchIn, true, 0, ISound.AttenuationType.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
				Minecraft.getInstance().getSoundHandler().play(currentlyPlayingSound);
			}
		}
	}

	@Override
	public void stopPlayingSound(World world) {
		if (world.isRemote) {
			if (currentlyPlayingSound != null && Minecraft.getInstance().getSoundHandler().isPlaying(currentlyPlayingSound)) {
				Minecraft.getInstance().getSoundHandler().stop(currentlyPlayingSound);
			}
			currentlyPlayingSound = null;
		}
	}

}
