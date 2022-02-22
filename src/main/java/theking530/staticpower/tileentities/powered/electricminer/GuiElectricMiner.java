package theking530.staticpower.tileentities.powered.electricminer;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.progressbars.SquareProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineHeatTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiElectricMiner extends StaticPowerTileEntityGui<ContainerElectricMiner, TileEntityElectricMiner> {
	private SpriteButton drawPreviewButton;
	private GuiInfoTab infoTab;

	public GuiElectricMiner(ContainerElectricMiner container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage.getStorage(), 26, 8, 2, 52));
		registerWidget(new SquareProgressBar(78, 55, 20, 2).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		// Add a button we can use to toggle the in world radius preview.
		registerWidget(drawPreviewButton = new SpriteButton(156, 61, 12, 12, StaticPowerSprites.RANGE_ICON, null, this::buttonPressed));
		drawPreviewButton.setTooltip(new TextComponent("Preview Range"));
		drawPreviewButton.setToggleable(true);
		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		getTabManager().registerTab(infoTab = new GuiInfoTab(100), true);
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineHeatTab(getTileEntity().heatStorage).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == drawPreviewButton) {
			getTileEntity().setShouldDrawRadiusPreview(drawPreviewButton.isToggled());
		}
	}

	@Override
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {
		super.getExtraTooltips(tooltips, stack, mouseX, mouseY);

		// Make sure that we are not done mining. If we are done, getting the currently
		// targeted block will throw an exception (since there is none). This is better
		// than having it return some arbitrary value.
		if (!getTileEntity().isDoneMining()) {
			if (mouseX > leftPos + 72 && mouseX < leftPos + 102 && mouseY > topPos + 62 && mouseY < topPos + 72) {
				BlockPos currentPos = getTileEntity().getCurrentlyTargetedBlockPos();
				tooltips.add(new TextComponent(String.format("X=%1$s Z=%2$d", currentPos.getX(), currentPos.getZ())));
			} else if (mouseX > leftPos + 66 && mouseX < leftPos + 110 && mouseY > topPos + 16 && mouseY < topPos + 26) {
				tooltips.add(new TextComponent("Time remaining until this miner reaches bedrock."));
			}
		}
	}

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);

		// Draw the vertical dividers.
		GuiDrawUtilities.drawRectangle(stack, 1.0f, 55, 50, 20, 0.0f, Color.GREY);
		GuiDrawUtilities.drawRectangle(stack, 1.0f, 55, 125, 20, 0.0f, Color.GREY);

		// If we are done mining, simply render "Done!". Otherwise, render the Y
		// coordinate of the block we are mining & the remaining time.
		if (getTileEntity().isDoneMining()) {
			font.draw(stack, "Done!", 75, 64, 4210752);
		} else {
			// Draw the current Y Level.
			String currentYLevel = "Y=" + getTileEntity().getCurrentlyTargetedBlockPos().getY();
			font.draw(stack, currentYLevel, 89 - (font.width(currentYLevel) / 2), 64, 4210752);

			// Draw the time remaining.
			String timeRemaining = SDTime.ticksToTimeString(getTileEntity().getTicksRemainingUntilCompletion());
			font.draw(stack, timeRemaining, 89 - (font.width(timeRemaining) / 2), 18, 4210752);
		}
	}

	@Override
	public void updateData() {
		infoTab.addKeyValueTwoLiner("radius", new TextComponent("Mining Radius"),
				new TextComponent(String.valueOf(getTileEntity().getRadius())).append(" ").append(new TranslatableComponent("gui.staticpower.blocks")), ChatFormatting.BLUE);

		infoTab.addKeyValueTwoLiner("heat_generation", new TextComponent("Heat Genereation"), GuiTextUtilities.formatHeatRateToString(getTileEntity().getHeatGeneration()),
				ChatFormatting.RED);
	}
}
