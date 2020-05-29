package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import theking530.api.gui.RectangleBounds;
import theking530.api.gui.widgets.tabs.BaseGuiTab;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public class JEITabSlotAdjuster<T extends ContainerScreen<?>> implements IGuiContainerHandler<StaticPowerContainerGui<?>> {
	@Override
	public List<Rectangle2d> getGuiExtraAreas(StaticPowerContainerGui<?> containerScreen) {
		List<Rectangle2d> tabBoxes = new ArrayList<>();

		for (BaseGuiTab tab : containerScreen.getTabManager().getRegisteredTabs()) {
			RectangleBounds rect = tab.getBounds();
			tabBoxes.add(new Rectangle2d((int) rect.getX(), (int) rect.getY(), (int) rect.getW(), (int) rect.getH()));
		}
		return tabBoxes;
	}
}
