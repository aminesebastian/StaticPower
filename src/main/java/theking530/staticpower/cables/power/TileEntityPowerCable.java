package theking530.staticpower.cables.power;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityPowerCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.BASIC), ModBlocks.PowerCableBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.ADVANCED), ModBlocks.PowerCableAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.STATIC), ModBlocks.PowerCableStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.ENERGIZED), ModBlocks.PowerCableEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.LUMUM), ModBlocks.PowerCableLumum);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, false, StaticPowerTiers.CREATIVE), ModBlocks.PowerCableCreative);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_BASIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.BASIC), ModBlocks.IndustrialPowerCableBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_ADVANCED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.ADVANCED), ModBlocks.IndustrialPowerCableAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_STATIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.STATIC), ModBlocks.IndustrialPowerCableStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_ENERGIZED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.ENERGIZED), ModBlocks.IndustrialPowerCableEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_LUMUM = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.LUMUM), ModBlocks.IndustrialPowerCableLumum);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_CREATIVE = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator) -> new TileEntityPowerCable(allocator, true, StaticPowerTiers.CREATIVE), ModBlocks.IndustrialPowerCableCreative);

	public final PowerCableComponent powerCableComponent;

	public TileEntityPowerCable(BlockEntityTypeAllocator<TileEntityPowerCable> allocator, boolean isIndustrial, ResourceLocation tier) {
		super(allocator);
		long powerRate = isIndustrial ? StaticPowerConfig.getTier(tier).cableIndustrialPowerDelivery.get() : StaticPowerConfig.getTier(tier).cablePowerDelivery.get();
		long powerCapacity = isIndustrial ? StaticPowerConfig.getTier(tier).cableIndustrialPowerCapacity.get() : StaticPowerConfig.getTier(tier).cablePowerCapacity.get();
		registerComponent(powerCableComponent = new PowerCableComponent("PowerCableComponent", isIndustrial, powerCapacity, powerRate));
	}

	@Override
	public void process() {

	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPowerCable(windowId, inventory, this);
	}
}
