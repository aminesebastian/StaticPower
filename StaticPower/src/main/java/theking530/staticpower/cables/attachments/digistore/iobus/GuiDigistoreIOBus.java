package theking530.staticpower.cables.attachments.digistore.iobus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachmentGui;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;
import theking530.staticpower.client.StaticPowerSprites;

public class GuiDigistoreIOBus extends AbstractCableAttachmentGui<ContainerDigistoreIOBus, DigistoreIOBusAttachment> {

	public GuiDigistoreIOBus(ContainerDigistoreIOBus container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 151);
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getAttachment(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100));
		getTabManager().setInitiallyOpenTab(redstoneTab);

		SpriteDrawable importDrawable = new SpriteDrawable(StaticCoreSprites.IMPORT, 16, 16);
		SpriteDrawable exportDrawable = new SpriteDrawable(StaticCoreSprites.EXPORT, 16, 16);

		registerWidget(new DrawableWidget<SpriteDrawable>(6, 20, 16, 16, importDrawable).setTooltip(Component.literal("Imports")));
		registerWidget(new DrawableWidget<SpriteDrawable>(6, 45, 16, 16, exportDrawable).setTooltip(Component.literal("Exports")));

		// Add island for the upgrades.
		registerWidget(new GuiIslandWidget(-25, 8, 30, 64));
	}
}
