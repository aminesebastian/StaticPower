package theking530.staticpower.blockentities.nonpowered.placer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;

public class GuiAutomaticPlacer extends StaticCoreBlockEntityScreen<ContainerAutomaticPlacer, BlockEntityAutomaticPlacer> {
	public GuiAutomaticPlacer(ContainerAutomaticPlacer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
	}
}
