package theking530.staticpower.tileentities.nonpowered.conveyors.rampdown;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorTileEntity;

public class TileEntityRampDownConveyor extends AbstractConveyorTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampDownConveyor> TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampDownConveyor(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.RampDownConveyorBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampDownConveyor> TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampDownConveyor(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.RampDownConveyorAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampDownConveyor> TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampDownConveyor(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.RampDownConveyorStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampDownConveyor> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampDownConveyor(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.RampDownConveyorEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampDownConveyor> TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampDownConveyor(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.RampDownConveyorLumum);

	public TileEntityRampDownConveyor(BlockEntityTypeAllocator<TileEntityRampDownConveyor> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state, tier);
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, Level world, BlockPos pos, BlockState state) {
		component.setVelocity(new Vector3D(0.1f, -0.1f, 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
