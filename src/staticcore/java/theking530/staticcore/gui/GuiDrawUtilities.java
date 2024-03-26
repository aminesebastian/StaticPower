package theking530.staticcore.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.StaticCore;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.utilities.math.Vector4D;

@OnlyIn(Dist.CLIENT)
public class GuiDrawUtilities {
	public static final SDColor DEFAULT_BACKGROUND_COLOR = new SDColor(198, 198, 198, 255).fromEightBitToFloat();
	public static final SDColor DEFAULT_BACKGROUND_EDGE_TINT = new SDColor(255, 255, 255, 255).fromEightBitToFloat();
	public static final SDColor DEFAULT_SLOT_DARK_EDGE_COLOR = new SDColor(55, 55, 55, 255).fromEightBitToFloat();
	public static final SDColor DEFAULT_SLOT_LIGHT_EDGE_COLOR = new SDColor(255, 255, 255, 255).fromEightBitToFloat();
	public static final SDColor DEFAULT_SLOT_CORNER_COLOR = new SDColor(139, 139, 139, 255).fromEightBitToFloat();

	private static final float BACKGROUND_PIXEL_SIZE = 1.0f / 9.0f;

	public static final PoseStack IDENTITY_STACK;
	static {
		IDENTITY_STACK = new PoseStack();
		IDENTITY_STACK.setIdentity();
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height,
			Vector4D borderDefenitions, float x, float y, float z, SDColor tint) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());
		RenderSystem.enableBlend();

		// Body
		drawTexture(pose, texture, width - 4, height - 4, x + 3, y + 3, z, 5 * BACKGROUND_PIXEL_SIZE,
				5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexture(pose, texture, 4, 4, x, y, z, 0.0f, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 5, 4, x + width - 5, y, z, 4 * BACKGROUND_PIXEL_SIZE, 0.0f,
				9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 5, x, y + height - 5, z, 0.0f, 4 * BACKGROUND_PIXEL_SIZE,
				4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 4, x + width - 4, y + height - 4, 0.0f, 5 * BACKGROUND_PIXEL_SIZE,
				5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexture(pose, texture, width - 7, 3, x + 4, y, z, 3 * BACKGROUND_PIXEL_SIZE, 0.0f,
				4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x, y + 4, 3, z, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE,
				4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x + width - 3, y + 4, z, 6 * BACKGROUND_PIXEL_SIZE,
				3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, width - 8, 3, x + 4, y + height - 3, z, 4 * BACKGROUND_PIXEL_SIZE,
				6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height,
			Vector4D borderDefenitions, float x, float y, float z) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, x, y, z, SDColor.WHITE);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height,
			Vector4D borderDefenitions, float x, float y) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, x, y, 0.0f, SDColor.WHITE);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height,
			Vector4D borderDefenitions) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, 0, 0, 0.0f, SDColor.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y, float z,
			SDColor tint) {
		ResourceLocation texture = StaticCoreGuiTextures.GENERIC_GUI;
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());

		// Body
		drawTexture(pose, texture, width - 6, height - 6, x + 3, y + 3, z, 5 * BACKGROUND_PIXEL_SIZE,
				5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexture(pose, texture, 4, 4, x, y, z, 0.0f, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 4, x + width - 4, y, z, 5 * BACKGROUND_PIXEL_SIZE, 0.0f,
				9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 4, x, y + height - 4, z, 0.0f, 5 * BACKGROUND_PIXEL_SIZE,
				4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 4, x + width - 4, y + height - 4, z, 5 * BACKGROUND_PIXEL_SIZE,
				5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexture(pose, texture, width - 7, 3, x + 4, y, z, 3 * BACKGROUND_PIXEL_SIZE, 0.0f,
				4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x, y + 4, z, 0.0f, 3 * BACKGROUND_PIXEL_SIZE,
				3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x + width - 3, y + 4, z, 6 * BACKGROUND_PIXEL_SIZE,
				3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, width - 8, 4, x + 4, y + height - 4, z, 4 * BACKGROUND_PIXEL_SIZE,
				5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);

	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y, float z) {
		drawGenericBackground(pose, width, height, x, y, z, SDColor.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y) {
		drawGenericBackground(pose, width, height, x, y, 0.0f, SDColor.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height) {
		drawGenericBackground(pose, width, height, 0, 0, 0.0f, SDColor.WHITE);
	}

	public static void drawTab(PoseStack pose, float width, float height, float x, float y, float z, SDColor tint,
			boolean left) {
		ResourceLocation texture = StaticCoreGuiTextures.GENERIC_GUI;
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());

		// Body
		drawTexture(pose, texture, width - 4, height - 4, x + 3, y + 3, z, 5 * BACKGROUND_PIXEL_SIZE,
				5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		if (left) {
			drawTexture(pose, texture, 4, 4, x, y, z, 0.0f, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
			drawTexture(pose, texture, 4, 5, x, y + height - 5, z, 0.0f, 4 * BACKGROUND_PIXEL_SIZE,
					4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		} else {
			drawTexture(pose, texture, 4, 4, x + width - 4, y + height - 4, 0.0f, 5 * BACKGROUND_PIXEL_SIZE,
					5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
			drawTexture(pose, texture, 5, 4, x + width - 5, y, z, 4 * BACKGROUND_PIXEL_SIZE, 0.0f,
					9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		}

		// Sides
		if (left) {
			drawTexture(pose, texture, 3, height - 8, x, y + 4, z, 0.0f, 3 * BACKGROUND_PIXEL_SIZE,
					3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		} else {
			drawTexture(pose, texture, 3, height - 8, x + width - 3, y + 4, z, 6 * BACKGROUND_PIXEL_SIZE,
					3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		}

		drawTexture(pose, texture, width - 7, 3, x + 4, y, z, 3 * BACKGROUND_PIXEL_SIZE, 0.0f,
				4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, width - 8, 3, x + 4, y + height - 3, z, 4 * BACKGROUND_PIXEL_SIZE,
				6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);

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

	public static void drawSlotWithBorder(PoseStack pose, float width, float height, float x, float y, float z,
			SDColor borderColor) {
		if (borderColor != null) {
			drawRectangle(pose, width + 4, height + 4, x - 2, y - 2, z, borderColor);
		}

		drawRectangle(pose, 1, 1 + height, x - 1, y - 1, z, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawRectangle(pose, width, 1, x, y - 1, z, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawRectangle(pose, 1, 1, x + width, y - 1, z, DEFAULT_SLOT_CORNER_COLOR);

		drawRectangle(pose, 1, 1, x - 1, y + height, z, DEFAULT_SLOT_CORNER_COLOR);
		drawRectangle(pose, width + 1, 1, x, y + height, z, DEFAULT_SLOT_LIGHT_EDGE_COLOR);
		drawRectangle(pose, 1, height, x + width, y, z, DEFAULT_SLOT_LIGHT_EDGE_COLOR);

		drawRectangle(pose, width, height, x, y, z, DEFAULT_SLOT_CORNER_COLOR);

		if (borderColor != null) {
			SDColor lighterFill = borderColor.copy();
			lighterFill.setAlpha(0.25f);
			drawRectangle(pose, width, height, x, y, z, lighterFill);
		}
	}

	public static void drawSlot(PoseStack pose, float width, float height, float x, float y, float z) {
		drawSlotWithBorder(pose, width, height, x, y, z, null);
	}

	public static void drawSlot(PoseStack pose, float width, float height) {
		drawSlotWithBorder(pose, width, height, 0, 0, 0, null);
	}

	public static void drawRectangle(PoseStack pose, float width, float height, SDColor color) {
		drawRectangle(pose, width, height, 0, 0, 0, color);
	}

	public static void drawRectangle(PoseStack pose, float width, float height, float x, float y, float z,
			SDColor color) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(pose.last().pose(), x, y + height, z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x + width, y + height, z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x + width, y, z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x, y, z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.end();
		RenderSystem.disableBlend();
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x,
			float y) {
		drawTexture(pose, texture, width, height, x, y, 0, 0, 0, 1, 1, SDColor.WHITE);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x,
			float y, SDColor color) {
		drawTexture(pose, texture, width, height, x, y, 0, 0, 0, 1, 1, color);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float minU,
			float minV, float maxU, float maxV, SDColor color) {
		drawTexture(pose, texture, width, height, 0, 0, 0, minU, minV, maxU, maxV, color);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x,
			float y, float z, float minU, float minV, float maxU, float maxV) {
		drawTexture(pose, texture, width, height, x, y, z, minU, minV, maxU, maxV, SDColor.WHITE);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x,
			float y, float z, float minU, float minV, float maxU, float maxV, SDColor color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(pose.last().pose(), x, (y + height), z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(minU, maxV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), (y + height), z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(maxU, maxV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), y, z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(maxU, minV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x, y, z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(minU, minV).endVertex();
		tessellator.end();
	}

	public static void drawScreenOverlay(ResourceLocation texture, SDColor color, float alpha, float width,
			float height) {
		if (Minecraft.getInstance().getWindow() != null) {
			RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
			RenderSystem.setShaderTexture(0, texture);
			RenderSystem.enableBlend();

			int screenWidth = Minecraft.getInstance().getWindow().getWidth();
			int screenHeight = Minecraft.getInstance().getWindow().getHeight();

			for (int x = 0; x < Math.ceil(screenWidth / width); x++) {
				for (int y = 0; y < Math.ceil(screenHeight / height); y++) {
					Tesselator tessellator = Tesselator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuilder();
					bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
					bufferbuilder.vertex((x * width), (y * height) + height, 0)
							.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * alpha).uv(0, 1)
							.endVertex();
					bufferbuilder.vertex((x * width) + width, (y * height) + height, 0)
							.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * alpha).uv(1, 1)
							.endVertex();
					bufferbuilder.vertex((x * width) + width, (y * height), 0)
							.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * alpha).uv(1, 0)
							.endVertex();
					bufferbuilder.vertex((x * width), (y * height), 0)
							.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * alpha).uv(0, 0)
							.endVertex();
					tessellator.end();
				}
			}
		}
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height,
			float minU, float minV, float maxU, float maxV, SDColor color) {
		drawSprite(pose, spriteLocation, 0, 0, width, height, 0, minU, minV, maxU, maxV);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x,
			float y, float z, float minU, float minV, float maxU, float maxV) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, minU, minV, maxU, maxV, SDColor.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x,
			float y, float z, SDColor color) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, 0, 0, 1, 1, color);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x,
			float y, float z) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, 0, 0, 1, 1, SDColor.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height,
			SDColor color) {
		drawSprite(pose, spriteLocation, width, height, 0, 0, 0, 0, 0, 1, 1, color);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height) {
		drawSprite(pose, spriteLocation, width, height, 0, 0, 0, 0, 0, 1, 1, SDColor.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x,
			float y, float z, float minU, float minV, float maxU, float maxV, SDColor color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
				.apply(spriteLocation);

		float uDiff = sprite.getU1() - sprite.getU0();
		float vDiff = sprite.getV1() - sprite.getV0();

		float minSpriteU = sprite.getU0() + (minU * uDiff);
		float maxSpriteU = sprite.getU1() - ((1.0f - maxU) * vDiff);
		float minSpriteV = sprite.getV0() + (minV * vDiff);
		float maxSpriteV = sprite.getV1() - ((1.0f - maxV) * vDiff);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(pose.last().pose(), (x + 0), (y + height), z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(minSpriteU, maxSpriteV)
				.endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), (y + height), z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(maxSpriteU, maxSpriteV)
				.endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), (y + 0), z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(maxSpriteU, minSpriteV)
				.endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + 0), (y + 0), z)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(minSpriteU, minSpriteV)
				.endVertex();
		tessellator.end();
	}

	public static void drawString(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale,
			SDColor color, boolean withShadow) {
		drawString(matrixStack, text, xPos, yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringCentered(PoseStack matrixStack, String text, float xPos, float yPos, float zPos,
			float scale, SDColor color, boolean withShadow) {
		@SuppressWarnings("resource")
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawString(matrixStack, text, xPos + (width / 2), yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringLeftAligned(PoseStack matrixStack, String text, float xPos, float yPos, float zPos,
			float scale, SDColor color, boolean withShadow) {
		@SuppressWarnings("resource")
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawString(matrixStack, text, xPos + width, yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static List<String> wrapString(String string, float maxWidth) {
		// Draw the description.
		List<String> lines = new ArrayList<String>();
		StringBuilder currentLine = new StringBuilder();
		int currentLineWidth = 0;

		for (String word : string.split(" ")) {
			@SuppressWarnings("resource")
			int wordWidth = Minecraft.getInstance().font.width(word);
			if (wordWidth == 0 || currentLineWidth + wordWidth < maxWidth) {
				currentLine.append(word + " ");
				currentLineWidth += wordWidth + 1;
			} else {
				lines.add(currentLine.toString().trim());
				currentLine = new StringBuilder();
				currentLine.append(word + " ");
				currentLineWidth = wordWidth + 1;
			}
		}

		if (currentLine.toString().stripTrailing().length() > 0) {
			lines.add(currentLine.toString().stripTrailing());
			currentLineWidth = 0;
		}

		return lines;
	}

	public static void drawStringWithSize(PoseStack matrixStack, String text, float xPos, float yPos, float zPos,
			float scale, ChatFormatting color, boolean withShadow) {
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
	@SuppressWarnings("resource")
	public static void drawString(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale,
			int color, boolean withShadow) {
		// The matrix stack cannot be null.
		if (matrixStack == null) {
			StaticCore.LOGGER.error("A non-null matrix stack must be provided to this method!");
			return;
		}

		final float scaleFactor = scale;
		final float inverseScaleFactor = 1.0f / scaleFactor;
		matrixStack.pushPose();
		matrixStack.scale(scaleFactor, scaleFactor, 1.0f);
		matrixStack.translate(0, 0, zPos);

		RenderSystem.disableBlend();
		final int X = (int) ((xPos - Minecraft.getInstance().font.width(text) * scaleFactor) * inverseScaleFactor);
		final int Y = (int) ((yPos - 7.0f * scaleFactor) * inverseScaleFactor);
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		Minecraft.getInstance().font.drawInBatch(text, X, Y, color, withShadow, matrixStack.last().pose(), buffer, true,
				0, 15728880);
		buffer.endBatch();
		RenderSystem.enableBlend();
		matrixStack.popPose();
	}

	public static void drawAlignedLine(PoseStack pose, Vector3D start, Vector3D end, SDColor startColor,
			SDColor endColor, float thickness) {
		Vector3D line1Start = new Vector3D(start.getX(), start.getY(), start.getZ());
		Vector3D line1End = new Vector3D(start.getX(), end.getY() + 25, start.getZ());
		Vector3D line2Start = line1End;
		Vector3D line2End = new Vector3D(end.getX(), end.getY() + 25, end.getZ());
		Vector3D line3Start = line2End;
		Vector3D line3End = new Vector3D(end.getX(), end.getY(), end.getZ());

		drawLine(pose, line1Start, line1End, startColor, endColor, thickness);
		drawLine(pose, line2Start, line2End, startColor, endColor, thickness);
		drawLine(pose, line3Start, line3End, startColor, endColor, thickness);
	}

	public static void drawLine(PoseStack pose, Vector3D start, Vector3D end, SDColor startcolor, SDColor endColor,
			float thickness) {
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.lineWidth(thickness);

		Vector3D normal = end.copy().subtract(start).normalize();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
		bufferbuilder.vertex(pose.last().pose(), start.getX(), start.getY(), start.getZ())
				.color(startcolor.getRed(), startcolor.getGreen(), startcolor.getBlue(), startcolor.getAlpha())
				.normal(normal.getX(), normal.getY(), 0).endVertex();
		bufferbuilder.vertex(pose.last().pose(), end.getX(), end.getY(), end.getZ())
				.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha())
				.normal(normal.getX(), normal.getY(), 0).endVertex();
		tessellator.end();
		RenderSystem.enableCull();
	}

	public static void drawLine(PoseStack pose, Vector3D start, Vector3D end, SDColor color, float thickness) {
		drawLine(pose, start, end, color, color, thickness);
	}

	public static void drawLine(PoseStack pose, Vector3D start, Vector3D end, SDColor color) {
		drawLine(pose, start, end, color, color, 1.0f);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float alpha) {
		drawItem(pose, item, 0, 0, 0, 16, 16, alpha);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z) {
		drawItem(pose, item, x, y, z, 16, 16, 1);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float alpha) {
		drawItem(pose, item, x, y, z, 16, 16, alpha);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float width,
			float height) {
		drawItem(pose, item, x, y, z, width, height, 1);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float width,
			float height, float alpha) {
		drawItem(pose, item, x, y, z, width, height, alpha, 15728880);
	}

	public static void drawItemSilhouette(@Nullable PoseStack pose, ItemStack item, float x, float y, float z) {
		drawItem(pose, item, x, y, z, 16.0f, 16.0f, 1.0f, 0);
	}

	public static void drawItemSilhouette(@Nullable PoseStack pose, ItemStack item, float x, float y, float z,
			float width, float height) {
		drawItem(pose, item, x, y, z, width, height, 1.0f, 0);
	}

	public static void drawItemSilhouette(@Nullable PoseStack pose, ItemStack item, float x, float y, float z,
			float width, float height, float alpha) {
		drawItem(pose, item, x, y, z, width, height, alpha, 0);
	}

	@SuppressWarnings("resource")
	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float width,
			float height, float alpha, int lightLevel) {
		BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(item, (Level) null,
				Minecraft.getInstance().player, 0);
		Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();

		if (pose != null) {
			Vector3D offset = GuiDrawUtilities.translatePositionByMatrix(pose, x, y, z);
			posestack.translate(offset.getX(), offset.getY(), offset.getZ());

			// Extract the scale too.
			Vector3D scale = getScaleFromMatrix(pose);
			posestack.scale(scale.getX(), scale.getY(), 1.0F);

		} else {
			posestack.translate(x, y, z);
		}
		posestack.translate(8.0D, 8.0D, 0.0D);

		posestack.scale(1.0F, -1.0F, 1.0F);
		posestack.scale(width, height, 16.0F);
		RenderSystem.applyModelViewMatrix();

		PoseStack posestack1 = new PoseStack();
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers()
				.bufferSource();
		boolean flag = !model.usesBlockLight();
		if (flag) {
			Lighting.setupForFlatItems();
		}
		Minecraft.getInstance().getItemRenderer().render(item, ItemTransforms.TransformType.GUI, false, posestack1,
				multibuffersource$buffersource, lightLevel, OverlayTexture.NO_OVERLAY, model);

		multibuffersource$buffersource.endBatch();
		RenderSystem.enableDepthTest();
		if (flag) {
			Lighting.setupFor3DItems();
		}

		posestack.popPose();

		RenderSystem.applyModelViewMatrix();

		if (item.isBarVisible()) {
			pose.pushPose();
			pose.translate(8.0D, 8.0D, 0.0D);
			pose.scale(width / 16.0f, height/ 16.0f, 1.0F);
			pose.translate(-8.0D, -8.0D, 0.0D);
			float barWidth = item.getBarWidth();
			float barOffset = (16 - barWidth) / 2;
			int rawBarColor = item.getBarColor();
			SDColor barColor = SDColor.fromEncodedInteger(rawBarColor);
			barColor = barColor.fromEightBitToFloat();
			barColor.setAlpha(1);

			GuiDrawUtilities.drawRectangle(pose, barWidth, 2, x + barOffset, y + 13f, z + 1, SDColor.BLACK);
			GuiDrawUtilities.drawRectangle(pose, barWidth, 1, x + barOffset, y + 13f, z + 2, barColor);
			pose.popPose();
		}

		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();

		GuiDrawUtilities.drawRectangle(pose, width, height, x, y, 0, new SDColor(DEFAULT_SLOT_CORNER_COLOR.getRed(),
				DEFAULT_SLOT_CORNER_COLOR.getGreen(), DEFAULT_SLOT_CORNER_COLOR.getBlue(), 1.0f - alpha));
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
	}

	@SuppressWarnings("resource")
	public static Matrix4f drawBlockState(PoseStack pose, BlockState state, BlockPos pos, ModelData modelData,
			Vector3D translation, Vector3D rotation, Vector3D scale) {
		BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		BakedModel model = renderer.getBlockModel(state);
		ModelData data = model.getModelData(Minecraft.getInstance().level, pos, state, modelData);
		pose.pushPose();
		pose.translate(translation.getX(), translation.getY(), translation.getZ());
		pose.scale(scale.getX(), scale.getY(), scale.getZ());
		pose.translate(0.5f, 0.5f, 0.5f);
		pose.mulPose(Quaternion.fromXYZDegrees(new Vector3f(rotation.getX(), rotation.getY(), rotation.getZ())));
		pose.translate(-0.5f, -0.5f, -0.5f);

		renderer.renderSingleBlock(state, pose, buffer, 15728880, OverlayTexture.NO_OVERLAY, data, null);

		buffer.endBatch();

		Matrix4f output = pose.last().pose().copy();
		pose.popPose();
		return output;
	}

	public static float getSinFunction(float period, float amplitude) {
		@SuppressWarnings("resource")
		double sin = (Math.sin(Minecraft.getInstance().level.getGameTime() / period));
		sin = (sin + amplitude) / (amplitude + 1);
		return (float) sin;
	}

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		ResourceLocation fluidStill = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture();
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
	}

	public static SDColor getFluidColor(FluidStack fluid) {
		int encodedFluidColor = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor();
		return SDColor.fromEncodedInteger(encodedFluidColor).fromEightBitToFloat();
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

	public static Vector3D translatePositionByMatrix(PoseStack matrixStack, float xPos, float yPos, float zPos) {
		if (matrixStack != null) {
			Vector4f vector4f = new Vector4f(xPos, yPos, zPos, 1);
			vector4f.transform(matrixStack.last().pose());
			return new Vector3D(vector4f.x(), vector4f.y(), vector4f.z());
		} else {
			return new Vector3D(xPos, yPos, zPos);
		}
	}

	public static Vector3D getScaleFromMatrix(PoseStack pose) {
		Vector3D zero = GuiDrawUtilities.translatePositionByMatrix(pose, 0, 0, 0);
		Vector3D one = GuiDrawUtilities.translatePositionByMatrix(pose, 1, 1, 1);
		return one.subtract(zero.getX(), zero.getY(), zero.getZ());
	}
}
