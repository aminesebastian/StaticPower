package theking530.staticpower.blockentities.nonpowered.conveyors.hopper;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.BottomOutputOnly;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.components.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.blockentities.nonpowered.conveyors.IConveyorBlock;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.itemfilter.ItemFilter;

public class BlockEntityConveyorHopper extends AbstractConveyorBlockEntity {

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityConveyorHopper> TYPE = new BlockEntityTypeAllocator<>("conveyor_hopper",
			(type, pos, state) -> new BlockEntityConveyorHopper(type, pos, state, false), ModBlocks.ConveyorsHopper.values());

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityConveyorHopper> FILTERED_TYPE = new BlockEntityTypeAllocator<>("conveyor_filtered_hopper",
			(type, pos, state) -> new BlockEntityConveyorHopper(type, pos, state, true), ModBlocks.ConveyorsFilteredHopper.values());

	public final InventoryComponent internalInventory;
	public final InventoryComponent filterInventory;
	public final SideConfigurationComponent ioSideConfiguration;
	protected AABB hopperBox;
	protected boolean filtered;

	public BlockEntityConveyorHopper(BlockEntityTypeAllocator<BlockEntityConveyorHopper> type, BlockPos pos, BlockState state, boolean filtered) {
		super(type, pos, state);
		this.filtered = filtered;
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", BottomOutputOnly.INSTANCE));
		registerComponent(
				internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Output).setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(false));
		registerComponent(
				filterInventory = new InventoryComponent("FilterInventory", 1, MachineSideMode.Never).setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(false));
		registerComponent(new OutputServoComponent("OutputServo", 0, internalInventory));
	}

	@Override
	public void process() {
		// Do nothing on the client.
		if (level.isClientSide) {
			return;
		}

		// Just let physics work if the block below is a conveyor.
		if (level.getBlockState(getBlockPos().relative(Direction.DOWN)).getBlock() instanceof IConveyorBlock) {
			return;
		}

		// Get all entities in the import space.
		List<Entity> entities = getLevel().getEntitiesOfClass(Entity.class, hopperBox);
		for (Entity entity : entities) {
			if (entity instanceof ConveyorBeltEntity) {
				// Get the item entity.
				ConveyorBeltEntity conveyorEntity = (ConveyorBeltEntity) entity;

				// Check if this item should be skipped.
				if (!doesItemPassFilter(conveyorEntity)) {
					continue;
				}

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

	public boolean doesItemPassFilter(ItemEntity entity) {
		// If we're not filtered, do nothing. Perform this check here for safety.
		if (!filtered) {
			return false;
		}

		// If the filter inventory is empty, do nothing.
		if (filterInventory.getStackInSlot(0).isEmpty()) {
			return false;
		}

		// If this is an item entity, and there is a filter, perform the filter.
		ItemStack filterStack = filterInventory.getStackInSlot(0);
		if (filterStack.getItem() instanceof ItemFilter) {
			// Get the filter item.
			ItemFilter filter = (ItemFilter) filterStack.getItem();

			// Get the item in the filter inventory.
			ItemStack stack = entity.getItem();
			return filter.evaluateItemStackAgainstFilter(filterStack, stack);
		}
		return false;
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state) {
		hopperBox = new AABB(pos.getX() + .25, pos.getY(), pos.getZ() + .25, pos.getX() + .75, pos.getY() + 0.1, pos.getZ() + .75);
		component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));
		component.setShouldAffectEntitiesAbove(false);
		component.setVelocity(new Vector3D((float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f, 0f));
		// Make sure the front is output only.
		ioSideConfiguration.setWorldSpaceDirectionConfiguration(Direction.DOWN, MachineSideMode.Output);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFilteredConveyorHopper(windowId, inventory, this);
	}
}
