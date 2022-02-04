package theking530.staticpower.cables.attachments.digistore.terminalbase;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;
import theking530.staticpower.cables.attachments.digistore.terminal.DigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.AutoCraftingStepsWidget;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;
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
	public static final int DEFAULT_ITEMS_PER_ROW = 9;
	public static final int DEFAULT_MAX_ROWS_ON_SCREEN = 8;
	public static final Vector2D DEFAULT_INVENTORY_START_POSITION = new Vector2D(8, 22);

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

	public DrawableWidget<SpriteDrawable> filledAmountSprite;
	public DrawableWidget<SpriteDrawable> typeAmountSprite;

	/**
	 * Indicates how many items will be displayed per row of digitsore inventory.
	 */
	protected int itemsPerRow;
	/**
	 * Indicates the maximum amount of digitstore inventory rows to render.
	 */
	protected int maxRows;
	/**
	 * Sets the position of the digistore inventory to render at.
	 */
	protected Vector2D digistoreInventoryPosition;

	private List<DigistoreSlotButton> fakeContainerSlots;

	private int craftingRequestUpdateTimer;
	private int currentCraftingRequestIndex;

	public AbstractGuiDigistoreTerminal(T container, Inventory invPlayer, Component name, int width, int height) {
		super(container, invPlayer, name, width, height);
		craftingRequestUpdateTimer = 0;
		itemsPerRow = DEFAULT_ITEMS_PER_ROW;
		maxRows = DEFAULT_MAX_ROWS_ON_SCREEN;
		digistoreInventoryPosition = DEFAULT_INVENTORY_START_POSITION;
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		fakeContainerSlots = new ArrayList<DigistoreSlotButton>();

		// Set the default view type.
		viewType = TerminalViewType.ITEMS;

		// Add search bar and sync it with JEI if requested.
		String initialSerachString = "";
		if (DigistoreTerminal.getSearchMode(getMenu().getAttachment()) == DigistoreSyncedSearchMode.TWO_WAY) {
			initialSerachString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
		}
		registerWidget(searchBar = new TextInputWidget(initialSerachString, 79, 6, 89, 12).setTypedCallback(this::onSearchTextChanged));

		// Add island for scroll bar.
		registerWidget(new GuiIslandWidget(170, 31, 26, 110));

		// Add scroll bar.
		registerWidget(scrollBar = new ScrollBarWidget(179, 36, 100));

		// Add island for the buttons.
		registerWidget(new GuiIslandWidget(-24, 14, 28, 46));

		// Add island for fill amount.
		registerWidget(new GuiIslandWidget(-24, 62, 28, 44));

		// Add the sprites for the fill amounts.
		registerWidget(filledAmountSprite = new DrawableWidget<SpriteDrawable>(-18, 65, 6, 0, new SpriteDrawable(GuiTextures.DIGISTORE_FILLED_BAR, 6, 50)));
		registerWidget(typeAmountSprite = new DrawableWidget<SpriteDrawable>(-8, 65, 6, 0, new SpriteDrawable(GuiTextures.DIGISTORE_FILLED_BAR, 6, 50)));

		// Add sort button.
		registerWidget(sortButton = new SpriteButton(-19, 18, 18, 18, StaticPowerSprites.SORT_NUMERICAL_DESC, null, this::onSortButtonPressed));

		// Add search mode button.
		if (ModList.get().isLoaded(StaticPowerModEventRegistry.JEI_MODID)) {
			registerWidget(searchModeButton = new SpriteButton(-19, 38, 18, 18, StaticPowerSprites.SEARCH_MODE_DEFAULT, null, this::onSearchModeButtonPressed));
		}

		// Add island for the armor.
		registerWidget(new GuiIslandWidget(-24, 108, 30, 80));

		// Add island for the regular tab.
		registerWidget(new GuiIslandWidget(this.imageWidth - 4, 143, 25, 24));
		registerWidget(itemViewButton = (SpriteButton) new SpriteButton(this.imageWidth - 1, 145, 20, 20, StaticPowerSprites.FURNACE_ICON, null, (a, b) -> switchToDefaultView())
				.setShouldDrawButtonBackground(false));

		// Add island for the crafting tab.
		registerWidget(new GuiIslandWidget(this.imageWidth - 4, 168, 25, 24));
		registerWidget(craftingViewButton = (SpriteButton) new SpriteButton(this.imageWidth - 1, 170, 20, 20, StaticPowerSprites.CRAFTING_TABLE_ICON, null, (a, b) -> switchToCraftingStatusView())
				.setShouldDrawButtonBackground(false));

		// Add the crafting steps renderer.
		registerWidget(craftingStepsWidget = new AutoCraftingStepsWidget(8, 40, 160, 133, MAX_CRAFTING_STEPS_ROWS, MAX_CRAFTING_STEP_COLUMNS));
		craftingStepsWidget.setVisible(false);

		// Add the crafting rotation buttons and default them to invisible.
		registerWidget(activeCraftingLeft = new TextButton(7, 20, 14, 14, "<", (a, b) -> rotateCurrentlyViewedRecipeStatus(-1)));
		registerWidget(activeCraftingRight = new TextButton(this.imageWidth - 23, 20, 14, 14, ">", (a, b) -> rotateCurrentlyViewedRecipeStatus(-1)));
		activeCraftingLeft.setVisible(false);
		activeCraftingRight.setVisible(false);

		// Cancel crafting request button.
		registerWidget(craftingRequestCancelButton = (SpriteButton) new SpriteButton(98, 24, 8, 8, StaticPowerSprites.ERROR, null, (a, b) -> cancelCurrentCraftingRequest())
				.setShouldDrawButtonBackground(false));
		craftingRequestCancelButton.setTooltip(new TextComponent("Cancel"));
		craftingRequestCancelButton.setVisible(false);

		// This is just a constant used to indicate the size of a slot (16 + 2(1) for
		// each border).
		final int adjustedSlotSize = 18;

		// Add fake slots.
		for (int y = 0; y < maxRows; y++) {
			for (int x = 0; x < itemsPerRow; x++) {
				DigistoreSlotButton slotButton = new DigistoreSlotButton(ItemStack.EMPTY, digistoreInventoryPosition.getXi() + (x * adjustedSlotSize),
						digistoreInventoryPosition.getXi() + (y * adjustedSlotSize) + 14, this::digistoreSlotPressed);

				// Add the button to the recipe button array and register it.
				fakeContainerSlots.add(slotButton);
				registerWidget(slotButton);
			}
		}

		// Set default settings for tooltips/sprites.
		updateSortAndFilter();

		// Refresh the crafting queue.
		getMenu().refreshCraftingQueue();
	}

	@Override
	public void renderSlot(PoseStack matrixStack, Slot slotIn) {
		if (slotIn instanceof DigistoreSlot && slotIn.hasItem()) {
			// Draw the slot with no count visible.
			renderSlot(matrixStack, new NoCountRenderSlot(slotIn));

			ItemStack slotStack = slotIn.getItem();
			matrixStack.pushPose();
			matrixStack.translate(0.0, 0.0, 260.0);
			if (DigistoreInventorySnapshot.getCraftableStateOfItem(slotStack) == DigistoreItemCraftableState.ONLY_CRAFTABLE) {
				// Draw a string that says: "Craft".
				GuiDrawUtilities.drawStringWithSize(matrixStack, "Craft", slotIn.x + 16, slotIn.y + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			} else {
				// Pass the itemstack count through the metric converter.
				MetricConverter count = new MetricConverter(slotIn.getItem().getCount());

				// Draw the item count string manually.
				GuiDrawUtilities.drawStringWithSize(matrixStack, count.getValueAsString(true), slotIn.x + 16, slotIn.y + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}
			matrixStack.popPose();
		} else {
			super.renderSlot(matrixStack, slotIn);
		}
	}

	@Override
	public void updateData() {
		// Get the inventory.
		DigistoreInventorySnapshot snapshot = getMenu().getDigistoreClientInventory();

		// If we have JEI installed two way syncing enabled, changes to the JEI search
		// bar will also
		// affect the digistore search bar.
		if (ModList.get().isLoaded(StaticPowerModEventRegistry.JEI_MODID)) {
			if (DigistoreTerminal.getSearchMode(getMenu().getAttachment()) == DigistoreSyncedSearchMode.TWO_WAY) {
				String jeiSearchString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
				searchBar.setText(jeiSearchString);
			}
		}

		// Update the crafting queue regardless of if we are in that view or not.
		craftingRequestUpdateTimer++;
		if (craftingRequestUpdateTimer >= CRAFTING_LIST_UPDATE_RATE) {
			getMenu().refreshCraftingQueue();
			craftingRequestUpdateTimer = 0;
		}

		// Update the enabled states for the left and right buttons.
		activeCraftingLeft.setEnabled(getMenu().getCurrentCraftingQueue().size() > 1);
		activeCraftingRight.setEnabled(getMenu().getCurrentCraftingQueue().size() > 1);

		// Update the crafting widget and the tool tips.
		if (getMenu().getCurrentCraftingQueue().size() > 0) {
			CraftingRequestResponse currentRequest = getMenu().getCurrentCraftingQueue().get(currentCraftingRequestIndex);
			craftingStepsWidget.setRequest(currentRequest);
			craftingViewButton.setTooltip(new TextComponent(String.format("%1$d jobs currently queued.", getMenu().getCurrentCraftingQueue().size())));
		} else {
			craftingStepsWidget.setRequest(null);
			craftingViewButton.setTooltip(new TextComponent("No crafting jobs currently queued."));
		}

		// IF we are in the crafting view and have crafting requests, show the cancel
		// button.
		if (viewType == TerminalViewType.CRAFTING) {
			// Update the scroll offset.
			scrollBar.setMaxScroll(craftingStepsWidget.getMaxScrollPosition());
			craftingRequestCancelButton.setVisible(getMenu().getCurrentCraftingQueue().size() > 0);
			craftingStepsWidget.setScrollPosition(scrollBar.getScrollAmount());
		} else {
			// Update the scroll bar.
			scrollBar.setMaxScroll(getMaxScroll());
		}

		// Update the fake items slots.
		for (int i = 0; i < fakeContainerSlots.size(); i++) {
			int adjustedIndex = (i + (scrollBar.getScrollAmount() * itemsPerRow));
			if (adjustedIndex < snapshot.getSlots()) {
				fakeContainerSlots.get(i).setItemStack(snapshot.getStackInSlot(adjustedIndex));
			} else {
				fakeContainerSlots.get(i).setItemStack(ItemStack.EMPTY);
			}

			// Update the enabled state of the slot.
			fakeContainerSlots.get(i).setEnabled(getMenu().isManagerOnline());
		}

		// Update the capacity bar.
		float filledPercent = (float) snapshot.getUsedCapacity() / snapshot.getTotalCapacity();
		float filledHeight = 34 * filledPercent;
		float filledYCoord = 101 - filledHeight;
		filledAmountSprite.setPosition(-18, filledYCoord);
		filledAmountSprite.setSize(6, filledHeight);
		filledAmountSprite.getDrawable().setUV(0, 1 - filledPercent, 1, 1);

		// Update the types bar.
		float typesPercent = (float) snapshot.getUsedUniqueTypes() / snapshot.getMaxUniqueTypes();
		float typesHeight = 34 * typesPercent;
		float typesYCoord = 101 - typesHeight;
		typeAmountSprite.setPosition(-8, typesYCoord);
		typeAmountSprite.setSize(6, typesHeight);
		typeAmountSprite.getDrawable().setUV(0, 1 - typesPercent, 1, 1);
	}

	@SuppressWarnings("resource")
	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (!searchBar.isFocused()) {
			// Update the fake items slots.
			for (DigistoreSlotButton slot : fakeContainerSlots) {
				if (slot.isEnabled() && slot.isHovered()) {
					// Get the stack represented by the fake slot.
					ItemStack buttonStack = slot.getItemStack();

					// Do a little extra work here to support JEI lookups.
					for (KeyMapping binding : Minecraft.getInstance().options.keyMappings) {
						if (binding.getKey().getValue() == key) {
							if (binding.getName() == "key.jei.showRecipe") {
								if (!buttonStack.isEmpty()) {
									IFocus<ItemStack> focus = PluginJEI.RUNTIME.getRecipeManager().createFocus(Mode.OUTPUT, buttonStack);
									PluginJEI.RUNTIME.getRecipesGui().show(focus);
									return true;
								}
							} else if (binding.getName() == "key.jei.showUses") {
								if (!buttonStack.isEmpty()) {
									IFocus<ItemStack> focus = PluginJEI.RUNTIME.getRecipeManager().createFocus(Mode.INPUT, buttonStack);
									PluginJEI.RUNTIME.getRecipesGui().show(focus);
									return true;
								}
							}
						}
					}
				}
			}
		}

		// Return false (same as the super).
		return super.keyPressed(key, scanCode, modifiers);
	}

	public int getItemsPerRow() {
		return itemsPerRow;
	}

	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Gets the maximum amount of scroll that should be available to the user.
	 * 
	 * @return
	 */
	public int getMaxScroll() {
		int overflow = menu.getDigistoreClientInventory().getSlots() - getMaximumVisibleDigistoreSlots();
		return (int) Math.max(0, Math.ceil((float) overflow / itemsPerRow));
	}

	/**
	 * Returns how many digistore slots are on screen.
	 * 
	 * @return
	 */
	public int getMaximumVisibleDigistoreSlots() {
		return itemsPerRow * maxRows;
	}

	public Vector2D getDigistoreInventoryPosition() {
		return digistoreInventoryPosition;
	}

	@Override
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {
		super.getExtraTooltips(tooltips, stack, mouseX, mouseY);

		// Get the inventory.
		DigistoreInventorySnapshot snapshot = getMenu().getDigistoreClientInventory();

		// Add the tooltips for the capacity and types bars here so that even if the
		// bars are small, the tooltip gets rendered.
		if (mouseY - topPos >= 65) {
			if (mouseX - leftPos >= -19 && mouseX - leftPos <= -12 && mouseY - topPos <= 99) {
				float filledPercent = (float) snapshot.getUsedCapacity() / snapshot.getTotalCapacity();
				tooltips.add(new TranslatableComponent("gui.staticpower.digistore_capacity_utilization", GuiTextUtilities.formatNumberAsStringNoDecimal(filledPercent * 100)));

				// Include actual numbers.
				MetricConverter totalCapacity = new MetricConverter(snapshot.getTotalCapacity());
				MetricConverter usedCapacity = new MetricConverter(snapshot.getUsedCapacity());
				tooltips.add(new TextComponent(usedCapacity.getValueAsString(true) + "/" + totalCapacity.getValueAsString(true)));
			} else if (mouseX - leftPos >= -9 && mouseX - leftPos <= -2 && mouseY - topPos <= 99) {
				float typesPercent = (float) snapshot.getUsedUniqueTypes() / snapshot.getMaxUniqueTypes();
				tooltips.add(new TranslatableComponent("gui.staticpower.digistore_types_utilization", GuiTextUtilities.formatNumberAsStringNoDecimal(typesPercent * 100)));

				// Include actual numbers.
				MetricConverter totalTypes = new MetricConverter(snapshot.getMaxUniqueTypes());
				MetricConverter usedTypes = new MetricConverter(snapshot.getUsedUniqueTypes());
				tooltips.add(new TextComponent(usedTypes.getValueAsString(true) + "/" + totalTypes.getValueAsString(true)));
			}
		}
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);
		// Draw the bar slots.
		GuiDrawUtilities.drawSlot(stack, -18, 67, 6, 34, 0);
		GuiDrawUtilities.drawSlot(stack, -8, 67, 6, 34, 0);
	}

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);
		itemRenderer.drawItem(Items.IRON_HELMET, leftPos, topPos, -18, 113, 0.3f);
		itemRenderer.drawItem(Items.IRON_CHESTPLATE, leftPos, topPos, -18, 131, 0.3f);
		itemRenderer.drawItem(Items.IRON_LEGGINGS, leftPos, topPos, -18, 149, 0.3f);
		itemRenderer.drawItem(Items.IRON_BOOTS, leftPos, topPos, -18, 167, 0.3f);

		if (viewType == TerminalViewType.CRAFTING) {
			if (getMenu().getCurrentCraftingQueue().size() > 0) {
				if (currentCraftingRequestIndex >= getMenu().getCurrentCraftingQueue().size()) {
					currentCraftingRequestIndex = getMenu().getCurrentCraftingQueue().size() - 1;
				}
				CraftingRequestResponse currentRequest = getMenu().getCurrentCraftingQueue().get(currentCraftingRequestIndex);
				itemRenderer.drawItem(currentRequest.getCraftingItem(), leftPos, topPos, (imageWidth / 2) - 8, 20, 1.0f);
			}
		}
	}

	protected void setItemsPerRow(int itemsPerRow) {
		this.itemsPerRow = itemsPerRow;
	}

	protected void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	protected void setDigistoreInventoryPosition(Vector2D digistoreInventoryPosition) {
		this.digistoreInventoryPosition = digistoreInventoryPosition;
	}

	protected void digistoreSlotPressed(StandardButton button, MouseButton mouse) {
		// Get the index of the button pressed.
		int index = -1;
		for (int i = 0; i < fakeContainerSlots.size(); i++) {
			if (fakeContainerSlots.get(i) == button) {
				index = i;
			}
		}

		// If we found the button, offset it by the scroll.
		if (index != -1) {
			index += scrollBar.getScrollAmount() * getItemsPerRow();
		}

		// We call the method even if the fake slot is empty because the server uses
		// clicks on these buttons for other things beyond picking items (such as
		// inserting items).
		menu.digistoreFakeSlotClickedOnClient(index, mouse, hasShiftDown(), hasControlDown(), hasAltDown());
	}

	protected void setHideDigistoreInventory(boolean hide) {
		for (DigistoreSlotButton slot : fakeContainerSlots) {
			slot.setVisible(!hide);
		}
	}

	protected void rotateCurrentlyViewedRecipeStatus(int direction) {
		if (getMenu().getCurrentCraftingQueue().size() == 0) {
			return;
		}

		currentCraftingRequestIndex = Math.floorMod(currentCraftingRequestIndex + direction, getMenu().getCurrentCraftingQueue().size());
		currentCraftingRequestIndex = SDMath.clamp(currentCraftingRequestIndex, 0, getMenu().getCurrentCraftingQueue().size() - 1);
	}

	protected void cancelCurrentCraftingRequest() {
		CraftingRequestResponse currentRequest = getMenu().getCurrentCraftingQueue().get(currentCraftingRequestIndex);
		getMenu().cancelCraftingRequest(currentRequest.getId());
	}

	protected boolean switchToCraftingStatusView() {
		if (viewType != TerminalViewType.CRAFTING && getMenu().getCurrentCraftingQueue().size() > 0) {
			getMenu().refreshCraftingQueue();
			viewType = TerminalViewType.CRAFTING;
			setHideDigistoreInventory(true);
			getMenu().setViewType(TerminalViewType.CRAFTING);
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
			setHideDigistoreInventory(false);
			getMenu().setViewType(TerminalViewType.ITEMS);
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
		if (DigistoreTerminal.getSearchMode(getMenu().getAttachment()) != DigistoreSyncedSearchMode.DEFAULT) {
			PluginJEI.RUNTIME.getIngredientFilter().setFilterText(text);
		}

		// Update the modes and filters
		updateSortAndFilter();
	}

	protected void onSortButtonPressed(StandardButton button, MouseButton mouseButton) {
		if (mouseButton == MouseButton.LEFT) {
			// If we are descending, go to ascending mode. Otherwise, go to the next mode.
			if (getMenu().isSortDescending()) {
				DigistoreTerminal.setSortDescending(getMenu().getAttachment(), false);
			} else {
				DigistoreTerminal.setSortType(getMenu().getAttachment(), DigistoreTerminal.getSortType(getMenu().getAttachment()).getNextType());
				DigistoreTerminal.setSortDescending(getMenu().getAttachment(), true);
			}
		} else {
			// If we are ascending, go to descending mode. Otherwise, go to the next mode.
			if (!getMenu().isSortDescending()) {
				DigistoreTerminal.setSortDescending(getMenu().getAttachment(), true);
			} else {
				DigistoreTerminal.setSortType(getMenu().getAttachment(), DigistoreTerminal.getSortType(getMenu().getAttachment()).getPrevType());
				DigistoreTerminal.setSortDescending(getMenu().getAttachment(), false);
			}
		}

		// Update the modes and filters
		updateSortAndFilter();
	}

	protected void onSearchModeButtonPressed(StandardButton button, MouseButton mouseButton) {
		if (mouseButton == MouseButton.LEFT) {
			// Increment the next search mode.
			DigistoreTerminal.setSearchMode(getMenu().getAttachment(), DigistoreTerminal.getSearchMode(getMenu().getAttachment()).getNextType());
		} else {
			// Increment the previous search mode.
			DigistoreTerminal.setSearchMode(getMenu().getAttachment(), DigistoreTerminal.getSearchMode(getMenu().getAttachment()).getPrevType());
		}

		// Update the modes and filters
		updateSortAndFilter();
	}

	protected void updateSortAndFilter() {
		// Get the attachment.
		ItemStack attachment = getMenu().getAttachment();

		// Get all the modes and filter settings.
		DigistoreSyncedSearchMode searchMode = DigistoreTerminal.getSearchMode(attachment);
		DigistoreInventorySortType sortType = DigistoreTerminal.getSortType(attachment);
		boolean descending = DigistoreTerminal.getSortDescending(attachment);

		// Update tooltips and sprites.
		if (ModList.get().isLoaded(StaticPowerModEventRegistry.JEI_MODID)) {
			switch (searchMode) {
			case DEFAULT:
				searchModeButton.setTooltip(new TextComponent("Default Search Mode"));
				searchModeButton.setRegularTexture(StaticPowerSprites.SEARCH_MODE_DEFAULT);
				break;
			case ONE_WAY:
				searchModeButton.setTooltip(new TextComponent("One-Way JEI Search Mode"));
				searchModeButton.setRegularTexture(StaticPowerSprites.SEARCH_MODE_ONE_WAY);
				break;
			case TWO_WAY:
				searchModeButton.setTooltip(new TextComponent("Two-Way JEI Search Mode"));
				searchModeButton.setRegularTexture(StaticPowerSprites.SEARCH_MODE_TWO_WAY);
				break;
			}
		}

		switch (sortType) {
		case NAME:
			sortButton.setTooltip(new TextComponent("Sort by Name").append(" ").append(descending ? "(Descending)" : "(Ascending)"));
			sortButton.setRegularTexture(descending ? StaticPowerSprites.SORT_ALPHA_DESC : StaticPowerSprites.SORT_ALPHA_ASC);
			break;
		case COUNT:
			sortButton.setTooltip(new TextComponent("Sort by Count").append(" ").append(descending ? "(Descending)" : "(Ascending)"));
			sortButton.setRegularTexture(descending ? StaticPowerSprites.SORT_NUMERICAL_DESC : StaticPowerSprites.SORT_NUMERICAN_ASC);
			break;
		}

		// Update the container who will in turn update the server side values.
		getMenu().updateSortAndFilter(searchBar.getText(), searchMode, sortType, descending);
	}

	@Override
	public DigistoreCableProviderComponent getCableComponent() {
		return getMenu().getCableComponent();
	}
}
