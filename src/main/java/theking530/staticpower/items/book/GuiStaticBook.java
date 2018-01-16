package theking530.staticpower.items.book;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import theking530.staticpower.utils.GuiTextures;

public class GuiStaticBook extends GuiScreen {
	
	private int xSize;
	private int ySize;
	
	public GuiStaticBook() {
		xSize = 176;
		ySize = 205;	
	}
	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
	}
	@Override			
	protected void actionPerformed(GuiButton B) {
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

	}	
	public void drawScreen(int par1, int par2, float par3) {
	    	super.drawScreen(par1, par2, par3);
	    	int var1 = (this.width - this.xSize) / 2;
	        int var2 = (this.height - this.ySize) / 2;       
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = "Item Filter";
		//this.fontRenderer.drawString(name, this.xSize - 169, 6, 4210752 );
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.AFILTER_GUI);
		//drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
	}	
}
