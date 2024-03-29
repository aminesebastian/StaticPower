package theking530.staticpower.blockentities.nonpowered.conveyors.supplier;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.FrontOutputOnly;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.components.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityConveyorSupplier extends AbstractConveyorBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityConveyorSupplier> TYPE = new BlockEntityTypeAllocator<>("conveyor_supplier",
			(type, pos, state) -> new BlockEntityConveyorSupplier(type, pos, state), ModBlocks.ConveyorsSupplier.values());

	public final InventoryComponent internalInventory;
	public final SideConfigurationComponent ioSideConfiguration;
	protected AABB importBox;

	public BlockEntityConveyorSupplier(BlockEntityTypeAllocator<BlockEntityConveyorSupplier> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Output) {
			public int getSlotLimit(int slot) {
				return 64;
			}
		}.setCapabilityExtractEnabled(true).setCapabilityInsertEnabled(false));
		registerComponent(new OutputServoComponent("OutputServo", 0, internalInventory));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", FrontOutputOnly.INSTANCE));
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

				// Create a copy of the item and calculate the amount to supply.
				ItemStack stackToSupply = conveyorEntity.getItem().copy();
				int amountToSupply = Math.min(getTierObject().conveyorSupplierStackSize.get(), stackToSupply.getCount());
				stackToSupply.setCount(amountToSupply);

				// Calculate how many items would be left over IF the insert is 100% successful.
				int leftover = conveyorEntity.getItem().getCount() - amountToSupply;

				// Perform the insert, then update the entity with how many were NOT
				// successfully inserted PLUS any leftover.
				ItemStack remaining = internalInventory.insertItem(0, stackToSupply, false);
				conveyorEntity.getItem().setCount(leftover + remaining.getCount());

				// Update or remove the item entity.
				if (conveyorEntity.getItem().isEmpty()) {
					conveyorEntity.remove(RemovalReason.DISCARDED);
				} else {
					conveyorEntity.setItem(remaining.copy());
				}
			}
		}
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticCoreTier tier, Level world, BlockPos pos, BlockState state) {
		float conveyorLength = 0.9f;
		float inverseConveyorLength = 1.0f - conveyorLength;

		component.setShouldAffectEntitiesAbove(false);
		component.setVelocity(new Vector3D((float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f, 0f));

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
}
