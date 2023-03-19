package theking530.staticpower.items.itemfilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.gui.screens.StaticCoreItemStackScreen;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiItemFilter extends StaticCoreItemStackScreen<ContainerItemFilter, ItemFilter> {
	public static final Logger LOGGER = LogManager.getLogger(GuiItemFilter.class);

	public GuiInfoTab infoTab;
	private ItemStackHandler filterInventory;

	private SpriteButton whitelistButton;
	private SpriteButton nbtButton;
	private SpriteButton tagButton;
	private SpriteButton modButton;

	public GuiItemFilter(ContainerItemFilter container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 156);
	}

	@Override
	public void initializeGui() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getItemStack().getHoverName()));
			return;
		}

		leftPos = (this.width - this.imageWidth) / 2;
		topPos = (this.height - this.imageHeight) / 2;

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110));
		infoTab.addLine("desc", Component.literal("Filter items going into an inventory."));

		registerWidget(whitelistButton = new SpriteButton(45, 40, 20, 20, StaticPowerSprites.FILTER_WHITELIST, null, this::buttonPressed));
		registerWidget(nbtButton = new SpriteButton(67, 40, 20, 20, StaticPowerSprites.FILTER_NBT, null, this::buttonPressed));
		registerWidget(tagButton = new SpriteButton(89, 40, 20, 20, StaticPowerSprites.FILTER_TAG, null, this::buttonPressed));
		registerWidget(modButton = new SpriteButton(111, 40, 20, 20, StaticPowerSprites.FILTER_MOD, null, this::buttonPressed));

		whitelistButton.setRegularTexture(getItemFilter().isWhiteListMode(getItemStack()) ? StaticPowerSprites.FILTER_WHITELIST : StaticPowerSprites.FILTER_BLACKLIST);
		whitelistButton.setTooltip(Component.translatable(getItemFilter().isWhiteListMode(getItemStack()) ? "Whitelist" : "Blacklist"));

		nbtButton.setToggleable(true).setToggled(getItemFilter().filterForNBT(getItemStack())).setTooltip(Component.translatable("Enable NBT Match"));
		tagButton.setToggleable(true).setToggled(getItemFilter().filterForTag(getItemStack())).setTooltip(Component.translatable("Enable Ore Dictionary Match"));
		modButton.setToggleable(true).setToggled(getItemFilter().filterForMod(getItemStack())).setTooltip(Component.translatable("Enable Mod Match"));

		if (this.getMenu().filterInventory.getSlots() > 9) {
			int additionalHeight = 16;
			this.setDesieredGuiSize(imageWidth, imageHeight + additionalHeight - 4);
			whitelistButton.setPosition(whitelistButton.getPosition().getX(), whitelistButton.getPosition().getY() + additionalHeight);
			nbtButton.setPosition(nbtButton.getPosition().getX(), nbtButton.getPosition().getY() + additionalHeight);
			tagButton.setPosition(tagButton.getPosition().getX(), tagButton.getPosition().getY() + additionalHeight);
			modButton.setPosition(modButton.getPosition().getX(), modButton.getPosition().getY() + additionalHeight);
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == whitelistButton) {
			getItemFilter().setWhitelistMode(getItemStack(), !getItemFilter().isWhiteListMode(getItemStack()));
			whitelistButton.setRegularTexture(getItemFilter().isWhiteListMode(getItemStack()) ? StaticPowerSprites.FILTER_WHITELIST : StaticPowerSprites.FILTER_BLACKLIST);
			whitelistButton.setTooltip(Component.translatable(getItemFilter().isWhiteListMode(getItemStack()) ? "Whitelist" : "Blacklist"));
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
		NetworkMessage msg = new PacketItemFilter(getItemFilter().isWhiteListMode(getItemStack()), getItemFilter().filterForNBT(getItemStack()), getItemFilter().filterForTag(getItemStack()),
				getItemFilter().filterForMod(getItemStack()));
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float f, int i, int j) {

	}

	protected ItemFilter getItemFilter() {
		return (ItemFilter) getItemStack().getItem();
	}
}
