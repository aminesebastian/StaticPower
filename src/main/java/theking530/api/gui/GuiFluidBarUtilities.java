package theking530.api.gui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.utilities.Color;
import theking530.staticpower.tileentity.SideModeList.Mode;

public class GuiFluidBarUtilities {

	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, boolean drawOverlay) {
		drawFluidBar(fluid, capacity, amount, x, y, zLevel, width, height, null, drawOverlay);
	}

	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, Mode mode, boolean drawOverlay) {
		if (mode != null) {
			GuiDrawUtilities.drawSlot((int) x, (int) (y - height), (int) width, (int) height, mode.getBorderColor());
		} else {
			GuiDrawUtilities.drawSlot((int) x, (int) (y - height), (int) width, (int) height);
		}
		GlStateManager.disableLighting();
		if (fluid != null && fluid.getFluid() != null) {

			TextureAtlasSprite icon = getStillFluidSprite(fluid);
			if (icon != null) {
				float ratio = ((float) amount / (float) capacity);
				float renderAmount = ratio * (float) height;

				// RenderUtil.bindBlockTexture();
				GlStateManager.enableBlend();

				float segmentCapacity = capacity / ((float) height / 16);
				int segmentsUsed = (int) ((renderAmount + 16) / 16);

				float minU = icon.getMinU();
				float maxU = icon.getMaxU();
				float minV = icon.getMinV();
				float maxV = icon.getMaxV();
				float diffV = maxV - minV;

				GL11.glColor3f(1.0f, 1.0f, 1.0f);

				for (int i = 0; i < segmentsUsed; i++) {
					float currentSegmentCapacity = segmentCapacity * (i + 1);
					float fillRatio = (float) Math.min(1.0, amount / currentSegmentCapacity);

					float yMin = (i * 16);
					float yMax = ((i + 1) * 16) * fillRatio;
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder tes = tessellator.getBuffer();
					tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					tes.pos(x + width, y - yMin, zLevel).tex(maxU, minV).endVertex();
					tes.pos(x + width, y - yMax, zLevel).tex(maxU, minV + (fillRatio * diffV)).endVertex();
					tes.pos(x, y - yMax, zLevel).tex(minU, minV + (fillRatio * diffV)).endVertex();
					tes.pos(x, y - yMin, zLevel).tex(minU, minV).endVertex();
					tessellator.draw();

				}
			}
		}
		GlStateManager.enableAlphaTest();
		GlStateManager.disableBlend();
		if (drawOverlay) {
			Color linesColor = new Color(40, 40, 120);
			for (int i = 0; i < height / 10; i++) {
				GuiDrawUtilities.drawColoredRectangle((int) x, (int) (y - height) + 2, (int) (x + width - 10), (int) (y - height + 3), zLevel, linesColor);
				GuiDrawUtilities.drawColoredRectangle((int) x, (int) (y - height + 7 + (i * 10)), (int) (x + width - 10), (int) (y - height + 8 + (i * 10)), zLevel, linesColor);
				if (i != height / 10 - 1) {
					GuiDrawUtilities.drawColoredRectangle((int) x, (int) (y - height + 12 + (i * 10)), (int) (x + width - 7), (int) (y - height + 13 + (i * 10)), zLevel, linesColor);
				}
			}
		}
		GlStateManager.enableLighting();
	}

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes attributes = fluid.getAttributes();
		ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
	}

	public static List<String> getTooltip(int fluidAmount, int maxCapacity, FluidStack fluid) {
		if (fluid != null) {
			ITextComponent name = fluid.getDisplayName();
			String text = (name + "=" + NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity) + "mB");
			String[] splitMsg = text.split("=");
			return Arrays.asList(splitMsg);
		}
		String text = ("Empty" + "=" + NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity) + "mB");
		String[] splitMsg = text.split("=");
		return Arrays.asList(splitMsg);
	}
}
