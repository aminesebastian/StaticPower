package theking530.staticpower.cables.power;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.DynamicGraphDataSet;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerCable extends StaticPowerTileEntityGui<ContainerPowerCable, TileEntityPowerCable> {
	private DataGraphWidget graphWidget;

	public GuiPowerCable(ContainerPowerCable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 120);
	}

	@Override
	public void initializeGui() {
		this.registerWidget(new GuiPowerBarFromEnergyStorage(this.getTileEntity().powerCableComponent, 150, 17, 16, 78));
		this.registerWidget(graphWidget = new DataGraphWidget(10, 17, 130, 78));
		graphWidget.setDataSet("test", new DynamicGraphDataSet(new Color(0, 1.0f, 0.2f, 0.75f)).setMaxDataPoints(100));
		graphWidget.setDataSet("test2", new DynamicGraphDataSet(new Color(1.0f, 0, 0.1f, 0.75f)).setMaxDataPoints(100));
		graphWidget.setDataSet("test3", new DynamicGraphDataSet(new Color(0, 0.1f, 1.0f, 1)).setMaxDataPoints(100));
	}

	@Override
	public void updateData() {
		float recieve = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		float extract = -this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		float net = recieve + extract;

		DynamicGraphDataSet dataSet = (DynamicGraphDataSet) graphWidget.getDataSet("test");
		dataSet.addNewDataPoint(recieve);

		DynamicGraphDataSet dataSet2 = (DynamicGraphDataSet) graphWidget.getDataSet("test2");
		dataSet2.addNewDataPoint(extract);

		DynamicGraphDataSet dataSet3 = (DynamicGraphDataSet) graphWidget.getDataSet("test3");
		dataSet3.addNewDataPoint(net);
	}

	@Override
	protected void drawForegroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		float textScale = 0.70f;
		float inPosition = 17;
		float outPosition = 65;
		float netPosition = 110;
		float yPos = 101;

		// Draw the values background.
		GuiDrawUtilities.drawSlot(stack, 10, yPos, 130, 12);

		// Draw the labels.
		GuiDrawUtilities.drawStringWithSize(stack, "In:", inPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSize(stack, "Out:", outPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSize(stack, "Net:", netPosition, yPos + 4.5f, 0.5f, Color.EIGHT_BIT_DARK_GREY, false);

		float recieve = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String recivePerTick = GuiTextUtilities.formatEnergyRateToString(recieve).getString();
		int recieveWidth = (int) (this.font.getStringWidth(recivePerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, recivePerTick, inPosition + 14 + (recieveWidth / 2), yPos + 9, textScale, new Color(0.0f, 255.0f, 50.0f), true);

		float extract = -this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String extractPerTick = GuiTextUtilities.formatEnergyRateToString(extract).getString();
		int extractWidth = (int) (this.font.getStringWidth(extractPerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, extractPerTick, outPosition + 14 + (extractWidth / 2), yPos + 9, textScale, new Color(255.0f, 0.0f, 30.0f), true);

		float net = recieve + extract;
		String netPerTick = GuiTextUtilities.formatEnergyRateToString(net).getString();
		int netWidth = (int) (this.font.getStringWidth(netPerTick) * textScale);
		GuiDrawUtilities.drawStringWithSize(stack, netPerTick, netPosition + 14 + (netWidth / 2), yPos + 9, textScale, new Color(0.0f, 100.0f, 255.0f), true);
	}
}
