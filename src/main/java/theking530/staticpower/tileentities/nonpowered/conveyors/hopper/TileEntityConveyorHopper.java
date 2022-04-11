package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorTileEntity;
import theking530.staticpower.tileentities.nonpowered.conveyors.IConveyorBlock;

public class TileEntityConveyorHopper extends AbstractConveyorTileEntity {

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.BASIC, false), ModBlocks.ConveyorHopperBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.ADVANCED, false), ModBlocks.ConveyorHopperAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.STATIC, false), ModBlocks.ConveyorHopperStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.ENERGIZED, false), ModBlocks.ConveyorHopperEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.LUMUM, false), ModBlocks.ConveyorHopperLumum);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> FILTERED_TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.BASIC, true), ModBlocks.ConveyorFilteredHopperBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> FILTERED_TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.ADVANCED, true), ModBlocks.ConveyorFilteredHopperAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> FILTERED_TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.STATIC, true), ModBlocks.ConveyorFilteredHopperStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> FILTERED_TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.ENERGIZED, true), ModBlocks.ConveyorFilteredHopperEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorHopper> FILTERED_TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorHopper(type, pos, state, StaticPowerTiers.LUMUM, true), ModBlocks.ConveyorFilteredHopperLumum);

	public final InventoryComponent internalInventory;
	public final InventoryComponent filterInventory;
	protected AABB hopperBox;
	protected boolean filtered;

	public TileEntityConveyorHopper(BlockEntityTypeAllocator<TileEntityConveyorHopper> type, BlockPos pos, BlockState state, ResourceLocation tier, boolean filtered) {
		super(type, pos, state, tier);
		this.filtered = filtered;
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
