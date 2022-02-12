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
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);

		// Get the screen space position.
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());

		// Draw the background.
		GuiDrawUtilities.drawSlot(null, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0);

		// Draw the fluid.
		if (!displayFluidStack.isEmpty()) {
			// Get the fluid icon.
			TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(displayFluidStack);

			// If the icon is valid, draw the progress.
			if (icon != null) {
				// Get the fluid color.
				Color fluidColor = GuiDrawUtilities.getFluidColor(displayFluidStack);

				// Calculate the UV difference.
				float uvDiff = icon.getU1() - icon.getU0();

				Minecraft.getInstance().getTextureManager().bindForSetup(InventoryMenu.BLOCK_ATLAS);
				GuiDrawUtilities.drawTexturedModalRect(InventoryMenu.BLOCK_ATLAS, null, screenSpacePosition.getX(), screenSpacePosition.getY(), 0.0f,
						visualCurrentProgresPercentage * getSize().getX(), getSize().getY(), icon.getU0(), icon.getV0(), icon.getU0() + (uvDiff * visualCurrentProgresPercentage), icon.getV1(), fluidColor);
			}

			// Draw the leading white line.
			GuiDrawUtilities.drawColoredRectangle(screenSpacePosition.getX() + (visualCurrentProgresPercentage * getSize().getX()), screenSpacePosition.getY(), 0.75f, getSize().getY(), 1.0f, Color.WHITE);
		}
		
		// Draw the error indicator if needed.
		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + (getSize().getX() / 2.0f) - 8.0f, screenSpacePosition.getY() - (16 - getSize().getY()) / 2);
		}
	}
}
