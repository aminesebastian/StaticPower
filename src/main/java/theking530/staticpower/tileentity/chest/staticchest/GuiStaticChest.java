package theking530.staticpower.tileentity.chest.staticchest;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.GuiTextures;

public class GuiStaticChest extends GuiContainer{
	
	private TileEntityStaticChest staticChest;
	public GuiStaticChest(InventoryPlayer invPlayer, TileEntityStaticChest teStaticChest) {
		super(new ContainerStaticChest(invPlayer, teStaticChest));
		staticChest = teStaticChest;		
		this.xSize = 176;
		this.ySize = 205;		
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.staticChest.getName());
		this.fontRenderer.drawString(name, this.xSize - 169, 6, 4210752 );
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.SCHEST_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}


