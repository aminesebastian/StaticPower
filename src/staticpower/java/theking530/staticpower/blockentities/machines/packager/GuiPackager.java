package theking530.staticpower.blockentities.machines.packager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiPackager extends StaticCoreBlockEntityScreen<ContainerPackager, BlockEntityPackager> {

	private SpriteButton recipeSizeButton;

	public GuiPackager(ContainerPackager container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new ArrowProgressBar(75, 35).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);

		registerWidget(recipeSizeButton = new SpriteButton(50, 56, 16, 16, StaticPowerSprites.SIZE_TWO_CRAFTING, null, this::buttonPressed));
		updateButtonSprite(getTileEntity().getRecipeSize());
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		// Get the other size to use.
		int size = getTileEntity().getRecipeSize() == 2 ? 3 : 2;

		// Update the local tile entity.
		this.getTileEntity().setRecipeSize(size);

		// Create the packet and send it.
		PacketPackagerSizeChange packet = new PacketPackagerSizeChange(getTileEntity(), size);
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(packet);

		// Update the button sprite.
		updateButtonSprite(getTileEntity().getRecipeSize());
	}

	private void updateButtonSprite(int size) {
		if (size == 2) {
			recipeSizeButton.setRegularTexture(StaticPowerSprites.SIZE_TWO_CRAFTING);
			recipeSizeButton.setTooltip(Component.literal("Recipe Size: 2x2"));
		} else {
			recipeSizeButton.setRegularTexture(StaticPowerSprites.SIZE_THREE_CRAFTING);
			recipeSizeButton.setTooltip(Component.literal("Recipe Size: 3x3"));
		}
	}
}
