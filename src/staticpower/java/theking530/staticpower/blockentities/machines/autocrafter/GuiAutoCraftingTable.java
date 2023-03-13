package theking530.staticpower.blockentities.machines.autocrafter;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.control.RedstoneControlComponent;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiAutoCraftingTable extends StaticPowerTileEntityGui<ContainerAutoCraftingTable, BlockEntityAutoCraftingTable> {
	private final SpriteDrawable lockedSprite;

	public GuiAutoCraftingTable(ContainerAutoCraftingTable container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 194);

		lockedSprite = new SpriteDrawable(StaticPowerSprites.DIGISTORE_LOCKED_INDICATOR, 8, 8);
		lockedSprite.setTint(new SDColor(1.0f, 1.0f, 1.0f, 0.95f));
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

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBehindItems(stack, partialTicks, mouseX, mouseY);
		// Check if we have a recipe currently processing.
		CraftingRecipe recipe = getTileEntity().processingComponent.getCurrentRecipe().orElse(null);

		// If we do not, check to see if we have a potential recipe.
		if (recipe == null) {
			recipe = getTileEntity().processingComponent.getPendingRecipe().orElse(null);
		}

		// If there is a recipe, draw a phantom output.
		if (recipe != null) {
			GuiDrawUtilities.drawItem(stack, recipe.getResultItem(), 129, 38, 10, 0.3f);
		}
	}
}
