package theking530.staticpower.blockentities.machines.refinery.condenser;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.blockentities.machines.refinery.controller.BlockEntityRefineryController;

public class GuiRefineryCondenser
		extends StaticCoreBlockEntityScreen<ContainerRefineryCondenser, BlockEntityRefineryCondenser> {

	public GuiRefineryCondenser(ContainerRefineryCondenser container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));

		if (getTileEntity().hasController()) {
			BlockEntityRefineryController controller = getTileEntity().getController();
			registerWidget(new GuiFluidBarFromTank(controller.getOutputTank(0), 67, 22, 16, 54, MachineSideMode.Output2,
					getTileEntity()));
			registerWidget(new GuiFluidBarFromTank(controller.getOutputTank(1), 91, 22, 16, 54, MachineSideMode.Output3,
					getTileEntity()));
			registerWidget(new GuiFluidBarFromTank(controller.getOutputTank(2), 110, 22, 16, 54,
					MachineSideMode.Output4, getTileEntity()));
		}
	}
}
