package theking530.staticpower.cables.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidCable;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityFluidCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.BASIC), ModBlocks.FluidCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.ADVANCED), ModBlocks.FluidCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.STATIC), ModBlocks.FluidCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.ENERGIZED), ModBlocks.FluidCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.LUMUM), ModBlocks.FluidCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.CREATIVE), ModBlocks.FluidCableCreative);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_BASIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.BASIC), ModBlocks.IndustrialFluidCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_ADVANCED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.ADVANCED), ModBlocks.IndustrialFluidCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_STATIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.STATIC), ModBlocks.IndustrialFluidCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_ENERGIZED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.ENERGIZED), ModBlocks.IndustrialFluidCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_LUMUM = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.LUMUM), ModBlocks.IndustrialFluidCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_CREATIVE = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.CREATIVE), ModBlocks.IndustrialFluidCableCreative);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);

			TYPE_INDUSTRIAL_BASIC.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_STATIC.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderFluidCable::new);
		}
	}

	public FluidCableComponent fluidCableComponent;
	public float fluidRenderRadius;

	public TileEntityFluidCable(TileEntityTypeAllocator<TileEntityFluidCable> allocator, float radius, boolean isIndustrial, ResourceLocation tier) {
		super(allocator);
		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent",
				isIndustrial ? TierReloadListener.getTier(tier).getCableIndustrialFluidCapacity() : TierReloadListener.getTier(tier).getCableFluidCapacity()));
		fluidRenderRadius = radius;
	}
}
