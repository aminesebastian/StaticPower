package theking530.staticpower.cables.power;

import net.minecraft.util.ResourceLocation;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityPowerCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, StaticPowerTiers.BASIC), ModBlocks.PowerCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, StaticPowerTiers.ADVANCED), ModBlocks.PowerCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, StaticPowerTiers.STATIC), ModBlocks.PowerCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, StaticPowerTiers.ENERGIZED), ModBlocks.PowerCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, StaticPowerTiers.LUMUM), ModBlocks.PowerCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, StaticPowerTiers.CREATIVE), ModBlocks.PowerCableCreative);

	public TileEntityPowerCable(TileEntityTypeAllocator<TileEntityPowerCable> allocator, ResourceLocation tier) {
		super(allocator);
		registerComponent(new PowerCableComponent("PowerCableComponent", TierReloadListener.getTier(tier).getCablePowerCapacity(), TierReloadListener.getTier(tier).getCablePowerDelivery()));
	}

	@Override
	public void process() {

	}
}
