package theking530.staticpower.tileentities.powered.electricminer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.button.SpriteButton;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.progressbars.SquareProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.GuiMachineHeatTab;
import theking530.common.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.common.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.common.utilities.Color;
import theking530.common.utilities.SDTime;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiElectricMiner extends StaticPowerTileEntityGui<ContainerElectricMiner, TileEntityElectricMiner> {
	private SpriteButton drawPreviewButton;

	public GuiElectricMiner(ContainerElectricMiner container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage.getStorage(), 26, 8, 2, 52));
		registerWidget(new SquareProgressBar(78, 54, 20, 2).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		// Add a button we can use to toggle the in world radius preview.
		registerWidget(drawPreviewButton = new SpriteButton(156, 61, 12, 12, StaticPowerSprites.RANGE_ICON, null, this::buttonPressed));
		drawPreviewButton.setTooltip(new StringTextComponent("Preview Range"));
		drawPreviewButton.setToggleable(true);
		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		getTabManager().registerTab(new GuiInfoTab(100, 80));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineHeatTab(getTileEntity().heatStorage).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == drawPreviewButton) {
			getTileEntity().setShouldDrawRadiusPreview(drawPreviewButton.isToggled());
		}
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		super.renderHoveredToolTip(mouseX, mouseY);

		// Make sure that we are not done mining. If we are done, getting the currently
		// targeted block will throw an exception (since there is none). This is better
		// than having it return some arbitrary value.
		if (!getTileEntity().isDoneMining()) {
			if (mouseX > guiLeft + 72 && mouseX < guiLeft + 102 && mouseY > guiTop + 62 && mouseY < guiTop + 72) {
				List<String> tooltip = new ArrayList<String>();
				BlockPos currentPos = getTileEntity().getCurrentlyTargetedBlockPos();
				tooltip.add(String.format("X=%1$s Z=%2$d", currentPos.getX(), currentPos.getZ()));
				this.renderTooltip(tooltip, mouseX, mouseY, font);
			} else if (mouseX > guiLeft + 66 && mouseX < guiLeft + 110 && mouseY > guiTop + 16 && mouseY < guiTop + 26) {
				List<String> tooltip = new ArrayList<String>();
				tooltip.add("Time remaining until this miner has reached bedrock.");
				this.renderTooltip(tooltip, mouseX, mouseY, font);
			}
		}
	}

	@Override
	protected void drawBehindItems(float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(partialTicks, mouseX, mouseY);

		// Draw the vertical dividers.
		GuiDrawUtilities.drawColoredRectangle(guiLeft + 50, guiTop + 20, 1.0f, 55, 0.0f, Color.GREY);
		GuiDrawUtilities.drawColoredRectangle(guiLeft + 125, guiTop + 20, 1.0f, 55, 0.0f, Color.GREY);

		// If we are done mining, simply render "Done!". Otherwise, render the Y
		// coordinate of the block we are mining & the remaining time.
		if (getTileEntity().isDoneMining()) {
			font.drawString("Done!", guiLeft + 75, guiTop + 64, 4210752);
		} else {
			// Draw the current Y Level.
			String currentYLevel = "Y=" + getTileEntity().getCurrentlyTargetedBlockPos().getY();
			font.drawString(currentYLevel, guiLeft + 89 - (font.getStringWidth(currentYLevel) / 2), guiTop + 64, 4210752);

			// Draw the time remaining.
			String timeRemaining = SDTime.ticksToTimeString(getTileEntity().getTicksRemainingUntilCompletion());
			font.drawString(timeRemaining, guiLeft + 89 - (font.getStringWidth(timeRemaining) / 2), guiTop + 18, 4210752);
		}
	}
}
