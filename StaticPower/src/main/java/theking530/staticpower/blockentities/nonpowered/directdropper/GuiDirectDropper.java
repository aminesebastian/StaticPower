package theking530.staticpower.blockentities.nonpowered.directdropper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;

public class GuiDirectDropper extends StaticCoreBlockEntityScreen<ContainerDirectDropper, BlockEntityDirectDropper> {
	public GuiDirectDropper(ContainerDirectDropper container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
	}
}
