package theking530.staticpower.items.tools.miningdrill;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.StaticVoltHandler;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerItemStackGui;

public class GuiMiningDrill extends StaticPowerItemStackGui<ContainerMiningDrill, MiningDrill> {
	public static final Logger LOGGER = LogManager.getLogger(GuiMiningDrill.class);

	public GuiInfoTab infoTab;
	@SuppressWarnings("unused")
	private ItemStackHandler inventory;

	public GuiMiningDrill(ContainerMiningDrill container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 142);
		StaticVoltHandler svHandler = (StaticVoltHandler) getItemStack().getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).orElse(null);
		if (svHandler != null) {
			registerWidget(new GuiPowerBarFromEnergyStorage(svHandler, 8, 8, 16, 46));
		}
	}

	@Override
	public void initializeGui() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			inventory = (ItemStackHandler) handler;
		});

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110));
		infoTab.addLine(new StringTextComponent("Filter items going into an inventory."));
	}

	protected MiningDrill getMiningDrill() {
		return (MiningDrill) getItemStack().getItem();
	}
}
