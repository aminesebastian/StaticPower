package theking530.staticpower.cables;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.utilities.RaytracingUtilities;
import theking530.staticpower.utilities.RaytracingUtilities.AdvancedRayTraceResult;

public class CableBoundsCache {
	private static final Direction[] XAxisDirectionPriority = Direction.values();
	private static final Direction[] ZAxisDirectionPriority = new Direction[] { Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH };
	private static final Direction[] YAxisDirectionPriority = new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN };

	private VoxelShape CoreShape;
	private final HashMap<Direction, VoxelShape> CableExtensionShapes;
	private final double CableRadius;
	@Nullable
	private final Vector3D defaultAttachmentBounds;
	private boolean IsCached;

	public CableBoundsCache(double cableRadius, Vector3D defaultAttachmentBounds) {
		this.defaultAttachmentBounds = defaultAttachmentBounds;
		CableRadius = cableRadius;
		IsCached = false;
		CableExtensionShapes = new HashMap<Direction, VoxelShape>();
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
		// Get some attributes to use in the check.
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(world, pos);
		for (Direction dir : Direction.values()) {
			if ((cable != null && cable.hasAttachment(dir)) || CableUtilities.getConnectionState(world, pos, dir) == CableConnectionState.CABLE
					|| CableUtilities.getConnectionState(world, pos, dir) == CableConnectionState.TILE_ENTITY) {
				output = VoxelShapes.or(output, CableExtensionShapes.get(dir));
			}
		}

		// Add the attachment outline. Don't perform an OR here, let it take in the
		// original and modify that internally.
		if (ctx.getEntity() != null && ctx.getEntity() instanceof PlayerEntity) {
			output = addAttachmentOutline(pos, (PlayerEntity) ctx.getEntity(), ctx, output);
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
	public @Nullable CableBoundsHoverResult getHoveredAttachmentOrCover(BlockPos pos, PlayerEntity entity) {
		// Get the start and end vectors for the raytracing.
		Pair<Vector3d, Vector3d> vec = RaytracingUtilities.getVectors(entity);

		// Get the cable attachment.
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(entity.getEntityWorld(), pos);

		// Leave early if we dont find a cable there (this is in case the block starts
		// rendering before the TE is validated).
		if (cable == null) {
			return CableBoundsHoverResult.EMPTY;
		}

		// If we are holding an attachment that is NOT valid for this cable, return null
		// early.
		if (!entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() instanceof AbstractCableAttachment
				&& !cable.canAttachAttachment(entity.getHeldItemMainhand())) {
			return CableBoundsHoverResult.EMPTY;
		}

		// Get the bounding boxes for this attachment.
		List<CableHoverCheckRequest> bounds = new LinkedList<CableHoverCheckRequest>();
		List<AxisAlignedBB> rawBounds = new LinkedList<AxisAlignedBB>();

		// Setup the order by which we process the hovered directions to prioratize the
		// off angles first.
		Direction[] directionOrder = XAxisDirectionPriority;
		Vector3d lookAtVector = entity.getLookVec();
		Direction lookAtDirection = Direction.getFacingFromVector(lookAtVector.getX(), lookAtVector.getY(), lookAtVector.getZ());

		if (lookAtDirection.getAxis() == Axis.Z) {
			directionOrder = ZAxisDirectionPriority;
		} else if (lookAtDirection.getAxis() == Axis.Y) {
			directionOrder = YAxisDirectionPriority;
		}

		// Generate the bounds.
		for (Direction dir : directionOrder) {
			// Then check for a held cable cover or held cable attachment.
			if (!entity.getHeldItemMainhand().isEmpty()) {
				if (entity.getHeldItemMainhand().getItem() instanceof CableCover) {
					bounds.add(new CableHoverCheckRequest(getAttachmentShapeForSide(entity.getEntityWorld(), pos, entity.getHeldItemMainhand(), dir), dir, CableBoundsHoverType.HELD_COVER));
				}
				if (entity.getHeldItemMainhand().getItem() instanceof AbstractCableAttachment) {
					bounds.add(new CableHoverCheckRequest(getAttachmentShapeForSide(entity.getEntityWorld(), pos, entity.getHeldItemMainhand(), dir), dir,
							CableBoundsHoverType.HELD_ATTACHMENT));
				}
			}

			// Then put in the bounds for an attached attachment.
			if (!cable.getAttachment(dir).isEmpty()) {
				bounds.add(new CableHoverCheckRequest(getAttachmentShapeForSide(entity.getEntityWorld(), pos, cable.getAttachment(dir), dir), dir, CableBoundsHoverType.ATTACHED_ATTACHMENT));
			}

			// Then put in the bounds for an attached cover.
			if (!cable.getCover(dir).isEmpty()) {
				bounds.add(new CableHoverCheckRequest(getAttachmentShapeForSide(entity.getEntityWorld(), pos, cable.getCover(dir), dir), dir, CableBoundsHoverType.ATTACHED_COVER));
			}

			// If the side has neither an attachment, nor a cover, but is still attached to
			// a tile entity, check the default attachment.
			if (CableUtilities.getConnectionState(entity.world, pos, dir) == CableConnectionState.TILE_ENTITY && cable.getCover(dir).isEmpty() && cable.getAttachment(dir).isEmpty()) {
				bounds.add(new CableHoverCheckRequest(getAttachmentShapeForSide(entity.getEntityWorld(), pos, ItemStack.EMPTY, dir), dir, CableBoundsHoverType.DEFAULT_ATTACHMENT));
			}

			// Finally, put the bounds for the cable. Include the bounds for the default
			// attachment too if an attachment does not exist.
			if (CableUtilities.getConnectionState(entity.world, pos, dir) == CableConnectionState.CABLE
					|| CableUtilities.getConnectionState(entity.world, pos, dir) == CableConnectionState.TILE_ENTITY) {
				bounds.add(new CableHoverCheckRequest(CableExtensionShapes.get(dir), dir, CableBoundsHoverType.CABLE));
			}

		}

		// Create a list of just the raw bounds.
		bounds.forEach(x -> rawBounds.add(x.bounds.getBoundingBox()));

		// Raytrace against all 6 attachment bounds.
		AdvancedRayTraceResult<BlockRayTraceResult> result = RaytracingUtilities.collisionRayTrace(pos, vec.getLeft(), vec.getRight(), rawBounds);

		// Check to see which direction's bounds we hit and return it.
		if (result != null) {
			if (result.hit.getFace().getAxis() == Axis.Z) {
				bounds.sort(new Comparator<CableHoverCheckRequest>() {
					@Override
					public int compare(CableHoverCheckRequest o1, CableHoverCheckRequest o2) {
						return o1.direction.getAxis().ordinal() - Axis.X.ordinal();
					}
				});
			}
			for (CableHoverCheckRequest requests : bounds) {
				if (requests.bounds.getBoundingBox().equals(result.bounds)) {
					return new CableBoundsHoverResult(requests.type, result.hit.getFace(), requests.direction);
				}
			}
		}

		return CableBoundsHoverResult.EMPTY;
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

		CableExtensionShapes.put(Direction.NORTH, Block.makeCuboidShape(coreMin, coreMin, 0.0D, coreMax, coreMax, coreMin));
		CableExtensionShapes.put(Direction.SOUTH, Block.makeCuboidShape(coreMin, coreMin, coreMax, coreMax, coreMax, 16.0D));
		CableExtensionShapes.put(Direction.EAST, Block.makeCuboidShape(coreMax, coreMin, coreMin, 16.0D, coreMax, coreMax));
		CableExtensionShapes.put(Direction.WEST, Block.makeCuboidShape(0.0D, coreMin, coreMin, coreMin, coreMax, coreMax));
		CableExtensionShapes.put(Direction.UP, Block.makeCuboidShape(coreMin, coreMax, coreMin, coreMax, 16.0D, coreMax));
		CableExtensionShapes.put(Direction.DOWN, Block.makeCuboidShape(coreMin, 0.0D, coreMin, coreMax, coreMin, coreMax));
	}

	protected VoxelShape getAttachmentShapeForSide(IBlockReader world, BlockPos pos, @Nullable ItemStack attachment, Direction side) {
		double attachmentMin = 8.0D - getDefaultAttachmentBounds(world, pos, side).getX();
		double attachmentMax = 8.0D + getDefaultAttachmentBounds(world, pos, side).getX();
		double attachmentDepth = getDefaultAttachmentBounds(world, pos, side).getZ();

		if (attachment != null) {
			if (attachment.getItem() instanceof AbstractCableAttachment) {
				AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
				attachmentMin = 8.0D - attachmentItem.getBounds().getX();
				attachmentMax = 8.0D + attachmentItem.getBounds().getX();
				attachmentDepth = attachmentItem.getBounds().getZ();
			} else if (attachment.getItem() instanceof CableCover) {
				attachmentMin = 0.0D;
				attachmentMax = 16.0D;
				attachmentDepth = 2.0D;
			}
		}

		switch (side) {
		case NORTH:
			return Block.makeCuboidShape(attachmentMin, attachmentMin, 0.0D, attachmentMax, attachmentMax, attachmentDepth);
		case SOUTH:
			return Block.makeCuboidShape(attachmentMin, attachmentMin, 16.0D - attachmentDepth, attachmentMax, attachmentMax, 16.0D);
		case EAST:
			return Block.makeCuboidShape(16.0D - attachmentDepth, attachmentMin, attachmentMin, 16.0D, attachmentMax, attachmentMax);
		case WEST:
			return Block.makeCuboidShape(0.0D, attachmentMin, attachmentMin, attachmentDepth, attachmentMax, attachmentMax);
		case UP:
			return Block.makeCuboidShape(attachmentMin, 16.0D - attachmentDepth, attachmentMin, attachmentMax, 16.0D, attachmentMax);
		case DOWN:
			return Block.makeCuboidShape(attachmentMin, 0.0D, attachmentMin, attachmentMax, attachmentDepth, attachmentMax);
		}

		return CoreShape;
	}

	protected Vector3D getDefaultAttachmentBounds(IBlockReader world, BlockPos pos, Direction side) {
		return defaultAttachmentBounds;
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
	protected VoxelShape addAttachmentOutline(BlockPos pos, PlayerEntity entity, ISelectionContext context, VoxelShape shape) {
		// Gets the hovered result.
		CableBoundsHoverResult hoverResult = getHoveredAttachmentOrCover(pos, entity);

		// Get some attributes to use in the check.
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(entity.getEntityWorld(), pos);

		// Cover us just in case the cable renders before the te is ready.
		if (cable == null) {
			return shape;
		}

		// If the hovered result is not null, add the attachment shape.
		if (!hoverResult.isEmpty()) {
			// Get the hovered direction.
			Direction hoveredDirection = hoverResult.direction;

			switch (hoverResult.type) {
			case ATTACHED_COVER:
				shape = VoxelShapes.or(shape, getAttachmentShapeForSide(entity.getEntityWorld(), pos, cable.getCover(hoveredDirection), hoveredDirection));
				break;
			case ATTACHED_ATTACHMENT:
				shape = VoxelShapes.or(shape, getAttachmentShapeForSide(entity.getEntityWorld(), pos, cable.getAttachment(hoveredDirection), hoveredDirection));
				break;
			case HELD_COVER:
				shape = VoxelShapes.or(shape, getAttachmentShapeForSide(entity.getEntityWorld(), pos, entity.getHeldItemMainhand(), hoveredDirection));
				break;
			case HELD_ATTACHMENT:
				shape = VoxelShapes.or(shape, getAttachmentShapeForSide(entity.getEntityWorld(), pos, entity.getHeldItemMainhand(), hoveredDirection));
				break;
			default:
				if (cable.getConnectionState(hoveredDirection) == CableConnectionState.TILE_ENTITY) {
					shape = getAttachmentShapeForSide(entity.getEntityWorld(), pos, null, hoveredDirection);
				}
			}

			// Also include the extension shape if required.
			if (cable.getConnectionState(hoveredDirection) == CableConnectionState.TILE_ENTITY || cable.hasAttachment(hoveredDirection)) {
				shape = VoxelShapes.or(shape, CableExtensionShapes.get(hoveredDirection));
			}
		} else {
			for (Direction dir : Direction.values()) {
				if (cable.hasCover(dir)) {
					shape = VoxelShapes.or(shape, getAttachmentShapeForSide(entity.getEntityWorld(), pos, cable.getCover(dir), dir));
				} else if (cable.hasAttachment(dir)) {
					shape = VoxelShapes.or(shape, getAttachmentShapeForSide(entity.getEntityWorld(), pos, cable.getAttachment(dir), dir));
				} else if (cable.getConnectionState(dir) == CableConnectionState.TILE_ENTITY && !cable.hasAttachment(dir)) {
					shape = VoxelShapes.or(shape, getAttachmentShapeForSide(cable.getWorld(), pos, null, dir));
				}
			}
		}

		// Return either the new or initially provided shape.
		return shape;
	}

	private class CableHoverCheckRequest {
		public final VoxelShape bounds;
		public final Direction direction;
		public final CableBoundsHoverType type;

		public CableHoverCheckRequest(VoxelShape bounds, Direction direction, CableBoundsHoverType type) {
			super();
			this.bounds = bounds;
			this.direction = direction;
			this.type = type;
		}

	}
}
