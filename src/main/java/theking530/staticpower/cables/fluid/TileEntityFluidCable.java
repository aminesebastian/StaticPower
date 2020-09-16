package theking530.staticpower.cables.fluid;

import net.minecraft.util.ResourceLocation;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidCable;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityFluidCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.BASIC), TileEntityRenderFluidCable::new, ModBlocks.FluidCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.ADVANCED), TileEntityRenderFluidCable::new, ModBlocks.FluidCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.STATIC), TileEntityRenderFluidCable::new, ModBlocks.FluidCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.ENERGIZED), TileEntityRenderFluidCable::new, ModBlocks.FluidCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.LUMUM), TileEntityRenderFluidCable::new, ModBlocks.FluidCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, false, StaticPowerTiers.CREATIVE), TileEntityRenderFluidCable::new, ModBlocks.FluidCableCreative);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_BASIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.BASIC), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_ADVANCED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.ADVANCED), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_STATIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.STATIC), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_ENERGIZED = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.ENERGIZED), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_LUMUM = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.LUMUM), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL_CREATIVE = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, true, StaticPowerTiers.CREATIVE), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCableCreative);
	public FluidCableComponent fluidCableComponent;
	public float fluidRenderRadius;

	public TileEntityFluidCable(TileEntityTypeAllocator<TileEntityFluidCable> allocator, float radius, boolean isIndustrial, ResourceLocation tier) {
		super(allocator);
		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent",
				isIndustrial ? StaticPowerDataRegistry.getTier(tier).getCableIndustrialFluidCapacity() : StaticPowerDataRegistry.getTier(tier).getCableFluidCapacity()));
		fluidRenderRadius = radius;
	}
}
