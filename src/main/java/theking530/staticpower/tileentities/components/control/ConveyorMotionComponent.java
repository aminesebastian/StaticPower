package theking530.staticpower.tileentities.components.control;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public class ConveyorMotionComponent extends AbstractTileEntityComponent {
	private AABB entitySearchBounds;
	private Vector3D velocity;
	private double compensationRate;
	private boolean affectEntitiesAbove;
	private Predicate<Entity> filter;

	public ConveyorMotionComponent(String name, Vector3D velocity) {
		this(name, 0.05f, velocity, 0.075);
	}

	public ConveyorMotionComponent(String name, double centerChannelWidth, Vector3D velocity, double compensationRate) {
		super(name);
		this.compensationRate = compensationRate;
		this.velocity = velocity;
		this.affectEntitiesAbove = false;
		SDMath.clamp(0.5 - centerChannelWidth, 0, 1);
		SDMath.clamp(0.5 + centerChannelWidth, 0, 1);
		this.filter = (entity) -> true;
	}

	@Override
	public void postProcessUpdate() {
		// Do nothing if there are no bounds defined.
		if (entitySearchBounds == null) {
			return;
		}

		// Get the facing direction and vector.
		Direction facing = getTileEntity().getFacingDirection();

		// Get all entities in the block space above this block.
		List<Entity> entities = getWorld().getEntitiesOfClass(Entity.class, entitySearchBounds);
		for (Entity entity : entities) {
			// Skip any entities are not considered to be ON this block (prevents two
			// conveyors from fighting for an entity).
			if (!shouldEffectEntity(entity)) {
				continue;
			}

			// Create a direction vector for the entity and determine the entity's
			// coordinate
			// on the block (0-9).
			Vec3 directionVector = Vec3.atLowerCornerOf(facing.getNormal());
			Vec3 coordinate = determineEntityCoordinateOnBlock(entity, facing, directionVector);

			// Rotate the velocity towards the facing direction of the block (This should be
			// cached!!!).
			Vector3D rotatedVelocity = velocity.clone();
			// If we're on the Z axis as forward instead of X, swap X and Z.
			if (facing.getAxis() == Axis.Z) {
				float tempX = rotatedVelocity.getX();
				rotatedVelocity.setX(rotatedVelocity.getZ());
				rotatedVelocity.setZ(tempX);
			}

			if (facing == Direction.SOUTH) {
				rotatedVelocity.setZ(rotatedVelocity.getZ());
			} else if (facing == Direction.NORTH) {
				rotatedVelocity.setZ(-rotatedVelocity.getZ());
			} else if (facing == Direction.WEST) {
				rotatedVelocity.setZ(-rotatedVelocity.getZ());
				rotatedVelocity.setX(-rotatedVelocity.getX());
			}

			// Move the entity.
			moveEntity(entity, facing, rotatedVelocity, compensationRate, coordinate);
			// If the entity is an item but NOT a conveyor item, convert it to a
			// ConveyorBeltItem.
			if (entity instanceof ItemEntity && !(entity instanceof ConveyorBeltEntity)) {
				ItemEntity item = (ItemEntity) entity;
				ConveyorBeltEntity conveyorEntity = new ConveyorBeltEntity(this.getWorld(), entity.getX(), entity.getY(), entity.getZ(), item.getItem().copy());
				conveyorEntity.setPickUpDelay(30); // Set this value initially a little high!
				conveyorEntity.setDeltaMovement(0, 0, 0);
				getWorld().addFreshEntity(conveyorEntity);

				// Clear the old item.
				item.setItem(ItemStack.EMPTY);
				item.remove();
			}
		}
	}

	public ConveyorMotionComponent updateBounds(AABB newBounds) {
		this.entitySearchBounds = newBounds;
		return this;
	}

	public ConveyorMotionComponent setFilter(Predicate<Entity> filter) {
		this.filter = filter;
		return this;
	}

	public ConveyorMotionComponent setShouldAffectEntitiesAbove(boolean affectEntitiesAbove) {
		this.affectEntitiesAbove = affectEntitiesAbove;
		return this;
	}

	public boolean shouldEffectEntity(Entity entity) {
		if (entity.isShiftKeyDown()) {
			return false;
		}
		return filter.test(entity);
	}

	/**
	 * Determines the entities coordinates in a grid (0, 0, 0) to (1, 1, 1).
	 *
	 * @param entity
	 * @param conveyorFacing
	 * @return
	 */
	protected Vec3 determineEntityCoordinateOnBlock(Entity entity, Direction conveyorFacing, Vec3 directionVector) {
		// Get the entity's position.
		Vector3D localizedEntityPosition = new Vector3D((float) entity.position().x(), (float) entity.position().y(), (float) entity.position().z());

		// Transform it to a relative offset from the position of the conveyor (0 - 1).
		// Compensate for the y value being offset if entities are not inside the block
		// space.
		localizedEntityPosition = localizedEntityPosition.subtract(new Vector3D(getPos().getX(), getPos().getY() + (affectEntitiesAbove ? 1 : 0), getPos().getZ()));

		// If we're on the Z axis as forward instead of X, swap X and Z.
		if (conveyorFacing.getAxis() == Axis.Z) {
			float tempX = localizedEntityPosition.getX();
			localizedEntityPosition.setX(localizedEntityPosition.getZ());
			localizedEntityPosition.setZ(tempX);
		}

		// Transform based on facing direction (this should really be rotated
		// mathematically, not like this. Need to revisit.
		if (conveyorFacing == Direction.SOUTH) {
			localizedEntityPosition.setZ(1.0f - localizedEntityPosition.getZ());
		} else if (conveyorFacing == Direction.NORTH) {
			localizedEntityPosition.setX(1.0f - localizedEntityPosition.getX());
		} else if (conveyorFacing == Direction.WEST) {
			localizedEntityPosition.setZ(1.0f - localizedEntityPosition.getZ());
			localizedEntityPosition.setX(1.0f - localizedEntityPosition.getX());
		}

		// Create the output.
		return new Vec3(SDMath.clamp(localizedEntityPosition.getX(), 0, 1), SDMath.clamp(localizedEntityPosition.getY(), 0, 1), SDMath.clamp(localizedEntityPosition.getZ(), 0, 1));
	}

	/**
	 * This method will move entities along the provided conveyor with a speed of
	 * newMotion as long as the entity is in the middle channel of the conveyor. If
	 * the entity has veered, then the compensation rate will be applied to bring it
	 * back to the middle channel.
	 * 
	 * @param entity           The entity to move.
	 * @param conveyorForward  The conveyor's forward vector.
	 * @param newMotion        The new motion to apply to the entity (speed and
	 *                         direction).
	 * @param compensationRate The compensation rate to apply when fixing entities
	 *                         that have veered out of the center channel. The
	 *                         higher this value, the larger the turn radius. Too
	 *                         large and entities will start to misbehave.
	 * @param entityCoordinate The coordinate of the entity in the block (captured
	 *                         from
	 *                         {@link #determineEntityCoordinateOnBlock(Entity, Direction, Vector3d)}.
	 */
	protected void moveEntity(Entity entity, Direction conveyorForward, Vector3D newMotion, double compensationRate, Vec3 entityCoordinate) {
		// Calculate the X and Z compensation.
		double compensationX = conveyorForward.getAxis() == Axis.X ? 0 : compensationRate;
		double compensationZ = conveyorForward.getAxis() == Axis.Z ? 0 : compensationRate;
		compensationX *= conveyorForward.getAxisDirection() == AxisDirection.NEGATIVE ? -1 : 1;
		compensationZ *= conveyorForward.getAxisDirection() == AxisDirection.NEGATIVE ? 1 : -1;

		double delta = (entityCoordinate.z() - 0.5f);
		double xMotion = (delta * compensationX) + (0.5 - Math.abs(delta)) * newMotion.getX();
		double yMotion = (delta * compensationZ) + (0.5 - Math.abs(delta)) * newMotion.getZ();

		entity.setDeltaMovement(xMotion, newMotion.getY(), yMotion);
	}
}
