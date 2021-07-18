package theking530.staticcore.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiFluidBarUtilities {

	public static void drawFluidBar(@Nullable MatrixStack stack, FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, boolean drawOverlay) {
		drawFluidBar(stack, fluid, capacity, amount, x, y, zLevel, width, height, null, drawOverlay);
	}

	@SuppressWarnings("deprecation")
	public static void drawFluidBar(@Nullable MatrixStack matrixStack, FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height,
			MachineSideMode mode, boolean drawOverlay) {
		if (mode != null && mode != MachineSideMode.Regular && mode != MachineSideMode.Never) {
			GuiDrawUtilities.drawSlot(matrixStack, (int) x, (int) (y - height), (int) width, (int) height, 0, mode.getColor());
		} else {
			GuiDrawUtilities.drawSlot(matrixStack, (int) x, (int) (y - height), (int) width, (int) height, 0);
		}

		// 3D effect controls.
		float depthEffectSides = 1.5f;
		float depthDistance = 5.0f;

		// Calculate the origin.
		Vector2D origin = GuiDrawUtilities.translatePositionByMatrix(matrixStack, x, y);

		if (fluid != null && fluid.getFluid() != null) {
			Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			boolean isGas = fluid.getFluid().getAttributes().isGaseous();
			float topColorTint = isGas ? 0.5f : 0.55f;

			TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(fluid);
			if (icon != null) {
				Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

				float ratio = ((float) amount / (float) capacity);
				float renderAmount = ratio * (float) height;

				float segmentCapacity = capacity / ((float) height / 16);
				int segmentsUsed = (int) ((renderAmount + 16) / 16);

				float diffV = icon.getMaxV() - icon.getMinV();
				Tessellator tessellator = Tessellator.getInstance();
				
				{
					float filledRatio = ((float) amount / capacity);
					float depthEffect = (1.0f * filledRatio) + (depthDistance * (1.0f - filledRatio));
					float depthHeightStart = (filledRatio * height);
					if (isGas) {
						depthHeightStart = height - depthHeightStart;
					}
					depthEffect = Math.min(height - depthHeightStart, depthEffect);

					BufferBuilder tes = tessellator.getBuffer();
					tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
					tes.pos(origin.getX() + width + 0.1f, origin.getY() - depthHeightStart, zLevel)
							.color(fluidColor.getRed() * topColorTint, fluidColor.getGreen() * topColorTint, fluidColor.getBlue() * topColorTint, fluidColor.getAlpha())
							.tex(icon.getMaxU(), icon.getMinV()).endVertex();

					tes.pos(origin.getX() + width - depthEffectSides, origin.getY() - depthHeightStart - depthEffect, zLevel)
							.color(fluidColor.getRed() * topColorTint, fluidColor.getGreen() * topColorTint, fluidColor.getBlue() * topColorTint, fluidColor.getAlpha())
							.tex(icon.getMaxU(), icon.getMinV() + (filledRatio * diffV)).endVertex();

					tes.pos(origin.getX() + depthEffectSides, origin.getY() - depthHeightStart - depthEffect, zLevel)
							.color(fluidColor.getRed() * topColorTint, fluidColor.getGreen() * topColorTint, fluidColor.getBlue() * topColorTint, fluidColor.getAlpha())
							.tex(icon.getMinU(), icon.getMinV() + (filledRatio * diffV)).endVertex();

					tes.pos(origin.getX() - 0.1f, origin.getY() - depthHeightStart, zLevel)
							.color(fluidColor.getRed() * topColorTint, fluidColor.getGreen() * topColorTint, fluidColor.getBlue() * topColorTint, fluidColor.getAlpha())
							.tex(icon.getMinU(), icon.getMinV()).endVertex();
					tessellator.draw();
				}

				for (int i = 0; i < segmentsUsed; i++) {
					float currentSegmentCapacity = segmentCapacity * (i + 1);
					float fillRatio = (float) Math.min(1.0, amount / currentSegmentCapacity);

					float yMin;
					float yMax;
					if (isGas) {
						yMin = height - ((i + 1) * 16) * fillRatio;
						yMax = height - (i * 16);
					} else {
						yMin = (i * 16);
						yMax = ((i + 1) * 16) * fillRatio;
					}

					BufferBuilder tes = tessellator.getBuffer();
					tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
					tes.pos(origin.getX() + width, origin.getY() - yMin, zLevel).color(fluidColor.getRed(), fluidColor.getGreen(), fluidColor.getBlue(), fluidColor.getAlpha())
							.tex(icon.getMaxU(), icon.getMinV()).endVertex();
					tes.pos(origin.getX() + width, origin.getY() - yMax, zLevel).color(fluidColor.getRed(), fluidColor.getGreen(), fluidColor.getBlue(), fluidColor.getAlpha())
							.tex(icon.getMaxU(), icon.getMinV() + (fillRatio * diffV)).endVertex();
					tes.pos(origin.getX(), origin.getY() - yMax, zLevel).color(fluidColor.getRed(), fluidColor.getGreen(), fluidColor.getBlue(), fluidColor.getAlpha())
							.tex(icon.getMinU(), icon.getMinV() + (fillRatio * diffV)).endVertex();
					tes.pos(origin.getX(), origin.getY() - yMin, zLevel).color(fluidColor.getRed(), fluidColor.getGreen(), fluidColor.getBlue(), fluidColor.getAlpha())
							.tex(icon.getMinU(), icon.getMinV()).endVertex();
					tessellator.draw();
				}
			}
		}

		if (drawOverlay) {
			Color linesColor = new Color(0.2f, 0.2f, 0.2f, 0.5f);
			for (int i = 0; i < height / 10; i++) {
				if (y - height + 2 + (i * 10) < y) {
					GuiDrawUtilities.drawColoredRectangle(origin.getX(), origin.getY() - height + 2 + (i * 10), width - 3, 0.5f, zLevel, linesColor);
				}
				if (y - height + 7 + (i * 10) < y) {
					GuiDrawUtilities.drawColoredRectangle(origin.getX(), origin.getY() - height + 7 + (i * 10), width - 7, 0.5f, zLevel, linesColor);
				}
			}
		}
	}

	public static List<ITextComponent> getTooltip(int fluidAmount, int maxCapacity, FluidStack fluid) {
		List<ITextComponent> tooltip = new ArrayList<ITextComponent>();

		if (fluid != null && !fluid.isEmpty()) {
			ITextComponent name = fluid.getDisplayName();
			tooltip.add(name);
			tooltip.add(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity))
					.append(new TranslationTextComponent("gui.staticpower.millbuckets")));
			return tooltip;
		} else {
			tooltip.add(new TranslationTextComponent("gui.staticpower.empty"));
			tooltip.add(new StringTextComponent("0/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity)).append(new TranslationTextComponent("gui.staticpower.millbuckets")));
			return tooltip;
		}
	}
}
