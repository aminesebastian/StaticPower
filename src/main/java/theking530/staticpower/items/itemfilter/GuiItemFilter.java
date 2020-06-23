package theking530.staticpower.items.itemfilter;

import java.util.Arrays;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.gui.widgets.button.BaseButton;
import theking530.api.gui.widgets.button.TextButton;
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

	@Override
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

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110, 40));
		String text = ("Filter items going=into an inventory.");
		String[] splitMsg = text.split("=");
		infoTab.setText(inventoryItemFilter.owningItemStack.getDisplayName().getFormattedText(), Arrays.asList(splitMsg));

		registerWidget(whitelistButton = new TextButton(30, 40, 20, 20, "W", this::buttonPressed));
		registerWidget(nbtButton = new TextButton(52, 40, 20, 20, "N", this::buttonPressed));
		registerWidget(metaButton = new TextButton(74, 40, 20, 20, "M", this::buttonPressed));
		registerWidget(oreButton = new TextButton(96, 40, 20, 20, "O", this::buttonPressed));
		registerWidget(modButton = new TextButton(118, 40, 20, 20, "D", this::buttonPressed));

		whitelistButton.setText(inventoryItemFilter.getWhiteListMode() ? "W" : "B").setTooltip(new TranslationTextComponent(inventoryItemFilter.getWhiteListMode() ? "Whitelist" : "Blacklist"));

		nbtButton.setToggleable(true).setToggled(inventoryItemFilter.getMatchNBT()).setTooltip(new TranslationTextComponent("Enable NBT Match"));
		metaButton.setToggleable(true).setToggled(inventoryItemFilter.getMatchMetadata()).setTooltip(new TranslationTextComponent("Enable Metadata Match"));
		oreButton.setToggleable(true).setToggled(inventoryItemFilter.getMatchOreDictionary()).setTooltip(new TranslationTextComponent("Enable Ore Dictionary Match"));
		modButton.setToggleable(true).setToggled(inventoryItemFilter.getMatchModID()).setTooltip(new TranslationTextComponent("Enable Mod Match"));
	}

	public void buttonPressed(BaseButton button) {
		if (button == whitelistButton) {
			inventoryItemFilter.setWhiteListMode(!inventoryItemFilter.getWhiteListMode());
			String mode = inventoryItemFilter.getWhiteListMode() == true ? "W" : "B";
			whitelistButton.setText(mode);
			whitelistButton.setTooltip(new TranslationTextComponent(inventoryItemFilter.getWhiteListMode() ? "Whitelist" : "Blacklist"));
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

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawContainerSlots(container.inventorySlots);
	}
}
