package theking530.staticpower.tileentities.nonpowered.placer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiAutomaticPlacer extends StaticPowerTileEntityGui<ContainerAutomaticPlacer, TileEntityAutomaticPlacer> {
	public GuiAutomaticPlacer(ContainerAutomaticPlacer container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
	}
}
