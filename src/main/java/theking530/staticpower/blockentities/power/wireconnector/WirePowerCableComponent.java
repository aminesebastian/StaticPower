package theking530.staticpower.blockentities.power.wireconnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.cables.SparseCableLink.SparseCableConnectionType;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.destinations.CableDestination;
import theking530.staticpower.cables.network.destinations.ModCableDestinations;
import theking530.staticpower.cables.network.modules.CableNetworkModuleTypes;
import theking530.staticpower.cables.power.PowerCableComponent;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.utilities.WorldUtilities;

public class WirePowerCableComponent extends PowerCableComponent {

	public WirePowerCableComponent(String name, StaticPowerVoltage voltage, double maxPower, double powerLoss) {
		super(name, CableNetworkModuleTypes.POWER_NETWORK_MODULE, false, voltage, maxPower, powerLoss);
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
	}

	@Override
	protected void initializeCableProperties(ServerCable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);

		Direction attachToSide = state.getValue(StaticPowerBlock.FACING).getOpposite();
		for (Direction side : Direction.values()) {
			if (side != attachToSide) {
				cable.setDisabledStateOnSide(side, true);
			}
		}
	}

	@Override
	public void onOwningBlockEntityLoaded(Level level, BlockPos pos, BlockState state) {
		super.onOwningBlockEntityLoaded(level, pos, state);
		if (isClientSide()) {
			CustomRenderer.TEMP_CABLES.put(getPos(), this);
		}
	}

	@Override
	public void setSideDisabledState(Direction side, boolean disabledState) {
		// DO NOT DELETE THIS, WE CLEAR THIS METHOD OUT FOR A REASON.
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Power.get());
	}

	@Override
	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		CustomRenderer.TEMP_CABLES.remove(getPos());
		if (!isClientSide()) {
			ServerCable cable = CableNetworkManager.get(getLevel()).getCable(getPos());
			for (SparseCableLink link : cable.getSparseLinks()) {
				ItemStack wireStack = ItemStack.of(link.data().getCompound("wire"));
				WorldUtilities.dropItem(getLevel(), getPos(), wireStack);
			}
		}

		super.onOwningBlockEntityBroken(state, newState, isMoving);
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	protected void sparseConnectionAdded(SparseCableLink link) {

	}

	@Override
	protected void sparseConnectionsRemoved(List<SparseCableLink> links) {
		for (SparseCableLink link : links) {
			ItemStack wireStack = ItemStack.of(link.data().getCompound("wire"));
			WorldUtilities.dropItem(getLevel(), getPos(), wireStack);
		}

		if (links.size() > 0) {
			getLevel().playSound(null, getPos(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 0.5f, 0.75f);
		}
	}

	@Override
	protected boolean shouldCreateSparseCable() {
		return true;
	}

	public Vector3D getWireAttachLocation() {
		Vector3D normal = new Vector3D(getTileEntity().getFacingDirection().getNormal());
		normal.multiply(0.2f);
		return new Vector3D(getPos().getX() + 0.5f - normal.getX(), getPos().getY() + 0.5f - normal.getY(), getPos().getZ() + 0.5f - normal.getZ());
	}

	public void renderConnections(PoseStack pose, Camera camera, Frustum frustum) {
		if (!isClientSide() || !getTileEntity().isValid()) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableCull();

		for (SparseCableLink link : getSparseLinks()) {
			if (link.connectionType() == SparseCableConnectionType.ENDING) {
				// Try to get the start. If we can't, stop.
				BlockEntityBase startBe = (BlockEntityBase) getLevel().getBlockEntity(link.linkToPosition());
				if (startBe == null) {
					StaticPower.LOGGER.error(String.format("Encountered ending SparseCableLink with a null starting BlockEntity at location: %1$s.", getPos().toString()));
					continue;
				}

				Vector3D start = startBe.getComponent(WirePowerCableComponent.class).getWireAttachLocation();
				Vector3D end = getWireAttachLocation().add(0.001f, 0.001f, 0.001f);

				AABB cableBounds = new AABB(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
				if (frustum.isVisible(cableBounds)) {
					drawCable(pose, camera, frustum, start, end);
				}
			}
		}

		RenderSystem.enableCull();
	}

	protected void drawCable(PoseStack pose, Camera camera, Frustum frustum, Vector3D start, Vector3D end) {
		Matrix4f matrix = pose.last().pose();
		float wireThickness = 0.02f;

		int slicesPerBlock = 10;
		float distance = start.copy().substract(end).getLength();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		Vector3D controlPoint = start.copy().add(end).divide(2.0f);
		controlPoint.substract(0, distance / 10, 0.0f);

		List<WirePoint> points = generatePontsOnWire(start, controlPoint, end);

		for (WirePoint point : points) {
			Vector3D segStart = point.start();
			Vector3D segEnd = point.end();
			Vector3D normal = point.normal();
			normal.multiply(wireThickness);

			Color startColor = new Color(1, 0.55f, 0.1f);
			Color endColor = startColor.copy();

			BlockPos startPos = new BlockPos((int) segStart.getX(), (int) segStart.getY(), (int) segStart.getZ());
			BlockPos endPos = new BlockPos((int) segEnd.getX(), (int) segEnd.getY(), (int) segEnd.getZ());

			int startLightLevel = getLightLevelAtPosition(startPos);
			int endLightLevel = getLightLevelAtPosition(endPos);

			float startDarkenAmount = 1 - (startLightLevel / 16.0f);
			startDarkenAmount *= 0.75f;
			startColor.darken(startDarkenAmount, startDarkenAmount, startDarkenAmount, 0);
			startColor.desaturate(startDarkenAmount * 1.25f);

			float endDarkenAmount = 1 - (endLightLevel / 16.0f);
			endDarkenAmount *= 0.75f;
			endColor.darken(endDarkenAmount, endDarkenAmount, endDarkenAmount, 0);
			endColor.desaturate(endDarkenAmount * 1.25f);

			bufferbuilder.vertex(matrix, segStart.getX() + normal.getX(), segStart.getY() + normal.getY(), segStart.getZ() + normal.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() + normal.getX(), segEnd.getY() + normal.getY(), segEnd.getZ() + normal.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() - normal.getX(), segEnd.getY() - normal.getY(), segEnd.getZ() - normal.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segStart.getX() - normal.getX(), segStart.getY() - normal.getY(), segStart.getZ() - normal.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();

			bufferbuilder.vertex(matrix, segStart.getX() + normal.getX(), segStart.getY(), segStart.getZ() + normal.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() + normal.getX(), segEnd.getY(), segEnd.getZ() + normal.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() - normal.getX(), segEnd.getY(), segEnd.getZ() - normal.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segStart.getX() - normal.getX(), segStart.getY(), segStart.getZ() - normal.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
		}
		tessellator.end();
	}

	protected Vector3D findPointAlongBezierCurver(float alpha, Vector3D start, Vector3D end, Vector3D controlPoint) {
		float x = (1 - alpha) * (1 - alpha) * start.getX() + 2 * (1 - alpha) * alpha * controlPoint.getX() + alpha * alpha * end.getX();
		float y = (1 - alpha) * (1 - alpha) * start.getY() + 2 * (1 - alpha) * alpha * controlPoint.getY() + alpha * alpha * end.getY();
		float z = (1 - alpha) * (1 - alpha) * start.getZ() + 2 * (1 - alpha) * alpha * controlPoint.getZ() + alpha * alpha * end.getZ();
		return new Vector3D(x, y, z);

	}

	protected Vec3 getDerivativeAt(float alpha, Vector3D start, Vector3D controlPoint, Vector3D end) {
		Vector3D d1 = new Vector3D(2 * (controlPoint.getX() - start.getX()), 2 * (controlPoint.getY() - start.getY()), 2 * (controlPoint.getZ() - start.getZ()));
		Vector3D d2 = new Vector3D(2 * (end.getX() - controlPoint.getX()), 2 * (end.getY() - controlPoint.getY()), 2 * (controlPoint.getZ() - start.getZ()));

		float x = (1 - alpha) * d1.getX() + alpha * d2.getX();
		float y = (1 - alpha) * d1.getY() + alpha * d2.getY();
		float z = (1 - alpha) * d1.getZ() + alpha * d2.getZ();

		return new Vec3(x, y, z);
	}

	protected Vec3 getSecondDerivativeAt(float alpha, Vector3D start, Vector3D controlPoint, Vector3D end) {
		float x = (-2 * (controlPoint.getX() - start.getX())) + (2 * (end.getX() - controlPoint.getX()));
		float y = (-2 * (controlPoint.getY() - start.getY())) + (2 * (end.getY() - controlPoint.getY()));
		float z = (-2 * (controlPoint.getZ() - start.getZ())) + (2 * (end.getZ() - controlPoint.getZ()));

		return new Vec3(x, y, z);
	}

	protected Vector3D getNormalAt(float alpha, Vector3D start, Vector3D controlPoint, Vector3D end) {
		Vec3 deriv = getDerivativeAt(alpha, start, controlPoint, end).normalize();
		Vec3 secondDeriv = getSecondDerivativeAt(alpha, start, controlPoint, end);
		Vec3 b = deriv.add(secondDeriv).normalize();
		Vec3 r = b.cross(deriv).normalize();
		Vec3 normal = r.cross(deriv).normalize();
		return new Vector3D((float) normal.x, (float) normal.y, (float) normal.z);
	}

	protected int getLightLevelAtPosition(BlockPos position) {
		int light = getLevel().getBrightness(LightLayer.SKY, position) - getLevel().getSkyDarken();
		float sunAngle = getLevel().getSunAngle(1.0F);
		float sunOffset = sunAngle < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
		sunAngle += (sunOffset - sunAngle) * 0.2F;
		light = Math.round((float) light * Mth.cos(sunAngle));
		light = Math.max(light, getLevel().getBrightness(LightLayer.BLOCK, position));
		return (int) Mth.clamp(light * 1.5f, 0, 15);
	}

	public List<WirePoint> generatePontsOnWire(Vector3D start, Vector3D controlPoint, Vector3D end) {
		List<WirePoint> output = new ArrayList<WirePoint>();

		int slicesPerBlock = 10;
		float distance = start.copy().substract(end).getLength();
		int slices = (int) (Math.floor(distance) * slicesPerBlock);
		float sliceSize = 1.0f / slices;

		output.add(new WirePoint(start, findPointAlongBezierCurver(sliceSize, start, end, controlPoint), getNormalAt(0, start, controlPoint, end)));

		for (int i = 0; i < slices; i++) {
			float t = (float) i / slices;
			WirePoint previous = output.get(i);

			Vector3D newNormal = getNormalAt(t, start, controlPoint, end);
			if (newNormal.dot(previous.normal) < 0) {
				newNormal.negate();
			}

			Vector3D pointStart = findPointAlongBezierCurver(t, start, end, controlPoint);
			Vector3D pointEnd = findPointAlongBezierCurver(t + sliceSize, start, end, controlPoint);
			output.add(new WirePoint(pointStart, pointEnd, newNormal));
		}

		return output;
	}

	public record WirePoint(Vector3D start, Vector3D end, Vector3D normal) {
	}
}
