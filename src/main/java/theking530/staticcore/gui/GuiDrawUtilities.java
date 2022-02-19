package theking530.staticcore.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class GuiDrawUtilities {
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(198, 198, 198, 255).fromEightBitToFloat();
	public static final Color DEFAULT_BACKGROUND_EDGE_TINT = new Color(255, 255, 255, 255).fromEightBitToFloat();
	public static final Color DEFAULT_SLOT_DARK_EDGE_COLOR = new Color(55, 55, 55, 255).fromEightBitToFloat();
	public static final Color DEFAULT_SLOT_LIGHT_EDGE_COLOR = new Color(255, 255, 255, 255).fromEightBitToFloat();
	public static final Color DEFAULT_SLOT_CORNER_COLOR = new Color(139, 139, 139, 255).fromEightBitToFloat();

	private static final float BACKGROUND_PIXEL_SIZE = 1.0f / 9.0f;

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions, float x, float y, float z, Color tint) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());
		RenderSystem.enableBlend();

		// Body
		drawTexture(pose, texture, height - 4, z, x + 3, y + 3, width - 4, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexture(pose, texture, 4, 0.0f, x, y, 4, 0.0f, z, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, z, x + width - 5, y, 5, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 5, 0.0f, x, y + height - 5, 4, z, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, z, x + width - 4, y + height - 4, 4, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexture(pose, texture, 3, z, x + 4, y, width - 7, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, height - 8, 0.0f, x, y + 4, 3, z, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, height - 8, z, x + width - 3, y + 4, 3, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, z, x + 4, y + height - 3, width - 8, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions, float x, float y, float z) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, x, y, z, Color.WHITE);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions, float x, float y) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, x, y, 0.0f, Color.WHITE);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, 0, 0, 0.0f, Color.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y, float z, Color tint) {
		ResourceLocation texture = GuiTextures.GENERIC_GUI;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());
		RenderSystem.enableBlend();

		// Body
		drawTexture(pose, texture, height - 4, z, x + 3, y + 3, width - 4, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexture(pose, texture, 4, 0.0f, x, y, 4, 0.0f, z, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, z, x + width - 5, y, 5, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 5, 0.0f, x, y + height - 5, 4, z, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, z, x + width - 4, y + height - 4, 4, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexture(pose, texture, 3, z, x + 4, y, width - 7, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, height - 8, 0.0f, x, y + 4, 3, z, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, height - 8, z, x + width - 3, y + 4, 3, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, z, x + 4, y + height - 3, width - 8, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);

	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y, float z) {
		drawGenericBackground(pose, width, height, x, y, z, DEFAULT_BACKGROUND_COLOR);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y) {
		drawGenericBackground(pose, width, height, x, y, 0.0f, DEFAULT_BACKGROUND_COLOR);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height) {
		drawGenericBackground(pose, width, height, 0, 0, 0.0f, DEFAULT_BACKGROUND_COLOR);
	}

	public static void drawPlayerInventorySlots(PoseStack matrixStack, int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(matrixStack, 16, 16, xPos + j * 18, yPos + 1 + i * 18, 0);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(matrixStack, 16, 16, xPos + i * 18, yPos + 59, 0);
		}
	}

	public static void drawSlot(PoseStack pose, float width, float height, float x, float y, float z, Color color) {

		if (color != null) {
			drawRectangle(pose, width + 4, height + 4, -2, -2, z, color);
		}

		drawRectangle(pose, 1, 1 + height, -1, -1, z, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawRectangle(pose, width, 1, 0, -1, z, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawRectangle(pose, 1, 1, width, -1, z, DEFAULT_SLOT_CORNER_COLOR);

		drawRectangle(pose, 1, 1, -1, height, z, DEFAULT_SLOT_CORNER_COLOR);
		drawRectangle(pose, width + 1, 1, 0, height, z, DEFAULT_SLOT_LIGHT_EDGE_COLOR);
		drawRectangle(pose, 1, height, width, 0, z, DEFAULT_SLOT_LIGHT_EDGE_COLOR);
		drawRectangle(pose, width, height, 0, 0, z, DEFAULT_SLOT_CORNER_COLOR);
	}

	public static void drawSlot(PoseStack matrixStack, float width, float height, float x, float y, float z) {
		drawSlot(matrixStack, width, height, x, y, z, null);
	}

	public void drawVerticalBar(PoseStack pose, float width, float height, float x, float y, float fillAmount, Color color) {
		drawSlot(null, width, height, x, y, 0);
		int filledHeight = (int) (fillAmount * height);
		float zLevel = 0.0f;
		drawRectangle(pose, x + width, y + height, x, y + (height - filledHeight), zLevel, color);
	}

	public static void drawRectangle(PoseStack pose, float width, float height, Color color) {
		drawRectangle(pose, width, height, 0, 0, 0, color);
	}

	public static void drawRectangle(PoseStack pose, float width, float height, float x, float y, float z, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(pose.last().pose(), x, y + height, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x + width, y + height, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x + width, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		tessellator.end();
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float minU, float minV, float maxU, float maxV, Color color) {
		drawTexture(pose, texture, 0, 0, width, height, 0, minU, minV, maxU, maxV);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV) {
		drawTexture(pose, texture, width, height, x, y, z, minU, minV, maxU, maxV, Color.WHITE);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Matrix4f matrix = pose == null ? SDMath.IDENTITY : pose.last().pose();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(matrix, x, (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minU, maxV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxU, maxV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxU, minV).endVertex();
		bufferbuilder.vertex(matrix, x, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minU, minV).endVertex();
		tessellator.end();
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float minU, float minV, float maxU, float maxV, Color color) {
		drawSprite(pose, spriteLocation, 0, 0, width, height, 0, minU, minV, maxU, maxV);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, minU, minV, maxU, maxV, Color.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
		RenderSystem.enableBlend();
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
		Matrix4f matrix = pose == null ? SDMath.IDENTITY : pose.last().pose();

		float uDiff = sprite.getU1() - sprite.getU0();
		float vDiff = sprite.getV1() - sprite.getV0();

		float minSpriteU = sprite.getU0() + (minU * uDiff);
		float maxSpriteU = sprite.getU1() - ((1.0f - maxU) * vDiff);
		float minSpriteV = sprite.getV0() + (minV * vDiff);
		float maxSpriteV = sprite.getV1() - ((1.0f - maxV) * vDiff);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(matrix, (x + 0), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minSpriteU, maxSpriteV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxSpriteU, maxSpriteV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), (y + 0), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxSpriteU, minSpriteV).endVertex();
		bufferbuilder.vertex(matrix, (x + 0), (y + 0), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minSpriteU, minSpriteV).endVertex();
		tessellator.end();
	}

	public static void drawString(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, Color color, boolean withShadow) {
		drawString(matrixStack, text, xPos, yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringCentered(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, Color color, boolean withShadow) {
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawString(matrixStack, text, xPos + (width / 2), yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringLeftAligned(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, Color color, boolean withShadow) {
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawString(matrixStack, text, xPos + width, yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSize(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, ChatFormatting color, boolean withShadow) {
		drawString(matrixStack, text, xPos, yPos, zPos, scale, color.getColor(), withShadow);
	}

	/**
	 * Renders a string with the provided scale.
	 * 
	 * @param text
	 * @param xPos
	 * @param yPos
	 * @param zPos       TODO
	 * @param scale
	 * @param color
	 * @param withShadow
	 */
	public static void drawString(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, int color, boolean withShadow) {
		// The matrix stack cannot be null.
		if (matrixStack == null) {
			StaticPower.LOGGER.error("A non-null matrix stack must be provided to this method!");
			return;
		}

		final float scaleFactor = scale;
		final float inverseScaleFactor = 1.0f / scaleFactor;
		final int offset = 0;

		matrixStack.pushPose();
		matrixStack.scale(scaleFactor, scaleFactor, 1.0f);
		matrixStack.translate(0, 0, zPos);

		RenderSystem.disableBlend();
		final int X = (int) ((xPos + offset - Minecraft.getInstance().font.width(text) * scaleFactor) * inverseScaleFactor);
		final int Y = (int) ((yPos + offset - 7.0f * scaleFactor) * inverseScaleFactor);
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		Minecraft.getInstance().font.drawInBatch(text, X, Y, color, withShadow, matrixStack.last().pose(), buffer, true, 0, 15728880);
		buffer.endBatch();
		RenderSystem.enableBlend();
		matrixStack.popPose();
	}

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes attributes = fluid.getAttributes();
		ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
	}

	public static Color getFluidColor(FluidStack fluid) {
		FluidAttributes attributes = fluid.getFluid().getAttributes();
		int encodedFluidColor = attributes.getColor(fluid);
		return Color.fromEncodedInteger(encodedFluidColor).fromEightBitToFloat();
	}

	public static Vector2D translatePositionByMatrix(PoseStack matrixStack, Vector2D position) {
		return translatePositionByMatrix(matrixStack, position.getX(), position.getY());
	}

	public static Vector2D translatePositionByMatrix(PoseStack matrixStack, float xPos, float yPos) {
		if (matrixStack != null) {
			Vector4f vector4f = new Vector4f(xPos, yPos, 0, 1);
			vector4f.transform(matrixStack.last().pose());
			return new Vector2D(vector4f.x(), vector4f.y());
		} else {
			return new Vector2D(xPos, yPos);
		}
	}
}
