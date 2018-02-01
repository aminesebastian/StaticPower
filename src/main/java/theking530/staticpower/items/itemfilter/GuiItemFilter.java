package theking530.staticpower.items.itemfilter;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.utilities.GuiTextures;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.handlers.PacketHandler;

public class GuiItemFilter extends BaseGuiContainer {
	
	public GuiInfoTab INFO_TAB;
	private InventoryItemFilter INV_FILTER;
	private FilterTier TIER;
	
	public GuiItemFilter(InventoryPlayer invPlayer, FilterTier tier, InventoryItemFilter invFilter) {
		super(new ContainerItemFilter(invPlayer, invFilter, tier), 176, 205);
		INV_FILTER = invFilter;
		TIER = tier;
		
		INFO_TAB = new GuiInfoTab(100, 100);
		getTabManager().registerTab(INFO_TAB);
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
			IMessage msg = new PacketItemFilter(!tempMode, true, true, true);
			PacketHandler.net.sendToServer(msg);
			String mode = !tempMode == true ? "Whitelist" : "Blacklist";
			B.displayString = mode;
			INV_FILTER.markDirty();
		}
	}
	public void updateScreen() {
;
	}	
	public void drawScreen(int par1, int par2, float par3) {
	    super.drawScreen(par1, par2, par3);
     
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
		this.fontRenderer.drawString(name, this.xSize - 169, 6, 4210752 );
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
		
    	String text = ("Filter items going into an inventory.");
    		String[] splitMsg = text.split("=");
		
		INFO_TAB.setText(INV_FILTER.ITEMSTACK.getDisplayName(), Arrays.asList(splitMsg));
	}
}



