package theking530.staticpower.cables.attachments.digistore.terminalbase;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModList;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;
import theking530.staticpower.cables.attachments.digistore.terminal.DigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.AutoCraftingStepsWidget;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.container.slots.DigistoreSlot;
import theking530.staticpower.container.slots.NoCountRenderSlot;
import theking530.staticpower.events.StaticPowerModEventRegistry;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.utilities.MetricConverter;

public abstract class AbstractGuiDigistoreTerminal<T extends AbstractContainerDigistoreTerminal<K>, K extends AbstractCableAttachment> extends AbstractCableAttachmentGui<T, K> {
	public enum TerminalViewType {
		ITEMS, CRAFTING;
	}

	public static final int MAX_CRAFTING_STEP_COLUMNS = 2;
	public static final int MAX_CRAFTING_STEPS_ROWS = 5;
	public static final int CRAFTING_LIST_UPDATE_RATE = 10;

	public TerminalViewType viewType;
	public TextInputWidget searchBar;
	public ScrollBarWidget scrollBar;
	public SpriteButton sortButton;
	public SpriteButton searchModeButton;
	public SpriteButton craftingViewButton;
	public SpriteButton itemViewButton;
	public AutoCraftingStepsWidget craftingStepsWidget;

	public TextButton activeCraftingLeft;
	public TextButton activeCraftingRight;
	public SpriteButton craftingRequestCancelButton;

	private int craftingRequestUpdateTimer;
	private int currentCraftingRequestIndex;

	public AbstractGuiDigistoreTerminal(T container, PlayerInventory invPlayer, ITextComponent name, int width, int height) {
		super(container, invPlayer, name, width, height);
		craftingRequestUpdateTimer = 0;
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		// Set the default view type.
		viewType = TerminalViewType.ITEMS;

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
		if (ModList.get().isLoaded(StaticPowerModEventRegistry.JEI_MODID)) {
			registerWidget(searchModeButton = new SpriteButton(-20, 40, 16, 16, StaticPowerSprites.SEARCH_MODE_DEFAULT, null, this::onSearchModeButtonPressed));
		}

		// Add island for the armor.
		registerWidget(new GuiIslandWidget(-24, 104, 30, 80));

		// Add island for the regular tab.
		registerWidget(new GuiIslandWidget(this.xSize - 4, 143, 25, 24));
		registerWidget(itemViewButton = (SpriteButton) new SpriteButton(this.xSize - 1, 145, 20, 20, StaticPowerSprites.FURNACE_ICON, null, (a, b) -> switchToDefaultView())
				.setShouldDrawButtonBackground(false));

		// Add island for the crafting tab.
		registerWidget(new GuiIslandWidget(this.xSize - 4, 168, 25, 24));
		registerWidget(craftingViewButton = (SpriteButton) new SpriteButton(this.xSize - 1, 170, 20, 20, StaticPowerSprites.CRAFTING_TABLE_ICON, null, (a, b) -> switchToCraftingStatusView())
				.setShouldDrawButtonBackground(false));

		// Add the crafting steps renderer.
		registerWidget(craftingStepsWidget = new AutoCraftingStepsWidget(8, 40, 160, 133, MAX_CRAFTING_STEPS_ROWS, MAX_CRAFTING_STEP_COLUMNS));
		craftingStepsWidget.setVisible(false);

		// Add the crafting rotation buttons and default them to invisible.
		registerWidget(activeCraftingLeft = new TextButton(7, 20, 14, 14, "<", (a, b) -> rotateCurrentlyViewedRecipeStatus(-1)));
		registerWidget(activeCraftingRight = new TextButton(this.xSize - 23, 20, 14, 14, ">", (a, b) -> rotateCurrentlyViewedRecipeStatus(-1)));
		activeCraftingLeft.setVisible(false);
		activeCraftingRight.setVisible(false);

		// Cancel crafting request button.
		registerWidget(craftingRequestCancelButton = (SpriteButton) new SpriteButton(98, 24, 8, 8, StaticPowerSprites.ERROR, null, (a, b) -> cancelCurrentCraftingRequest())
				.setShouldDrawButtonBackground(false));
		craftingRequestCancelButton.setTooltip(new StringTextComponent("Cancel"));
		craftingRequestCancelButton.setVisible(false);

		// Set default settings for tooltips/sprites.
		updateSortAndFilter();

		// Refresh the crafting queue.
		getContainer().refreshCraftingQueue();
	}

	@Override
	public void moveItems(MatrixStack matrixStack, Slot slotIn) {
		if (slotIn instanceof DigistoreSlot && slotIn.getHasStack()) {
			// Draw the slot with no count visible.
			moveItems(matrixStack, new NoCountRenderSlot(slotIn));

			ItemStack slotStack = slotIn.getStack();
			matrixStack.push();
			matrixStack.translate(0.0, 0.0, 260.0);
			if (DigistoreInventorySnapshot.getCraftableStateOfItem(slotStack) == DigistoreItemCraftableState.ONLY_CRAFTABLE) {
				// Draw a string that says: "Craft".
				GuiDrawUtilities.drawStringWithSize(matrixStack, "Craft", slotIn.xPos + 16, slotIn.yPos + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			} else {
				// Pass the itemstack count through the metric converter.
				MetricConverter count = new MetricConverter(slotIn.getStack().getCount());

				// Draw the item count string manually.
				GuiDrawUtilities.drawStringWithSize(matrixStack, count.getValueAsString(true), slotIn.xPos + 16, slotIn.yPos + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}
			matrixStack.pop();
		} else {
			super.moveItems(matrixStack, slotIn);
		}
	}

	@Override
	public void updateData() {
		// If we have JEI installed two way syncing enabled, changes to the JEI search
		// bar will also
		// affect the digistore search bar.
		if (ModList.get().isLoaded(StaticPowerModEventRegistry.JEI_MODID)) {
			if (DigistoreTerminal.getSearchMode(getContainer().getAttachment()) == DigistoreSearchMode.TWO_WAY) {
				String jeiSearchString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
				searchBar.setText(jeiSearchString);
			}
		}

		// Update the crafting queue regardless of if we are in that view or not.
		craftingRequestUpdateTimer++;
		if (craftingRequestUpdateTimer >= CRAFTING_LIST_UPDATE_RATE) {
			getContainer().refreshCraftingQueue();
			craftingRequestUpdateTimer = 0;
		}

		// Update the enabled states for the left and right buttons.
		activeCraftingLeft.setEnabled(getContainer().getCurrentCraftingQueue().size() > 1);
		activeCraftingRight.setEnabled(getContainer().getCurrentCraftingQueue().size() > 1);

		// Update the crafting widget and the tool tips.
		if (getContainer().getCurrentCraftingQueue().size() > 0) {
			CraftingRequestResponse currentRequest = getContainer().getCurrentCraftingQueue().get(currentCraftingRequestIndex);
			craftingStepsWidget.setRequest(currentRequest.getStepsBundle());
			craftingViewButton.setTooltip(new StringTextComponent(String.format("%1$d jobs currently queued.", getContainer().getCurrentCraftingQueue().size())));
		} else {
			craftingStepsWidget.setRequest(null);
			craftingViewButton.setTooltip(new StringTextComponent("No crafting jobs currently queued."));
		}

		// IF we are in the crafting view and have crafting requests, show the cancel
		// button.
		if (viewType == TerminalViewType.CRAFTING) {
			// Update the scroll offset.
			scrollBar.setMaxScroll(craftingStepsWidget.getMaxScrollPosition());
			craftingRequestCancelButton.setVisible(getContainer().getCurrentCraftingQueue().size() > 0);
			craftingStepsWidget.setScrollPosition(scrollBar.getScrollAmount());
		} else {
			// Update the scroll bar.
			scrollBar.setMaxScroll(getContainer().getMaxScroll());
			getContainer().setScrollOffset(scrollBar.getScrollAmount());
		}
	}

	@Override
	protected void drawBehindItems(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);
		itemRenderer.drawItem(Items.IRON_HELMET, guiLeft, guiTop, -18, 109, 0.3f);
		itemRenderer.drawItem(Items.IRON_CHESTPLATE, guiLeft, guiTop, -18, 127, 0.3f);
		itemRenderer.drawItem(Items.IRON_LEGGINGS, guiLeft, guiTop, -18, 145, 0.3f);
		itemRenderer.drawItem(Items.IRON_BOOTS, guiLeft, guiTop, -18, 163, 0.3f);

		if (viewType == TerminalViewType.CRAFTING) {
			if (getContainer().getCurrentCraftingQueue().size() > 0) {
				if (currentCraftingRequestIndex >= getContainer().getCurrentCraftingQueue().size()) {
					currentCraftingRequestIndex = getContainer().getCurrentCraftingQueue().size() - 1;
				}
				CraftingRequestResponse currentRequest = getContainer().getCurrentCraftingQueue().get(currentCraftingRequestIndex);
				this.itemRenderer.drawItem(currentRequest.getCraftingItem(), guiLeft, guiTop, (xSize / 2) - 8, 20, 1.0f);
			}
		}
	}

	protected void rotateCurrentlyViewedRecipeStatus(int direction) {
		if (getContainer().getCurrentCraftingQueue().size() == 0) {
			return;
		}

		currentCraftingRequestIndex = Math.floorMod(currentCraftingRequestIndex + direction, getContainer().getCurrentCraftingQueue().size());
		currentCraftingRequestIndex = SDMath.clamp(currentCraftingRequestIndex, 0, getContainer().getCurrentCraftingQueue().size() - 1);
	}

	protected void cancelCurrentCraftingRequest() {
		CraftingRequestResponse currentRequest = getContainer().getCurrentCraftingQueue().get(currentCraftingRequestIndex);
		getContainer().cancelCraftingRequest(currentRequest.getId());
	}

	protected boolean switchToCraftingStatusView() {
		if (viewType != TerminalViewType.CRAFTING && getContainer().getCurrentCraftingQueue().size() > 0) {
			getContainer().refreshCraftingQueue();
			viewType = TerminalViewType.CRAFTING;
			getContainer().setHideDigistoreInventory(true);
			getContainer().setViewType(TerminalViewType.CRAFTING);
			searchBar.setVisible(false);
			craftingStepsWidget.setVisible(true);
			activeCraftingLeft.setVisible(true);
			activeCraftingRight.setVisible(true);
			craftingRequestCancelButton.setVisible(true);
			scrollBar.setMaxScroll(0);
			return true;
		}
		return false;
	}

	protected boolean switchToDefaultView() {
		if (viewType != TerminalViewType.ITEMS) {
			viewType = TerminalViewType.ITEMS;
			getContainer().setHideDigistoreInventory(false);
			getContainer().setViewType(TerminalViewType.ITEMS);
			searchBar.setVisible(true);
			craftingStepsWidget.setVisible(false);
			activeCraftingLeft.setVisible(false);
			activeCraftingRight.setVisible(false);
			craftingRequestCancelButton.setVisible(false);
			scrollBar.setMaxScroll(0);
			return true;
		}
		return false;
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
		if (ModList.get().isLoaded(StaticPowerModEventRegistry.JEI_MODID)) {
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
		}

		switch (sortType) {
		case NAME:
			sortButton.setTooltip(new StringTextComponent("Sort by Name").appendString(" ").appendString(descending ? "(Descending)" : "(Ascending)"));
			sortButton.setRegularTexture(descending ? StaticPowerSprites.SORT_ALPHA_DESC : StaticPowerSprites.SORT_ALPHA_ASC);
			break;
		case COUNT:
			sortButton.setTooltip(new StringTextComponent("Sort by Count").appendString(" ").appendString(descending ? "(Descending)" : "(Ascending)"));
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
