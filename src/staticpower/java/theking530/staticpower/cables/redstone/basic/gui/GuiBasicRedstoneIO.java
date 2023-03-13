package theking530.staticpower.cables.redstone.basic.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import theking530.staticcore.gui.widgets.button.ItemButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.cables.redstone.RedstoneCableSideConfiguration;
import theking530.staticpower.cables.redstone.basic.BlockEntityRedstoneCable;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiBasicRedstoneIO extends StaticPowerTileEntityGui<ContainerBasicRedstoneIO, BlockEntityRedstoneCable> {

	private ItemButton inputSignalButton;

	public GuiBasicRedstoneIO(ContainerBasicRedstoneIO container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 151);
		getTabManager().registerTab(new GuiInfoTab(100));

		// Setup the input/output button.
		registerWidget(inputSignalButton = new ItemButton(Items.REDSTONE, 16, 16, 18, 18, this::inputLogicButtonPressed));
		updateSignalButton();
	}

	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
	}

	protected void inputLogicButtonPressed(StandardButton button, MouseButton mouse) {
		// Increment the logic type, then update the configuration and update the UI.
		getMenu().getSideConfiguration().incrementLogicType();
		getMenu().getCableComponent().updateConfiguration(getMenu().getConfiguration());
		updateSignalButton();
	}

	protected void updateSignalButton() {
		// Get the configuration for this side.
		RedstoneCableSideConfiguration configuration = getMenu().getSideConfiguration();

		// Update the icon and tooltip.
		if (configuration.isInputSide()) {
			inputSignalButton.setItem(Items.GUNPOWDER);
			inputSignalButton.setTooltip(Component.translatable("gui.staticpower.redstone_cable_input_mode"));
		} else if (configuration.isOutputSide()) {
			inputSignalButton.setItem(Blocks.REDSTONE_TORCH.asItem());
			inputSignalButton.setTooltip(Component.translatable("gui.staticpower.redstone_cable_output_mode"));
		} else if (configuration.isInputOutputSide()) {
			inputSignalButton.setItem(Items.REDSTONE);
			inputSignalButton.setTooltip(Component.translatable("gui.staticpower.redstone_cable_input_output_mode"));
		}

	}
}
