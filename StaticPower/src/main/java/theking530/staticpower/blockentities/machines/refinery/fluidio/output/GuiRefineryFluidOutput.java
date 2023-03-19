package theking530.staticpower.blockentities.machines.refinery.fluidio.output;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.blockentities.machines.refinery.fluidio.BlockEntityRefineryFluidIO;

public class GuiRefineryFluidOutput extends StaticCoreBlockEntityScreen<ContainerRefineryFluidOutput, BlockEntityRefineryFluidIO> {

	public GuiRefineryFluidOutput(ContainerRefineryFluidOutput container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().getTank(2), 56, 22, 16, 54, MachineSideMode.Output2, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getTank(3), 80, 22, 16, 54, MachineSideMode.Output3, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getTank(4), 104, 22, 16, 54, MachineSideMode.Output4, getTileEntity()));
	}
}
