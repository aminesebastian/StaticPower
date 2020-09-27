package theking530.staticpower.tileentities.nonpowered.miner;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.progressbars.SquareProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineHeatTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiMiner extends StaticPowerTileEntityGui<ContainerMiner, TileEntityMiner> {
	private SpriteButton drawPreviewButton;

	public GuiMiner(ContainerMiner container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage.getStorage(), 8, 16, 2, 52));
		registerWidget(new FireProgressBar(19, 52).bindToMachineProcessingComponent(getTileEntity().fuelComponent));
		registerWidget(new SquareProgressBar(78, 55, 20, 2)
				.bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(drawPreviewButton = new SpriteButton(156, 61, 12, 12, StaticPowerSprites.RANGE_ICON, null,
				this::buttonPressed));
		drawPreviewButton.setTooltip(new StringTextComponent("Preview Range"));
		drawPreviewButton.setToggleable(true);
		drawPreviewButton.setToggled(getTileEntity().getShouldDrawRadiusPreview());

		getTabManager().registerTab(new GuiInfoTab(100));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(new GuiMachineHeatTab(getTileEntity().heatStorage).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == drawPreviewButton) {
			getTileEntity().setShouldDrawRadiusPreview(drawPreviewButton.isToggled());
		}
	}

	@Override
	protected void renderHoveredTooltip(MatrixStack stack, int mouseX, int mouseY) {
		super.renderHoveredTooltip(stack, mouseX, mouseY);

		if (mouseX > guiLeft + 72 && mouseX < guiLeft + 102 && mouseY > guiTop + 62 && mouseY < guiTop + 72) {
			List<ITextComponent> tooltip = new ArrayList<ITextComponent>();
			BlockPos currentPos = getTileEntity().getCurrentlyTargetedBlockPos();
			tooltip.add(new StringTextComponent(String.format("X=%1$s Z=%2$d", currentPos.getX(), currentPos.getZ())));
			this.func_243308_b(stack, tooltip, mouseX, mouseY);
		} else if (mouseX > guiLeft + 66 && mouseX < guiLeft + 110 && mouseY > guiTop + 16 && mouseY < guiTop + 26) {
			List<ITextComponent> tooltip = new ArrayList<ITextComponent>();
			tooltip.add(new StringTextComponent("Time remaining until this miner has reached bedrock."));
			this.func_243308_b(stack, tooltip, mouseX, mouseY);
		}
	}

	@Override
	protected void drawBehindItems(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);

		GuiDrawUtilities.drawColoredRectangle(guiLeft + 50, guiTop + 20, 1.0f, 55, 0.0f, Color.GREY);

		GuiDrawUtilities.drawColoredRectangle(guiLeft + 125, guiTop + 20, 1.0f, 55, 0.0f, Color.GREY);

		if (getTileEntity().isDoneMining()) {
			font.drawString(stack, "Done!", guiLeft + 75, guiTop + 64, 4210752);
		} else {
			// Draw the current Y Level.
			String currentYLevel = "Y=" + getTileEntity().getCurrentlyTargetedBlockPos().getY();
			font.drawString(stack, currentYLevel, guiLeft + 89 - (font.getStringWidth(currentYLevel) / 2), guiTop + 64,
					4210752);

			// Draw the time remaining.
			String timeRemaining = SDTime.ticksToTimeString(getTileEntity().getTicksRemainingUntilCompletion());
			font.drawString(stack, timeRemaining, guiLeft + 89 - (font.getStringWidth(timeRemaining) / 2), guiTop + 18,
					4210752);
		}
	}
}
