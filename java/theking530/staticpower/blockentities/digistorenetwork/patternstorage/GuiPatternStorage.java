package theking530.staticpower.blockentities.digistorenetwork.patternstorage;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.utilities.MetricConverter;

public class GuiPatternStorage extends StaticPowerTileEntityGui<ContainerPatternStorage, TileEntityPatternStorage> {

	private GuiInfoTab infoTab;

	public GuiPatternStorage(ContainerPatternStorage container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 158);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(infoTab = new GuiInfoTab(getTileEntity().getDisplayName().getString(), 100));
	}

	@Override
	public void updateData() {
		// Update the info tab.
		infoTab.clear();
		infoTab.addLine("desc", new TextComponent("Stores crafting patterns that can be atomically reconstructed into items and blocks!."));
		infoTab.addLineBreak();

		// Pass the itemstack count through the metric converter.
		MetricConverter count = new MetricConverter(getTileEntity().patternInventory.getSlots());
		infoTab.addKeyValueLine("max", new TextComponent("Max Patterns"),
				new TextComponent(ChatFormatting.WHITE.toString()).append(new TextComponent(count.getValueAsString(true))), ChatFormatting.RED);
	}
}
