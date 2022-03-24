package theking530.staticpower.entities.player.datacapability;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomFluidUnderwaterSoundInstance extends AbstractTickableSoundInstance {
	private final Player player;
	private int fade;
	private boolean isUnderwater;

	public CustomFluidUnderwaterSoundInstance(Player player) {
		super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.AMBIENT);
		this.player = player;
		this.looping = true;
		this.delay = 0;
		this.volume = 1.0F;
		this.relative = true;
	}

	public void tick() {
		if (!this.player.isRemoved() && this.fade >= 0) {
			if (isUnderwater) {
				++this.fade;
			} else {
				this.fade -= 2;
			}

			this.fade = Math.min(this.fade, 40);
			this.volume = Math.max(0.0F, Math.min((float) this.fade / 40.0F, 1.0F));
		} else {
			this.stop();
		}
	}

	public void setIsUnderwater(boolean isUnderwater) {
		this.isUnderwater = isUnderwater;
	}
}