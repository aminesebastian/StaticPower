package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import theking530.common.utilities.Color;
import theking530.staticpower.client.container.slots.DigistoreSlot;
import theking530.staticpower.client.container.slots.NoCountRenderSlot;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDigistoreManager extends StaticPowerTileEntityGui<ContainerDigistoreManager, TileEntityDigistoreManager> {

	public GuiDigistoreManager(ContainerDigistoreManager container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 250);
	}

	@Override
	public void initializeGui() {

	}

	@Override
	public void drawSlot(Slot slotIn) {
		if (slotIn instanceof DigistoreSlot && slotIn.getHasStack()) {
			super.drawSlot(new NoCountRenderSlot(slotIn));
			this.drawStringWithSize(String.valueOf(slotIn.getStack().getCount()), slotIn.xPos + 16, slotIn.yPos + 16, 0.6f, Color.WHITE, true);
		} else {
			super.drawSlot(slotIn);
		}
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);

	}
}
