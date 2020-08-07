package theking530.staticpower.tileentities.powered.miner;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.common.gui.widgets.button.SpriteButton;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiMiner extends StaticPowerTileEntityGui<ContainerMiner, TileEntityMiner> {
	private SpriteButton drawPreviewButton;

	public GuiMiner(ContainerMiner container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new ArrowProgressBar(59, 32).bindToMachineProcessingComponent(getTileEntity().fuelComponent));
		registerWidget(new ArrowProgressBar(59, 64).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(drawPreviewButton = new SpriteButton(132, 61, 12, 12, StaticPowerSprites.RANGE_ICON, null, this::buttonPressed));
		drawPreviewButton.setTooltip(new StringTextComponent("Preview Range"));
		drawPreviewButton.setToggleable(true);
		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));
		getTabManager().registerTab(new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == drawPreviewButton) {
			getTileEntity().setShouldDrawRadiusPreview(drawPreviewButton.isToggled());
		}
	}
}
