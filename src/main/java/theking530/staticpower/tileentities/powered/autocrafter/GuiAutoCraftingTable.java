package theking530.staticpower.tileentities.powered.autocrafter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.drawables.SpriteDrawable;
import theking530.common.gui.widgets.GuiDrawItem;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.common.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.common.gui.widgets.tabs.GuiSideConfigTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.common.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.common.utilities.Color;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;

public class GuiAutoCraftingTable extends StaticPowerTileEntityGui<ContainerAutoCraftingTable, TileEntityAutoCraftingTable> {
	private final GuiDrawItem itemRenderer;
	private final SpriteDrawable lockedSprite;

	public GuiAutoCraftingTable(ContainerAutoCraftingTable container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
		itemRenderer = new GuiDrawItem();

		lockedSprite = new SpriteDrawable(StaticPowerSprites.DIGISTORE_LOCKED_INDICATOR, 8, 8);
		lockedSprite.setTint(new Color(1.0f, 1.0f, 1.0f, 0.95f));
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 45));
		registerWidget(new ArrowProgressBar(99, 38).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(false, getTileEntity()));

		BaseGuiTab powerTab;
		getTabManager().registerTab(powerTab = new GuiPowerInfoTab(ComponentUtilities.getComponent(EnergyStorageComponent.class, "MainEnergyStorage", getTileEntity()).get()).setTabSide(TabSide.LEFT));
		getTabManager().setInitiallyOpenTab(powerTab);

		setOutputSlotSize(20);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		// Check if we have a recipe currently processing.
		ICraftingRecipe recipe = getTileEntity().getCurrentProcessingRecipe().orElse(null);

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

	}
}
