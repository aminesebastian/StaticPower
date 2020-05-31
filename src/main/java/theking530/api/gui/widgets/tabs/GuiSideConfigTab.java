package theking530.api.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.api.gui.GuiTextures;
import theking530.api.gui.button.BaseButton;
import theking530.api.gui.button.BaseButton.ClickedButton;
import theking530.api.gui.button.TextButton;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.utilities.SideUtilities;
import theking530.staticpower.tileentities.utilities.SideUtilities.BlockSide;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable.SideIncrementDirection;

public class GuiSideConfigTab extends BaseGuiTab {

	public TileEntityBase tileEntity;
	private FontRenderer fontRenderer;

	private TextButton topButton;
	private TextButton bottomButton;
	private TextButton leftButton;
	private TextButton rightButton;
	private TextButton backButton;
	private TextButton frontButton;

	private boolean allowFaceInteraction;

	public GuiSideConfigTab(int width, int height, boolean faceInteraction, TileEntityBase te) {
		super(80, 80, GuiTextures.BLUE_TAB, te.getBlockState().getBlock());
		tileEntity = te;
		fontRenderer = Minecraft.getInstance().fontRenderer;
		allowFaceInteraction = faceInteraction;

		int xOffset = 3;
		int yOffset = 8;
		widgetContainer.registerWidget(topButton = new TextButton(xOffset + tabWidth / 2, yOffset + 15, 20, 20, "T", this::buttonPressed));
		widgetContainer.registerWidget(bottomButton = new TextButton(xOffset + tabWidth / 2, yOffset + tabHeight - 15, 20, 20, "B", this::buttonPressed));
		widgetContainer.registerWidget(rightButton = new TextButton(xOffset + tabWidth - 15, yOffset + tabHeight / 2, 20, 20, "L", this::buttonPressed));
		widgetContainer.registerWidget(leftButton = new TextButton(xOffset + 15, yOffset + tabHeight / 2, 20, 20, "R", this::buttonPressed));
		widgetContainer.registerWidget(backButton = new TextButton(xOffset + tabWidth / 2, yOffset + tabHeight / 2, 20, 20, "B", this::buttonPressed));
		widgetContainer.registerWidget(frontButton = new TextButton(xOffset + 15, yOffset + 15, 20, 20, "F", this::buttonPressed));
		frontButton.setVisible(allowFaceInteraction);

		updateTooltips();
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		drawText(xPosition + 10, yPosition + 8);
		drawButtonBG(xPosition, yPosition);
		super.renderBackground(mouseX, mouseY, partialTicks);
	}

	public void drawText(int xPos, int yPos) {
		String tabName = "Side Config";
		modeText(xPos, yPos);
		fontRenderer.drawStringWithShadow(tabName, xPos - this.fontRenderer.getStringWidth(tabName) / 2 + 45, yPos, 16777215);
	}

	public void modeText(int tabLeft, int tabTop) {

	}

	public void drawButtonBG(int xPos, int yPos) {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos + 95, yPos + 95, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPos + 95, yPos + 20, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPos + 10, yPos + 20, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPos + 10, yPos + 95, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void buttonPressed(BaseButton button) {
		if (!(tileEntity instanceof ISideConfigurable)) {
			return;
		}
		ISideConfigurable sideConfigurable = (ISideConfigurable) tileEntity;
		SideIncrementDirection direction = button.getClickedState() == ClickedButton.LEFT ? SideIncrementDirection.FORWARD : SideIncrementDirection.BACKWARDS;
		if (button == topButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getDirectionFromSide(BlockSide.TOP, tileEntity.getFacingDirection()), direction);
		} else if (button == bottomButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getDirectionFromSide(BlockSide.BOTTOM, tileEntity.getFacingDirection()), direction);
		} else if (button == leftButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getDirectionFromSide(BlockSide.LEFT, tileEntity.getFacingDirection()), direction);
		} else if (button == rightButton) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getDirectionFromSide(BlockSide.RIGHT, tileEntity.getFacingDirection()), direction);
		} else if (button == frontButton && allowFaceInteraction) {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getDirectionFromSide(BlockSide.FRONT, tileEntity.getFacingDirection()), direction);
		} else {
			sideConfigurable.incrementSideConfiguration(SideUtilities.getDirectionFromSide(BlockSide.BACK, tileEntity.getFacingDirection()), direction);
		}
		updateTooltips();

		// Send a packet to the server with the updated values.
		NetworkMessage msg = new PacketSideConfigTab(sideConfigurable.getSideConfigurations(), tileEntity.getPos());
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	public void updateTooltips() {
		if (!(tileEntity instanceof ISideConfigurable)) {
			return;
		}
		ISideConfigurable sideConfigurable = (ISideConfigurable) tileEntity;

		for (BlockSide side : BlockSide.values()) {
			TextButton button = null;
			switch (side) {
			case TOP:
				button = topButton;
				break;
			case BOTTOM:
				button = bottomButton;
				break;
			case LEFT:
				button = leftButton;
				break;
			case RIGHT:
				button = rightButton;
				break;
			case FRONT:
				button = frontButton;
				break;
			case BACK:
				button = backButton;
				break;
			default:
				button = topButton;
				break;
			}

//			button.setText(sideConfigurable.getSideConfiguration(side).getFontColor() + side.getLocalizedName().substring(0, 1));
//			button.setTooltip(new StringTextComponent(side.getLocalizedName() + "=" + sideConfigurable.getSideConfiguration(side).getFontColor()
//					+ sideConfigurable.getSideConfiguration(side).getLocalizedName() + "=" + I18n.format(conditionallyGetCardinal(side))));
		}
	}

	public String conditionallyGetCardinal(BlockSide side) {
		if (tileEntity instanceof TileEntityBase) {
			TileEntityBase te = (TileEntityBase) tileEntity;
			return "gui." + SideUtilities.getDirectionFromSide(side, te.getFacingDirection()).toString();
		}
		return "";
	}
}