package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class AbstractGuiSolderingTable<T extends TileEntitySolderingTable, K extends AbstractContainerSolderingTable<T>> extends StaticPowerTileEntityGui<K, T> {

	protected GuiInfoTab infoTab;

	public AbstractGuiSolderingTable(K container, PlayerInventory invPlayer, ITextComponent name, int width, int height) {
		super(container, invPlayer, name, width, height);
	}

	@Override
	public void initializeGui() {
		tabManager.registerTab(infoTab = new GuiInfoTab(100));
		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {

	}
}
