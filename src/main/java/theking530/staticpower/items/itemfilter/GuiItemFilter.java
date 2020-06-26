package theking530.staticpower.items.itemfilter;

import java.util.Arrays;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.gui.widgets.button.SpriteButton;
import theking530.api.gui.widgets.button.StandardButton;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerItemStackGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiItemFilter extends StaticPowerItemStackGui<ContainerItemFilter, ItemFilter> {
	public GuiInfoTab infoTab;
	private ItemStackHandler filterInventory;

	private SpriteButton whitelistButton;
	private SpriteButton nbtButton;
	private SpriteButton tagButton;
	private SpriteButton modButton;

	public GuiItemFilter(ContainerItemFilter container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
	}

	@Override
	public void initializeGui() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			StaticPower.LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getItemStack().getDisplayName()));
			return;
		}

		guiLeft = (this.width - this.xSize) / 2;
		guiTop = (this.height - this.ySize) / 2;

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110, 40));
		String text = ("Filter items going=into an inventory.");
		String[] splitMsg = text.split("=");
		infoTab.setText(getItemStack().getDisplayName().getFormattedText(), Arrays.asList(splitMsg));

		registerWidget(whitelistButton = new SpriteButton(41, 40, 20, 20, StaticPowerSprites.FILTER_WHITELIST, null, this::buttonPressed));
		registerWidget(nbtButton = new SpriteButton(63, 40, 20, 20, StaticPowerSprites.FILTER_NBT, null, this::buttonPressed));
		registerWidget(tagButton = new SpriteButton(85, 40, 20, 20, StaticPowerSprites.FILTER_TAG, null, this::buttonPressed));
		registerWidget(modButton = new SpriteButton(107, 40, 20, 20, StaticPowerSprites.FILTER_MOD, null, this::buttonPressed));

		whitelistButton.setRegularTexture(getItemFilter().isWhiteListMode(getItemStack()) ? StaticPowerSprites.FILTER_WHITELIST : StaticPowerSprites.FILTER_BLACKLIST);
		whitelistButton.setTooltip(new TranslationTextComponent(getItemFilter().isWhiteListMode(getItemStack()) ? "Whitelist" : "Blacklist"));

		nbtButton.setToggleable(true).setToggled(getItemFilter().filterForNBT(getItemStack())).setTooltip(new TranslationTextComponent("Enable NBT Match"));
		tagButton.setToggleable(true).setToggled(getItemFilter().filterForTag(getItemStack())).setTooltip(new TranslationTextComponent("Enable Ore Dictionary Match"));
		modButton.setToggleable(true).setToggled(getItemFilter().filterForMod(getItemStack())).setTooltip(new TranslationTextComponent("Enable Mod Match"));
	}

	public void buttonPressed(StandardButton button) {
		if (button == whitelistButton) {
			getItemFilter().setWhitelistMode(getItemStack(), !getItemFilter().isWhiteListMode(getItemStack()));
			whitelistButton.setRegularTexture(getItemFilter().isWhiteListMode(getItemStack()) ? StaticPowerSprites.FILTER_WHITELIST : StaticPowerSprites.FILTER_BLACKLIST);
			whitelistButton.setTooltip(new TranslationTextComponent(getItemFilter().isWhiteListMode(getItemStack()) ? "Whitelist" : "Blacklist"));
		}
		if (button == nbtButton) {
			getItemFilter().setFilterForNBT(getItemStack(), !getItemFilter().filterForNBT(getItemStack()));
		}
		if (button == tagButton) {
			getItemFilter().setFilterForTag(getItemStack(), !getItemFilter().filterForTag(getItemStack()));
		}
		if (button == modButton) {
			getItemFilter().setFilterForMod(getItemStack(), !getItemFilter().filterForMod(getItemStack()));
		}

		// Send a packet to the server with the updated values.
		NetworkMessage msg = new PacketItemFilter(getItemFilter().isWhiteListMode(getItemStack()), getItemFilter().filterForNBT(getItemStack()), getItemFilter().filterForTag(getItemStack()), getItemFilter().filterForMod(getItemStack()));
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawContainerSlots(container.inventorySlots);
	}

	protected ItemFilter getItemFilter() {
		return (ItemFilter) getItemStack().getItem();
	}
}
