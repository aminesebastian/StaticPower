package theking530.staticpower.items.cableattachments.digistoreterminal;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.utilities.Vector2D;

public class GuiDigistoreTerminal extends AbstractGuiDigistoreTerminal<ContainerDigistoreTerminal, DigistoreTerminal> {

	public GuiDigistoreTerminal(ContainerDigistoreTerminal container, PlayerInventory invPlayer, ITextComponent name) {
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
