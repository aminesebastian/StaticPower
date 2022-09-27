package theking530.staticpower.client.rendering;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;

public class WireRenderCache {
	public static final int LOW_DETAIL_SLICES_PER_BLOCK = 2;
	public static final int HIGH_DETAIL_SLICES_PER_BLOCK = 10;

	public record WirePoint(Vec3 start, Vec3 end, Vec3 tangent, Vec3 normal, Vec3 biTangent) {
	}

	private final Vec3 start;
	private final Vec3 end;
	private final List<WirePoint> pointCache;
	private final List<Vector3f> vertecies;
	private final List<Vector4f> colors;

	private final List<WirePoint> distancePointCache;
	private final List<Vector3f> distanceVerticies;
	private final List<Vector4f> distanceColors;

	private final AABB cableBounds;

	private boolean pointCacheDirty;
	private boolean lightingCacheDirty;
	private boolean geometryCacheDirty;

	private SDColor color;
	private float wireThickness;
	private float wireSagCoefficient;

	public WireRenderCache(Vec3 start, Vec3 end, SDColor color, float wireThickness, float wireSagCoefficient) {
		this.start = start;
		this.end = end;
		this.cableBounds = new AABB(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
		this.color = color;
		this.wireThickness = wireThickness;
		this.wireSagCoefficient = wireSagCoefficient;

		pointCache = new ArrayList<WirePoint>();
		vertecies = new ArrayList<Vector3f>();
		colors = new ArrayList<Vector4f>();

		distancePointCache = new ArrayList<WirePoint>();
		distanceVerticies = new ArrayList<Vector3f>();
		distanceColors = new ArrayList<Vector4f>();

		pointCacheDirty = true;
		lightingCacheDirty = true;
		geometryCacheDirty = true;
	}

	public void render(Level level, PoseStack pose, Camera camera, Frustum frustum, BufferBuilder bufferbuilder) {
		// Early return to cull out any invisible cables.
		if (!isVisible(camera, frustum)) {
			return;
		}
		invalidatePointCache();
		if (pointCacheDirty) {
			cachePoints(pointCache, start, end, HIGH_DETAIL_SLICES_PER_BLOCK);
			cachePoints(distancePointCache, start, end, LOW_DETAIL_SLICES_PER_BLOCK);
			pointCacheDirty = false;
		}
		if (geometryCacheDirty) {
			cacheVertecies(pointCache, vertecies);
			cacheVertecies(distancePointCache, distanceVerticies);
			geometryCacheDirty = false;
		}
		if (lightingCacheDirty) {
			cacheLighting(level, pointCache, colors);
			cacheLighting(level, distancePointCache, distanceColors);
			lightingCacheDirty = false;
		}

		Matrix4f matrix = pose.last().pose();

		// If we should render the LOD version, render that.
		// Otherwise render the full detail version.
		if (shouldRenderDistanceVersion(camera)) {
			for (int i = 0; i < distanceVerticies.size(); i += 8) {
				for (int v = 0; v < 8; v++) {
					bufferbuilder.vertex(matrix, distanceVerticies.get(i + v).x(), distanceVerticies.get(i + v).y(), distanceVerticies.get(i + v).z())
							.color(distanceColors.get(i + v).x(), distanceColors.get(i + v).y(), distanceColors.get(i + v).z(), distanceColors.get(i + v).w()).endVertex();
				}
			}
		} else {
			for (int i = 0; i < vertecies.size(); i += 8) {
				for (int v = 0; v < 8; v++) {
					bufferbuilder.vertex(matrix, vertecies.get(i + v).x(), vertecies.get(i + v).y(), vertecies.get(i + v).z())
							.color(colors.get(i + v).x(), colors.get(i + v).y(), colors.get(i + v).z(), colors.get(i + v).w()).endVertex();
				}
			}
		}

	}

	public boolean isVisible(Camera camera, Frustum frustum) {
		if (!frustum.isVisible(cableBounds)) {
			return false;
		}

		double minimumDistance = getClosestWireEndpoint(camera);
		return minimumDistance <= getMaximumRenderDistance();
	}

	public boolean shouldRenderDistanceVersion(Camera camera) {
		if (!Minecraft.useFancyGraphics()) {
			return true;
		}
		double minimumDistance = getClosestWireEndpoint(camera);
		return minimumDistance >= getLODRenderDistance();
	}

	/**
	 * TODO: Solve for the cloest point on the bezier, NOT to the terminals. This
	 * will break on wires where the two terminals are far apart but the player is
	 * near the midpoints.
	 * 
	 * @param camera
	 * @return
	 */
	public double getClosestWireEndpoint(Camera camera) {
		double startDistance = camera.getPosition().distanceTo(start);
		double endDistance = camera.getPosition().distanceTo(end);
		return Math.min(startDistance, endDistance);
	}

	@SuppressWarnings("resource")
	public double getMaximumRenderDistance() {
		return Minecraft.getInstance().levelRenderer.getLastViewDistance() * 6;
	}

	public double getLODRenderDistance() {
		return getMaximumRenderDistance() / 3;
	}

	public void invalidatePointCache() {
		pointCacheDirty = true;
		geometryCacheDirty = true;
		lightingCacheDirty = true;
	}

	public void invalidateGeometryCache() {
		geometryCacheDirty = true;
		lightingCacheDirty = true;
	}

	public void invalidateLightingCache() {
		lightingCacheDirty = true;
	}

	private void cacheVertecies(List<WirePoint> points, List<Vector3f> vertexCache) {
		vertexCache.clear();

		float adjustedThickness = wireThickness;
		if (points == distancePointCache) {
			adjustedThickness *= 1.5;
		}
		for (WirePoint point : points) {
			Vector3D segStart = new Vector3D(point.start());
			Vector3D segEnd = new Vector3D(point.end());
			Vector3D normal = new Vector3D(point.normal());
			normal.multiply(adjustedThickness);
			Vector3D biTangent = new Vector3D(point.biTangent());
			biTangent.normalize();
			biTangent.multiply(adjustedThickness);

			vertexCache.add(new Vector3f(segStart.getX() + normal.getX(), segStart.getY() + normal.getY(), segStart.getZ() + normal.getZ()));
			vertexCache.add(new Vector3f(segEnd.getX() + normal.getX(), segEnd.getY() + normal.getY(), segEnd.getZ() + normal.getZ()));
			vertexCache.add(new Vector3f(segEnd.getX() - normal.getX(), segEnd.getY() - normal.getY(), segEnd.getZ() - normal.getZ()));
			vertexCache.add(new Vector3f(segStart.getX() - normal.getX(), segStart.getY() - normal.getY(), segStart.getZ() - normal.getZ()));

			vertexCache.add(new Vector3f(segStart.getX() + biTangent.getX(), segStart.getY() + biTangent.getY(), segStart.getZ() + biTangent.getZ()));
			vertexCache.add(new Vector3f(segEnd.getX() + biTangent.getX(), segEnd.getY() + biTangent.getY(), segEnd.getZ() + biTangent.getZ()));
			vertexCache.add(new Vector3f(segEnd.getX() - biTangent.getX(), segEnd.getY() - biTangent.getY(), segEnd.getZ() - biTangent.getZ()));
			vertexCache.add(new Vector3f(segStart.getX() - biTangent.getX(), segStart.getY() - biTangent.getY(), segStart.getZ() - biTangent.getZ()));
		}
	}

	private void cacheLighting(Level level, List<WirePoint> points, List<Vector4f> colorCache) {
		colorCache.clear();

		for (WirePoint point : points) {
			Vector3D segStart = new Vector3D(point.start());
			Vector3D segEnd = new Vector3D(point.end());

			// Lighting
			SDColor startColor = color.copy();
			SDColor endColor = startColor.copy();
			{
				BlockPos startPos = new BlockPos(segStart.getX(), segStart.getY(), segStart.getZ());
				BlockPos endPos = new BlockPos(segEnd.getX(), segEnd.getY(), segEnd.getZ());

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

			colorCache.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));
			colorCache.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colorCache.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colorCache.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));

			colorCache.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));
			colorCache.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colorCache.add(new Vector4f(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()));
			colorCache.add(new Vector4f(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()));
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

	private void cachePoints(List<WirePoint> cache, Vec3 start, Vec3 end, int slicesPerBlock) {
		cache.clear();

		double distance = start.subtract(end).length();
		int slices = (int) (Math.floor(distance) * slicesPerBlock);
		float sliceSize = 1.0f / slices;
		double rawSag = Math.pow(distance / wireSagCoefficient, 2);
		double sag = Math.min(rawSag, 1); // Maximum sag of 2

		Vec3 controlPoint = start.add(end).multiply(0.5, 0.5, 0.5).subtract(0, sag, 0.0f);
		Vec3 tangent = SDMath.getQuadrativeBezierDerivative(0, start, controlPoint, end);
		Vec3 normal = SDMath.getQuadraticBezierNormal(0, start, controlPoint, end);
		Vec3 biTangent = tangent.cross(normal);
		cache.add(new WirePoint(start, SDMath.getPointAlongQuadraticBezierCurve(sliceSize, start, controlPoint, end), tangent, normal, biTangent));

		for (int i = 0; i < slices; i++) {
			float t = (float) i / slices;
			WirePoint previous = cache.get(i);

			Vec3 newTangent = SDMath.getQuadrativeBezierDerivative(0, start, controlPoint, end);
			Vec3 newNormal = SDMath.getQuadraticBezierNormal(t, start, controlPoint, end);
			if (newNormal.dot(previous.normal) < 0) {
				newNormal.multiply(-1, -1, -1);
			}
			Vec3 newBiTangent = tangent.cross(normal);

			Vec3 pointStart = SDMath.getPointAlongQuadraticBezierCurve(t, start, controlPoint, end);
			Vec3 pointEnd = SDMath.getPointAlongQuadraticBezierCurve(t + sliceSize, start, controlPoint, end);
			cache.add(new WirePoint(pointStart, pointEnd, newTangent, newNormal, newBiTangent));
		}
	}
}
