package theking530.staticpower.machines.batteries;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.tabs.GuiPowerControlTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;
import theking530.staticpower.utils.GuiTextures;

public class GuiBattery extends CustomGuiContainer {
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	public GuiPowerControlTab POWER_TAB;
	public GuiSideConfigTab SIDE_TAB;
	public GuiRedstoneTab REDSTONE_TAB;
	
	private TileEntityBattery sBattery;
	public GuiBattery(InventoryPlayer invPlayer, TileEntityBattery teSBattery) {
		super(new ContainerBattery(invPlayer, teSBattery));
		sBattery = teSBattery;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teSBattery);
		REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop, teSBattery);
		POWER_TAB = new GuiPowerControlTab(guiLeft, guiTop);
		SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, teSBattery);
		this.xSize = 176;
		this.ySize = 166;
	}	
	
	public void updateScreen() {
		POWER_TAB.updateTab(width, height, xSize, ySize, fontRenderer, sBattery);
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRenderer, sBattery);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRenderer, sBattery);
		if(POWER_TAB.GROWTH_STATE == 1){
			REDSTONE_TAB.RED_TAB.GROWTH_STATE = 2;
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}
		if(REDSTONE_TAB.GROWTH_STATE == 1) {
			SIDE_TAB.BLUE_TAB.GROWTH_STATE = 2;
		}	
	}
	//Draw Main
	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		guiButtons(j, k);
		POWER_TAB.fieldInit();
	}
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		//System.out.println("HI");
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String INPUT = (String.valueOf(sBattery.STORAGE.getMaxReceive()) + " RF/t");
		String OUTPUT = (String.valueOf(sBattery.STORAGE.getMaxExtract()) + " RF/t");
		
		String current_input = (String.valueOf(sBattery.CURRENT_RF_TICK) + " RF/t");

		String NAME = I18n.format(this.sBattery.getName());
		this.fontRenderer.drawString("Input", this.xSize/2-this.fontRenderer.getStringWidth("Input")/2 - 35, 35, 4210752);
		this.fontRenderer.drawString(INPUT, this.xSize/2-this.fontRenderer.getStringWidth(INPUT)/2 - 35, 45, 4210752);
		this.fontRenderer.drawString("Output", this.xSize/2-this.fontRenderer.getStringWidth("Output")/2 + 35, 35, 4210752);
		this.fontRenderer.drawString(OUTPUT, this.xSize/2-this.fontRenderer.getStringWidth(OUTPUT)/2 + 35, 45, 4210752);
		this.fontRenderer.drawString(NAME, this.xSize / 2 - this.fontRenderer.getStringWidth(NAME) / 2, 6, 4210752);
		
		this.fontRenderer.drawString(current_input, this.xSize/2-this.fontRenderer.getStringWidth(INPUT)/2 - 40, 25, 4210752);
		
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GuiTextures.BATTERY_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		POWER_BAR.drawPowerBar(guiLeft + 80, guiTop + 71, 16, 51, 1, f);
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		POWER_TAB.drawTab();	
	}
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		
		this.zLevel = -1.0f;
		this.drawDefaultBackground();	
		this.zLevel = 0.0f;
		
		this.zLevel = -1.0f;
		this.drawDefaultBackground();
		this.zLevel = 0.0f;
		int var1 = (this.width - this.xSize) / 2;
		int var2 = (this.height - this.ySize) / 2;
		if (par1 >= 80 + var1 && par2 >= 20 + var2 && par1 <= 96 + var1
				&& par2 <= 71 + var2) {
			int j1 = sBattery.STORAGE.getEnergyStored();
			int k1 = sBattery.STORAGE.getMaxEnergyStored();
			String[] text = { NumberFormat.getNumberInstance(Locale.US).format(
					j1)
					+ " "
					+ "RF/"
					+ NumberFormat.getNumberInstance(Locale.US).format(k1)
					+ " " + "RF" };
			List<String> temp = Arrays.asList(text);
			drawHoveringText(temp, par1, par2, fontRenderer);
		}
		this.renderHoveredToolTip(par1, par2);
	}
    //Draw Misc	
	public void guiButtons(int j, int k) {
		this.buttonList.add(new GuiButton(1, j + 8, k + 25, 18, 18, "+"));
		this.buttonList.add(new GuiButton(2, j + 8, k + 45, 18, 18, "-"));
		this.buttonList.add(new GuiButton(3, j + 151, k + 25, 18, 18, "+"));
		this.buttonList.add(new GuiButton(4, j + 151, k + 45, 18, 18, "-"));
	}			
	
	//Interaction
	@Override			
	protected void actionPerformed(GuiButton B) {	
		
		switch(B.id) {
		case 1:
			if (isShiftKeyDown() && sBattery.STORAGE.getMaxReceive() + 100 <= sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()+100);
			} else if (isShiftKeyDown() && sBattery.STORAGE.getMaxReceive() + 100 > sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.MAX_INPUT);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxReceive() + 1 <= sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()+1);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxReceive() + 1 > sBattery.MAX_INPUT) {
				//INPUT_PER_TICK = sBattery.MAX_INPUT;
			} else if (sBattery.STORAGE.getMaxReceive() + 50 <= sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()+50);
			} else if (sBattery.STORAGE.getMaxReceive() + 50 > sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.MAX_INPUT);
			}
			break;
		case 2 :
			if (isShiftKeyDown() && sBattery.STORAGE.getMaxReceive() - 100 >= 0) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()-100);
			} else if (isShiftKeyDown() && sBattery.STORAGE.getMaxReceive() - 100 < 0) {
				sBattery.STORAGE.setMaxReceive(0);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxReceive() - 1 >= 0) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()-1);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxReceive() - 1 < 0) {
				sBattery.STORAGE.setMaxReceive(0);
			} else if (sBattery.STORAGE.getMaxReceive() - 50 >= 0) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()-50);
			} else if (sBattery.STORAGE.getMaxReceive() - 50 < 0) {
				sBattery.STORAGE.setMaxReceive(0);
			}
			break;
		case 3 :
			if (isShiftKeyDown() && sBattery.STORAGE.getMaxExtract() + 50 <= sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()+50);
			} else if (isShiftKeyDown() && sBattery.STORAGE.getMaxExtract() + 50 > sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.MAX_OUTPUT);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxExtract() + 1 <= sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()+1);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxExtract() + 1 > sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.MAX_OUTPUT);
			} else if (sBattery.STORAGE.getMaxExtract() + 5 <= sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()+5);
			} else if (sBattery.STORAGE.getMaxExtract() + 5 > sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.MAX_OUTPUT);
			}
			break;
		case 4 :
			if (isShiftKeyDown() && sBattery.STORAGE.getMaxExtract() - 50 >= 0) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()-50);
			} else if (isShiftKeyDown() && sBattery.STORAGE.getMaxExtract() - 50 < 0) {
				sBattery.STORAGE.setMaxExtract(0);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxExtract() - 1 >= 0) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()-1);
			} else if (isCtrlKeyDown() && sBattery.STORAGE.getMaxExtract() - 1 < 0) {
				sBattery.STORAGE.setMaxExtract(0);
			} else if (sBattery.STORAGE.getMaxExtract() - 5 >= 0) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()-5);
			} else if (sBattery.STORAGE.getMaxExtract() - 5 < 0) {
				sBattery.STORAGE.setMaxExtract(0);
			}
			break;
		}
		if(!sBattery.getWorld().isRemote) {
			sBattery.updateBlock();		
		}

	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException{
	    super.mouseClicked(x, y, button);
	    POWER_TAB.mouseInteraction(x, y, button);
	    REDSTONE_TAB.mouseInteraction(x, y, button);
	    SIDE_TAB.mouseInteraction(x, y, button);
	}	
	protected void mouseClickMove(int x, int y, int button, long time) {
		super.mouseClickMove(x, y, button, time);
		SIDE_TAB.mouseDrag(x, y, button, time);
	}
	protected void keyTyped(char par1, int par2) throws IOException {
        super.keyTyped(par1, par2);
        POWER_TAB.keyboardInteraction(par1, par2);
    }
}
