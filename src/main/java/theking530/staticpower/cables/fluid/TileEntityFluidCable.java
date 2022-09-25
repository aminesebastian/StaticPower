package theking530.staticpower.cables.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidCable;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.init.ModBlocks;

public class TileEntityFluidCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, false), ModBlocks.FluidCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, false), ModBlocks.FluidCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, false), ModBlocks.FluidCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, false), ModBlocks.FluidCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, false), ModBlocks.FluidCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, false), ModBlocks.FluidCableCreative);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_BASIC = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, true), ModBlocks.IndustrialFluidCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_ADVANCED = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, true), ModBlocks.IndustrialFluidCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_STATIC = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, true), ModBlocks.IndustrialFluidCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_ENERGIZED = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, true), ModBlocks.IndustrialFluidCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_LUMUM = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, true), ModBlocks.IndustrialFluidCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_CREATIVE = new BlockEntityTypeAllocator<TileEntityFluidCable>(
			(allocator, pos, state) -> new TileEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, true), ModBlocks.IndustrialFluidCableCreative);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);

			TYPE_INDUSTRIAL_BASIC.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_ADVANCED.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_STATIC.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_ENERGIZED.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_LUMUM.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_CREATIVE.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
		}
	}

	public FluidCableComponent fluidCableComponent;
	public float fluidRenderRadius;

	public TileEntityFluidCable(BlockEntityTypeAllocator<TileEntityFluidCable> allocator, BlockPos pos, BlockState state, float radius, boolean isIndustrial) {
		super(allocator, pos, state);

		TierFluidCableConfiguration config = getTierObject().cableFluidConfiguration;
		int transferRate = isIndustrial ? config.cableIndustrialFluidTransferRate.get() : config.cableFluidTransferRate.get();

		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent", isIndustrial, transferRate * 5, transferRate));
		fluidRenderRadius = radius;
	}
}
