package theking530.staticpower.tileentities.nonpowered.conveyors.extractor;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.entities.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class TileEntityConveyorExtractor extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityConveyorExtractor> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityConveyorExtractor(), ModBlocks.ConveyorExtractor);

	public final InventoryComponent internalInventory;
	protected final ConveyorMotionComponent conveyor;
	protected AxisAlignedBB importBox;

	public TileEntityConveyorExtractor() {
		super(TYPE);
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
		if (world.isRemote) {
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
		ConveyorBeltEntity newEntity = new ConveyorBeltEntity(world, getPos().getX() + 0.5f + offset.getX(), getPos().getY() + 0.5f, getPos().getZ() + 0.5f + offset.getZ(), extracted);
		newEntity.setMotion(0, 0, 0);
		world.addEntity(newEntity);
	}

	@Override
	protected void postInit(World world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		conveyor.setBounds(new AxisAlignedBB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));

		// Make sure the front is input only.
		Direction facing = getFacingDirection();
		ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, facing), MachineSideMode.Input);
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.FRONT) {
			return mode == MachineSideMode.Input;
		} else {
			return mode == MachineSideMode.Never;
		}
	}

	protected MachineSideMode[] getDefaultSideConfiguration() {
		return new MachineSideMode[] { MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never };
	}
}
