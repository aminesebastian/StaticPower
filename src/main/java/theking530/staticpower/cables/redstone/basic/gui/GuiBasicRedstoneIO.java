package theking530.staticpower.cables.redstone.basic.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.widgets.button.ItemButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.cables.redstone.RedstoneCableSideConfiguration;
import theking530.staticpower.cables.redstone.basic.TileEntityRedstoneCable;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiBasicRedstoneIO extends StaticPowerTileEntityGui<ContainerBasicRedstoneIO, TileEntityRedstoneCable> {

	private ItemButton inputSignalButton;

	public GuiBasicRedstoneIO(ContainerBasicRedstoneIO container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
		getTabManager().registerTab(new GuiInfoTab(100));

		// Setup the input/output button.
		registerWidget(inputSignalButton = new ItemButton(Items.REDSTONE, 16, 16, 18, 18, this::inputLogicButtonPressed));
		updateSignalButton();
	}

	protected void drawForegroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
	}

	protected void inputLogicButtonPressed(StandardButton button, MouseButton mouse) {
		// Increment the logic type, then update the configuration and update the UI.
		getContainer().getSideConfiguration().incrementLogicType();
		getContainer().getCableComponent().updateConfiguration(getContainer().getConfiguration());
		updateSignalButton();
	}

	protected void updateSignalButton() {
		// Get the configuration for this side.
		RedstoneCableSideConfiguration configuration = getContainer().getSideConfiguration();

		// Update the icon and tooltip.
		if (configuration.isInputSide()) {
			inputSignalButton.setItem(Items.GUNPOWDER);
			inputSignalButton.setTooltip(new TranslationTextComponent("gui.staticpower.redstone_cable_input_mode"));
		} else if (configuration.isOutputSide()) {
			inputSignalButton.setItem(Blocks.REDSTONE_TORCH.asItem());
			inputSignalButton.setTooltip(new TranslationTextComponent("gui.staticpower.redstone_cable_output_mode"));
		} else if (configuration.isInputOutputSide()) {
			inputSignalButton.setItem(Items.REDSTONE);
			inputSignalButton.setTooltip(new TranslationTextComponent("gui.staticpower.redstone_cable_input_output_mode"));
		}

	}
}
