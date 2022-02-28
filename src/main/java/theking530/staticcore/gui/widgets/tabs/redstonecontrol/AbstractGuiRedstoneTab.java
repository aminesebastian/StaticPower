package theking530.staticcore.gui.widgets.tabs.redstonecontrol;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.button.ItemButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGuiRedstoneTab extends BaseGuiTab {

	public ItemButton ignoreRedstoneButton;
	public ItemButton lowRedstoneButton;
	public ItemButton highRedstoneButton;

	public AbstractGuiRedstoneTab(RedstoneMode currentMode) {
		super("Redstone Control", Color.EIGHT_BIT_YELLOW, 124, 100, new Color(1, 0.1f, 0.1f, 1), Items.REDSTONE);

		registerWidget(ignoreRedstoneButton = new ItemButton(Items.GUNPOWDER, 23, 25, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Ignore);
			updateToggledButton(ignoreRedstoneButton);
		}));
		registerWidget(lowRedstoneButton = new ItemButton(Items.REDSTONE, 53, 25, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.Low);
			updateToggledButton(lowRedstoneButton);
		}));
		registerWidget(highRedstoneButton = new ItemButton(Blocks.REDSTONE_TORCH.asItem(), 83, 25, 20, 20, (button, mouseButton) -> {
			synchronizeRedstoneMode(RedstoneMode.High);
			updateToggledButton(highRedstoneButton);
		}));

		highRedstoneButton.setTooltip(new TranslatableComponent("gui.staticpower.redstone_mode.high"));
		ignoreRedstoneButton.setClickSoundPitch(0.7f).setTooltip(new TranslatableComponent("gui.staticpower.redstone_mode.ignore"));
		lowRedstoneButton.setClickSoundPitch(0.85f).setTooltip(new TranslatableComponent("gui.staticpower.redstone_mode.low"));

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
	public void renderWidgetBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBackground(matrix, mouseX, mouseY, partialTicks);
		drawDarkBackground(matrix, 15, 20, 95, 30);
		drawText(matrix, 0, 0);
	}

	protected void drawText(PoseStack stack, int xPos, int yPos) {
		String redstoneMode = "Mode: ";
		getFontRenderer().drawShadow(stack, redstoneMode, 9, 55, 16777215);
		if (getCurrentMode() == RedstoneMode.Low) {
			getFontRenderer().drawShadow(stack, "Low", 38, 55, 16777215);
			getFontRenderer().drawShadow(stack, "This machine will", 9, 67, 16777215);
			getFontRenderer().drawShadow(stack, "only operate with no", 9, 76, 16777215);
			getFontRenderer().drawShadow(stack, "signal.", 9, 85, 16777215);
		} else if (getCurrentMode() == RedstoneMode.High) {
			getFontRenderer().drawShadow(stack, "High", 38, 55, 16777215);
			getFontRenderer().drawShadow(stack, "This machine will", 9, 67, 16777215);
			getFontRenderer().drawShadow(stack, "only operate with a", 9, 76, 16777215);
			getFontRenderer().drawShadow(stack, "redstone signal.", 9, 85, 16777215);
		} else if (getCurrentMode() == RedstoneMode.Ignore) {
			getFontRenderer().drawShadow(stack, "Ignore", 38, 55, 16777215);
			getFontRenderer().drawShadow(stack, "This machine will", 9, 67, 16777215);
			getFontRenderer().drawShadow(stack, "ignore any redstone", 9, 76, 16777215);
			getFontRenderer().drawShadow(stack, "signal.", 9, 85, 16777215);
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
