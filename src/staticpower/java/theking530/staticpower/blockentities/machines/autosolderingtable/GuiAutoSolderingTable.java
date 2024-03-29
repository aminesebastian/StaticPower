package theking530.staticpower.blockentities.machines.autosolderingtable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.blockentities.nonpowered.solderingtable.AbstractGuiSolderingTable;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;

public class GuiAutoSolderingTable extends AbstractGuiSolderingTable<BlockEntityAutoSolderingTable, ContainerAutoSolderingTable> {

	public GuiAutoSolderingTable(ContainerAutoSolderingTable container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 195);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 45));
		registerWidget(new ArrowProgressBar(99, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiUpgradeTab(this.menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	protected int getPatternRecipeXOffset() {
		return -18;
	}

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);
		// Check if we have a recipe currently processing.
		SolderingRecipe recipe = getTileEntity().processingComponent.getProcessingRecipe().orElse(null);

		// If we do not, check to see if we have a potential recipe.
		if (recipe == null) {
			recipe = getTileEntity().processingComponent.getPendingRecipe().orElse(null);
		}

		// If there is a recipe, draw a phantom output.
		if (recipe != null) {
			GuiDrawUtilities.drawItem(stack, recipe.getResultItem(), 129, 38, 10, 0.3f);
		}
	}

	@Override
	public void updateData() {
		super.updateData();

	}
}
