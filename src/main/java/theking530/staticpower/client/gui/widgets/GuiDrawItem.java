package theking530.staticpower.client.gui.widgets;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public class GuiDrawItem {
	
	public static RenderItem render = Minecraft.getMinecraft().getRenderItem();
	
	public static void drawItem(Item item, int guiLeft, int guiTop, int x, int y, float zLevel) {
        renderItemModelIntoGUI(new ItemStack(item), guiLeft+x, guiTop+y, zLevel, render.getItemModelWithOverrides(new ItemStack(item), (World)null, Minecraft.getMinecraft().thePlayer));
	}
    private static void setupGuiTransform(int xPosition, int yPosition, float zLevel, boolean isGui3d) {
        GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);

        if (isGui3d) {
            GlStateManager.enableLighting();
        }else {
            GlStateManager.disableLighting();
        }
    }
    protected static void renderItemModelIntoGUI(ItemStack stack, int x, int y, float zLevel, IBakedModel bakedmodel) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        setupGuiTransform(x, y, zLevel, bakedmodel.isGui3d());
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        GL11.glColor4f(1, 1, 1, 0.5F);
        render.renderItem(stack, bakedmodel);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }
}
