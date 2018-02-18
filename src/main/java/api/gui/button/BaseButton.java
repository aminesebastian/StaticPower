package api.gui.button;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;

public abstract class BaseButton extends Gui{
	
	public enum ClickedState {
		NONE, LEFT, RIGHT, MIDDLE;
	}
	
	protected int width;
	protected int height;
	protected int xPosition;
	protected int yPosition;

	private boolean hovered = false;
	private boolean isVisible = true;
	private ClickedState clicked = ClickedState.NONE;
	
	private boolean toggleable = false;
	private boolean toggled = false;
	
	private int mouseX;
	private int mouseY;
	
	private float clickSoundPitch;
	
	private List<String> tooltip;
	
	public BaseButton(int width, int height, int xPos, int yPos) {
		this.width = width;
		this.height = height;	
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.clickSoundPitch = 1.0f;
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
	    		clicked = button == 0 ? ClickedState.LEFT : ClickedState.RIGHT;
	    		playSound(clicked);
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
	
	protected void playSound(ClickedState state) {
		float pitch = state == ClickedState.LEFT ? clickSoundPitch : clickSoundPitch / 1.2f;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, pitch));
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
		return clicked != ClickedState.NONE;
	}
	public ClickedState getClickedState() {
		return clicked;
	}
	public void setClicked(ClickedState newClickedState) {
		this.clicked = newClickedState;
	}
	public void setTooltip(List<String> tooltip) {
		this.tooltip = tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = Arrays.asList(tooltip.split("="));
	}
	public void setClickSoundPitch(float newSoundPitch) {
		this.clickSoundPitch = newSoundPitch;
	}
	public float getClickSoundPitch() {
		return clickSoundPitch;
	}
}
