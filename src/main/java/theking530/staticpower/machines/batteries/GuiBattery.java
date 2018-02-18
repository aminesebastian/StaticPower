package theking530.staticpower.machines.batteries;

import api.gui.tab.BaseGuiTab.TabSide;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.client.gui.BaseGuiContainer;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.gui.widgets.tabs.GuiPowerControlTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiPowerInfoTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.client.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;

public class GuiBattery extends BaseGuiContainer {
	
	private TileEntityBattery battery;
	
	public GuiBattery(InventoryPlayer invPlayer, TileEntityBattery teSBattery) {
		super(new ContainerBattery(invPlayer, teSBattery), 176, 166);
		battery = teSBattery;
		
		registerWidget(new GuiPowerBarFromEnergyStorage(teSBattery, 80, 71, 16, 51));
		getTabManager().registerTab(new GuiRedstoneTab(100, 85, teSBattery));
		getTabManager().registerTab(new GuiPowerControlTab(100, 70, teSBattery));
		getTabManager().registerTab(new GuiSideConfigTab(80, 80, teSBattery));
		
		GuiPowerInfoTab powerInfoTab;
		getTabManager().registerTab(powerInfoTab = new GuiPowerInfoTab(80, 60, teSBattery.getEnergyStorage()));
		powerInfoTab.setTabSide(TabSide.LEFT);	
	}	

	//Draw Main
	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		guiButtons(j, k);
	}
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String INPUT = (String.valueOf(battery.energyStorage.getMaxReceive()) + " RF/t");
		String OUTPUT = (String.valueOf(battery.energyStorage.getMaxExtract()) + " RF/t");
		
		String current_input = (String.valueOf(battery.currentEnergyPerTick) + " RF/t");

		String NAME = I18n.format(this.battery.getName());
		this.fontRenderer.drawString("Input", this.xSize/2-this.fontRenderer.getStringWidth("Input")/2 - 35, 35, 4210752);
		this.fontRenderer.drawString(INPUT, this.xSize/2-this.fontRenderer.getStringWidth(INPUT)/2 - 35, 45, 4210752);
		this.fontRenderer.drawString("Output", this.xSize/2-this.fontRenderer.getStringWidth("Output")/2 + 35, 35, 4210752);
		this.fontRenderer.drawString(OUTPUT, this.xSize/2-this.fontRenderer.getStringWidth(OUTPUT)/2 + 35, 45, 4210752);
		this.fontRenderer.drawString(NAME, this.xSize / 2 - this.fontRenderer.getStringWidth(NAME) / 2, 6, 4210752);
		
		this.fontRenderer.drawString(current_input, this.xSize/2-this.fontRenderer.getStringWidth(INPUT)/2 - 30, 25, 4210752);
		
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 3, 4210752);
	}
	@Override
	protected void drawExtra(float f, int i, int j) {
		this.mc.getTextureManager().bindTexture(GuiTextures.BATTERY_GUI);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
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
			if (isShiftKeyDown() && battery.energyStorage.getMaxReceive() + 100 <= battery.MAX_INPUT) {
				battery.energyStorage.setMaxReceive(battery.energyStorage.getMaxReceive()+100);
			} else if (isShiftKeyDown() && battery.energyStorage.getMaxReceive() + 100 > battery.MAX_INPUT) {
				battery.energyStorage.setMaxReceive(battery.MAX_INPUT);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxReceive() + 1 <= battery.MAX_INPUT) {
				battery.energyStorage.setMaxReceive(battery.energyStorage.getMaxReceive()+1);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxReceive() + 1 > battery.MAX_INPUT) {
				//INPUT_PER_TICK = sBattery.MAX_INPUT;
			} else if (battery.energyStorage.getMaxReceive() + 50 <= battery.MAX_INPUT) {
				battery.energyStorage.setMaxReceive(battery.energyStorage.getMaxReceive()+50);
			} else if (battery.energyStorage.getMaxReceive() + 50 > battery.MAX_INPUT) {
				battery.energyStorage.setMaxReceive(battery.MAX_INPUT);
			}
			break;
		case 2 :
			if (isShiftKeyDown() && battery.energyStorage.getMaxReceive() - 100 >= 0) {
				battery.energyStorage.setMaxReceive(battery.energyStorage.getMaxReceive()-100);
			} else if (isShiftKeyDown() && battery.energyStorage.getMaxReceive() - 100 < 0) {
				battery.energyStorage.setMaxReceive(0);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxReceive() - 1 >= 0) {
				battery.energyStorage.setMaxReceive(battery.energyStorage.getMaxReceive()-1);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxReceive() - 1 < 0) {
				battery.energyStorage.setMaxReceive(0);
			} else if (battery.energyStorage.getMaxReceive() - 50 >= 0) {
				battery.energyStorage.setMaxReceive(battery.energyStorage.getMaxReceive()-50);
			} else if (battery.energyStorage.getMaxReceive() - 50 < 0) {
				battery.energyStorage.setMaxReceive(0);
			}
			break;
		case 3 :
			if (isShiftKeyDown() && battery.energyStorage.getMaxExtract() + 50 <= battery.MAX_OUTPUT) {
				battery.energyStorage.setMaxExtract(battery.energyStorage.getMaxExtract()+50);
			} else if (isShiftKeyDown() && battery.energyStorage.getMaxExtract() + 50 > battery.MAX_OUTPUT) {
				battery.energyStorage.setMaxExtract(battery.MAX_OUTPUT);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxExtract() + 1 <= battery.MAX_OUTPUT) {
				battery.energyStorage.setMaxExtract(battery.energyStorage.getMaxExtract()+1);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxExtract() + 1 > battery.MAX_OUTPUT) {
				battery.energyStorage.setMaxExtract(battery.MAX_OUTPUT);
			} else if (battery.energyStorage.getMaxExtract() + 5 <= battery.MAX_OUTPUT) {
				battery.energyStorage.setMaxExtract(battery.energyStorage.getMaxExtract()+5);
			} else if (battery.energyStorage.getMaxExtract() + 5 > battery.MAX_OUTPUT) {
				battery.energyStorage.setMaxExtract(battery.MAX_OUTPUT);
			}
			break;
		case 4 :
			if (isShiftKeyDown() && battery.energyStorage.getMaxExtract() - 50 >= 0) {
				battery.energyStorage.setMaxExtract(battery.energyStorage.getMaxExtract()-50);
			} else if (isShiftKeyDown() && battery.energyStorage.getMaxExtract() - 50 < 0) {
				battery.energyStorage.setMaxExtract(0);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxExtract() - 1 >= 0) {
				battery.energyStorage.setMaxExtract(battery.energyStorage.getMaxExtract()-1);
			} else if (isCtrlKeyDown() && battery.energyStorage.getMaxExtract() - 1 < 0) {
				battery.energyStorage.setMaxExtract(0);
			} else if (battery.energyStorage.getMaxExtract() - 5 >= 0) {
				battery.energyStorage.setMaxExtract(battery.energyStorage.getMaxExtract()-5);
			} else if (battery.energyStorage.getMaxExtract() - 5 < 0) {
				battery.energyStorage.setMaxExtract(0);
			}
			break;
		}
		if(!battery.getWorld().isRemote) {
			battery.updateBlock();		
		}

	}
}
