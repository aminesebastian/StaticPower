package theking530.staticcore.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;

public class GuiFluidBarUtilities {
	private static final int TEXTURE_SIZE = 16;
	private static final int MIN_FLUID_HEIGHT = 1;
	public static float depthEffectSides = 1.5f;
	public static float depthDistance = 5.0f;

	public static void drawFluidBar(PoseStack pose, FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, boolean drawOverlay) {
		drawFluidBar(pose, fluid, capacity, amount, x, y, zLevel, width, height, null, drawOverlay);
	}

	public static void drawFluidBarOutline(PoseStack pose, float x, float y, float zLevel, float width, float height, MachineSideMode mode, boolean drawLines) {
		// Draw the outline around the fluid slot.
		if (mode != null && mode != MachineSideMode.Never) {
			GuiDrawUtilities.drawSlotWithBorder(pose, (int) width, (int) height, (int) x, (int) (y - height), 0, mode.getColor());
		} else {
			GuiDrawUtilities.drawSlot(pose, (int) width, (int) height, (int) x, (int) (y - height), 0);
		}

		if (drawLines) {
			drawFluidOverlay(pose, x, y, zLevel, width, height);
		}
	}

	public static void drawFluidBar(PoseStack pose, FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, MachineSideMode mode,
			boolean drawOverlay) {

		drawFluidBarOutline(pose, x, y, zLevel, width, height, mode, false);

		// Only draw the fluid if a valid fluid was provided.
		if (fluid != null && !fluid.isEmpty()) {
			drawFluidBody(pose, fluid, capacity, amount, x, y, zLevel, width, height);
		}

		if (drawOverlay) {
			drawFluidOverlay(pose, x, y, zLevel, width, height);
		}
	}

	public static List<Component> getTooltip(int fluidAmount, int maxCapacity, FluidStack fluid) {
		List<Component> tooltip = new ArrayList<Component>();

		if (fluid != null && !fluid.isEmpty()) {
			Component name = fluid.getDisplayName();
			tooltip.add(name);
			tooltip.add(Component.literal(NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity))
					.append(Component.translatable("gui.staticpower.millbuckets")));
			return tooltip;
		} else {
			tooltip.add(Component.translatable("gui.staticpower.empty"));
			tooltip.add(Component.literal("0/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity)).append(Component.translatable("gui.staticpower.millbuckets")));
			return tooltip;
		}
	}

	private static void drawFluidOverlay(PoseStack pose, float x, float y, float zLevel, float width, float height) {
		SDColor linesColor = new SDColor(0.2f, 0.2f, 0.2f, 0.5f);
		for (int i = 0; i < height / 10; i++) {
			if (y - height + 2 + (i * 10) < y) {
				GuiDrawUtilities.drawRectangle(pose, width - 3, 0.5f, x, y - height + 2 + (i * 10), zLevel, linesColor);
			}
			if (y - height + 7 + (i * 10) < y) {
				GuiDrawUtilities.drawRectangle(pose, width - 7, 0.5f, x, y - height + 7 + (i * 10), zLevel, linesColor);
			}
		}
	}

	private static void drawFluidBody(PoseStack pose, FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height) {
		// Make sure the fluid is valid.
		if (fluid == null || fluid.isEmpty()) {
			return;
		}

		SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
		boolean isGas = false;// TOOD: Figure out how this changed in 1.19.2 fluid.getFluid().getFluidType().
		float topColorTint = isGas ? 0.5f : 0.55f;

		// We'll use the still texture here.
		TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(fluid);

		if (icon != null) {
			RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
			RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
			RenderSystem.setShaderColor(fluidColor.getRed(), fluidColor.getGreen(), fluidColor.getBlue(), fluidColor.getAlpha());

			float ratio = ((float) amount / (float) capacity);
			float renderAmount = ratio * (float) height;

			float segmentCapacity = capacity / ((float) height / 16);
			int segmentsUsed = (int) ((renderAmount + 16) / 16);

			float uMin = icon.getU0();
			float uMax = icon.getU1();
			float vMin = icon.getV0();
			float vMax = icon.getV1();
			float diffV = vMax - vMin;

			Tesselator tessellator = Tesselator.getInstance();
			{
				float filledRatio = ((float) amount / capacity);
				float depthEffect = (1.0f * filledRatio) + (depthDistance * (1.0f - filledRatio));
				float depthHeightStart = (filledRatio * height);
				if (isGas) {
					depthHeightStart = height - depthHeightStart;
				}
				depthEffect = Math.min(height - depthHeightStart, depthEffect);

				BufferBuilder tes = tessellator.getBuilder();
				tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
				tes.vertex(pose.last().pose(), x + width + 0.1f, y - depthHeightStart, zLevel).color(topColorTint, topColorTint, topColorTint, 1.0f).uv(uMax, vMin).endVertex();
				tes.vertex(pose.last().pose(), x + width - depthEffectSides, y - depthHeightStart - depthEffect, zLevel).color(topColorTint, topColorTint, topColorTint, 1.0f)
						.uv(uMax, vMin + (filledRatio * diffV)).endVertex();
				tes.vertex(pose.last().pose(), x + depthEffectSides, y - depthHeightStart - depthEffect, zLevel).color(topColorTint, topColorTint, topColorTint, 1.0f)
						.uv(uMin, vMin + (filledRatio * diffV)).endVertex();
				tes.vertex(pose.last().pose(), x - 0.1f, y - depthHeightStart, zLevel).color(topColorTint, topColorTint, topColorTint, 1.0f).uv(uMin, vMin).endVertex();
				tessellator.end();
			}

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
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

				BufferBuilder tes = tessellator.getBuilder();
				tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				tes.vertex(pose.last().pose(), x + width, y - yMin, zLevel).uv(icon.getU1(), icon.getV0()).endVertex();
				tes.vertex(pose.last().pose(), x + width, y - yMax, zLevel).uv(icon.getU1(), icon.getV0() + (fillRatio * diffV)).endVertex();
				tes.vertex(pose.last().pose(), x, y - yMax, zLevel).uv(icon.getU0(), icon.getV0() + (fillRatio * diffV)).endVertex();
				tes.vertex(pose.last().pose(), x, y - yMin, zLevel).uv(icon.getU0(), icon.getV0()).endVertex();
				tessellator.end();
			}

			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	public static void drawFluid(PoseStack poseStack, final int xPosition, final int yPosition, final float width, final float height, FluidStack fluidStack, int capacity) {
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) {
			return;
		}

		TextureAtlasSprite fluidStillSprite = GuiDrawUtilities.getStillFluidSprite(fluidStack);
		int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor();

		int amount = fluidStack.getAmount();
		float scaledAmount = (amount * height) / capacity;
		if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
			scaledAmount = MIN_FLUID_HEIGHT;
		}
		if (scaledAmount > height) {
			scaledAmount = height;
		}

		drawTiledSprite(poseStack, xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
	}

	private static void drawTiledSprite(PoseStack poseStack, final int xPosition, final int yPosition, final float tiledWidth, final float tiledHeight, int color,
			float scaledAmount, TextureAtlasSprite sprite) {
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		Matrix4f matrix = poseStack.last().pose();
		setGLColorFromInt(color);

		final float xTileCount = tiledWidth / TEXTURE_SIZE;
		final float xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
		final float yTileCount = scaledAmount / TEXTURE_SIZE;
		final float yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

		final float yStart = yPosition + tiledHeight;

		for (int xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				float width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
				float height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
				int x = xPosition + (xTile * TEXTURE_SIZE);
				float y = yStart - ((yTile + 1) * TEXTURE_SIZE);
				if (width > 0 && height > 0) {
					float maskTop = TEXTURE_SIZE - height;
					float maskRight = TEXTURE_SIZE - width;

					drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
				}
			}
		}
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		float alpha = ((color >> 24) & 0xFF) / 255F;

		RenderSystem.setShaderColor(red, green, blue, alpha);
	}

	private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, float maskTop, float maskRight, float zLevel) {
		float uMin = textureSprite.getU0();
		float uMax = textureSprite.getU1();
		float vMin = textureSprite.getV0();
		float vMax = textureSprite.getV1();
		uMax = uMax - (maskRight / 16F * (uMax - uMin));
		vMax = vMax - (maskTop / 16F * (vMax - vMin));

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
		bufferBuilder.vertex(xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
		bufferBuilder.vertex(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
		bufferBuilder.vertex(xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
		tessellator.end();
	}
}
