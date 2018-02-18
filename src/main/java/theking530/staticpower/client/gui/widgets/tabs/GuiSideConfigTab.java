package theking530.staticpower.client.gui.widgets.tabs;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import api.gui.IInteractableGui;
import api.gui.button.BaseButton;
import api.gui.button.BaseButton.ClickedState;
import api.gui.button.ButtonManager;
import api.gui.button.TextButton;
import api.gui.tab.BaseGuiTab;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.assists.utilities.SideUtilities;
import theking530.staticpower.assists.utilities.SideUtilities.BlockSide;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.tileentity.ISideConfigurable;
import theking530.staticpower.tileentity.ISideConfigurable.SideIncrementDirection;

public class GuiSideConfigTab extends BaseGuiTab implements IInteractableGui {

	public TileEntity tileEntity;
	private FontRenderer fontRenderer;

	private ButtonManager buttonManager;
	
	private TextButton topButton;
	private TextButton bottomButton;
	private TextButton leftButton;
	private TextButton rightButton;
	private TextButton backButton;
	
	public GuiSideConfigTab(int width, int height, TileEntity te){
		super(width, height, GuiTextures.BLUE_TAB, te.getBlockType());
		tileEntity = te;
		fontRenderer = Minecraft.getMinecraft().fontRenderer;
		
		topButton = new TextButton(20, 20, 0, 0, "T");
		bottomButton = new TextButton(20, 20, 0, 0, "B");
		leftButton = new TextButton(20, 20, 0, 0, "L");
		rightButton = new TextButton(20, 20, 0, 0, "R");
		backButton = new TextButton(20, 20, 0, 0, "B");
		
		buttonManager = new ButtonManager(this);
		
		updateTooltips();
		
		buttonManager.registerButton(topButton);
		buttonManager.registerButton(bottomButton);
		buttonManager.registerButton(leftButton);
		buttonManager.registerButton(rightButton);
		buttonManager.registerButton(backButton);
	}
	@Override
	public void drawExtra(int xPos, int yPos, float partialTicks) {
		if(isOpen()) {
	    	drawText(xPos+10, yPos+8);
			drawButtonBG(xPos, yPos);
			buttonManager.drawButtons(xPos, yPos);
			
			int xOffset = 3;
			int yOffset = 8;
			
			topButton.setPosition(xOffset + xPos+tabWidth/2, yOffset+yPos+15);
			bottomButton.setPosition(xOffset + xPos+tabWidth/2, yOffset+yPos+tabHeight-15);
			rightButton.setPosition(xOffset + xPos+tabWidth-15, yOffset+yPos+tabHeight/2);
			leftButton.setPosition(xOffset + xPos+15, yOffset+yPos+tabHeight/2);
			backButton.setPosition(xOffset + xPos+tabWidth/2, yOffset+yPos+tabHeight/2);
		}
	}
	public void drawText(int xPos, int yPos) {
		String tabName = "Side Config";
		modeText(xPos, yPos);	
		fontRenderer.drawStringWithShadow(tabName, xPos-this.fontRenderer.getStringWidth(tabName)/2 + 45, yPos, 16777215);	
	}
	public void modeText(int tabLeft, int tabTop) {

	}
	public void drawButtonBG(int xPos, int yPos) {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		GlStateManager.color(1, 1, 1);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos+95, yPos+95, 0).tex(0,1).endVertex();
		vertexbuffer.pos(xPos+95, yPos+20, 0).tex(0,0).endVertex();
		vertexbuffer.pos(xPos+10, yPos+20, 0).tex(1,0).endVertex();
		vertexbuffer.pos(xPos+10, yPos+95, 0).tex(1,1).endVertex();	
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
    @Override
    public void handleExtraMouseInteraction(int par1, int par2, int button) {
    	buttonManager.handleMouseInteraction(par1, par2, button);
	}	
	@Override
	protected void handleExtraKeyboardInteraction(char par1, int par2) {
		
	}
	@Override
	protected void handleExtraMouseMove(int mouseX, int mouseY) {
		buttonManager.handleMouseMoveInteraction(mouseX, mouseY);
	}
	
	@Override
	public void buttonPressed(BaseButton button, ClickedState mouseButton) {
		if(!(tileEntity instanceof ISideConfigurable)) {
			return;
		}
		ISideConfigurable sideConfigurable = (ISideConfigurable)tileEntity;
		SideIncrementDirection direction = mouseButton == ClickedState.LEFT ? SideIncrementDirection.FORWARD : SideIncrementDirection.BACKWARDS;
		if(button == topButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getEnumFacingFromSide(BlockSide.TOP, getFacingDirection()), direction);
		}else if(button == bottomButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getEnumFacingFromSide(BlockSide.BOTTOM, getFacingDirection()), direction);
		}else if(button == leftButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getEnumFacingFromSide(BlockSide.LEFT, getFacingDirection()), direction);
		}else if(button == rightButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getEnumFacingFromSide(BlockSide.RIGHT, getFacingDirection()), direction);
		}else{
			sideConfigurable.incrementSideConfiguration(SideUtilities.getEnumFacingFromSide(BlockSide.BACK, getFacingDirection()), direction);
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				for(EnumFacing facing : EnumFacing.values()) {
					sideConfigurable.setSideConfiguration(Mode.Regular, facing);
				}
			}
		}
		updateTooltips();
		IMessage msg = new PacketSideConfigTab(sideConfigurable.getSideConfigurations(), tileEntity.getPos());
		PacketHandler.net.sendToServer(msg);
	}
	@Override
	public void buttonHovered(BaseButton button) {

	}
    public EnumFacing getFacingDirection() {
    	if(tileEntity.getWorld().getBlockState(tileEntity.getPos()).getProperties().containsKey(BlockHorizontal.FACING)) {
        	return tileEntity.getWorld().getBlockState(tileEntity.getPos()).getValue(BlockHorizontal.FACING);
    	}else{
        	return EnumFacing.UP;
    	}
    }
    public void updateTooltips() {
		if(!(tileEntity instanceof ISideConfigurable)) {
			return;
		}
		ISideConfigurable sideConfigurable = (ISideConfigurable)tileEntity;
		topButton.setText(sideConfigurable.getSideConfiguration(BlockSide.TOP).getFontColor() + BlockSide.TOP.getLocalizedName().substring(0, 1));
		bottomButton.setText(sideConfigurable.getSideConfiguration(BlockSide.BOTTOM).getFontColor() + BlockSide.BOTTOM.getLocalizedName().substring(0, 2));
		leftButton.setText(sideConfigurable.getSideConfiguration(BlockSide.LEFT).getFontColor() + BlockSide.LEFT.getLocalizedName().substring(0, 1));
		rightButton.setText(sideConfigurable.getSideConfiguration(BlockSide.RIGHT).getFontColor() + BlockSide.RIGHT.getLocalizedName().substring(0, 1));
		backButton.setText(sideConfigurable.getSideConfiguration(BlockSide.BACK).getFontColor() + BlockSide.BACK.getLocalizedName().substring(0, 1));
		
		topButton.setTooltip(BlockSide.TOP.getLocalizedName() + "=" + sideConfigurable.getSideConfiguration(BlockSide.TOP).getFontColor() +sideConfigurable.getSideConfiguration(BlockSide.TOP).getLocalizedName());
		bottomButton.setTooltip(BlockSide.BOTTOM.getLocalizedName() + "=" + sideConfigurable.getSideConfiguration(BlockSide.BOTTOM).getFontColor() +sideConfigurable.getSideConfiguration(BlockSide.BOTTOM).getLocalizedName());
		leftButton.setTooltip(BlockSide.LEFT.getLocalizedName() + "=" + sideConfigurable.getSideConfiguration(BlockSide.LEFT).getFontColor() +sideConfigurable.getSideConfiguration(BlockSide.LEFT).getLocalizedName());
		rightButton.setTooltip(BlockSide.RIGHT.getLocalizedName() + "=" + sideConfigurable.getSideConfiguration(BlockSide.RIGHT).getFontColor() +sideConfigurable.getSideConfiguration(BlockSide.RIGHT).getLocalizedName());
		backButton.setTooltip(BlockSide.BACK.getLocalizedName() + "=" +sideConfigurable.getSideConfiguration(BlockSide.BACK).getFontColor() + sideConfigurable.getSideConfiguration(BlockSide.BACK).getLocalizedName());
    }
}