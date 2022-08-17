package theking530.staticpower.tileentities.nonpowered.conveyors.rampup;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorTileEntity;

public class TileEntityRampUpConveyor extends AbstractConveyorTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampUpConveyor> TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampUpConveyor(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.RampUpConveyorBasic.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampUpConveyor> TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampUpConveyor(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.RampUpConveyorAdvanced.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampUpConveyor> TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampUpConveyor(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.RampUpConveyorStatic.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampUpConveyor> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampUpConveyor(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.RampUpConveyorEnergized.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampUpConveyor> TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampUpConveyor(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.RampUpConveyorLumum.get());

	public TileEntityRampUpConveyor(BlockEntityTypeAllocator<TileEntityRampUpConveyor> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state, tier);
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state) {
		component.setVelocity(new Vector3D((float) (0.1f * tier.conveyorSpeedMultiplier.get()), (float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
