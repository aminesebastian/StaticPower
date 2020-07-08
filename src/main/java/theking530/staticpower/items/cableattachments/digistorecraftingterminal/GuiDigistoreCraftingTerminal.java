package theking530.staticpower.items.cableattachments.digistorecraftingterminal;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.items.cableattachments.digistoreterminal.AbstractGuiDigistoreTerminal;

public class GuiDigistoreCraftingTerminal extends AbstractGuiDigistoreTerminal<ContainerDigistoreCraftingTerminal, DigistoreCraftingTerminal> {

	public GuiDigistoreCraftingTerminal(ContainerDigistoreCraftingTerminal container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 270);
		registerWidget(new ArrowProgressBar(118, 138, 32, 16));
		searchBar.setSize(70, 12);
		searchBar.setPosition(98, 6);

		GuiInfoTab tab = new GuiInfoTab(100, 100);
		tab.setTabSide(TabSide.RIGHT);
		getTabManager().setPosition(0, -5);
		getTabManager().registerTab(tab);
	}

	@Override
	protected Vector2D getContainerLabelDrawLocation() {
		return new Vector2D(8, 8);
	}

	@Override
	protected boolean shouldDrawInventoryLabel() {
		return true;
	}

	@Override
	protected Vector2D getInventoryLabelDrawLocation() {
		return new Vector2D(8, 176);
	}
}
