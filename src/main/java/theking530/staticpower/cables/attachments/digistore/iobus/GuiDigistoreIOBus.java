package theking530.staticpower.cables.attachments.digistore.iobus;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;
import theking530.staticpower.client.StaticPowerSprites;

public class GuiDigistoreIOBus extends AbstractCableAttachmentGui<ContainerDigistoreIOBus, DigistoreIOBusAttachment> {

	public GuiDigistoreIOBus(ContainerDigistoreIOBus container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getAttachment(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100));
		getTabManager().setInitiallyOpenTab(redstoneTab);

		SpriteDrawable importDrawable = new SpriteDrawable(StaticPowerSprites.IMPORT, 16, 16);
		SpriteDrawable exportDrawable = new SpriteDrawable(StaticPowerSprites.EXPORT, 16, 16);

		registerWidget(new DrawableWidget(6, 20, 16, 16, importDrawable).setTooltip(new StringTextComponent("Imports")));
		registerWidget(new DrawableWidget(6, 45, 16, 16, exportDrawable).setTooltip(new StringTextComponent("Exports")));
		
		// Add island for the upgrades.
		registerWidget(new GuiIslandWidget(-25, 8, 30, 64));
	}
}
