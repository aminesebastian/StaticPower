package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class FluidProgressBar extends AbstractProgressBar {
	private FluidStack displayFluidStack;

	public FluidProgressBar(int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		displayFluidStack = FluidStack.EMPTY;
	}

	public void setFluidStack(FluidStack fluidStack) {
		this.displayFluidStack = fluidStack;
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);

		// Get the screen space position.
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());

		// Draw the background.
		GuiDrawUtilities.drawSlot(null, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY());

		// Draw the fluid.
		if (!displayFluidStack.isEmpty()) {
			// Get the adjusted progress.
			float adjustedProgress = visualCurrentProgress / maxProgress;

			// Get the fluid icon.
			TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(displayFluidStack);

			// If the icon is valid, draw the progress.
			if (icon != null) {
				// Get the fluid color.
				Color fluidColor = GuiDrawUtilities.getFluidColor(displayFluidStack);

				// Calculate the UV difference.
				float uvDiff = icon.getMaxU() - icon.getMinU();

				Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
				GuiDrawUtilities.drawTexturedModalRect(PlayerContainer.LOCATION_BLOCKS_TEXTURE, screenSpacePosition.getX(), screenSpacePosition.getY(), adjustedProgress * getSize().getX(), getSize().getY(),
						icon.getMinU(), icon.getMinV(), icon.getMinU() + (uvDiff * adjustedProgress), icon.getMaxV(), fluidColor);
			}

			// Draw the leading white line.
			GuiDrawUtilities.drawColoredRectangle(screenSpacePosition.getX() + (adjustedProgress * getSize().getX()), screenSpacePosition.getY(), 0.75f, getSize().getY(), 1.0f, Color.WHITE);
		}

		// Draw the error indicator if needed.
		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + (getSize().getX() / 2.0f) - 8.0f, screenSpacePosition.getY() - (getSize().getY() / 2.0f) - 3.25f);
		}
	}
}
