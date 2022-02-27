package theking530.staticcore.gui.widgets.tabs;

import java.util.Collections;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

@OnlyIn(Dist.CLIENT)
public class GuiSideConfigTab extends BaseGuiTab {
	private static final BlockModel HIGHLIGHT_RENDERER = new BlockModel();
	private static final AABB BOUNDS = new AABB(new Vec3(0, 0, 0), new Vec3(1, 1, 1));

	public TileEntityBase tileEntity;

	private TextButton topButton;
	private TextButton bottomButton;
	private TextButton leftButton;
	private TextButton rightButton;
	private TextButton backButton;
	private TextButton frontButton;
	private Vector2D rotation;
	private Vector2D rotationVelocity;
	private boolean mouseDownInside;
	private Direction highlightedSide;

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

		topButton.setVisible(false);
		bottomButton.setVisible(false);
		frontButton.setVisible(false);
		backButton.setVisible(false);
		rightButton.setVisible(false);
		leftButton.setVisible(false);
		updateTooltips();
		rotation = new Vector2D(55, -25);
		rotationVelocity = new Vector2D(0, 0);

		// Rotate initially to reflect the angle the player is looking from.
		@SuppressWarnings("resource")
		Player player = Minecraft.getInstance().player;
		Vec3 eyeLocation = player.getPosition(0.5f);
		Vec3 blockPosition = new Vec3(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ());
		Vec3 direction = eyeLocation.subtract(blockPosition).normalize();
		double xAngle = Math.toDegrees(Math.atan2(direction.x(), direction.z()));
		rotation = new Vector2D(xAngle, -25);
	}

	@Override
	public void updateBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		// Add the velocity.
		rotation.add(rotationVelocity);

		// Perform the decay.
		float xDecay = Math.signum(rotationVelocity.getX()) * partialTicks * 0.25f;
		float yDecay = Math.signum(rotationVelocity.getY()) * partialTicks * 0.25f;
		rotationVelocity.subtract(xDecay, yDecay);
		if (Math.abs(rotationVelocity.getX()) < xDecay) {
			rotationVelocity.setX(0);
		}
		if (Math.abs(rotationVelocity.getY()) < yDecay) {
			rotationVelocity.setY(0);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(matrix, mouseX, mouseY, partialTicks);
		drawButtonBG(matrix, 0, 0);
		fontRenderer.drawShadow(matrix, getTitle(), (getTabSide() == TabSide.LEFT ? 11 : 24), 8, titleColor);

		RenderSystem.disableCull();
		BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		BakedModel model = renderer.getBlockModel(tileEntity.getBlockState());
		IModelData data = model.getModelData(Minecraft.getInstance().level, tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity.getModelData());
		matrix.pushPose();
		matrix.translate(73f, 38.5f, 0);
		matrix.scale(-42, 42, 42);
		matrix.translate(0.5f, 0.5f, 0.5f);
		matrix.mulPose(Quaternion.fromXYZDegrees(new Vector3f(rotation.getY(), rotation.getX(), 180)));
		matrix.translate(-0.5f, -0.5f, -0.5f);
		renderer.renderSingleBlock(tileEntity.getBlockState(), matrix, buffer, 15728880, OverlayTexture.NO_OVERLAY, data);
		buffer.endBatch();

		Matrix4f inverse = matrix.last().pose().copy();
		inverse.invert();
		Vector4f mouseMin = createMouseVector(inverse, mouseX, mouseY, 1000);
		Vector4f mouseMax = createMouseVector(inverse, mouseX, mouseY, -1000);

		BlockHitResult result = AABB.clip(Collections.singleton(BOUNDS), new Vec3(mouseMin.x(), mouseMin.y(), mouseMin.z()), new Vec3(mouseMax.x(), mouseMax.y(), mouseMax.z()), new BlockPos(0, 0, 0));
		if (result != null) {
			highlightedSide = result.getDirection();
		} else {
			highlightedSide = null;
		}

		if (highlightedSide != null) {
			SideConfigurationComponent sideConfig = tileEntity.getComponent(SideConfigurationComponent.class);
			if (sideConfig != null) {
				boolean enabled = sideConfig.getWorldSpaceEnabledState(highlightedSide);
				if (enabled) {
					MachineSideMode mode = sideConfig.getWorldSpaceDirectionConfiguration(highlightedSide);
					Color color = mode.getColor().copy();
					color.setW(0.75f);
					HIGHLIGHT_RENDERER.drawPreviewSide(matrix, result.getDirection(), new Vector3f(-0.025f, -0.025f, -0.025f), new Vector3f(1.05f, 1.05f, 1.05f), color);
				} else {
					highlightedSide = null; // Clear out the highlighted side if its a disabled side.
				}
			}
		}

		matrix.popPose();
	}

	private Vector4f createMouseVector(Matrix4f inverse, int mouseX, int mouseY, int distance) {
		Vector4f mouse = new Vector4f(mouseX, mouseY, distance, 1);
		mouse.transform(inverse);
		float w = mouse.w();
		mouse.set(mouse.x() / w, mouse.y() / w, mouse.z() / w, 1);
		return mouse;
	}

	@Override
	protected void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
	}

	public void drawButtonBG(PoseStack matrix, int xPos, int yPos) {
		drawDarkBackground(matrix, 10, 22, 85, 75);
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
			button.setTooltip(translatedSideName.append(" (").append(new TranslatableComponent("gui.staticpower.direction." + worldSpaceSide.toString())).append(ChatFormatting.WHITE + ")"),
					translatedModeName);
		}
	}

	public EInputResult mouseClick(PoseStack matrixStack, double mouseX, double mouseY, int button) {
		if (this.getBounds().isPointInBounds(new Vector2D(mouseX, mouseY))) {
			if (this.getTabState() == TabState.OPEN) {
				mouseDownInside = true;
			}
		} else {
			mouseDownInside = false;
		}

		return super.mouseClick(matrixStack, mouseX, mouseY, button);
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (mouseDownInside) {
			rotationVelocity.setX((float) deltaX * 1.5f);
			rotationVelocity.setY((float) deltaY * -1.5f);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	@SuppressWarnings("resource")
	public EInputResult mouseReleased(double mouseX, double mouseY, int button) {
		if (highlightedSide != null) {
			SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);
			SideIncrementDirection direction = button == 1 ? SideIncrementDirection.FORWARD : SideIncrementDirection.BACKWARDS;
			sideComp.modulateWorldSpaceSideMode(highlightedSide, direction);

			// Play the click sound.
			Minecraft.getInstance().level.playLocalSound(tileEntity.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0f, 1.0f, false);
			// Send a packet to the server with the updated values.
			NetworkMessage msg = new PacketSideConfigTab(sideComp.getWorldSpaceConfiguration(), tileEntity.getBlockPos());
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	public TranslatableComponent conditionallyGetCardinal(BlockSide side) {
		if (tileEntity instanceof TileEntityBase) {
			TileEntityBase te = (TileEntityBase) tileEntity;
			return new TranslatableComponent("gui." + SideConfigurationUtilities.getDirectionFromSide(side, te.getFacingDirection()).toString().toLowerCase());
		}
		return new TranslatableComponent("ERROR");
	}
}