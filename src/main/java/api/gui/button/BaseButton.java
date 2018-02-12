package api.gui.button;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;

public abstract class BaseButton extends Gui{
	
	protected int width;
	protected int height;
	protected int xPosition;
	protected int yPosition;

	private boolean hovered = false;
	private boolean isVisible = true;
	private boolean clicked = false;
	
	private boolean toggleable = false;
	private boolean toggled = false;
	
	private int mouseX;
	private int mouseY;
	
	private List<String> tooltip;
	
	public BaseButton(int width, int height, int xPos, int yPos) {
		this.width = width;
		this.height = height;	
		this.xPosition = xPos;
		this.yPosition = yPos;
	}
	public void draw() {
		GlStateManager.disableLighting();
		drawButton();
		drawExtra();
		GlStateManager.enableLighting();
	}
	public void handleMouseInteraction(int mouseX, int mouseY, int button) {
		if(mouseX > xPosition && mouseX < xPosition + width && isVisible) {
	    	if(mouseY > yPosition && mouseY < yPosition + height) {
	    		clicked = true;
	    		playSound();
	    		if(toggleable) {
	    			toggled = !toggled;
	    		}
	    	}
	    }
	}
	public void handleMouseMoveInteraction(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		if(mouseX > xPosition && mouseX < xPosition + width && isVisible) {
	    	if(mouseY > yPosition && mouseY < yPosition + height) {
	    		hovered = true;
	    		return;
	    	}
		}
    	hovered = false;
	}
	protected abstract void drawButton();
	protected void drawExtra() {}
	
	protected void playSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
	public void drawTooltip() {
		if(tooltip != null && tooltip.size() > 0) {
	        GlStateManager.disableDepth();
	        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(tooltip, mouseX-4, mouseY+8, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, -1, Minecraft.getMinecraft().fontRenderer);
	        GlStateManager.disableLighting();
	        GlStateManager.disableDepth();
	        RenderHelper.disableStandardItemLighting();
	        GlStateManager.enableRescaleNormal();	
		}
    }
	public void setPosition(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	public void setToggleable(boolean toggleable) {
		this.toggleable = toggleable;
	}
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
	public boolean isToggleable() {
		return toggled;
	}
	public boolean isToggled() {
		return toggled;
	}
	public boolean isVisible() {
		return isVisible;
	}
	public boolean isHovered() {
		return hovered;
	}
	public boolean isClicked() {
		return clicked;
	}
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	public void setTooltip(List<String> tooltip) {
		this.tooltip = tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = new ArrayList<String>();
		this.tooltip.add(tooltip);
	}
}
