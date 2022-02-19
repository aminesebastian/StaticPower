package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class FluidProgressBar extends AbstractProgressBar<FluidProgressBar> {
	private FluidStack displayFluidStack;

	public FluidProgressBar(int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		displayFluidStack = FluidStack.EMPTY;
	}

	public void setFluidStack(FluidStack fluidStack) {
		this.displayFluidStack = fluidStack;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(pose, mouseX, mouseY, partialTicks);

		// Draw the background.
		GuiDrawUtilities.drawSlot(pose, getSize().getX(), getSize().getY(), 0, 0, 0);

		// Draw the fluid.
		if (!displayFluidStack.isEmpty()) {
			// Get the fluid icon.
			TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(displayFluidStack);

			// If the icon is valid, draw the progress.
			if (icon != null) {
				// Get the fluid color.
				Color fluidColor = GuiDrawUtilities.getFluidColor(displayFluidStack);
				fluidColor.setW(1.0f);

				// Calculate the UV difference.
				float uvDiff = icon.getU1() - icon.getU0();

				Minecraft.getInstance().getTextureManager().bindForSetup(InventoryMenu.BLOCK_ATLAS);
				GuiDrawUtilities.drawTexture(pose, InventoryMenu.BLOCK_ATLAS, visualCurrentProgresPercentage * getSize().getX(), getSize().getY(), 0, 0, 0.0f, icon.getU0(), icon.getV0(),
						icon.getU0() + (uvDiff * visualCurrentProgresPercentage), icon.getV1(), fluidColor);
			}

			// Draw the leading white line.
			GuiDrawUtilities.drawRectangle(pose, (visualCurrentProgresPercentage * getSize().getX()), 0, 0.75f, getSize().getY(), 1.0f, Color.WHITE);
		}

		// Draw the error indicator if needed.
		if (isProcessingErrored && drawErrorIcons) {
			getErrorDrawable().draw(pose, (getSize().getX() / 2.0f) - 8.0f, -(16 - getSize().getY()) / 2);
		}
	}
}
