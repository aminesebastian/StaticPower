package theking530.staticpower.items.itemfilter;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.client.gui.widgets.GuiInfoTab;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.utils.GuiTextures;

public class GuiItemFilter extends GuiContainer{
	
	public GuiInfoTab INFO_TAB = new GuiInfoTab(guiLeft, guiTop);
	private InventoryItemFilter INV_FILTER;
	private FilterTier TIER;
	
	public GuiItemFilter(InventoryPlayer invPlayer, FilterTier tier, InventoryItemFilter invFilter) {
		super(new ContainerItemFilter(invPlayer, invFilter, tier));
		this.xSize = 176;
		this.ySize = 205;	
		INV_FILTER = invFilter;
		TIER = tier;
	}
	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String mode = INV_FILTER.getWhiteListMode() == true ? "Whitelist" : "Blacklist";
		this.buttonList.add(new GuiButton(1, j + 30, k + 40, 55, 18, mode));
	}
	@Override			
	protected void actionPerformed(GuiButton B) {
		if(B.id == 1) {
			boolean tempMode = INV_FILTER.getWhiteListMode();
			INV_FILTER.setWhiteListMode(!tempMode);
			IMessage msg = new PacketItemFilter(INV_FILTER, !tempMode);
			PacketHandler.net.sendToServer(msg);
			String mode = !tempMode == true ? "Whitelist" : "Blacklist";
			B.displayString = mode;
			INV_FILTER.markDirty();
		}
	}
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		//INFO_TAB.updateTab(width, height, xSize, ySize, fontRendererObj);
	}	
	public void drawScreen(int par1, int par2, float par3) {
	    	super.drawScreen(par1, par2, par3);
	    	int var1 = (this.width - this.xSize) / 2;
	        int var2 = (this.height - this.ySize) / 2;       
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = "Item Filter";
		switch(TIER) {
		case BASIC:
			name = "Basic Item Filter";
			break;
		case UPGRADED:
			name = "Upgraded Item Filter";
			break;
		case ADVANCED:
			name = "Advanced Item Filter";
			break;
		}
		this.fontRendererObj.drawString(name, this.xSize - 169, 6, 4210752 );
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		switch(TIER) {
			case BASIC:
				Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BFILTER_GUI);
				break;
			case UPGRADED:
				Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.UFILTER_GUI);
				break;
			case ADVANCED:
				Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.AFILTER_GUI);
				break;
		}

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
    	String text = ("Filter ites going into an inventory.");
    	String[] splitMsg = text.split("=");
		
		//INFO_TAB.drawTab(Arrays.asList(splitMsg));
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    INFO_TAB.mouseInteraction(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
	}	
	
}



