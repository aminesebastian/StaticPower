package theking530.staticpower.client.gui.widgets;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GuiDrawItem {
	
	public static RenderItem render = Minecraft.getMinecraft().getRenderItem();
	public static Vec3d COLOR;
	public static boolean SHOULD_DRAW = true;
	
	public GuiDrawItem(boolean ShouldDraw) {
		SHOULD_DRAW = ShouldDraw;
	}
	
	public void setShouldDraw(boolean ShouldDraw) {
		SHOULD_DRAW = ShouldDraw;
	}
	public static void drawItem(Item item, int guiLeft, int guiTop, int x, int y, float zLevel) {
        if(SHOULD_DRAW) {
		renderItemModelIntoGUI(new ItemStack(item), guiLeft+x, guiTop+y, zLevel, render.getItemModelWithOverrides(new ItemStack(item), (World)null, Minecraft.getMinecraft().thePlayer));
        }
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
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


       // System.out.println(Minecraft.getMinecraft().currentScreen.height);
        setupGuiTransform(x, y, zLevel, bakedmodel.isGui3d());
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);        
        GL11.glColor4d(1.0, 1.0, 1.0, 0.5);
        render.renderItem(stack, bakedmodel);
        

        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }
}
