package theking530.staticpower.tileentities.nonpowered.conveyors.extractor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class TileEntityConveyorExtractor extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorExtractor> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityConveyorExtractor(pos, state),
			ModBlocks.ConveyorExtractor);

	public final InventoryComponent internalInventory;
	protected final ConveyorMotionComponent conveyor;
	protected AABB importBox;

	public TileEntityConveyorExtractor(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(-0.075f, 0f, 0f)).setShouldAffectEntitiesAbove(false));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Input) {
			public int getSlotLimit(int slot) {
				return 1;
			}
		}.setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(true));
		registerComponent(new InputServoComponent("InputServo", 20, internalInventory));
		enableFaceInteraction();
	}

	@Override
	public void process() {
		// Do nothing on the client.
		if (level.isClientSide) {
			return;
		}

		// Do nothing if the internal inventory is empty.
		if (internalInventory.getStackInSlot(0).isEmpty()) {
			return;
		}

		// Extract the stored item.
		ItemStack extracted = internalInventory.extractItem(0, 1, false);

		// Get the facing direction.
		Direction facing = getFacingDirection();
		Vector3D offset = new Vector3D(facing);
		offset.multiply(0.3f);

		// Create the entity and spawn it.
		ConveyorBeltEntity newEntity = new ConveyorBeltEntity(level, getBlockPos().getX() + 0.5f + offset.getX(), getBlockPos().getY() + 0.5f, getBlockPos().getZ() + 0.5f + offset.getZ(), extracted);
		newEntity.setDeltaMovement(0, 0, 0);
		level.addFreshEntity(newEntity);
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		conveyor.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));

		// Make sure the front is input only.
		Direction facing = getFacingDirection();
		ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, facing), MachineSideMode.Input);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.FRONT) {
			return mode == MachineSideMode.Input;
		} else {
			return mode == MachineSideMode.Never;
		}
	}

	@Override
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
					return MachineSideMode.Input;
				}
				return MachineSideMode.Never;
			}
		};
	}
}
