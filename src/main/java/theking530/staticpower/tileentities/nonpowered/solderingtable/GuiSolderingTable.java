package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiSolderingTable extends StaticPowerTileEntityGui<ContainerSolderingTable, TileEntitySolderingTable> {

	private GuiInfoTab infoTab;

	public GuiSolderingTable(ContainerSolderingTable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {
		tabManager.registerTab(infoTab = new GuiInfoTab(100, 65));
		setOutputSlotSize(20);
	}
}
