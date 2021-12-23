package theking530.staticpower.cables.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderItemCable;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityItemCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.BASIC), ModBlocks.ItemCableBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.ADVANCED), ModBlocks.ItemCableAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.STATIC), ModBlocks.ItemCableStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.ENERGIZED), ModBlocks.ItemCableEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator) -> new TileEntityItemCable(allocator, StaticPowerTiers.LUMUM), ModBlocks.ItemCableLumum);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityItemCable>(
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

	public TileEntityItemCable(BlockEntityTypeAllocator<TileEntityItemCable> allocator, ResourceLocation tier) {
		super(allocator);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", tier, tierObject.itemCableMaxSpeed.get(), tierObject.itemCableFriction.get(),
				1.0f / Math.max(tierObject.itemCableAcceleration.get(), 0.00000001f)));
	}
}
