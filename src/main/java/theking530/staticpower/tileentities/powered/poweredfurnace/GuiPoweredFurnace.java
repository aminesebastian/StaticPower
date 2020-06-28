package theking530.staticpower.tileentities.powered.poweredfurnace;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;

public class GuiPoweredFurnace extends StaticPowerTileEntityGui<ContainerPoweredFurnace, TileEntityPoweredFurnace> {

	public GuiPoweredFurnace(ContainerPoweredFurnace container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 54));
		registerWidget(new ArrowProgressBar(73, 32, 32, 16).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT));
		getTabManager().setInitiallyOpenTab(powerTab);

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(partialTicks, mouseX, mouseY);

		// Flames
		Optional<MachineProcessingComponent> processingComp = ComponentUtilities.getComponent(MachineProcessingComponent.class, getTileEntity());

		if (processingComp.isPresent()) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.VANILLA_FURNACE_GUI);
			int k = processingComp.get().getProgressScaled(13);
			blit(guiLeft + 50, guiTop + 48, 55, 37, 20, 14);
			blit(guiLeft + 51, guiTop + 62 - k, 176, 14 - k, 14, k + 2);
		}
	}
}
