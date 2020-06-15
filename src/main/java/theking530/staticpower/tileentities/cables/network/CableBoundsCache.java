package theking530.staticpower.tileentities.cables.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import theking530.api.utilities.RaytracingUtilities;
import theking530.api.utilities.RaytracingUtilities.AdvancedRayTraceResult;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;

public class CableBoundsCache {

	private VoxelShape CoreShape;
	private HashMap<Direction, VoxelShape> CableAttachmentShapes;
	private HashMap<Direction, VoxelShape> TileEntityAttachmentShapes;
	private final List<AxisAlignedBB> TileEntityAttachmentBounds;
	private double CableRadius;
	private double AttachmentRadius;
	private double AttachmentDepth;
	private boolean IsCached;

	public CableBoundsCache(double cableRadius, double attachmentRadius, double attachmentDepth) {
		CableRadius = cableRadius;
		AttachmentRadius = attachmentRadius;
		AttachmentDepth = attachmentDepth;
		IsCached = false;
		CableAttachmentShapes = new HashMap<Direction, VoxelShape>();
		TileEntityAttachmentShapes = new HashMap<Direction, VoxelShape>();
		TileEntityAttachmentBounds = new ArrayList<AxisAlignedBB>();
	}

	/**
	 * Gets the shape of the bounding box for this cable based on the cable and tile
	 * entity attachments.
	 * 
	 * @param state
	 * @param world
	 * @param pos
	 * @param ctx
	 * @return
	 */
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		// Cache the shapes if need be.
		if (!IsCached) {
			// Cache the shapes
			cacheShapes(state);

			// Cache the bounds as well.
			TileEntityAttachmentShapes.values().forEach(teShape -> TileEntityAttachmentBounds.add(teShape.getBoundingBox()));

			// Mark the shapes as cached.
			IsCached = true;
		}

		// Get the output.
		VoxelShape output = CoreShape;

		// Add the shapes for each side.
		for (Direction dir : Direction.values()) {
			if (CableUtilities.isSideConnectionDisabled(world, pos, dir)) {
				continue;
			}
			if (CableUtilities.getConnectionState(world, pos, dir) == CableConnectionState.CABLE || CableUtilities.getConnectionState(world, pos, dir) == CableConnectionState.TILE_ENTITY) {
				output = VoxelShapes.or(output, CableAttachmentShapes.get(dir));
			}
		}

		// Add the attachment outline.
		if (ctx.getEntity() != null && ctx.getEntity() instanceof PlayerEntity) {
			output = addAttachmentOutline(pos, (PlayerEntity) ctx.getEntity(), output);
		}

		return output;
	}

	/**
	 * Gets the direction of the attachment that is hovered (or null if no
	 * attachment side is hovered).
	 * 
	 * @param pos    The position to check.
	 * @param entity The entity that is hovering.
	 * @return
	 */
	public @Nullable Direction getHoveredAttachmentDirection(BlockPos pos, PlayerEntity entity) {
		// Get the start and end vectors for the raytracing.
		Pair<Vec3d, Vec3d> vec = RaytracingUtilities.getVectors(entity);

		// Raytrace against all 6 attachment bounds.
		AdvancedRayTraceResult<BlockRayTraceResult> result = RaytracingUtilities.collisionRayTrace(pos, vec.getLeft(), vec.getRight(), TileEntityAttachmentBounds);

		// Check to see which direction's bounds we hit and return it.
		if (result != null) {
			for (Direction dir : Direction.values()) {
				if (TileEntityAttachmentShapes.get(dir).getBoundingBox().equals(result.bounds)) {
					return dir;
				}
			}
		}

		return null;
	}

	/**
	 * Caches all the shapes, including the {@link #CoreShape}, the
	 * {@link #CableAttachmentShapes} and the {@link #TileEntityAttachmentShapes}.
	 * The {@link #TileEntityAttachmentBounds} are populated automatically
	 * afterwards.
	 * 
	 * @param state
	 */
	protected void cacheShapes(BlockState state) {
		double coreMin = 8.0D - CableRadius;
		double coreMax = 8.0D + CableRadius;
		CoreShape = Block.makeCuboidShape(coreMin, coreMin, coreMin, coreMax, coreMax, coreMax);

		CableAttachmentShapes.put(Direction.NORTH, Block.makeCuboidShape(coreMin, coreMin, 0.0D, coreMax, coreMax, coreMin));
		CableAttachmentShapes.put(Direction.SOUTH, Block.makeCuboidShape(coreMin, coreMin, coreMax, coreMax, coreMax, 16.0D));
		CableAttachmentShapes.put(Direction.EAST, Block.makeCuboidShape(coreMax, coreMin, coreMin, 16.0D, coreMax, coreMax));
		CableAttachmentShapes.put(Direction.WEST, Block.makeCuboidShape(0.0D, coreMin, coreMin, coreMin, coreMax, coreMax));
		CableAttachmentShapes.put(Direction.UP, Block.makeCuboidShape(coreMin, coreMax, coreMin, coreMax, 16.0D, coreMax));
		CableAttachmentShapes.put(Direction.DOWN, Block.makeCuboidShape(coreMin, 0.0D, coreMin, coreMax, coreMin, coreMax));

		double attachmentMin = 8.0D - AttachmentRadius;
		double attachmentMax = 8.0D + AttachmentRadius;
		TileEntityAttachmentShapes.put(Direction.NORTH, Block.makeCuboidShape(attachmentMin, attachmentMin, 0.0D, attachmentMax, attachmentMax, attachmentMin - AttachmentDepth));
		TileEntityAttachmentShapes.put(Direction.SOUTH, Block.makeCuboidShape(attachmentMin, attachmentMin, attachmentMax + AttachmentDepth, attachmentMax, attachmentMax, 16.0D));
		TileEntityAttachmentShapes.put(Direction.EAST, Block.makeCuboidShape(attachmentMax + AttachmentDepth, attachmentMin, attachmentMin, 16.0D, attachmentMax, attachmentMax));
		TileEntityAttachmentShapes.put(Direction.WEST, Block.makeCuboidShape(0.0D, attachmentMin, attachmentMin, attachmentMin - AttachmentDepth, attachmentMax, attachmentMax));
		TileEntityAttachmentShapes.put(Direction.UP, Block.makeCuboidShape(attachmentMin, attachmentMax + AttachmentDepth, attachmentMin, attachmentMax, 16.0D, attachmentMax));
		TileEntityAttachmentShapes.put(Direction.DOWN, Block.makeCuboidShape(attachmentMin, 0.0D, attachmentMin, attachmentMax, attachmentMin - AttachmentDepth, attachmentMax));
	}

	/**
	 * Adds an attachment outline if one was hovered. Otherwise, just returns the
	 * shape that was provided.
	 * 
	 * @param pos    The position of the cable.
	 * @param entity The entity that is looking at the cable.
	 * @param shape  The shape to add the outline too.
	 * @return
	 */
	protected VoxelShape addAttachmentOutline(BlockPos pos, PlayerEntity entity, VoxelShape shape) {
		// Gets the hovered location.
		Direction hoveredDirection = getHoveredAttachmentDirection(pos, entity);

		// If the hovered direction is not null, add the attachment shape.
		if (hoveredDirection != null) {
			if (CableUtilities.getConnectionState(entity.getEntityWorld(), pos, hoveredDirection) == CableConnectionState.TILE_ENTITY) {
				shape = VoxelShapes.or(shape, TileEntityAttachmentShapes.get(hoveredDirection));
			} else if (entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof AbstractCableAttachment) {
				shape = VoxelShapes.or(shape, TileEntityAttachmentShapes.get(hoveredDirection));
			}
		}

		// Return either the new or initially provided shape.
		return shape;
	}

}
