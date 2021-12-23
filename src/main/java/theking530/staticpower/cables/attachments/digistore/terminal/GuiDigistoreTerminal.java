package theking530.staticpower.cables.attachments.digistore.terminal;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal;

public class GuiDigistoreTerminal extends AbstractGuiDigistoreTerminal<ContainerDigistoreTerminal, DigistoreTerminal> {

	public GuiDigistoreTerminal(ContainerDigistoreTerminal container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 270);
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
