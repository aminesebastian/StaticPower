package theking530.staticpower.tileentities.nonpowered.heatsink;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiHeatSink extends StaticPowerTileEntityGui<ContainerHeatSink, TileEntityHeatSink> {
	public GuiHeatSink(ContainerHeatSink container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 146);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().cableComponent, 64, 16, 48, 44));
	}

	@Override
	protected void drawForegroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(partialTicks, mouseX, mouseY);

	}
}
