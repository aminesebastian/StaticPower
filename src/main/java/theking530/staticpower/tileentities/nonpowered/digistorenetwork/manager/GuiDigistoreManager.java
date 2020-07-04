package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import com.google.common.base.Strings;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.TextInputWidget;
import theking530.common.utilities.Color;
import theking530.staticpower.client.container.slots.DigistoreSlot;
import theking530.staticpower.client.container.slots.NoCountRenderSlot;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.utilities.MetricConverter;

public class GuiDigistoreManager extends StaticPowerTileEntityGui<ContainerDigistoreManager, TileEntityDigistoreManager> {
	private final TextInputWidget searchBar;

	public GuiDigistoreManager(ContainerDigistoreManager container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 270);
		this.registerWidget(searchBar = new TextInputWidget("Search", 46, 16, 80, 14).setTypedCallback(this::onSearchTextChanged));
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

	public void updateData() {
		String jeiSearchString = Strings.nullToEmpty(PluginJEI.RUNTIME.getIngredientFilter().getFilterText());
		searchBar.setText(jeiSearchString);
	}

	public void onSearchTextChanged(TextInputWidget searchBar, String text) {
		getContainer().setFilterString(text);
		PluginJEI.RUNTIME.getIngredientFilter().setFilterText(text);
	}
}
