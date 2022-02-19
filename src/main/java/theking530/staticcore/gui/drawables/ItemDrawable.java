package theking530.staticcore.gui.drawables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class ItemDrawable implements IDrawable {
	private ItemStack itemStack;
	private final Vector2D size;

	public ItemDrawable() {
		this(ItemStack.EMPTY, new Vector2D(1, 1));
	}

	public ItemDrawable(@Nonnull ItemLike item) {
		this(item, new Vector2D(1, 1));
	}

	public ItemDrawable(@Nonnull ItemStack stack) {
		this(stack, new Vector2D(1, 1));
	}

	public ItemDrawable(@Nonnull ItemLike item, Vector2D size) {
		itemStack = new ItemStack(item);
		this.size = size;
	}

	public ItemDrawable(@Nonnull ItemStack stack, Vector2D size) {
		itemStack = stack.copy();
		this.size = size;
	}

	@Override
	public void draw(@Nullable PoseStack stack, float x, float y, float z) {
		if (itemStack != null && !itemStack.isEmpty()) {
			// We add +10 here to account for the depth of the item clipping through its background.
			GuiDrawUtilities.drawItem(stack, itemStack, x, y, z + 10, size.getX(), size.getY());
		}
	}

	@Deprecated()
	@SuppressWarnings("resource")
	protected void renderGuiItem(@Nullable PoseStack postSackIn, ItemStack item, float x, float y, float z) {
		BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(item, (Level) null, Minecraft.getInstance().player, 0);
		Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate(8.0D, 8.0D, 0.0D);

		if (postSackIn != null) {
			Vector2D offset = GuiDrawUtilities.translatePositionByMatrix(postSackIn, x, y);
			posestack.translate(offset.getX(), offset.getY(), z);
		} else {
			posestack.translate(x, y, z);
		}

		posestack.scale(1.0F, -1.0F, 1.0F);
		posestack.scale(16.0F * size.getX(), 16.0F * size.getY(), 16.0F);
		RenderSystem.applyModelViewMatrix();

		PoseStack posestack1 = new PoseStack();
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		boolean flag = !model.usesBlockLight();
		if (flag) {
			Lighting.setupForFlatItems();
		}

		Minecraft.getInstance().getItemRenderer().render(item, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, model);
		multibuffersource$buffersource.endBatch();
		RenderSystem.enableDepthTest();
		if (flag) {
			Lighting.setupFor3DItems();
		}

		posestack.popPose();

		RenderSystem.applyModelViewMatrix();
	}

	public ItemDrawable setItemStack(ItemStack stack) {
		this.itemStack = stack;
		return this;
	}

	@Override
	public void setSize(float x, float y) {
		size.setX(x);
		size.setY(y);
	}

	@Override
	public Vector2D getSize() {
		return size;
	}
}
