package theking530.staticpower.integration.JEI;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public class JEIContainerHandler<T extends ContainerScreen<?>> implements IGuiContainerHandler<StaticPowerContainerGui<?>> {
	@Override
	public List<Rectangle2d> getGuiExtraAreas(StaticPowerContainerGui<?> containerScreen) {
		return containerScreen.getGuiBounds();
	}
}
