package theking530.staticpower.tileentity.chest.energizedchest;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.utils.GuiTextures;

public class GuiEnergizedChest extends GuiContainer{
	
	private TileEntityEnergizedChest ENERGIZED_CHEST;
	public GuiEnergizedChest(InventoryPlayer invPlayer, TileEntityEnergizedChest teEnergizedChest) {
		super(new ContainerEnergizedChest(invPlayer, teEnergizedChest));
		ENERGIZED_CHEST = teEnergizedChest;		
		this.xSize = 176;
		this.ySize = 253;		
	}
	public void drawScreen(int par1, int par2, float par3) {
	    	super.drawScreen(par1, par2, par3);
	    	int var1 = (this.width - this.xSize) / 2;
	        int var2 = (this.height - this.ySize) / 2;       
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.ENERGIZED_CHEST.getName());
		this.fontRendererObj.drawString(name, this.xSize - 169, 7, 4210752 );
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.ECHEST_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}


