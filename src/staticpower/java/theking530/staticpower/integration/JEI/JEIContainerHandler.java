package theking530.staticpower.integration.JEI;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import theking530.staticcore.gui.screens.StaticPowerContainerScreen;

public class JEIContainerHandler<T extends AbstractContainerScreen<?>> implements IGuiContainerHandler<StaticPowerContainerScreen<?>> {
	@Override
	public List<Rect2i> getGuiExtraAreas(StaticPowerContainerScreen<?> containerScreen) {
		return containerScreen.getGuiBounds();
	}
}
