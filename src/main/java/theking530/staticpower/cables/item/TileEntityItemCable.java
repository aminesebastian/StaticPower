package theking530.staticpower.cables.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
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
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state, StaticPowerTiers.BASIC),
			ModBlocks.ItemCableBasic.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state, StaticPowerTiers.ADVANCED),
			ModBlocks.ItemCableAdvanced.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state, StaticPowerTiers.STATIC),
			ModBlocks.ItemCableStatic.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state, StaticPowerTiers.ENERGIZED),
			ModBlocks.ItemCableEnergized.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state, StaticPowerTiers.LUMUM),
			ModBlocks.ItemCableLumum.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state, StaticPowerTiers.CREATIVE),
			ModBlocks.ItemCableCreative.get());

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

	public TileEntityItemCable(BlockEntityTypeAllocator<TileEntityItemCable> allocator, BlockPos pos, BlockState state,
			ResourceLocation tier) {
		super(allocator, pos, state);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", tier,
				tierObject.itemCableMaxSpeed.get(), tierObject.itemCableFriction.get(),
				1.0f / Math.max(tierObject.itemCableAcceleration.get(), 0.00000001f)));
	}
}
