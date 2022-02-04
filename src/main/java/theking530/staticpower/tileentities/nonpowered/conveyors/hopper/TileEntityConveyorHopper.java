package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.nonpowered.conveyors.IConveyorBlock;

public class TileEntityConveyorHopper extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityConveyorHopper(type, false), ModBlocks.ConveyorHopper);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> FILTERED_TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityConveyorHopper(type, true),
			ModBlocks.ConveyorFilteredHopper);

	public final InventoryComponent internalInventory;
	public final InventoryComponent filterInventory;
	protected final ConveyorMotionComponent conveyor;
	protected AABB hopperBox;
	protected boolean filtered;

	public TileEntityConveyorHopper(BlockEntityTypeAllocator<TileEntityConveyorHopper> type, boolean filtered) {
		super(type);
		this.filtered = filtered;
		registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.075f, 0f, 0f)).setShouldAffectEntitiesAbove(false));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Output).setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(false));
		registerComponent(filterInventory = new InventoryComponent("FilterInventory", 1, MachineSideMode.Never).setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(false));
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
				if (!filterItem(conveyorEntity)) {
					continue;
				}

				// Transfer the item into the internal inventory.
				ItemStack stack = conveyorEntity.getItem().copy();
				ItemStack remaining = internalInventory.insertItem(0, stack, false);

				// Update or remove the item entity.
				if (remaining.isEmpty()) {
					conveyorEntity.remove();
				} else {
					conveyorEntity.setItem(remaining.copy());
				}
			}
		}
	}

	public boolean filterItem(ItemEntity entity) {
		// If we're not filtered, do nothing. Perform this check here for safety.
		if (!filtered) {
			return true;
		}

		// If the filter inventory is empty, do nothing.
		if (filterInventory.getStackInSlot(0).isEmpty()) {
			return true;
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
		return true;
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		hopperBox = new AABB(pos.getX() + .25, pos.getY(), pos.getZ() + .25, pos.getX() + .75, pos.getY() + 0.1, pos.getZ() + .75);
		conveyor.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));

		// Make sure the front is output only.
		ioSideConfiguration.setWorldSpaceDirectionConfiguration(Direction.DOWN, MachineSideMode.Output);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.BOTTOM) {
			return mode == MachineSideMode.Output;
		} else {
			return mode == MachineSideMode.Never;
		}
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return new DefaultSideConfiguration().setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFilteredConveyorHopper(windowId, inventory, this);
	}
}
