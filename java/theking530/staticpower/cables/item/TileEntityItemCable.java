package theking530.staticpower.cables.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderItemCable;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class TileEntityItemCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state), ModBlocks.ItemCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state), ModBlocks.ItemCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state), ModBlocks.ItemCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state), ModBlocks.ItemCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state), ModBlocks.ItemCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityItemCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityItemCable>(
			(allocator, pos, state) -> new TileEntityItemCable(allocator, pos, state), ModBlocks.ItemCableCreative);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
		}
	}

	public final ItemCableComponent cableComponent;

	public TileEntityItemCable(BlockEntityTypeAllocator<TileEntityItemCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		StaticPowerTier tierObject = getTierObject();
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", getTier(), tierObject.cableItemConfiguration.itemCableMaxSpeed.get(),
				tierObject.cableItemConfiguration.itemCableFriction.get(), 1.0f / Math.max(tierObject.cableItemConfiguration.itemCableAcceleration.get(), 0.00000001f)));
	}
}
