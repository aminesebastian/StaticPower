package theking530.staticpower.tileentities.components.control;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.entities.ConveyorBeltEntity;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public class ConveyorMotionComponent extends AbstractTileEntityComponent {
	private AxisAlignedBB entitySearchBounds;
	private Vector3D velocity;
	private double compensationRate;
	private boolean affectEntitiesAbove;

	public ConveyorMotionComponent(String name, Vector3D velocity, double compensationRate) {
		super(name);
		this.compensationRate = compensationRate;
		this.velocity = velocity;
		this.affectEntitiesAbove = true;
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
		List<Entity> entities = getWorld().getEntitiesWithinAABB(Entity.class, entitySearchBounds);
		for (Entity entity : entities) {
			// Skip any entities are not considered to be ON this block (prevents two
			// conveyors from fighting for an entity).
			if (!shouldEffectEntity(entity)) {
				continue;
			}

			// Create a direction vector for the entity and determine the entity's
			// coordinate
			// on the block (0-3).
			Vector3d directionVector = Vector3d.copy(facing.getDirectionVec());
			Vector3d coordinate = determineEntityCoordinateOnBlock(entity, facing, 3, directionVector);

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
				ConveyorBeltEntity conveyorEntity = new ConveyorBeltEntity(this.getWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), item.getItem().copy());
				conveyorEntity.setPickupDelay(30); // Set this value initially a little high!
				conveyorEntity.setMotion(0, 0, 0);
				getWorld().addEntity(conveyorEntity);

				// Clear the old item.
				item.setItem(ItemStack.EMPTY);
				item.remove();
			}
		}
	}

	public void setBounds(AxisAlignedBB bounds) {
		this.entitySearchBounds = bounds;
	}

	public ConveyorMotionComponent updateBounds(AxisAlignedBB newBounds) {
		this.entitySearchBounds = newBounds;
		return this;
	}

	public ConveyorMotionComponent setShouldAffectEntitiesAbove(boolean affectEntitiesAbove) {
		this.affectEntitiesAbove = affectEntitiesAbove;
		return this;
	}

	public boolean shouldEffectEntity(Entity entity) {
		// Skip sneaking entities or entities are not considered to be ON this block
		// (prevents two conveyors from fighting for an entity).
		BlockPos positionToCompare = affectEntitiesAbove ? getPos().offset(Direction.UP) : getPos();
		if (entity.isSneaking() || !entity.getPosition().equals(positionToCompare)) {
			return false;
		}
		return true;

	}

	/**
	 * Determines the entities coordinates in the grid like so. The following example is what would happen if the gridSize = 3.
	 * // @formatter:off
	 * 	       Block Front
	 *  -------------------------
	 *  |				  		|
	 * 	|(0,0,2)|(1,0,2)|(2,0,2)|
	 *  |				  		|
	 *  |					    |
	 * 	|(0,0,1)|(1,0,1)|(2,0,1)|
	 *  |				 		|
	 *  |					    |
	 * 	|(0,0,0)|(1,0,0)|(2,0,0)|
	 *  |					    |
	 *  |				  		|
	 *  -------------------------
	 * 	       Block Back
	 * // @formatter:on
	 * 
	 * The Y Axis will also increase from 0-gridSize with 0 being down and gridSize being up.
	 * @param entity
	 * @param conveyorFacing
	 * @return
	 */
	protected Vector3d determineEntityCoordinateOnBlock(Entity entity, Direction conveyorFacing, int gridSize, Vector3d directionVector) {
		// Get the entity's position.
		Vector3D localizedEntityPosition = new Vector3D((float) entity.getPositionVec().getX(), (float) entity.getPositionVec().getY(), (float) entity.getPositionVec().getZ());

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

		// Map it into the range (0 - gridSize)
		localizedEntityPosition = localizedEntityPosition.multiply(gridSize);

		// Create the output.
		return new Vector3d(SDMath.clamp(Math.floor(localizedEntityPosition.getX()), 0, gridSize - 1), SDMath.clamp(Math.floor(localizedEntityPosition.getY()), 0, gridSize - 1),
				SDMath.clamp(Math.floor(localizedEntityPosition.getZ()), 0, gridSize - 1));
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
	protected void moveEntity(Entity entity, Direction conveyorForward, Vector3D newMotion, double compensationRate, Vector3d entityCoordinate) {
		// Calculate the X and Z compensation.
		double compensationX = conveyorForward.getAxis() == Axis.X ? 0 : compensationRate;
		double compensationZ = conveyorForward.getAxis() == Axis.Z ? 0 : compensationRate;
		compensationX *= conveyorForward.getAxisDirection() == AxisDirection.NEGATIVE ? -1 : 1;
		compensationZ *= conveyorForward.getAxisDirection() == AxisDirection.NEGATIVE ? 1 : -1;

		// If we're not in the 0 Z coordinat (middle channel), perform a compensation.
		if (entityCoordinate.getZ() != 1) {
			int delta = (int) (entityCoordinate.getZ() - 1);
			entity.setMotion(compensationX * delta, 0, compensationZ * delta);
		} else {
			entity.setMotion(newMotion.getX(), newMotion.getY(), newMotion.getZ());
		}
	}
}
