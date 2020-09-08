package theking530.staticpower.cables.attachments.digistore.terminal;

import com.google.common.base.Strings;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;
import theking530.staticpower.cables.attachments.digistore.digistoreterminal.DigistoreTerminal;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.container.slots.DigistoreSlot;
import theking530.staticpower.container.slots.NoCountRenderSlot;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.utilities.MetricConverter;

public abstract class AbstractGuiDigistoreTerminal<T extends AbstractContainerDigistoreTerminal<K>, K extends AbstractCableAttachment> extends AbstractCableAttachmentGui<T, K> {
	public TextInputWidget searchBar;
	public ScrollBarWidget scrollBar;
	public SpriteButton sortButton;
	public SpriteButton searchModeButton;

	public AbstractGuiDigistoreTerminal(T container, PlayerInventory invPlayer, ITextComponent name, int width, int height) {
		super(container, invPlayer, name, width, height);

	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		// Add search bar and sync it with JEI if requested.
		String initialSerachString = "";
		if (DigistoreTerminal.getSearchMode(getContainer().getAttachment()) == DigistoreSearchMode.TWO_WAY) {
			initialSerachString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
		}
		registerWidget(searchBar = new TextInputWidget(initialSerachString, 79, 6, 89, 12).setTypedCallback(this::onSearchTextChanged));

		// Add island for scroll bar.
		registerWidget(new GuiIslandWidget(170, 31, 26, 110));

		// Add scroll bar.
		registerWidget(scrollBar = new ScrollBarWidget(179, 36, 100));

		// Add island for the buttons.
		registerWidget(new GuiIslandWidget(-24, 16, 24, 44));

		// Add sort button.
		registerWidget(sortButton = new SpriteButton(-20, 20, 16, 16, StaticPowerSprites.SORT_NUMERICAL_DESC, null, this::onSortButtonPressed));

		// Add search mode button.
		registerWidget(searchModeButton = new SpriteButton(-20, 40, 16, 16, StaticPowerSprites.SEARCH_MODE_DEFAULT, null, this::onSearchModeButtonPressed));

		// Add island for the armor.
		registerWidget(new GuiIslandWidget(-24, 104, 30, 80));

		// Set default settings for tooltips/sprites.
		updateSortAndFilter();
	}

	@Override
	public void drawSlot(Slot slotIn) {
		if (slotIn instanceof DigistoreSlot && slotIn.getHasStack()) {
			// Draw the slot with no count visible.
			drawSlot(new NoCountRenderSlot(slotIn));

			ItemStack slotStack = slotIn.getStack();
			if (DigistoreInventorySnapshot.getCraftableStateOfItem(slotStack) == DigistoreItemCraftableState.ONLY_CRAFTABLE) {
				// Draw a string that says: "Craft".
				GuiDrawUtilities.drawStringWithSize("Craft", slotIn.xPos + 16, slotIn.yPos + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			} else {
				// Pass the itemstack count through the metric converter.
				MetricConverter count = new MetricConverter(slotIn.getStack().getCount());

				// Draw the item count string manually.
				GuiDrawUtilities.drawStringWithSize(count.getValueAsString(true), slotIn.xPos + 16, slotIn.yPos + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}
		} else {
			super.drawSlot(slotIn);
		}
	}

	@Override
	public void updateData() {
		// If we have two way syncing enabled, changes to the JEI search bar will also
		// affect the digistore search bar.
		if (DigistoreTerminal.getSearchMode(getContainer().getAttachment()) == DigistoreSearchMode.TWO_WAY) {
			String jeiSearchString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
			searchBar.setText(jeiSearchString);
		}
		scrollBar.setMaxScroll(getContainer().getMaxScroll());
		getContainer().setScrollOffset(scrollBar.getScrollAmount());
	}

	@Override
	protected void drawBehindItems(float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(partialTicks, mouseX, mouseY);
		itemRenderer.drawItem(Items.IRON_HELMET, guiLeft, guiTop, -18, 109, 0.3f);
		itemRenderer.drawItem(Items.IRON_CHESTPLATE, guiLeft, guiTop, -18, 127, 0.3f);
		itemRenderer.drawItem(Items.IRON_LEGGINGS, guiLeft, guiTop, -18, 145, 0.3f);
		itemRenderer.drawItem(Items.IRON_BOOTS, guiLeft, guiTop, -18, 163, 0.3f);
	}

	protected void onSearchTextChanged(TextInputWidget searchBar, String text) {
		// If we have one way or two way syncing enabled, changes to the digistore
		// search bar will also affect the JEI search bar.
		if (DigistoreTerminal.getSearchMode(getContainer().getAttachment()) != DigistoreSearchMode.DEFAULT) {
			PluginJEI.RUNTIME.getIngredientFilter().setFilterText(text);
		}

		// Update the modes and filters
		updateSortAndFilter();
	}

	protected void onSortButtonPressed(StandardButton button, MouseButton mouseButton) {
		if (mouseButton == MouseButton.LEFT) {
			// If we are descending, go to ascending mode. Otherwise, go to the next mode.
			if (getContainer().isSortDescending()) {
				DigistoreTerminal.setSortDescending(getContainer().getAttachment(), false);
			} else {
				DigistoreTerminal.setSortType(getContainer().getAttachment(), DigistoreTerminal.getSortType(getContainer().getAttachment()).getNextType());
				DigistoreTerminal.setSortDescending(getContainer().getAttachment(), true);
			}
		} else {
			// If we are ascending, go to descending mode. Otherwise, go to the next mode.
			if (!getContainer().isSortDescending()) {
				DigistoreTerminal.setSortDescending(getContainer().getAttachment(), true);
			} else {
				DigistoreTerminal.setSortType(getContainer().getAttachment(), DigistoreTerminal.getSortType(getContainer().getAttachment()).getPrevType());
				DigistoreTerminal.setSortDescending(getContainer().getAttachment(), false);
			}
		}

		// Update the modes and filters
		updateSortAndFilter();
	}

	protected void onSearchModeButtonPressed(StandardButton button, MouseButton mouseButton) {
		if (mouseButton == MouseButton.LEFT) {
			// Increment the next search mode.
			DigistoreTerminal.setSearchMode(getContainer().getAttachment(), DigistoreTerminal.getSearchMode(getContainer().getAttachment()).getNextType());
		} else {
			// Increment the previous search mode.
			DigistoreTerminal.setSearchMode(getContainer().getAttachment(), DigistoreTerminal.getSearchMode(getContainer().getAttachment()).getPrevType());
		}

		// Update the modes and filters
		updateSortAndFilter();
	}

	protected void updateSortAndFilter() {
		// Get the attachment.
		ItemStack attachment = getContainer().getAttachment();

		// Get all the modes and filter settings.
		DigistoreSearchMode searchMode = DigistoreTerminal.getSearchMode(attachment);
		DigistoreInventorySortType sortType = DigistoreTerminal.getSortType(attachment);
		boolean descending = DigistoreTerminal.getSortDescending(attachment);

		// Update tooltips and sprites.
		switch (searchMode) {
		case DEFAULT:
			searchModeButton.setTooltip(new StringTextComponent("Default Search Mode"));
			searchModeButton.setRegularTexture(StaticPowerSprites.SEARCH_MODE_DEFAULT);
			break;
		case ONE_WAY:
			searchModeButton.setTooltip(new StringTextComponent("One-Way JEI Search Mode"));
			searchModeButton.setRegularTexture(StaticPowerSprites.SEARCH_MODE_ONE_WAY);
			break;
		case TWO_WAY:
			searchModeButton.setTooltip(new StringTextComponent("Two-Way JEI Search Mode"));
			searchModeButton.setRegularTexture(StaticPowerSprites.SEARCH_MODE_TWO_WAY);
			break;
		}

		switch (sortType) {
		case NAME:
			sortButton.setTooltip(new StringTextComponent("Sort by Name").appendText(" ").appendText(descending ? "(Descending)" : "(Ascending)"));
			sortButton.setRegularTexture(descending ? StaticPowerSprites.SORT_ALPHA_DESC : StaticPowerSprites.SORT_ALPHA_ASC);
			break;
		case COUNT:
			sortButton.setTooltip(new StringTextComponent("Sort by Count").appendText(" ").appendText(descending ? "(Descending)" : "(Ascending)"));
			sortButton.setRegularTexture(descending ? StaticPowerSprites.SORT_NUMERICAL_DESC : StaticPowerSprites.SORT_NUMERICAN_ASC);
			break;
		}

		// Update the container who will in turn update the server side values.
		getContainer().updateSortAndFilter(searchBar.getText(), searchMode, sortType, descending);
	}

	@Override
	public DigistoreCableProviderComponent getCableComponent() {
		return getContainer().getCableComponent();
	}
}
