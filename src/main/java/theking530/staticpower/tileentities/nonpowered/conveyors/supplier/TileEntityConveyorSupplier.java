package theking530.staticpower.tileentities.nonpowered.conveyors.supplier;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorTileEntity;

public class TileEntityConveyorSupplier extends AbstractConveyorTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorSupplier> TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorSupplier(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.ConveyorSupplierBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorSupplier> TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorSupplier(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.ConveyorSupplierAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorSupplier> TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorSupplier(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.ConveyorSupplierStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorSupplier> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorSupplier(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.ConveyorSupplierEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorSupplier> TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorSupplier(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.ConveyorSupplierLumum);

	public final InventoryComponent internalInventory;
	protected AABB importBox;

	public TileEntityConveyorSupplier(BlockEntityTypeAllocator<TileEntityConveyorSupplier> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state, tier);
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
	protected void configureConveyorComponent(ConveyorMotionComponent component, Level world, BlockPos pos, BlockState state) {
		float conveyorLength = 0.9f;
		float inverseConveyorLength = 1.0f - conveyorLength;
		
		component.setShouldAffectEntitiesAbove(false);
		component.setVelocity(new Vector3D(0.075f, 0f, 0f));
		
		Direction facing = getFacingDirection();
		if (facing == Direction.EAST) {
			importBox = new AABB(pos.getX() + conveyorLength, pos.getY() + 0.5, pos.getZ(), pos.getX() + 1.0, pos.getY() + 0.9, pos.getZ() + 1);
			component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + conveyorLength, pos.getY() + 0.9, pos.getZ() + 1));
		} else if (facing == Direction.NORTH) {
			importBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + inverseConveyorLength);
			component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ() + inverseConveyorLength, pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + 1));
		} else if (facing == Direction.SOUTH) {
			importBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ() + conveyorLength, pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + 1);
			component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + conveyorLength));
		} else if (facing == Direction.WEST) {
			importBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + inverseConveyorLength, pos.getY() + 0.9, pos.getZ() + 1);
			component.updateBounds(new AABB(pos.getX() + inverseConveyorLength, pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.9, pos.getZ() + 1));
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
