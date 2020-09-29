package theking530.staticcore.gui.widgets.tabs.redstonecontrol;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.button.ItemButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGuiRedstoneTab extends BaseGuiTab {

	public ItemButton ignoreRedstoneButton;
	public ItemButton lowRedstoneButton;
	public ItemButton highRedstoneButton;

	public AbstractGuiRedstoneTab(RedstoneMode currentMode) {
		super("Redstone Control", 100, 85, GuiTextures.RED_TAB, Items.REDSTONE);

		widgetContainer.registerWidget(ignoreRedstoneButton = new ItemButton(Items.GUNPOWDER, 23, 30, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Ignore);
			updateToggledButton(ignoreRedstoneButton);
		}));
		widgetContainer.registerWidget(lowRedstoneButton = new ItemButton(Items.REDSTONE, 53, 30, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Low);
			updateToggledButton(lowRedstoneButton);
		}));
		widgetContainer.registerWidget(highRedstoneButton = new ItemButton(Blocks.REDSTONE_TORCH.asItem(), 83, 30, 20, 20, (button, mouseButton) -> {
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
		drawDarkBackground(matrix, 6, 56, 110, 47);
		drawText(matrix, 0, 0);
	}

	protected void drawText(MatrixStack stack, int xPos, int yPos) {
		String tabName = TextFormatting.YELLOW + "Redstone Config";
		String redstoneMode = "Mode: ";

		modeText(stack, xPos, yPos);
		fontRenderer.drawStringWithShadow(stack, redstoneMode, 9, 60, 16777215);
		fontRenderer.drawStringWithShadow(stack, tabName, 20, 8, 16777215);
	}

	protected void modeText(MatrixStack stack, int tabLeft, int tabTop) {
		if (getCurrentMode() == RedstoneMode.Low) {
			fontRenderer.drawStringWithShadow(stack, "Low", 38, 60, 16777215);
			fontRenderer.drawStringWithShadow(stack, "This machine will", 9, 72, 16777215);
			fontRenderer.drawStringWithShadow(stack, "only operate with no", 10, 81, 16777215);
			fontRenderer.drawStringWithShadow(stack, "signal.", 10, 90, 16777215);
		} else if (getCurrentMode() == RedstoneMode.High) {
			fontRenderer.drawStringWithShadow(stack, "High", 38, 60, 16777215);
			fontRenderer.drawStringWithShadow(stack, "This machine will", 9, 72, 16777215);
			fontRenderer.drawStringWithShadow(stack, "only operate with a", 10, 81, 16777215);
			fontRenderer.drawStringWithShadow(stack, "redstone signal.", 10, 90, 16777215);
		} else if (getCurrentMode() == RedstoneMode.Ignore) {
			fontRenderer.drawStringWithShadow(stack, "Ignore", 38, 60, 16777215);
			fontRenderer.drawStringWithShadow(stack, "This machine will", 9, 72, 16777215);
			fontRenderer.drawStringWithShadow(stack, "ignore any redstone", 10, 81, 16777215);
			fontRenderer.drawStringWithShadow(stack, "signal.", 10, 90, 16777215);
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
