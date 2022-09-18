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
import theking530.staticcore.utilities.SDMath;
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

	public Vec3 getWireAttachLocation() {
		Vector3D normal = new Vector3D(getTileEntity().getFacingDirection().getNormal());
		normal.multiply(0.2f);
		return new Vec3(getPos().getX() + 0.5f - normal.getX(), getPos().getY() + 0.5f - normal.getY(), getPos().getZ() + 0.5f - normal.getZ());
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

				Vec3 start = startBe.getComponent(WirePowerCableComponent.class).getWireAttachLocation();
				Vec3 end = getWireAttachLocation().add(0.001f, 0.001f, 0.001f);

				AABB cableBounds = new AABB(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
				if (frustum.isVisible(cableBounds)) {
					drawCable(pose, camera, frustum, start, end);
				}
			}
		}

		RenderSystem.enableCull();
	}

	protected void drawCable(PoseStack pose, Camera camera, Frustum frustum, Vec3 start, Vec3 end) {
		Matrix4f matrix = pose.last().pose();
		float wireThickness = 0.01f;

		int slicesPerBlock = 10;
		double distance = start.subtract(end).length();

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		Vec3 controlPoint = start.add(end).multiply(0.5, 0.5, 0.5).subtract(0, distance / 10, 0.0f);

		List<WirePoint> points = generatePontsOnWire(slicesPerBlock, start, controlPoint, end);

		for (WirePoint point : points) {
			Vector3D segStart = new Vector3D(point.start());
			Vector3D segEnd = new Vector3D(point.end());
			Vector3D normal = new Vector3D(point.normal());
			normal.multiply(wireThickness);
			Vector3D biTangent = new Vector3D(point.biTangent());
			biTangent.normalize();
			biTangent.multiply(wireThickness);

			// Lighting
			Color startColor = new Color(1, 0.55f, 0.1f);
			Color endColor = startColor.copy();
			{

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
			}

			bufferbuilder.vertex(matrix, segStart.getX() + normal.getX(), segStart.getY() + normal.getY(), segStart.getZ() + normal.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() + normal.getX(), segEnd.getY() + normal.getY(), segEnd.getZ() + normal.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() - normal.getX(), segEnd.getY() - normal.getY(), segEnd.getZ() - normal.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segStart.getX() - normal.getX(), segStart.getY() - normal.getY(), segStart.getZ() - normal.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();

			bufferbuilder.vertex(matrix, segStart.getX() + biTangent.getX(), segStart.getY() + biTangent.getY(), segStart.getZ() + biTangent.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() + biTangent.getX(), segEnd.getY() + biTangent.getY(), segEnd.getZ() + biTangent.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segEnd.getX() - biTangent.getX(), segEnd.getY() - biTangent.getY(), segEnd.getZ() - biTangent.getZ())
					.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
			bufferbuilder.vertex(matrix, segStart.getX() - biTangent.getX(), segStart.getY() - biTangent.getY(), segStart.getZ() - biTangent.getZ())
					.color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();

		}
		tessellator.end();
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

	public List<WirePoint> generatePontsOnWire(int slicesPerBlock, Vec3 start, Vec3 controlPoint, Vec3 end) {
		List<WirePoint> output = new ArrayList<WirePoint>();

		double distance = start.subtract(end).length();
		int slices = (int) (Math.floor(distance) * slicesPerBlock);
		float sliceSize = 1.0f / slices;

		Vec3 tangent = SDMath.getQuadrativeBezierDerivative(0, start, controlPoint, end);
		Vec3 normal = SDMath.getQuadraticBezierNormal(0, start, controlPoint, end);
		Vec3 biTangent = tangent.cross(normal);
		output.add(new WirePoint(start, SDMath.getPointAlongQuadraticBezierCurve(sliceSize, start, controlPoint, end), tangent, normal, biTangent));

		for (int i = 0; i < slices; i++) {
			float t = (float) i / slices;
			WirePoint previous = output.get(i);

			Vec3 newTangent = SDMath.getQuadrativeBezierDerivative(0, start, controlPoint, end);
			Vec3 newNormal = SDMath.getQuadraticBezierNormal(t, start, controlPoint, end);
			if (newNormal.dot(previous.normal) < 0) {
				newNormal.multiply(-1, -1, -1);
			}
			Vec3 newBiTangent = tangent.cross(normal);

			Vec3 pointStart = SDMath.getPointAlongQuadraticBezierCurve(t, start, controlPoint, end);
			Vec3 pointEnd = SDMath.getPointAlongQuadraticBezierCurve(t + sliceSize, start, controlPoint, end);
			output.add(new WirePoint(pointStart, pointEnd, newTangent, newNormal, newBiTangent));
		}

		return output;
	}

	public record WirePoint(Vec3 start, Vec3 end, Vec3 tangent, Vec3 normal, Vec3 biTangent) {
	}
}
