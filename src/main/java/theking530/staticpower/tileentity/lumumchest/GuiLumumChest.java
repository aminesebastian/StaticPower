package theking530.staticpower.tileentity.lumumchest;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.utils.GuiTextures;

public class GuiLumumChest extends GuiContainer{
	
	private TileEntityLumumChest LUMUM_CHEST;
	public GuiLumumChest(InventoryPlayer invPlayer, TileEntityLumumChest teLumumChest) {
		super(new ContainerLumumChest(invPlayer, teLumumChest));
		LUMUM_CHEST = teLumumChest;		
		this.xSize = 266;
		this.ySize = 290;		
	}
	public void drawScreen(int par1, int par2, float par3) {
	    	super.drawScreen(par1, par2, par3);
	    	int var1 = (this.width - this.xSize) / 2;
	        int var2 = (this.height - this.ySize) / 2;       
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.LUMUM_CHEST.hasCustomName() ? this.LUMUM_CHEST.getName() : I18n.format(this.LUMUM_CHEST.getName());
		this.fontRendererObj.drawString(name, this.xSize - 165, 10, 4210752 );
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		float scale = 300f/256f;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.LCHEST_GUI);
		GL11.glScalef(scale, scale, scale);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize-20, ySize-43);
		GL11.glScalef(1/scale, 1/scale, 1/scale);
		}
	}


