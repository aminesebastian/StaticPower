package theking530.staticpower.tileentity.chest.staticchest;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.utils.GuiTextures;

public class GuiStaticChest extends GuiContainer{
	
	private TileEntityStaticChest staticChest;
	public GuiStaticChest(InventoryPlayer invPlayer, TileEntityStaticChest teStaticChest) {
		super(new ContainerStaticChest(invPlayer, teStaticChest));
		staticChest = teStaticChest;		
		this.xSize = 176;
		this.ySize = 205;		
	}
	public void drawScreen(int par1, int par2, float par3) {
	    	super.drawScreen(par1, par2, par3);
	    	int var1 = (this.width - this.xSize) / 2;
	        int var2 = (this.height - this.ySize) / 2;       
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.staticChest.getName());
		this.fontRendererObj.drawString(name, this.xSize - 169, 6, 4210752 );
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.SCHEST_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}


