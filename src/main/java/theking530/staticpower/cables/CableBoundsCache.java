package theking530.staticpower.cables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import theking530.common.utilities.RaytracingUtilities;
import theking530.common.utilities.RaytracingUtilities.AdvancedRayTraceResult;
import theking530.common.utilities.Vector3D;
import theking530.common.wrench.IWrenchTool;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;

public class CableBoundsCache {

	private VoxelShape CoreShape;
	private final HashMap<Direction, VoxelShape> CableAttachmentShapes;
	private final double CableRadius;
	private final Vector3D defaultAttachmentBounds;
	private boolean IsCached;

	public CableBoundsCache(double cableRadius, Vector3D defaultAttachmentBounds) {
		this.defaultAttachmentBounds = defaultAttachmentBounds;
		CableRadius = cableRadius;
		IsCached = false;
		CableAttachmentShapes = new HashMap<Direction, VoxelShape>();
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

		// Get the cable attachment.
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(entity.getEntityWorld(), pos);

		// If we are holding an attachment that is NOT valid for this cable, return null
		// early.
		if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof AbstractCableAttachment && !cable.canAttachAttachment(entity.getHeldItemMainhand())) {
			return null;
		}

		// Get the bounding boxes for this attachment.
		List<AxisAlignedBB> aabbs = new ArrayList<AxisAlignedBB>();
		for (Direction dir : Direction.values()) {
			// Set our bounds source to the attachment on the provided side.
			ItemStack attachment = cable.getAttachment(dir);

			// If we are holding a valid attachment however, then use that instead.
			if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof AbstractCableAttachment) {
				attachment = entity.getHeldItemMainhand();
			}

			// Get the bounds. In NO case should we not add an entry here. It can be empty
			// if needed. IF we are missing an entry here (ie. < 6 entries), we will crash.
			aabbs.add(getShapeForAttachment(entity.getEntityWorld(), pos, attachment, dir).getBoundingBox());
		}

		// Raytrace against all 6 attachment bounds.
		AdvancedRayTraceResult<BlockRayTraceResult> result = RaytracingUtilities.collisionRayTrace(pos, vec.getLeft(), vec.getRight(), aabbs);

		// Check to see which direction's bounds we hit and return it.
		if (result != null) {
			for (Direction dir : Direction.values()) {
				if (aabbs.get(dir.ordinal()).equals(result.bounds)) {
					return dir;
				}
			}
		}

		return null;
	}

	/**
	 * Cache the cable shapes.
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
	}

	protected VoxelShape getShapeForAttachment(IBlockReader world, BlockPos pos, ItemStack attachment, Direction side) {
		if (attachment.getItem() instanceof AbstractCableAttachment || CableUtilities.getConnectionState(world, pos, side) == CableConnectionState.TILE_ENTITY) {
			double attachmentMin = 8.0D - defaultAttachmentBounds.getX();
			double attachmentMax = 8.0D + defaultAttachmentBounds.getX();
			double attachmentDepth = defaultAttachmentBounds.getZ();

			if (attachment.getItem() instanceof AbstractCableAttachment) {
				AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
				attachmentMin = 8.0D - attachmentItem.getBounds().getX();
				attachmentMax = 8.0D + attachmentItem.getBounds().getX();
				attachmentDepth = attachmentItem.getBounds().getZ();
			}

			switch (side) {
			case NORTH:
				return Block.makeCuboidShape(attachmentMin, attachmentMin, 0.0D, attachmentMax, attachmentMax, attachmentMin - attachmentDepth);
			case SOUTH:
				return Block.makeCuboidShape(attachmentMin, attachmentMin, attachmentMax + attachmentDepth, attachmentMax, attachmentMax, 16.0D);
			case EAST:
				return Block.makeCuboidShape(attachmentMax + attachmentDepth, attachmentMin, attachmentMin, 16.0D, attachmentMax, attachmentMax);
			case WEST:
				return Block.makeCuboidShape(0.0D, attachmentMin, attachmentMin, attachmentMin - attachmentDepth, attachmentMax, attachmentMax);
			case UP:
				return Block.makeCuboidShape(attachmentMin, attachmentMax + attachmentDepth, attachmentMin, attachmentMax, 16.0D, attachmentMax);
			case DOWN:
				return Block.makeCuboidShape(attachmentMin, 0.0D, attachmentMin, attachmentMax, attachmentMin - attachmentDepth, attachmentMax);
			}
		}
		return CoreShape;
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
			// Get some attributes to use in the check.
			AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(entity.getEntityWorld(), pos);
			boolean hasAttachmentOnSide = cable.hasAttachment(hoveredDirection);
			CableConnectionState connectionState = CableUtilities.getConnectionState(entity.getEntityWorld(), pos, hoveredDirection);

			// If connected to a tile entity, or if we're holding an attachment and looking
			// at a side that can take one, or we're holding a wrench on a side that has an
			// attachment, or the side straight up has an attachment, add the attachment
			// bounds on that side.
			if (connectionState == CableConnectionState.TILE_ENTITY) {
				shape = VoxelShapes.or(shape, getShapeForAttachment(entity.getEntityWorld(), pos, cable.getAttachment(hoveredDirection), hoveredDirection));
			} else if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof AbstractCableAttachment && connectionState != CableConnectionState.CABLE && !hasAttachmentOnSide) {
				shape = VoxelShapes.or(shape, getShapeForAttachment(entity.getEntityWorld(), pos, entity.getHeldItemMainhand(), hoveredDirection));
			} else if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof IWrenchTool && connectionState != CableConnectionState.CABLE && hasAttachmentOnSide) {
				shape = VoxelShapes.or(shape, getShapeForAttachment(entity.getEntityWorld(), pos, cable.getAttachment(hoveredDirection), hoveredDirection));
			} else if (hasAttachmentOnSide) {
				shape = VoxelShapes.or(shape, getShapeForAttachment(entity.getEntityWorld(), pos, cable.getAttachment(hoveredDirection), hoveredDirection));
			}
		}

		// Return either the new or initially provided shape.
		return shape;
	}

}