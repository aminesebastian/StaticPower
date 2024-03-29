package theking530.staticpower.items.tools.chainsaw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.staticcore.gui.screens.StaticCoreItemStackScreen;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;

public class GuiChainsaw extends StaticCoreItemStackScreen<ContainerChainsaw, Chainsaw> {
	public static final Logger LOGGER = LogManager.getLogger(GuiChainsaw.class);

	public GuiInfoTab infoTab;
	@SuppressWarnings("unused")
	private ItemStackHandler inventory;

	public GuiChainsaw(ContainerChainsaw container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 152);
		IStaticPowerStorage svHandler = getItemStack().getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).orElse(null);
		if (svHandler != null) {
			registerWidget(new GuiPowerBarFromStorage(svHandler, 8, 8, 16, 46));
		}
	}

	@Override
	public void initializeGui() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			inventory = (ItemStackHandler) handler;
		});

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110));
		infoTab.addLine("desc", Component.literal("Filter items going into an inventory."));
	}

	protected Chainsaw getBlade() {
		return (Chainsaw) getItemStack().getItem();
	}
}
