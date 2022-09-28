package theking530.staticpower.blockentities.machines.hydroponics.farmer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.blockentities.components.control.RedstoneControlComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.machines.hydroponics.pod.BlockEntityHydroponicPod;
import theking530.staticpower.blockentities.machines.hydroponics.pod.HydroponicPodProgressWidget;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiHydroponicFarmer extends StaticPowerTileEntityGui<ContainerHydroponicFarmer, BlockEntityHydroponicFarmer> {
	private List<HydroponicPodProgressWidget> podWidgets;

	public GuiHydroponicFarmer(ContainerHydroponicFarmer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 150, 8, 16, 60, MachineSideMode.Input, getTileEntity()));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiUpgradeTab(menu, getTileEntity().upgradesInventory));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent).setTabSide(TabSide.LEFT));
		podWidgets = new ArrayList<>();

		int yStart = 21;
		int podBoxHeight = 28;
		int podBoxWidth = 59;
		int podPadding = 1;
		for (int i = 0; i < BlockEntityHydroponicFarmer.MAX_PODS; i++) {
			float xPos = (getXSize() / 2) + ((i % 2) * podBoxWidth) - podBoxWidth + (i % 2 == 0 ? podPadding / 2.0f : -podPadding / 2.0f) - 2;
			float yPos = yStart + (i / 2) * (podPadding + podBoxHeight);

			HydroponicPodProgressWidget widget = new HydroponicPodProgressWidget(xPos, yPos, podBoxWidth, podBoxHeight);
			registerWidget(widget);
			podWidgets.add(widget);
		}

		setOutputSlotSize(16);
	}

	@Override
	public void updateData() {
		List<BlockEntityHydroponicPod> pods = getTileEntity().getPods();
		for (int i = 0; i < podWidgets.size(); i++) {
			if (i < pods.size()) {
				podWidgets.get(i).setPod(pods.get(i));
			} else {
				podWidgets.get(i).clearPod();
			}
		}
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

	}

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

	}
}
