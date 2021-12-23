package theking530.staticpower.integration.JEI;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public class JEIContainerHandler<T extends AbstractContainerScreen<?>> implements IGuiContainerHandler<StaticPowerContainerGui<?>> {
	@Override
	public List<Rect2i> getGuiExtraAreas(StaticPowerContainerGui<?> containerScreen) {
		return containerScreen.getGuiBounds();
	}
}
