package theking530.staticpower.cables.power;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerCable extends StaticPowerTileEntityGui<ContainerPowerCable, TileEntityPowerCable> {

	public GuiPowerCable(ContainerPowerCable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 100);
	}

	@Override
	public void initializeGui() {
		this.registerWidget(new GuiPowerBarFromEnergyStorage(this.getTileEntity().powerCableComponent, 75, 22, 26, 60));
	}

	@Override
	public void updateData() {

	}

	@Override
	protected void drawForegroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		float recieve = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String recivePerTick = GuiTextUtilities.formatEnergyRateToString(recieve).getString();
		int recieveWidth = this.font.getStringWidth(recivePerTick);
		GuiDrawUtilities.drawStringWithSize(stack, recivePerTick, (recieveWidth / 2) + guiLeft + 40, guiTop + 50, 1.0f, new Color(0.0f, 255.0f, 0.0f), true);

		float extract = this.getTileEntity().powerCableComponent.getClientLastEnergyReceieve();
		String extractPerTick = GuiTextUtilities.formatEnergyRateToString(extract).getString();
		int extractWidth = this.font.getStringWidth(extractPerTick);
		GuiDrawUtilities.drawStringWithSize(stack, extractPerTick, (extractWidth / 2) + guiLeft + 138, guiTop + 50, 1.0f, new Color(255.0f, 0.0f, 0.0f), true);
	}
}
