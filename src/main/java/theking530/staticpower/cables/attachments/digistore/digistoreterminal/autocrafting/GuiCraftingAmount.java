package theking530.staticpower.cables.attachments.digistore.digistoreterminal.autocrafting;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public class GuiCraftingAmount extends StaticPowerContainerGui<ContainerCraftingAmount> {
	public TextInputWidget searchBar;
	public ScrollBarWidget scrollBar;
	public SpriteButton sortButton;
	public SpriteButton searchModeButton;

	public GuiCraftingAmount(ContainerCraftingAmount container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 100, 50);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
	}
}
