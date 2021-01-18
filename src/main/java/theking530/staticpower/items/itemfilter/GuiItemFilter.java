package theking530.staticpower.items.itemfilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerItemStackGui;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiItemFilter extends StaticPowerItemStackGui<ContainerItemFilter, ItemFilter> {
	public static final Logger LOGGER = LogManager.getLogger(GuiItemFilter.class);

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
			LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getItemStack().getDisplayName()));
			return;
		}

		guiLeft = (this.width - this.xSize) / 2;
		guiTop = (this.height - this.ySize) / 2;

		// Update the info tab label.
		getTabManager().registerTab(infoTab = new GuiInfoTab(110));
		infoTab.addLine("desc", new StringTextComponent("Filter items going into an inventory."));

		registerWidget(whitelistButton = new SpriteButton(45, 40, 20, 20, StaticPowerSprites.FILTER_WHITELIST, null, this::buttonPressed));
		registerWidget(nbtButton = new SpriteButton(67, 40, 20, 20, StaticPowerSprites.FILTER_NBT, null, this::buttonPressed));
		registerWidget(tagButton = new SpriteButton(89, 40, 20, 20, StaticPowerSprites.FILTER_TAG, null, this::buttonPressed));
		registerWidget(modButton = new SpriteButton(111, 40, 20, 20, StaticPowerSprites.FILTER_MOD, null, this::buttonPressed));

		whitelistButton.setRegularTexture(getItemFilter().isWhiteListMode(getItemStack()) ? StaticPowerSprites.FILTER_WHITELIST : StaticPowerSprites.FILTER_BLACKLIST);
		whitelistButton.setTooltip(new TranslationTextComponent(getItemFilter().isWhiteListMode(getItemStack()) ? "Whitelist" : "Blacklist"));

		nbtButton.setToggleable(true).setToggled(getItemFilter().filterForNBT(getItemStack())).setTooltip(new TranslationTextComponent("Enable NBT Match"));
		tagButton.setToggleable(true).setToggled(getItemFilter().filterForTag(getItemStack())).setTooltip(new TranslationTextComponent("Enable Ore Dictionary Match"));
		modButton.setToggleable(true).setToggled(getItemFilter().filterForMod(getItemStack())).setTooltip(new TranslationTextComponent("Enable Mod Match"));

		if (this.getContainer().filterInventory.getSlots() > 9) {
			int additionalHeight = 16;
			this.setDesieredGuiSize(xSize, ySize + additionalHeight - 4);
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
		NetworkMessage msg = new PacketItemFilter(getItemFilter().isWhiteListMode(getItemStack()), getItemFilter().filterForNBT(getItemStack()), getItemFilter().filterForTag(getItemStack()),
				getItemFilter().filterForMod(getItemStack()));
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	@Override
	protected void drawBackgroundExtras(MatrixStack stack, float f, int i, int j) {

	}

	protected ItemFilter getItemFilter() {
		return (ItemFilter) getItemStack().getItem();
	}
}
