package theking530.staticpower.entities.player.datacapability.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import theking530.staticpower.entities.player.datacapability.CustomFluidUnderwaterSoundInstance;

public class ClientPlayerCapabilityProxy implements IPlayerCapabilityProxy {
	private CustomFluidUnderwaterSoundInstance underwaterSoundInstance;

	@Override
	public void enteredCustomFluid(PlayerTickEvent event, Fluid Fluid) {
		if(underwaterSoundInstance != null) {
			underwaterSoundInstance.setIsUnderwater(false);
		}
		
		underwaterSoundInstance = new CustomFluidUnderwaterSoundInstance(event.player);
		underwaterSoundInstance.setIsUnderwater(true);
		Minecraft.getInstance().getSoundManager().play(underwaterSoundInstance);

	}

	@Override
	public void leftCustomFluid(PlayerTickEvent event, Fluid fluid) {
		if(underwaterSoundInstance != null) {
			underwaterSoundInstance.setIsUnderwater(false);
		}
	}

	@Override
	public void inCustomFluidTick(PlayerTickEvent event) {
		
	}
}
