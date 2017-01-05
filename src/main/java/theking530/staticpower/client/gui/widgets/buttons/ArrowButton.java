package theking530.staticpower.client.gui.widgets.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;

public class ArrowButton extends GuiButton{

    protected static final ResourceLocation ARROW_TEXTURES = new ResourceLocation(Reference.MODID + ":" + "textures/gui/ArrowButtons.png");
	
	public ArrowButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);

	}
	
	@Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(ARROW_TEXTURES);

            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            if(i == 1) {
                GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
            }else if(i == 2) {
                GlStateManager.color(0.8F, 0.8F, 1.0F, 1.0F);
            }else{
                GlStateManager.color(0.25F, 0.25F, 0.25F, 1.0F);
            }
            
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            double vMin = 0.0;
            double vMax = 0.5;
            
            if(displayString == ">") {
            	vMin = 0.5;
                vMax = 1.0;
            }
            
            float f = 0.00390625F;
            float f1 = 0.00390625F;
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(xPosition, yPosition + height, (double)this.zLevel).tex(0, vMax).endVertex();
            vertexbuffer.pos(xPosition + width, yPosition + height, (double)this.zLevel).tex(1, vMax).endVertex();
            vertexbuffer.pos(xPosition + width, yPosition, (double)this.zLevel).tex(1, vMin).endVertex();
            vertexbuffer.pos(xPosition, yPosition, (double)this.zLevel).tex(0, vMin).endVertex();
            tessellator.draw();
            
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0) {
                j = packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }
            
            //drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, j);

        }
    }
}
