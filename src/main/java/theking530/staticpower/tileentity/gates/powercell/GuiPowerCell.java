package theking530.staticpower.tileentity.gates.powercell;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.utils.GuiTextures;

public class GuiPowerCell extends GuiContainer{
	
	private TileEntityPowerCell P_CELL;
	
	public GuiPowerCell(InventoryPlayer invPlayer, TileEntityPowerCell tePowerCell) {
		super(new ContainerPowerCell(invPlayer, tePowerCell));
		P_CELL = tePowerCell;		
		this.xSize = 138;
		this.ySize = 86;		
	}
	public void drawScreen(int par1, int par2, float par3) {
    	super.drawScreen(par1, par2, par3);
    	int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;       
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.P_CELL.getName());
		String info = "Power: " + P_CELL.POWER;
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6,4210752 );
		this.fontRendererObj.drawString(info, this.xSize / 2 - this.fontRendererObj.getStringWidth(info)/2 - 1, 57, 4210752 );
	}
	@Override
	public void initGui() {
		super.initGui();
	 	int j = (width - xSize) / 2;
	    int k = (height - ySize) / 2;

	    this.buttonList.add(new GuiButton( 1, j+96, k+30, 18, 18, "+"));
	    this.buttonList.add(new GuiButton( 2, j+24, k+30, 18, 18, "-"));

	    this.buttonList.add(new GuiButton( 5, j+46, k+30, 46, 18, "Confirm"));
	}
	public boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	@Override
	protected void actionPerformed(GuiButton B) {
		int POWER = P_CELL.POWER;
		 if(B.id == 1) {
			 if(P_CELL.POWER < 15) {
				 if(isShiftDown() && P_CELL.POWER + 5 <= 15){
					 P_CELL.POWER += 5;
				 }else if(isShiftDown() && P_CELL.POWER + 5 > 15){
					 P_CELL.POWER = 15;
				 }else{
					 P_CELL.POWER++;
				 }
			 }
	 		}	
		 if(B.id == 2) {
			 if(P_CELL.POWER > 0) {
				 if(isShiftDown() && P_CELL.POWER - 5 >= 0){
					 P_CELL.POWER -= 5;
				 }else if(isShiftDown() && P_CELL.POWER - 5 < 0){
					 P_CELL.POWER = 0;
				 }else{
					 P_CELL.POWER--;
				 }
		 	}	
		 }
		 if(B.id == 5) {
			IMessage msg = new PacketPowerCell(POWER, P_CELL.getPos());
			PacketHandler.net.sendToServer(msg);
		 }
	}	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.MULTIPLIER_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}


