package theking530.staticpower.machines.batteries;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.CustomGuiContainer;
import theking530.staticpower.client.gui.widgets.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.widgets.GuiPowerControlTab;
import theking530.staticpower.client.gui.widgets.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.GuiSideConfigTab;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;
import theking530.staticpower.utils.GuiTextures;

public class GuiBattery extends CustomGuiContainer {
	
	public GuiPowerBarFromEnergyStorage POWER_BAR;
	public GuiPowerControlTab POWER_TAB = new GuiPowerControlTab(guiLeft, guiTop);
	public GuiSideConfigTab SIDE_TAB = new GuiSideConfigTab(guiLeft, guiTop, ModBlocks.StaticBattery);
	public GuiRedstoneTab REDSTONE_TAB = new GuiRedstoneTab(guiLeft, guiTop);
	
	private TileEntityBattery sBattery;
	public GuiBattery(InventoryPlayer invPlayer, TileEntityBattery teSBattery) {
		super(new ContainerBattery(invPlayer, teSBattery));
		sBattery = teSBattery;
		POWER_BAR = new GuiPowerBarFromEnergyStorage(teSBattery);
		this.xSize = 176;
		this.ySize = 166;
	}	
	
	public void updateScreen() {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		POWER_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, sBattery);
		SIDE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, sBattery);
		REDSTONE_TAB.updateTab(width, height, xSize, ySize, fontRendererObj, sBattery);
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
		String NAME = I18n.format(this.sBattery.getName());
		this.fontRendererObj.drawString("Input", this.xSize/2-this.fontRendererObj.getStringWidth("Input")/2 - 35, 35, 4210752);
		this.fontRendererObj.drawString(INPUT, this.xSize/2-this.fontRendererObj.getStringWidth(INPUT)/2 - 35, 45, 4210752);
		this.fontRendererObj.drawString("Output", this.xSize/2-this.fontRendererObj.getStringWidth("Output")/2 + 35, 35, 4210752);
		this.fontRendererObj.drawString(OUTPUT, this.xSize/2-this.fontRendererObj.getStringWidth(OUTPUT)/2 + 35, 45, 4210752);
		this.fontRendererObj.drawString(NAME, this.xSize / 2 - this.fontRendererObj.getStringWidth(NAME) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int w = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GuiTextures.BATTERY_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		POWER_BAR.drawPowerBar(guiLeft + 80, guiTop + 71, 16, 51, 1);
		SIDE_TAB.drawTab();		
		REDSTONE_TAB.drawTab();
		POWER_TAB.drawTab();	
	}
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
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
			List temp = Arrays.asList(text);
			drawHoveringText(temp, par1, par2, fontRendererObj);
		}
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
		int INPUT_PER_TICK = sBattery.STORAGE.getMaxReceive();
		int OUTPUT_PER_TICK = sBattery.STORAGE.getMaxExtract();
		
		switch(B.id) {
		case 1:
			if (isShiftKeyDown() && INPUT_PER_TICK + 100 <= sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()+100);
			} else if (isShiftKeyDown() && INPUT_PER_TICK + 100 > sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.MAX_INPUT);
			} else if (isCtrlKeyDown() && INPUT_PER_TICK + 1 <= sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()+1);
			} else if (isCtrlKeyDown() && INPUT_PER_TICK + 1 > sBattery.MAX_INPUT) {
				INPUT_PER_TICK = sBattery.MAX_INPUT;
			} else if (INPUT_PER_TICK + 50 <= sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()+50);
			} else if (INPUT_PER_TICK + 50 > sBattery.MAX_INPUT) {
				sBattery.STORAGE.setMaxReceive(sBattery.MAX_INPUT);
			}
			break;
		case 2 :
			if (isShiftKeyDown() && INPUT_PER_TICK - 100 >= 0) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()-100);
			} else if (isShiftKeyDown() && INPUT_PER_TICK - 100 < 0) {
				sBattery.STORAGE.setMaxReceive(0);
			} else if (isCtrlKeyDown() && INPUT_PER_TICK - 1 >= 0) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()-1);
			} else if (isCtrlKeyDown() && INPUT_PER_TICK - 1 < 0) {
				sBattery.STORAGE.setMaxReceive(0);
			} else if (INPUT_PER_TICK - 50 >= 0) {
				sBattery.STORAGE.setMaxReceive(sBattery.STORAGE.getMaxReceive()-50);
			} else if (INPUT_PER_TICK - 50 < 0) {
				sBattery.STORAGE.setMaxReceive(0);
			}
			break;
		case 3 :
			if (isShiftKeyDown() && OUTPUT_PER_TICK + 50 <= sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()+50);
			} else if (isShiftKeyDown() && OUTPUT_PER_TICK + 50 > sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.MAX_OUTPUT);
			} else if (isCtrlKeyDown() && OUTPUT_PER_TICK + 1 <= sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()+1);
			} else if (isCtrlKeyDown() && OUTPUT_PER_TICK + 1 > sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.MAX_OUTPUT);
			} else if (OUTPUT_PER_TICK + 5 <= sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()+5);
			} else if (OUTPUT_PER_TICK + 5 > sBattery.MAX_OUTPUT) {
				sBattery.STORAGE.setMaxExtract(sBattery.MAX_OUTPUT);
			}
			break;
		case 4 :
			if (isShiftKeyDown() && OUTPUT_PER_TICK - 50 >= 0) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()-50);
			} else if (isShiftKeyDown() && OUTPUT_PER_TICK - 50 < 0) {
				sBattery.STORAGE.setMaxExtract(0);
			} else if (isCtrlKeyDown() && OUTPUT_PER_TICK - 1 >= 0) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()-1);
			} else if (isCtrlKeyDown() && OUTPUT_PER_TICK - 1 < 0) {
				sBattery.STORAGE.setMaxExtract(0);
			} else if (OUTPUT_PER_TICK - 5 >= 0) {
				sBattery.STORAGE.setMaxExtract(sBattery.STORAGE.getMaxExtract()-5);
			} else if (OUTPUT_PER_TICK - 5 < 0) {
				sBattery.STORAGE.setMaxExtract(0);
			}
			break;
		}

		IMessage msg = new PacketGuiBattery(INPUT_PER_TICK, OUTPUT_PER_TICK, sBattery.getPos());
		PacketHandler.net.sendToServer(msg);
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
