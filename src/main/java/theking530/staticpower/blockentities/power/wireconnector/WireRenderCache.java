package theking530.staticpower.blockentities.power.wireconnector;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;

public class WireRenderCache {
	public static final int SLICES_PER_BLOCK = 10;

	public record WirePoint(Vec3 start, Vec3 end, Vec3 tangent, Vec3 normal, Vec3 biTangent) {
	}

	private final Vec3 start;
	private final Vec3 end;
	private final List<WirePoint> pointCache;
	private final List<Vector3f> vertecies;
	private final List<Vector4f> colors;

	private boolean pointCacheDirty;
	private boolean lightingCacheDirty;
	private boolean geometryCacheDirty;

	private float wireThickness;
	private float wireSagCoefficient;

	public WireRenderCache(Vec3 start, Vec3 end, float wireThickness, float wireSagCoefficient) {
		this.start = start;
		this.end = end;
		this.wireThickness = wireThickness;
		this.wireSagCoefficient = wireSagCoefficient;
		pointCache = new ArrayList<WirePoint>();
		vertecies = new ArrayList<Vector3f>();
		colors = new ArrayList<Vector4f>();
	}

	public void render(Level level, PoseStack pose, Camera camera, Frustum frustum) {
		if (pointCacheDirty) {
			cachePoints(start, end);
			pointCacheDirty = false;
		}
		if (geometryCacheDirty) {
			cacheVertecies();
			geometryCacheDirty = false;
		}

		if (lightingCacheDirty) {
			cacheLighting(level);
			lightingCacheDirty = false;
		}

		Matrix4f matrix = pose.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		for (int i = 0; i < vertecies.size() / 8; i++) {
			for (int v = 0; v < 8; v++) {
				bufferbuilder.vertex(matrix, vertecies.get(i + v).x(), vertecies.get(i + v).y(), vertecies.get(i + v).z())
						.color(colors.get(i + v).x(), colors.get(i + v).y(), colors.get(i + v).z(), colors.get(i + v).w()).endVertex();
			}
		}
		tessellator.end();
	}

	private void cacheVertecies() {
		vertecies.clear();

		for (WirePoint point : pointCache) {
			Vector3D segStart = new Vector3D(point.start());
			Vector3D segEnd = new Vector3D(point.end());
			Vector3D normal = new Vector3D(point.normal());
			normal.multiply(wireThickness);
			Vector3D biTangent = new Vector3D(point.biTangent());
			biTangent.normalize();
			biTangent.multiply(wireThickness);

			vertecies.add(new Vector3f(segStart.getX() + normal.getX(), segStart.getY() + normal.getY(), segStart.getZ() + normal.getZ()));
			vertecies.add(new Vector3f(segEnd.getX() + normal.getX(), segEnd.getY() + normal.getY(), segEnd.getZ() + normal.getZ()));
			vertecies.add(new Vector3f(segEnd.getX() - normal.getX(), segEnd.getY() - normal.getY(), segEnd.getZ() - normal.getZ()));
			vertecies.add(new Vector3f(segStart.getX() - normal.getX(), segStart.getY() - normal.getY(), segStart.getZ() - normal.getZ()));

			vertecies.add(new Vector3f(segStart.getX() + biTangent.getX(), segStart.getY() + biTangent.getY(), segStart.getZ() + biTangent.getZ()));
			vertecies.add(new Vector3f(segEnd.getX() + biTangent.getX(), segEnd.getY() + biTangent.getY(), segEnd.getZ() + biTangent.getZ()));
			vertecies.add(new Vector3f(segEnd.getX() - biTangent.getX(), segEnd.getY() - biTangent.getY(), segEnd.getZ() - biTangent.getZ()));
			vertecies.add(new Vector3f(segStart.getX() - biTangent.getX(), segStart.getY() - biTangent.getY(), segStart.getZ() - biTangent.getZ()));
		}
	}

	private void cacheLighting(Level level) {
		colors.clear();

		for (WirePoint point : pointCache) {
			Vector3D segStart = new Vector3D(point.start());
			Vector3D segEnd = new Vector3D(point.end());

			// Lighting
			Color startColor = new Color(1, 0.55f, 0.1f);
			Color endColor = startColor.copy();
			{

				BlockPos startPos = new BlockPos((int) segStart.getX(), (int) segStart.getY(), (int) segStart.getZ());
				BlockPos endPos = new BlockPos((int) segEnd.getX(), (int) segEnd.getY(), (int) segEnd.getZ());

				int startLightLevel = getLightLevelAtPosition(level, startPos);
				int endLightLevel = getLightLevelAtPosition(level, endPos);

				float startDarkenAmount = 1 - (startLightLevel / 16.0f);
				startDarkenAmount *= 0.75f;
				startColor.darken(startDarkenAmount, startDarkenAmount, startDarkenAmount, 0);
				startColor.desaturate(startDarkenAmount * 1.25f);

				float endDarkenAmount = 1 - (endLightLevel / 16.0f);
				endDarkenAmount *= 0.75f;
				endColor.darken(endDarkenAmount, endDarkenAmount, endDarkenAmount, 0);
				endColor.desaturate(endDarkenAmount * 1.25f);
			}

			colors.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));
			colors.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colors.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colors.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));

			colors.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));
			colors.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colors.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colors.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));
		}
	}

	protected int getLightLevelAtPosition(Level level, BlockPos position) {
		int light = level.getBrightness(LightLayer.SKY, position) - level.getSkyDarken();
		float sunAngle = level.getSunAngle(1.0F);
		float sunOffset = sunAngle < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
		sunAngle += (sunOffset - sunAngle) * 0.2F;
		light = Math.round((float) light * Mth.cos(sunAngle));
		light = Math.max(light, level.getBrightness(LightLayer.BLOCK, position));
		return (int) Mth.clamp(light * 1.5f, 0, 15);
	}

	private void cachePoints(Vec3 start, Vec3 end) {
		pointCache.clear();

		double distance = start.subtract(end).length();
		int slices = (int) (Math.floor(distance) * SLICES_PER_BLOCK);
		float sliceSize = 1.0f / slices;

		Vec3 controlPoint = start.add(end).multiply(0.5, 0.5, 0.5).subtract(0, distance / wireSagCoefficient, 0.0f);
		Vec3 tangent = SDMath.getQuadrativeBezierDerivative(0, start, controlPoint, end);
		Vec3 normal = SDMath.getQuadraticBezierNormal(0, start, controlPoint, end);
		Vec3 biTangent = tangent.cross(normal);
		pointCache.add(new WirePoint(start, SDMath.getPointAlongQuadraticBezierCurve(sliceSize, start, controlPoint, end), tangent, normal, biTangent));

		for (int i = 0; i < slices; i++) {
			float t = (float) i / slices;
			WirePoint previous = pointCache.get(i);

			Vec3 newTangent = SDMath.getQuadrativeBezierDerivative(0, start, controlPoint, end);
			Vec3 newNormal = SDMath.getQuadraticBezierNormal(t, start, controlPoint, end);
			if (newNormal.dot(previous.normal) < 0) {
				newNormal.multiply(-1, -1, -1);
			}
			Vec3 newBiTangent = tangent.cross(normal);

			Vec3 pointStart = SDMath.getPointAlongQuadraticBezierCurve(t, start, controlPoint, end);
			Vec3 pointEnd = SDMath.getPointAlongQuadraticBezierCurve(t + sliceSize, start, controlPoint, end);
			pointCache.add(new WirePoint(pointStart, pointEnd, newTangent, newNormal, newBiTangent));
		}
	}
}
