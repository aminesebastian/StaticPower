package theking530.staticpower.client.rendering.tileentity;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.digistorenetwork.digistore.TileEntityDigistore;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public abstract class StaticPowerTileEntitySpecialRenderer<T extends TileEntityBase> extends TileEntityRenderer<T> {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerTileEntitySpecialRenderer.class);
	protected static final float TEXEL = (1.0f / 16.0f);
	protected ItemRenderer ItemRenderer;

	public StaticPowerTileEntitySpecialRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	/**
	 * Performs the proper rotation to ensure the rendering is rotated to follow the
	 * underlying block.
	 */
	@Override
	public void render(T tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		Direction facing = tileEntity.getFacingDirection();
		BlockPos tileEntityPos = tileEntity.getPos();
		ItemRenderer = Minecraft.getInstance().getItemRenderer();

		matrixStack.push();

		// Rotate to face the side this tile is facing.
		renderToFaceSide(facing, matrixStack);

		// Draw the tile entity.
		try {
			// Update the rendering values for the components.
			for (AbstractTileEntityComponent comp : tileEntity.getComponents()) {
				comp.updateBeforeRendering(partialTicks);
			}

			// Render the tile entity.
			renderTileEntityBase(tileEntity, tileEntityPos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		} catch (Exception e) {
			LOGGER.error(String.format("An error occured when attempting to draw tile entity base: %1$s.", tileEntity), e);
		}

		matrixStack.pop();
		matrixStack.pop();
		RenderHelper.setupLevelDiffuseLighting(matrixStack.getLast().getMatrix());
		matrixStack.push();
	}

	/**
	 * This method is where the main drawing should occur.
	 * 
	 * @param tileEntity      The {@link TileEntity} being drawn.
	 * @param pos             The position of the {@link TileEntity} .
	 * @param partialTicks    The partial ticks.
	 * @param matrixStack     The matrix stack to draw with.
	 * @param buffer          The buffer type.
	 * @param combinedLight   The combined light at the block this
	 *                        {@link TileEntity} is rendering at.
	 * @param combinedOverlay The combined overlay.
	 */
	protected abstract void renderTileEntityBase(T tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay);

	/**
	 * Renders the provided {@link ItemStack} in the world.
	 * 
	 * @param item            The {@link ItemStack} to render.
	 * @param offset          The offset to apply in world space.
	 * @param partialTicks    The partial ticks.
	 * @param matrixStack     The matrix stack to use for transformations.
	 * @param buffer          The buffer type.
	 * @param combinedLight   The combined light at the block this
	 *                        {@link TileEntity} is rendering at.
	 * @param combinedOverlay The combined overlay.
	 */
	protected void drawItemInWorld(T tileEntity, ItemStack item, TransformType transformType, Vector3D offset, Vector3D scale, float partialTicks, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
		IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item, tileEntity.getWorld(), null);
		ItemRenderer.renderItem(item, transformType, false, matrixStack, buffer, combinedLight, combinedOverlay, itemModel);
		matrixStack.pop();
	}

	protected void drawFlatItemInWorld(TileEntityDigistore tile, @Nonnull ItemStack itemStack, Vector3D offset, Vector2D scale, float partialTickTime, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// Skip empty items.
		if (itemStack.isEmpty()) {
			return;
		}

		// Move and scale the item given the provided values.
		matrixStack.push();
		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), 0.01f);

		// Get the baked model and check if it wants to render the item in 3d or 2d.
		IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(itemStack, null, null);
		boolean render3D = itemModel.isGui3d();

		// Thank the lord for Storage Drawers
		// (https://github.com/jaquadro/StorageDrawers/blob/1.15/src/main/java/com/jaquadro/minecraft/storagedrawers/client/renderer/TileEntityDrawersRenderer.java).
		// Spent hours on the bug that's caused when you dont have this called twice.
		if (buffer instanceof IRenderTypeBuffer.Impl) {
			((IRenderTypeBuffer.Impl) buffer).finish();
		}

		// Set the correct lighting values.
		if (render3D) {
			RenderHelper.setupGui3DDiffuseLighting();
		} else {
			RenderHelper.setupGuiFlatDiffuseLighting();
		}

		// Invert the normals of the model on the Y-Axis.
		matrixStack.getLast().getNormal().set(Matrix3f.makeScaleMatrix(1, -1, 1));

		// Render the item.
		Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.GUI, false, matrixStack, buffer, combinedLight, combinedOverlay, itemModel);

		// Finish the buffer again (thanks again to Storage Drawers).
		if (buffer instanceof IRenderTypeBuffer.Impl) {
			((IRenderTypeBuffer.Impl) buffer).finish();
		}

		// Pop the matrix we pushed.
		matrixStack.pop();
	}

	/**
	 * Renders a string in the world.
	 * 
	 * @param text            The text to render.
	 * @param tileEntity
	 * @param color
	 * @param offset
	 * @param scale
	 * @param partialTicks
	 * @param matrixStack
	 * @param buffer
	 * @param combinedLight
	 * @param combinedOverlay
	 */
	protected void drawTextInWorld(String text, T tileEntity, Color color, Vector3D offset, float scale, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer,
			int combinedLight, int combinedOverlay) {
		if (text == null || text.isEmpty())
			return;

		FontRenderer fontRenderer = this.renderDispatcher.getFontRenderer();
		int textWidth = fontRenderer.getStringWidth(text);

		matrixStack.push();
		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.rotate(new Quaternion(new Vector3f(0, 1, 0), 180, true));
		matrixStack.rotate(new Quaternion(new Vector3f(0, 0, 1), 180, true));
		matrixStack.scale(scale, scale, scale);

		fontRenderer.renderString(text, -textWidth / 2f, 0.5f, color.encodeInInteger(), false, matrixStack.getLast().getMatrix(), buffer, false, 0, 15728880); // 15728880

		matrixStack.pop();
	}

	/**
	 * Draws a texture quad at the face of the block.
	 * 
	 * @param texture       The sprite to draw.
	 * @param matrixStack   The matrix stack to work with.
	 * @param buffer        The buffer to draw to.
	 * @param offset        The offset from the face of the block.
	 * @param scale         The scale.
	 * @param tint          The tint to apply.
	 * @param combinedLight The combined light level at the block.
	 */
	protected void drawTexturedQuadLit(ResourceLocation texture, MatrixStack matrixStack, IRenderTypeBuffer buffer, Vector3D offset, Vector3D scale, Vector4D uv, Color tint,
			int combinedLight) {
		matrixStack.push();
		IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());
		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(texture);

		float uDelta = sprite.getMaxU() - sprite.getMinU();
		float vDelta = sprite.getMaxV() - sprite.getMinV();

		matrixStack.translate(offset.getX(), offset.getY(), offset.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());
		builder.pos(matrixStack.getLast().getMatrix(), 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.tex(sprite.getMinU() + (uDelta) * uv.getZ(), sprite.getMinV() + (vDelta) * uv.getY()).lightmap(combinedLight).normal(1, 0, 0).endVertex();
		builder.pos(matrixStack.getLast().getMatrix(), 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.tex(sprite.getMinU() + (uDelta) * uv.getZ(), sprite.getMinV() + (vDelta) * uv.getW()).lightmap(combinedLight).normal(1, 0, 0).endVertex();
		builder.pos(matrixStack.getLast().getMatrix(), 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.tex(sprite.getMinU() + (uDelta) * uv.getX(), sprite.getMinV() + (vDelta) * uv.getW()).lightmap(combinedLight).normal(1, 0, 0).endVertex();
		builder.pos(matrixStack.getLast().getMatrix(), 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha())
				.tex(sprite.getMinU() + (uDelta) * uv.getX(), sprite.getMinV() + (vDelta) * uv.getY()).lightmap(combinedLight).normal(1, 0, 0).endVertex();
		matrixStack.pop();
	}

	protected void drawTexturedQuadUnlit(ResourceLocation texture, MatrixStack matrixStack, IRenderTypeBuffer buffer, Vector3D offset, Vector3D scale, Vector4D uv, Color tint) {
		drawTexturedQuadLit(texture, matrixStack, buffer, offset, scale, uv, tint, 15728880);
	}

	protected int getForwardFacingLightLevel(T tileEntity) {
		return WorldRenderer.getCombinedLight(tileEntity.getWorld(), tileEntity.getPos().offset(tileEntity.getFacingDirection()));
	}

	protected void drawFluidQuadLit(FluidStack fluid, MatrixStack matrixStack, IRenderTypeBuffer buffer, Vector3D offset, Vector3D scale, Vector4D uv, int lightlevel) {
		Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);
		TextureAtlasSprite icon = GuiDrawUtilities.getStillFluidSprite(fluid);
		drawTexturedQuadLit(icon.getName(), matrixStack, buffer, offset, scale, uv, fluidColor, lightlevel);
	}

	protected void drawFluidQuadUnlit(FluidStack fluid, MatrixStack matrixStack, IRenderTypeBuffer buffer, Vector3D offset, Vector3D scale, Vector4D uv) {
		drawFluidQuadLit(fluid, matrixStack, buffer, offset, scale, uv, 15728880);
	}

	/**
	 * Rotates the last matrix on the provided stack to face the provided direction.
	 * 
	 * @param side
	 * @param matrixStack
	 */
	protected void renderToFaceSide(Direction side, MatrixStack matrixStack) {
		if (side == Direction.WEST) {
			matrixStack.rotate(new Quaternion(new Vector3f(0, 1, 0), -90, true));
			matrixStack.translate(0, 0, -1);
		} else if (side == Direction.NORTH) {
			matrixStack.rotate(new Quaternion(new Vector3f(0, 1, 0), 180, true));
			matrixStack.translate(-1, 0, -1);
		} else if (side == Direction.EAST) {
			matrixStack.rotate(new Quaternion(new Vector3f(0, 1, 0), 90, true));
			matrixStack.translate(-1, 0, 0);
		}
	}
}
