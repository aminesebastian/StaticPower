package theking530.staticcore.utilities;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RaytracingUtilities {
	/**
	 * Performs a raytrace against the world.
	 * 
	 * @param worldIn
	 * @param player
	 * @param fluidMode
	 * @return
	 */
	public static BlockHitResult findPlayerRayTrace(Level worldIn, LivingEntity player, ClipContext.Fluid fluidMode) {
		float f = player.getXRot();
		float f1 = player.getYRot();
		Vec3 vector3d = player.getEyePosition(1.0F);
		float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
		Vec3 vector3d1 = vector3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
		return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
	}

	/**
	 * Sourced from:
	 * https://github.com/refinedmods/refinedpipes/blob/3b89450d533d37b0ea1033dd03558c7af0b52d5f/src/main/java/com/refinedmods/refinedpipes/util/Raytracer.java
	 * 
	 * @param entity
	 * @return
	 */
	public static Pair<Vec3, Vec3> getVectors(Entity entity) {
		float pitch = entity.getXRot();
		float yaw = entity.getYRot();
		Vec3 start = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
		float f1 = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f3 = -Mth.cos(-pitch * 0.017453292F);
		float f4 = Mth.sin(-pitch * 0.017453292F);
		float f5 = f2 * f3;
		float f6 = f1 * f3;
		double d3 = 5.0D;
		if (entity instanceof ServerPlayer) {
			d3 = ((ServerPlayer) entity).getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
		}
		Vec3 end = start.add(f5 * d3, f4 * d3, f6 * d3);
		return Pair.of(start, end);
	}

	public static AdvancedRayTraceResult<BlockHitResult> collisionRayTrace(BlockPos pos, Vec3 start, Vec3 end, Collection<AABB> boxes) {
		double minDistance = Double.POSITIVE_INFINITY;
		AdvancedRayTraceResult<BlockHitResult> hit = AdvancedRayTraceResult.MISS;
		int i = -1;

		for (AABB aabb : boxes) {
			AdvancedRayTraceResult<BlockHitResult> result = aabb == null ? null : collisionRayTrace(pos, start, end, aabb, i, null);
			if (result != null) {
				double d = result.squareDistanceTo(start);
				if (d < minDistance) {
					minDistance = d;
					hit = result;
				}
			}
			i++;
		}

		return hit;
	}

	public static AdvancedRayTraceResult<BlockHitResult> collisionRayTrace(BlockPos pos, Vec3 start, Vec3 end, AABB bounds, int subHit, Object hitInfo) {
		BlockHitResult result = AABB.clip(Collections.singleton(bounds), start, end, pos);
		if (result == null) {
			return null;
		}

		return new AdvancedRayTraceResult<>(result, bounds);
	}

	public static class AdvancedRayTraceResult<T extends BlockHitResult> {
		public static final AdvancedRayTraceResult<BlockHitResult> MISS = new AdvancedRayTraceResult<BlockHitResult>(null, null);
		public final AABB bounds;
		private final T hit;

		public AdvancedRayTraceResult(T mop, AABB aabb) {
			hit = mop;
			bounds = aabb;
		}

		public boolean valid() {
			return hit != null && bounds != null;
		}

		public Direction getDirection() {
			if (hit != null) {
				return hit.getDirection();
			}
			return null;
		}

		public HitResult.Type getType() {
			if (hit != null) {
				return hit.getType();
			}
			return HitResult.Type.MISS;
		}

		public double squareDistanceTo(Vec3 vec) {
			return hit.getLocation().distanceToSqr(vec);
		}
	}
}
