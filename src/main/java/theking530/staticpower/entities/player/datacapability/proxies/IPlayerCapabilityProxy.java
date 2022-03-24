package theking530.staticpower.entities.player.datacapability.proxies;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public interface IPlayerCapabilityProxy {

	public void enteredCustomFluid(PlayerTickEvent event, Fluid Fluid);

	public void inCustomFluidTick(PlayerTickEvent event);

	public void leftCustomFluid(PlayerTickEvent event, Fluid fluid);
}
