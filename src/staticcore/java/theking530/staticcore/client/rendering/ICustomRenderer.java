package theking530.staticcore.client.rendering;

import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public interface ICustomRenderer {
	public void render(Level level, RenderLevelStageEvent event);
}
