package theking530.staticpower.client.rendering.renderers;

import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public interface ICustomRenderer {
	public void render(Level level, RenderLevelStageEvent event);
}
