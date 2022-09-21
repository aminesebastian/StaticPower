package theking530.staticcore.gui.widgets.tabs;

import java.util.Collections;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.network.StaticPowerMessageHandler;

@OnlyIn(Dist.CLIENT)
public class GuiSideConfigTab extends BaseGuiTab {
	private static final BlockModel HIGHLIGHT_RENDERER = new BlockModel();
	private static final AABB BOUNDS = new AABB(new Vec3(-0.05, -0.05, -0.05), new Vec3(1.05, 1.05, 1.05));

	public BlockEntityBase tileEntity;
	private Vector2D rotation;
	private Vector2D rotationVelocity;
	private Vector2D mouseDownLocation;
	private boolean mouseDownInside;
	private Direction highlightedSide;

	public GuiSideConfigTab(BlockEntityBase te) {
		super("Side Config", Color.EIGHT_BIT_WHITE, 110, 105, new Color(0.1f, 0.4f, 0.95f, 1.0f), te.getBlockState().getBlock());
		tileEntity = te;
		rotationVelocity = new Vector2D(0, 0);
		mouseDownLocation = new Vector2D(0, 0);

		// Rotate initially to reflect the angle the player is looking from.
		Vec3 direction = getLocalPlayer().getLookAngle();
		double xAngle = Math.toDegrees(Math.atan2(direction.x(), direction.z()));
		double yAngle = Math.toDegrees(Math.asin(direction.y()));
		rotation = new Vector2D(xAngle + 180, yAngle);
	}

	@Override
	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
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
	public void renderWidgetBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBackground(matrix, mouseX, mouseY, partialTicks);

		// Don't render anything if we're not open.
		if (!isOpen()) {
			return;
		}

		drawDarkBackground(matrix, 12, 25, (int) getWidth() - 26, (int) getHeight() - 35);

		BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		BakedModel model = renderer.getBlockModel(tileEntity.getBlockState());
		IModelData data = model.getModelData(Minecraft.getInstance().level, tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity.getModelData());
		matrix.pushPose();
		matrix.translate(75f, 40.75f, 50);
		matrix.scale(-39, 39, 39);
		matrix.translate(0.5f, 0.5f, 0.5f);
		matrix.mulPose(Quaternion.fromXYZDegrees(new Vector3f(rotation.getY(), rotation.getX(), 180)));
		matrix.translate(-0.5f, -0.5f, -0.5f);
		Lighting.setupForEntityInInventory();
		renderer.renderSingleBlock(tileEntity.getBlockState(), matrix, buffer, 15728880, OverlayTexture.NO_OVERLAY, data);
		buffer.endBatch();

		Matrix4f inverse = matrix.last().pose().copy();
		inverse.invert();
		Vector4f mouseMin = createMouseVector(inverse, mouseX, mouseY, 1000);
		Vector4f mouseMax = createMouseVector(inverse, mouseX, mouseY, -1000);

		BlockHitResult result = AABB.clip(Collections.singleton(BOUNDS), new Vec3(mouseMin.x(), mouseMin.y(), mouseMin.z()), new Vec3(mouseMax.x(), mouseMax.y(), mouseMax.z()),
				new BlockPos(0, 0, 0));
		if (result != null) {
			highlightedSide = result.getDirection();
			SideConfigurationComponent sideConfig = tileEntity.getComponent(SideConfigurationComponent.class);
			if (sideConfig != null) {
				boolean enabled = sideConfig.getWorldSpaceEnabledState(highlightedSide);
				if (enabled) {
					MachineSideMode mode = sideConfig.getWorldSpaceDirectionConfiguration(highlightedSide);
					Color color = mode.getColor().copy();
					color.setAlpha(0.75f);

					matrix.pushPose();
					matrix.translate(0.5f, 0.5f, 0.5f);
					matrix.mulPose(result.getDirection().getRotation());
					matrix.translate(-0.5f, -0.5f, -0.5f);
					HIGHLIGHT_RENDERER.drawPreviewCube(new Vector3f(0.3f, 1.01f, 0.3f), new Vector3f(0.4f, 0.025f, 0.4f), color, matrix);
					matrix.popPose();
				} else {
					highlightedSide = null; // Clear out the highlighted side if its a disabled side.
				}
			}
		} else {
			highlightedSide = null;
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
	public EInputResult mouseClick(double mouseX, double mouseY, int button) {
		if (this.getBounds().isPointInBounds(new Vector2D(mouseX, mouseY))) {
			if (this.getTabState() == TabState.OPEN) {
				mouseDownInside = true;
			}
		} else {
			mouseDownInside = false;
		}
		mouseDownLocation = new Vector2D(mouseX, mouseY);
		return super.mouseClick(mouseX, mouseY, button);
	}

	@Override
	public EInputResult mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (mouseDownInside) {
			rotationVelocity.setX((float) deltaX * -1.5f);
			rotationVelocity.setY((float) deltaY * -1.5f);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	@SuppressWarnings("resource")
	@Override
	public EInputResult mouseReleased(double mouseX, double mouseY, int button) {
		// Make sure a side is highlighted AND the mouse hasn't moved too much. IF it
		// has, we don't change the side mode becuase its probably the result of someone
		// rotating the block, not intending to change the side.
		if (highlightedSide != null && (new Vector2D(mouseX, mouseY).subtract(mouseDownLocation).getLength() < 2.0f)) {
			SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);

			// Middle mouse blocks all sides up.
			if (ModKeyBindings.RESET_SIDE_CONFIGURATION.isDown()) {
				sideComp.setToDefault();
				Minecraft.getInstance().level.playLocalSound(tileEntity.getBlockPos(), SoundEvents.PAINTING_PLACE, SoundSource.MASTER, 0.6f, 1.1f, false);
			} else {
				SideIncrementDirection direction = button == 0 ? SideIncrementDirection.FORWARD : SideIncrementDirection.BACKWARDS;
				sideComp.modulateWorldSpaceSideMode(highlightedSide, direction);
				Minecraft.getInstance().level.playLocalSound(tileEntity.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 0.6f, button == 0 ? 1.25f : 1.1f, false);
			}

			// Send a packet to the server with the updated values.
			NetworkMessage msg = new PacketSideConfigTab(sideComp.getWorldSpaceConfiguration(), tileEntity.getBlockPos());
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	public TranslatableComponent conditionallyGetCardinal(BlockSide side) {
		if (tileEntity instanceof BlockEntityBase) {
			BlockEntityBase te = (BlockEntityBase) tileEntity;
			return new TranslatableComponent("gui." + SideConfigurationUtilities.getDirectionFromSide(side, te.getFacingDirection()).toString().toLowerCase());
		}
		return new TranslatableComponent("ERROR");
	}
}