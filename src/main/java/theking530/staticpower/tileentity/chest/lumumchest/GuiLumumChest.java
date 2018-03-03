package theking530.staticpower.tileentity.chest.lumumchest;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.GuiTextures;

public class GuiLumumChest extends GuiContainer{
	
	private TileEntityLumumChest LUMUM_CHEST;
	public GuiLumumChest(InventoryPlayer invPlayer, TileEntityLumumChest teLumumChest) {
		super(new ContainerLumumChest(invPlayer, teLumumChest));
		LUMUM_CHEST = teLumumChest;		
		this.xSize = 230;
		this.ySize = 254;		
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name =  I18n.format(this.LUMUM_CHEST.getName());
		this.fontRenderer.drawString(name, this.xSize - 222, 7, 4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.LCHEST_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}


