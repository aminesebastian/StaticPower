package theking530.staticpower.tileentities.powered.solarpanels;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.TimeOfDayDrawable;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiSolarPanel extends StaticPowerTileEntityGui<ContainerSolarPanel, TileEntitySolarPanel> {

	protected DrawableWidget<SpriteDrawable> generatingWidget;
	protected DrawableWidget<SpriteDrawable> notGeneratingWidget;

	public GuiSolarPanel(ContainerSolarPanel container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 120, 90);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);

		registerWidget(new TimeOfDayDrawable(34, 18, 50, getTileEntity().getWorld(), getTileEntity().getPos()));
		registerWidget(generatingWidget = new DrawableWidget<SpriteDrawable>(51, 65, 20, 20, new SpriteDrawable(StaticPowerSprites.GREEN_CHECK, 16, 16)));
		registerWidget(notGeneratingWidget = new DrawableWidget<SpriteDrawable>(51, 65, 20, 20, new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16)));

		generatingWidget.setVisible(getTileEntity().isGenerating());
		notGeneratingWidget.setVisible(!getTileEntity().isGenerating());
	}

	@Override
	protected void drawBackgroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);
	}

	@Override
	public void updateData() {
		generatingWidget.setVisible(getTileEntity().isGenerating());
		notGeneratingWidget.setVisible(!getTileEntity().isGenerating());
	}
}
