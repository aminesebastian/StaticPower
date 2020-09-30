package theking530.staticcore.gui.widgets.tabs.redstonecontrol;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.button.ItemButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGuiRedstoneTab extends BaseGuiTab {

	public ItemButton ignoreRedstoneButton;
	public ItemButton lowRedstoneButton;
	public ItemButton highRedstoneButton;

	public AbstractGuiRedstoneTab(RedstoneMode currentMode) {
		super("Redstone Control", Color.EIGHT_BIT_YELLOW, 100, 75, GuiTextures.RED_TAB, Items.REDSTONE);

		widgetContainer.registerWidget(ignoreRedstoneButton = new ItemButton(Items.GUNPOWDER, 23, 25, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Ignore);
			updateToggledButton(ignoreRedstoneButton);
		}));
		widgetContainer.registerWidget(lowRedstoneButton = new ItemButton(Items.REDSTONE, 53, 25, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Low);
			updateToggledButton(lowRedstoneButton);
		}));
		widgetContainer.registerWidget(highRedstoneButton = new ItemButton(Blocks.REDSTONE_TORCH.asItem(), 83, 25, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.High);
			updateToggledButton(highRedstoneButton);
		}));

		highRedstoneButton.setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.high"));
		ignoreRedstoneButton.setClickSoundPitch(0.7f).setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.ignore"));
		lowRedstoneButton.setClickSoundPitch(0.85f).setTooltip(new TranslationTextComponent("gui.staticpower.redstone_mode.low"));

		// Initialize the correct button.
		if (currentMode == RedstoneMode.Ignore) {
			ignoreRedstoneButton.setToggled(true);
		} else if (currentMode == RedstoneMode.Low) {
			lowRedstoneButton.setToggled(true);
		} else {
			highRedstoneButton.setToggled(true);
		}
	}

	@Override
	public void renderBackground(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(matrix, mouseX, mouseY, partialTicks);
		drawDarkBackground(matrix, 15, 20, 95, 30);
		drawText(matrix, 0, 0);
	}

	protected void drawText(MatrixStack stack, int xPos, int yPos) {
		String redstoneMode = "Mode: ";
		fontRenderer.drawStringWithShadow(stack, redstoneMode, 9, 55, 16777215);
		if (getCurrentMode() == RedstoneMode.Low) {
			fontRenderer.drawStringWithShadow(stack, "Low", 38, 55, 16777215);
			fontRenderer.drawStringWithShadow(stack, "This machine will", 9, 67, 16777215);
			fontRenderer.drawStringWithShadow(stack, "only operate with no", 9, 76, 16777215);
			fontRenderer.drawStringWithShadow(stack, "signal.", 9, 85, 16777215);
		} else if (getCurrentMode() == RedstoneMode.High) {
			fontRenderer.drawStringWithShadow(stack, "High", 38, 55, 16777215);
			fontRenderer.drawStringWithShadow(stack, "This machine will", 9, 67, 16777215);
			fontRenderer.drawStringWithShadow(stack, "only operate with a", 9, 76, 16777215);
			fontRenderer.drawStringWithShadow(stack, "redstone signal.", 9, 85, 16777215);
		} else if (getCurrentMode() == RedstoneMode.Ignore) {
			fontRenderer.drawStringWithShadow(stack, "Ignore", 38, 55, 16777215);
			fontRenderer.drawStringWithShadow(stack, "This machine will", 9, 67, 16777215);
			fontRenderer.drawStringWithShadow(stack, "ignore any redstone", 9, 76, 16777215);
			fontRenderer.drawStringWithShadow(stack, "signal.", 9, 85, 16777215);
		}
	}

	protected void updateToggledButton(StandardButton selectedButton) {
		ignoreRedstoneButton.setToggled(false);
		lowRedstoneButton.setToggled(false);
		highRedstoneButton.setToggled(false);
		selectedButton.setToggled(true);
	}

	protected abstract RedstoneMode getCurrentMode();

	protected abstract void synchronizeRedstoneMode(RedstoneMode mode);
}
