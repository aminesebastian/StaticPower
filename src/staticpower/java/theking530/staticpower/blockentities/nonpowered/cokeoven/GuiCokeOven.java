package theking530.staticpower.blockentities.nonpowered.cokeoven;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticpower.init.ModFluids;

public class GuiCokeOven extends StaticCoreBlockEntityScreen<ContainerCokeOven, BlockEntityCokeOven> {
	private GuiInfoTab infoTab;

	public GuiCokeOven(ContainerCokeOven container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);

		setShouldDrawSlotModeBorders(false);
		setTitleOverride(Component.translatable("gui.staticpower.coke_oven"));
	}

	@Override
	public void initializeGui() {
		registerWidget(
				new ArrowProgressBar(74, 40).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(new FireProgressBar(45, 60));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 145, 18, 16, 58,
				MachineSideMode.Output, getTileEntity()));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent,
				Items.BUCKET, ModFluids.CreosoteOil.getBucket()).setTabSide(TabSide.LEFT));

		infoTab.addLine("desc1", Component.literal("Combines basic items into their more complex constructions."));
		setOutputSlotSize(20);
	}
}
