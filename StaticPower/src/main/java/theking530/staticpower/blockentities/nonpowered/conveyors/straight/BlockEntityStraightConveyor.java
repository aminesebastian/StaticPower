package theking530.staticpower.blockentities.nonpowered.conveyors.straight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.blockentities.components.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityStraightConveyor extends AbstractConveyorBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityStraightConveyor> TYPE = new BlockEntityTypeAllocator<>("conveyor_straight",
			(type, pos, state) -> new BlockEntityStraightConveyor(type, pos, state), ModBlocks.ConveyorsStraight.values());

	public final InventoryComponent inventory;

	public BlockEntityStraightConveyor(BlockEntityTypeAllocator<BlockEntityStraightConveyor> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		registerComponent(inventory = new InventoryComponent("Inventory", 1, MachineSideMode.Input));
	}

	@Override
	public void process() {
		super.process();
		if (!getLevel().isClientSide()) {
			if (!inventory.getStackInSlot(0).isEmpty()) {
				WorldUtilities.dropItem(getLevel(), getBlockPos().offset(0, 0.5, 0), inventory.getStackInSlot(0));
				inventory.setStackInSlot(0, ItemStack.EMPTY);
			}
		}
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticCoreTier tier, Level world, BlockPos pos, BlockState state) {
		component.setShouldAffectEntitiesAbove(false);
		component.setVelocity(new Vector3D((float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f, 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Input;
	}
}
