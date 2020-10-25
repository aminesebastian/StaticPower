package theking530.staticpower.cables.item;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderItemCable;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityItemCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.BASIC), ModBlocks.ItemCableBasic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.ADVANCED), ModBlocks.ItemCableAdvanced);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.STATIC), ModBlocks.ItemCableStatic);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.ENERGIZED), ModBlocks.ItemCableEnergized);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.LUMUM), ModBlocks.ItemCableLumum);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityItemCable> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.CREATIVE), ModBlocks.ItemCableCreative);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderItemCable::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderItemCable::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderItemCable::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderItemCable::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderItemCable::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderItemCable::new);
		}
	}

	public final ItemCableComponent cableComponent;

	public TileEntityItemCable(TileEntityTypeAllocator<TileEntityItemCable> allocator, ResourceLocation tier) {
		super(allocator);
		StaticPowerTier tierObject = TierReloadListener.getTier(tier);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", tier, tierObject.getItemCableMaxSpeed(), tierObject.getItemCableFriction(),
				1.0f / Math.max(tierObject.getItemCableAcceleration(), 0.00000001f)));
	}
}
