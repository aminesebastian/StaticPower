package theking530.staticpower.blockentities.power.solarpanels;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.TimeOfDayDrawable;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiPowerInfoTab;

public class GuiSolarPanel extends StaticCoreBlockEntityScreen<ContainerSolarPanel, BlockEntitySolarPanel> {

	protected DrawableWidget<SpriteDrawable> generatingWidget;
	protected DrawableWidget<SpriteDrawable> notGeneratingWidget;

	public GuiSolarPanel(ContainerSolarPanel container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 130, 90);
		setShouldDrawInventoryLabel(false);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiPowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);

		registerWidget(new TimeOfDayDrawable(19, 9, 50, getTileEntity().getLevel(), getTileEntity().getBlockPos()));
		registerWidget(generatingWidget = new DrawableWidget<SpriteDrawable>(53, 65, 20, 20, new SpriteDrawable(StaticCoreSprites.GREEN_CHECK, 16, 16)));
		registerWidget(notGeneratingWidget = new DrawableWidget<SpriteDrawable>(53, 65, 20, 20, new SpriteDrawable(StaticCoreSprites.ERROR, 16, 16)));

		generatingWidget.setVisible(getTileEntity().isGenerating());
		notGeneratingWidget.setVisible(!getTileEntity().isGenerating());
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);
	}

	@Override
	public void updateData() {
		generatingWidget.setVisible(getTileEntity().isGenerating());
		notGeneratingWidget.setVisible(!getTileEntity().isGenerating());
	}
}
