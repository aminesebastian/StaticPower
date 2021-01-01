package theking530.staticpower.cables.power;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityPowerCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.BASIC), ModBlocks.PowerCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.ADVANCED), ModBlocks.PowerCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.STATIC), ModBlocks.PowerCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.ENERGIZED), ModBlocks.PowerCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.LUMUM), ModBlocks.PowerCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.CREATIVE), ModBlocks.PowerCableCreative);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_BASIC = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.BASIC), ModBlocks.IndustrialPowerCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_ADVANCED = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.ADVANCED), ModBlocks.IndustrialPowerCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_STATIC = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.STATIC), ModBlocks.IndustrialPowerCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_ENERGIZED = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.ENERGIZED), ModBlocks.IndustrialPowerCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_LUMUM = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.LUMUM), ModBlocks.IndustrialPowerCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_CREATIVE = new TileEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.CREATIVE), ModBlocks.IndustrialPowerCableCreative);

	public final PowerCableComponent powerCableComponent;

	public TileEntityPowerCable(TileEntityTypeAllocator<TileEntityPowerCable> allocator, boolean isIndustrial, ResourceLocation tier) {
		super(allocator);
		int powerRate = isIndustrial ? StaticPowerConfig.getTier(tier).cableIndustrialPowerDelivery.get() : StaticPowerConfig.getTier(tier).cablePowerDelivery.get();
		int powerCapacity = isIndustrial ? StaticPowerConfig.getTier(tier).cableIndustrialPowerCapacity.get() : StaticPowerConfig.getTier(tier).cablePowerCapacity.get();
		registerComponent(powerCableComponent = new PowerCableComponent("PowerCableComponent", isIndustrial, powerCapacity, powerRate));
	}

	@Override
	public void process() {

	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPowerCable(windowId, inventory, this);
	}
}
