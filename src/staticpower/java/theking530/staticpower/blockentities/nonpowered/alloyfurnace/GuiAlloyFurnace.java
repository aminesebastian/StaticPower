package theking530.staticpower.blockentities.nonpowered.alloyfurnace;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;

public class GuiAlloyFurnace extends StaticCoreBlockEntityScreen<ContainerAlloyFurnace, BlockEntityAlloyFurnace> {
	private GuiInfoTab infoTab;
	private FireProgressBar fireBar;

	public GuiAlloyFurnace(ContainerAlloyFurnace container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
		setShouldDrawSlotModeBorders(false);
	}

	@Override
	public void initializeGui() {
		registerWidget(new ArrowProgressBar(78, 40).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(fireBar = new FireProgressBar(47, 44));

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
		infoTab.addLine("desc1", Component.literal("Combines basic items into their more complex constructions."));
		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		fireBar.setMaxProgress(getTileEntity().getLastFuelBurnTime());
		fireBar.setCurrentProgress(getTileEntity().getLastFuelBurnTime() - getTileEntity().getBurnTimeRemaining());
	}
}
