package theking530.staticpower.tileentities.nonpowered.directdropper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDirectDropper extends StaticPowerTileEntityGui<ContainerDirectDropper, TileEntityDirectDropper> {
	public GuiDirectDropper(ContainerDirectDropper container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
	}
}
