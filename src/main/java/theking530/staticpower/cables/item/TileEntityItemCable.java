package theking530.staticpower.cables.item;

import net.minecraft.util.ResourceLocation;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderItemCable;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityItemCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.BASIC), TileEntityRenderItemCable::new, ModBlocks.ItemCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.ADVANCED), TileEntityRenderItemCable::new, ModBlocks.ItemCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.STATIC), TileEntityRenderItemCable::new, ModBlocks.ItemCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.ENERGIZED), TileEntityRenderItemCable::new, ModBlocks.ItemCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.LUMUM), TileEntityRenderItemCable::new, ModBlocks.ItemCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.CREATIVE), TileEntityRenderItemCable::new, ModBlocks.ItemCableCreative);

	public final ItemCableComponent cableComponent;

	public TileEntityItemCable(TileEntityTypeAllocator<TileEntityItemCable> allocator, ResourceLocation tier) {
		super(allocator);
		StaticPowerTier tierObject = StaticPowerDataRegistry.getTier(tier);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", tierObject.getItemCableMaxSpeed(), tierObject.getItemCableFriction(),
				1.0f / Math.max(tierObject.getItemCableAcceleration(), 0.00000001f)));
	}
}
