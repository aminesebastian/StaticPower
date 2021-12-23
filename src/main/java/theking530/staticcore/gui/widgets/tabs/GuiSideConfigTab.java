package theking530.staticcore.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

@OnlyIn(Dist.CLIENT)
public class GuiSideConfigTab extends BaseGuiTab {

	public TileEntityBase tileEntity;

	private TextButton topButton;
	private TextButton bottomButton;
	private TextButton leftButton;
	private TextButton rightButton;
	private TextButton backButton;
	private TextButton frontButton;

	public GuiSideConfigTab(TileEntityBase te) {
		super("Side Config", Color.EIGHT_BIT_WHITE, 80, 80, GuiTextures.BLUE_TAB, te.getBlockState().getBlock());
		tileEntity = te;

		int xOffset = 3;
		int yOffset = 8;
		widgetContainer.registerWidget(topButton = new TextButton(xOffset + tabWidth / 2, yOffset + 17, 20, 20, "T", this::buttonPressed));
		widgetContainer.registerWidget(bottomButton = new TextButton(xOffset + tabWidth / 2, yOffset + tabHeight - 13, 20, 20, "B", this::buttonPressed));
		widgetContainer.registerWidget(frontButton = new TextButton(xOffset + 15, yOffset + 17, 20, 20, "F", this::buttonPressed));
		widgetContainer.registerWidget(backButton = new TextButton(xOffset + tabWidth / 2, yOffset + 2 + tabHeight / 2, 20, 20, "B", this::buttonPressed));
		widgetContainer.registerWidget(rightButton = new TextButton(xOffset + tabWidth - 15, yOffset + 2 + tabHeight / 2, 20, 20, "L", this::buttonPressed));
		widgetContainer.registerWidget(leftButton = new TextButton(xOffset + 15, yOffset + 2 + tabHeight / 2, 20, 20, "R", this::buttonPressed));

		topButton.setVisible(te.getComponent(SideConfigurationComponent.class).getBlockSideEnabledState(BlockSide.TOP));
		bottomButton.setVisible(te.getComponent(SideConfigurationComponent.class).getBlockSideEnabledState(BlockSide.BOTTOM));
		frontButton.setVisible(te.getComponent(SideConfigurationComponent.class).getBlockSideEnabledState(BlockSide.FRONT));
		backButton.setVisible(te.getComponent(SideConfigurationComponent.class).getBlockSideEnabledState(BlockSide.BACK));
		rightButton.setVisible(te.getComponent(SideConfigurationComponent.class).getBlockSideEnabledState(BlockSide.RIGHT));
		leftButton.setVisible(te.getComponent(SideConfigurationComponent.class).getBlockSideEnabledState(BlockSide.LEFT));

		updateTooltips();
	}

	@Override
	public void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		drawButtonBG(matrix, 0, 0);
		super.renderBackground(matrix, mouseX, mouseY, partialTicks);
	}

	public void drawButtonBG(PoseStack matrix, int xPos, int yPos) {
		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(matrix, xPos, yPos);

		GL11.glEnable(GL11.GL_BLEND);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		Minecraft.getInstance().getTextureManager().bindForSetup(GuiTextures.BUTTON_BG);
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(position.getX() + 95, position.getY() + 97, 0).uv(0, 1).endVertex();
		vertexbuffer.vertex(position.getX() + 95, position.getY() + 22, 0).uv(0, 0).endVertex();
		vertexbuffer.vertex(position.getX() + 10, position.getY() + 22, 0).uv(1, 0).endVertex();
		vertexbuffer.vertex(position.getX() + 10, position.getY() + 97, 0).uv(1, 1).endVertex();
		tessellator.end();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (!tileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
			return;
		}
		SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);

		SideIncrementDirection direction = button.getClickedState() == MouseButton.LEFT ? SideIncrementDirection.FORWARD : SideIncrementDirection.BACKWARDS;
		if (button == topButton) {
			sideComp.modulateWorldSpaceSideMode(SideConfigurationUtilities.getDirectionFromSide(BlockSide.TOP, tileEntity.getFacingDirection()), direction);
		} else if (button == bottomButton) {
			sideComp.modulateWorldSpaceSideMode(SideConfigurationUtilities.getDirectionFromSide(BlockSide.BOTTOM, tileEntity.getFacingDirection()), direction);
		} else if (button == leftButton) {
			sideComp.modulateWorldSpaceSideMode(SideConfigurationUtilities.getDirectionFromSide(BlockSide.LEFT, tileEntity.getFacingDirection()), direction);
		} else if (button == rightButton) {
			sideComp.modulateWorldSpaceSideMode(SideConfigurationUtilities.getDirectionFromSide(BlockSide.RIGHT, tileEntity.getFacingDirection()), direction);
		} else if (button == frontButton) {
			sideComp.modulateWorldSpaceSideMode(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, tileEntity.getFacingDirection()), direction);
		} else if (button == backButton) {
			sideComp.modulateWorldSpaceSideMode(SideConfigurationUtilities.getDirectionFromSide(BlockSide.BACK, tileEntity.getFacingDirection()), direction);
		}
		updateTooltips();

		// Send a packet to the server with the updated values.
		NetworkMessage msg = new PacketSideConfigTab(sideComp.getWorldSpaceConfiguration(), tileEntity.getBlockPos());
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	public void updateTooltips() {
		if (!tileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
			return;
		}
		SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);

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

			// Get the world space direction and current side mode.
			Direction worldSpaceSide = SideConfigurationUtilities.getDirectionFromSide(side, tileEntity.getFacingDirection());
			MachineSideMode currentMode = sideComp.getWorldSpaceDirectionConfiguration(worldSpaceSide);

			// Get the translation components.
			TranslatableComponent translatedSideName = side.getName();
			Component translatedModeName = currentMode.getName();

			button.setText(currentMode.getFontColor() + translatedSideName.getString().substring(0, 1));
			button.setTooltip(translatedSideName.append(" (").append(new TranslatableComponent("gui.staticpower.direction." + worldSpaceSide.toString()))
					.append(ChatFormatting.WHITE + ")"), translatedModeName);
		}
	}

	public TranslatableComponent conditionallyGetCardinal(BlockSide side) {
		if (tileEntity instanceof TileEntityBase) {
			TileEntityBase te = (TileEntityBase) tileEntity;
			return new TranslatableComponent("gui." + SideConfigurationUtilities.getDirectionFromSide(side, te.getFacingDirection()).toString().toLowerCase());
		}
		return new TranslatableComponent("ERROR");
	}
}