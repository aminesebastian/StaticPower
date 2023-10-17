package theking530.staticpower.blockentities.nonpowered.blastfurnace;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.utilities.SDColor;

public class GuiBlastFurnace extends StaticCoreBlockEntityScreen<ContainerBlastFurnace, BlockEntityBlastFurnace> {
	private GuiInfoTab infoTab;
	private FireProgressBar fireBar;

	public GuiBlastFurnace(ContainerBlastFurnace container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
		setShouldDrawSlotModeBorders(false);
		setBackgroundTint(new SDColor(0.15f, 0.15f, 0.15f, 1.0f));
		setTextColor(SDColor.EIGHT_BIT_WHITE);
		setTitleOverride(Component.translatable("gui.staticpower.blast_furnace"));
	}

	@Override
	public void initializeGui() {
		registerWidget(
				new ArrowProgressBar(78, 44).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		registerWidget(fireBar = new FireProgressBar(43, 45));

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
