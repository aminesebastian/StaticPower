package theking530.staticpower.tileentities.powered.autosolderingtable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticcore.utilities.GuiDrawItem;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.nonpowered.solderingtable.AbstractGuiSolderingTable;

public class GuiAutoSolderingTable
		extends AbstractGuiSolderingTable<TileEntityAutoSolderingTable, ContainerAutoSolderingTable> {
	private final GuiDrawItem itemRenderer;

	public GuiAutoSolderingTable(ContainerAutoSolderingTable container, PlayerInventory invPlayer,
			ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
		itemRenderer = new GuiDrawItem();
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 45));
		registerWidget(
				new ArrowProgressBar(99, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		getTabManager().registerTab(
				new GuiMachinePowerInfoTab(getTileEntity().energyStorage)
						.setTabSide(TabSide.LEFT),
				true);
		getTabManager().registerTab(
				new GuiUpgradeTab(this.container, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	protected void drawBehindItems(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);
		// Check if we have a recipe currently processing.
		SolderingRecipe recipe = getTileEntity().getCurrentProcessingRecipe().orElse(null);

		// If we do not, check to see if we have a potential recipe.
		if (recipe == null) {
			recipe = getTileEntity().getCurrentRecipe().orElse(null);
		}

		// If there is a recipe, draw a phantom output.
		if (recipe != null) {
			itemRenderer.drawItem(recipe.getRecipeOutput(), guiLeft, guiTop, 129, 38, 0.3f);
		}
	}

	@Override
	public void updateData() {
		super.updateData();

	}
}
