package theking530.staticpower.blockentities.machines.refinery.fluidio.input;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.blockentities.machines.refinery.fluidio.BlockEntityRefineryFluidIO;

public class GuiRefineryFluidInput extends StaticCoreBlockEntityScreen<ContainerRefineryFluidInput, BlockEntityRefineryFluidIO> {

	public GuiRefineryFluidInput(ContainerRefineryFluidInput container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().getTank(0), 67, 22, 16, 54, MachineSideMode.Input2, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().getTank(1), 91, 22, 16, 54, MachineSideMode.Input3, getTileEntity()));
	}
}
