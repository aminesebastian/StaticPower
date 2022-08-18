package theking530.staticpower.tileentities.nonpowered.conveyors.straight;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorTileEntity;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityStraightConveyor extends AbstractConveyorTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStraightConveyor> TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStraightConveyor(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.StraightConveyorBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStraightConveyor> TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStraightConveyor(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.StraightConveyorAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStraightConveyor> TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStraightConveyor(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.StraightConveyorStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStraightConveyor> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStraightConveyor(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.StraightConveyorEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStraightConveyor> TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStraightConveyor(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.StraightConveyorLumum);

	public final InventoryComponent inventory;

	public TileEntityStraightConveyor(BlockEntityTypeAllocator<TileEntityStraightConveyor> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state, tier);
		registerComponent(inventory = new InventoryComponent("Inventory", 1, MachineSideMode.Input));
		enableFaceInteraction();
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
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state) {
		component.setShouldAffectEntitiesAbove(false);
		component.setVelocity(new Vector3D((float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f, 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Input;
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.ALL_SIDES_INPUT;
	}
}
