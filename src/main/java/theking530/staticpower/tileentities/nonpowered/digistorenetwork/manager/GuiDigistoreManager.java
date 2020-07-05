package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import com.google.common.base.Strings;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.GuiIslandWidget;
import theking530.common.gui.widgets.button.SpriteButton;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.common.gui.widgets.textinput.TextInputWidget;
import theking530.common.utilities.Color;
import theking530.staticpower.cables.digistore.DigistoreInventorySortType;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.container.slots.DigistoreSlot;
import theking530.staticpower.client.container.slots.NoCountRenderSlot;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.utilities.MetricConverter;

public class GuiDigistoreManager extends StaticPowerTileEntityGui<ContainerDigistoreManager, TileEntityDigistoreManager> {
	public final TextInputWidget searchBar;
	public final ScrollBarWidget scrollBar;
	public final SpriteButton sortButton;

	public GuiDigistoreManager(ContainerDigistoreManager container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 270);

		// Add search bar and sync it with JEI.
		registerWidget(searchBar = new TextInputWidget("", 8, 169, 80, 12).setTypedCallback(this::onSearchTextChanged));
		String jeiSearchString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
		searchBar.setText(jeiSearchString);
		container.setClientGui(this);

		// Add island for scroll bar.
		registerWidget(new GuiIslandWidget(180, 31, 22, 74));

		// Add scroll bar.
		registerWidget(scrollBar = new ScrollBarWidget(185, 36, 64));
		scrollBar.setMaxScroll(3);

		// Add island for the buttons.
		registerWidget(new GuiIslandWidget(-29, 16, 26, 26));
		
		// Add sort button.
		registerWidget(sortButton = new SpriteButton(-25, 20, 18, 18, StaticPowerSprites.SORT_NUMERICAL_DESC, null, this::onSortButtonPressed));
	}

	@Override
	public void drawSlot(Slot slotIn) {
		if (slotIn instanceof DigistoreSlot && slotIn.getHasStack()) {
			super.drawSlot(new NoCountRenderSlot(slotIn));

			// Pass the itemstack count through the metric converter.
			MetricConverter count = new MetricConverter(slotIn.getStack().getCount());

			// Draw the item count string.
			this.drawStringWithSize(count.getValueAsString(true), slotIn.xPos + 16, slotIn.yPos + 15, 0.5f, Color.WHITE, true);
		} else {
			super.drawSlot(slotIn);
		}
	}

	@Override
	public void updateData() {
		String jeiSearchString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
		searchBar.setText(jeiSearchString);
		getContainer().setScrollOffset(scrollBar.getScrollAmount());
	}

	protected void onSearchTextChanged(TextInputWidget searchBar, String text) {
		getContainer().setFilterString(text);
		PluginJEI.RUNTIME.getIngredientFilter().setFilterText(text);
	}

	protected void onSortButtonPressed(StandardButton button) {
		if (getContainer().isSortDescending()) {
			getContainer().setSortType(getContainer().getSortType(), false);
		} else {
			getContainer().setSortType(getContainer().getSortType().getNextType(), true);
		}

		if (getContainer().getSortType() == DigistoreInventorySortType.NAME) {
			sortButton.setRegularTexture(getContainer().isSortDescending() ? StaticPowerSprites.SORT_ALPHA_DESC : StaticPowerSprites.SORT_ALPHA_ASC);
		} else {
			sortButton.setRegularTexture(getContainer().isSortDescending() ? StaticPowerSprites.SORT_NUMERICAL_DESC : StaticPowerSprites.SORT_NUMERICAN_ASC);
		}
	}
}
