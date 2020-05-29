package theking530.staticpower.items.itemfilter;

import java.util.Arrays;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.gui.button.BaseButton;
import theking530.api.gui.button.BaseButton.ClickedState;
import theking530.api.gui.button.TextButton;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.StaticPowerItemStackGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiItemFilter extends StaticPowerItemStackGui<ContainerItemFilter, ItemFilter> {

	public GuiInfoTab infoTab;
	private InventoryItemFilter inventoryItemFilter;

	private TextButton whitelistButton;
	private TextButton nbtButton;
	private TextButton metaButton;
	private TextButton oreButton;
	private TextButton modButton;

	public GuiItemFilter(ContainerItemFilter container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
	}

	public void initializeGui() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((IItemHandler handler) -> {
			inventoryItemFilter = (InventoryItemFilter) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (inventoryItemFilter == null) {
			StaticPower.LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getItemStack().getDisplayName()));
			return;
		}

		guiLeft = (this.width - this.xSize) / 2;
		guiTop = (this.height - this.ySize) / 2;

		infoTab = new GuiInfoTab(110, 40);
		getTabManager().registerTab(infoTab);
		getTabManager().setInitiallyOpenTab(infoTab);

		whitelistButton = new TextButton(20, 20, 30, 40, "W");
		nbtButton = new TextButton(20, 20, 52, 40, "N");
		metaButton = new TextButton(20, 20, 74, 40, "M");
		oreButton = new TextButton(20, 20, 96, 40, "O");
		modButton = new TextButton(20, 20, 118, 40, "D");

		nbtButton.setToggleable(true);
		metaButton.setToggleable(true);
		oreButton.setToggleable(true);
		modButton.setToggleable(true);

		nbtButton.setToggled(inventoryItemFilter.getMatchNBT());
		metaButton.setToggled(inventoryItemFilter.getMatchMetadata());
		oreButton.setToggled(inventoryItemFilter.getMatchOreDictionary());
		modButton.setToggled(inventoryItemFilter.getMatchModID());

		whitelistButton.setText(inventoryItemFilter.getWhiteListMode() ? "W" : "B");
		whitelistButton.setTooltip(inventoryItemFilter.getWhiteListMode() ? "Whitelist" : "Blacklist");
		nbtButton.setTooltip("Enable NBT Match");
		metaButton.setTooltip("Enable Metadata Match");
		oreButton.setTooltip("Enable Ore Dictionary Match");
		modButton.setTooltip("Enable Mod Match");

		buttonManager.registerButton(whitelistButton);
		buttonManager.registerButton(nbtButton);
		buttonManager.registerButton(metaButton);
		buttonManager.registerButton(oreButton);
		buttonManager.registerButton(modButton);
	}

	@Override
	public void buttonPressed(BaseButton button, ClickedState mouseButton) {
		if (button == whitelistButton) {
			inventoryItemFilter.setWhiteListMode(!inventoryItemFilter.getWhiteListMode());
			String mode = inventoryItemFilter.getWhiteListMode() == true ? "W" : "B";
			whitelistButton.setText(mode);
			whitelistButton.setTooltip(inventoryItemFilter.getWhiteListMode() ? "Whitelist" : "Blacklist");
		}
		if (button == nbtButton) {
			inventoryItemFilter.setMatchNBT(!inventoryItemFilter.getMatchNBT());
		}
		if (button == metaButton) {
			inventoryItemFilter.setMatchMetadata(!inventoryItemFilter.getMatchMetadata());
		}
		if (button == oreButton) {
			inventoryItemFilter.setMatchOreDictionary(!inventoryItemFilter.getMatchOreDictionary());
		}

		// Send a packet to the server with the updated values.
		NetworkMessage msg = new PacketItemFilter(inventoryItemFilter.getWhiteListMode(), inventoryItemFilter.getMatchMetadata(), inventoryItemFilter.getMatchNBT(),
				inventoryItemFilter.getMatchOreDictionary());
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(inventoryItemFilter.getName());
		this.fontRenderer.drawString(name, this.xSize - 169, 6, 4210752);
	}

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawPlayerInventorySlots();

		int slotOffset = inventoryItemFilter.getFilterTier() == FilterTier.BASIC ? 3 : inventoryItemFilter.getFilterTier() == FilterTier.UPGRADED ? 1 : 0;
		for (int k = 0; k < inventoryItemFilter.getFilterTier().getSlotCount(); k++) {
			drawSlot(guiLeft + 8 + (k + slotOffset) * 18, guiTop + 19, 16, 16);
		}

		String text = ("Filter items going=into an inventory.");
		String[] splitMsg = text.split("=");
		infoTab.setText(inventoryItemFilter.owningItemStack.getDisplayName().getFormattedText(), Arrays.asList(splitMsg));
	}
}
