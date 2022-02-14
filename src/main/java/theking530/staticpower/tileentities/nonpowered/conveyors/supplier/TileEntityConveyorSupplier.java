package theking530.staticpower.tileentities.nonpowered.conveyors.supplier;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;

public class TileEntityConveyorSupplier extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorSupplier> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityConveyorSupplier(pos, state),
			ModBlocks.ConveyorSupplier);

	public final InventoryComponent internalInventory;
	protected final ConveyorMotionComponent conveyor;
	protected AABB importBox;

	public TileEntityConveyorSupplier(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.075f, 0f, 0f)).setShouldAffectEntitiesAbove(false));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Output) {
			public int getSlotLimit(int slot) {
				return 64;
			}
		}.setCapabilityExtractEnabled(true).setCapabilityInsertEnabled(false));
		registerComponent(new OutputServoComponent("OutputServo", 0, internalInventory));
		enableFaceInteraction();
	}

	@Override
	public void process() {
		// Do nothing on the client.
		if (level.isClientSide) {
			return;
		}

		// Get all entities in the import space.
		List<Entity> entities = getLevel().getEntitiesOfClass(Entity.class, importBox);
		for (Entity entity : entities) {
			if (entity instanceof ConveyorBeltEntity) {
				// Get the item entity.
				ConveyorBeltEntity conveyorEntity = (ConveyorBeltEntity) entity;

				// Transfer the item into the internal inventory.
				ItemStack stack = conveyorEntity.getItem().copy();
				ItemStack remaining = internalInventory.insertItem(0, stack, false);

				// Update or remove the item entity.
				if (remaining.isEmpty()) {
					conveyorEntity.remove(RemovalReason.DISCARDED);
				} else {
					conveyorEntity.setItem(remaining.copy());
				}
			}
		}
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		float conveyorLength = 0.9f;
		float inverseConveyorLength = 1.0f - conveyorLength;

		Direction facing = getFacingDirection();
		if (facing == Direction.EAST) {
			importBox = new AABB(pos.getX() + conveyorLength, pos.getY() + 0.5, pos.getZ(), pos.getX() + 1.0, pos.getY() + 0.9, pos.getZ() + 1);
			conveyor.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + conveyorLength, pos.getY() + 0.9, pos.getZ() + 1));
		} else if (facing == Direction.NORTH) {
			importBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + inverseConveyorLength);
			conveyor.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ() + inverseConveyorLength, pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + 1));
		} else if (facing == Direction.SOUTH) {
			importBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ() + conveyorLength, pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + 1);
			conveyor.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + conveyorLength));
		} else if (facing == Direction.WEST) {
			importBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + inverseConveyorLength, pos.getY() + 0.9, pos.getZ() + 1);
			conveyor.updateBounds(new AABB(pos.getX() + inverseConveyorLength, pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + 1));
		}

		// Make sure the front is output only.
		ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, facing), MachineSideMode.Output);
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.FRONT) {
			return mode == MachineSideMode.Output;
		} else {
			return mode == MachineSideMode.Never;
		}
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return new DefaultSideConfiguration() {
			@Override
			public boolean getSideDefaultEnabled(BlockSide side) {
				if (side == BlockSide.FRONT) {
					return true;
				}
				return false;
			}

			@Override
			public MachineSideMode getSideDefaultMode(BlockSide side) {
				if (side == BlockSide.FRONT) {
					return MachineSideMode.Output;
				}
				return MachineSideMode.Never;
			}
		};
	}
}
