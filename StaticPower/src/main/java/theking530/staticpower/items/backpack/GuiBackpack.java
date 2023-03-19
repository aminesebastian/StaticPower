package theking530.staticpower.items.backpack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.gui.screens.StaticCoreItemStackScreen;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;

public class GuiBackpack extends StaticCoreItemStackScreen<ContainerBackpack, Backpack> {
	public static final Logger LOGGER = LogManager.getLogger(GuiBackpack.class);

	public GuiInfoTab infoTab;
	@SuppressWarnings("unused")
	private ItemStackHandler inventory;

	public GuiBackpack(ContainerBackpack container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 172);
	}

	@Override
	public void initializeGui() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			inventory = (ItemStackHandler) handler;
		});

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110));
	}

	protected Backpack getMiningDrill() {
		return (Backpack) getItemStack().getItem();
	}
}
