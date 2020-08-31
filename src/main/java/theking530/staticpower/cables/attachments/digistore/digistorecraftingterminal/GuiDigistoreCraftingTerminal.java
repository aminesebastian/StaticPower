package theking530.staticpower.cables.attachments.digistore.digistorecraftingterminal;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.EntityRenderWidget;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.attachments.digistore.digistoreterminal.AbstractGuiDigistoreTerminal;

public class GuiDigistoreCraftingTerminal extends AbstractGuiDigistoreTerminal<ContainerDigistoreCraftingTerminal, DigistoreCraftingTerminal> {
	private ArrowProgressBar progressBar;
	
	public GuiDigistoreCraftingTerminal(ContainerDigistoreCraftingTerminal container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 270);

	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		registerWidget(progressBar = (ArrowProgressBar) new ArrowProgressBar(118, 138).disableProgressTooltip());
		registerWidget(new EntityRenderWidget(10, 115, 42, 58, this.minecraft.player));
		searchBar.setSize(70, 12);
		searchBar.setPosition(98, 6);

		GuiInfoTab tab = new GuiInfoTab(100);
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

	@Override
	public void updateData() {
		if (getCableComponent().isManagerPresent()) {
			progressBar.setErrorState(false);
		} else {
			progressBar.setErrorState(true).setErrorMessage("Digistore Manager not present or out of power!");
		}
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);


	}
}
