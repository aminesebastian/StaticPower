package theking530.staticpower.cables.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderItemCable;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityItemCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityItemCable> TYPE = new BlockEntityTypeAllocator<BlockEntityItemCable>("cable_item",
			(allocator, pos, state) -> new BlockEntityItemCable(allocator, pos, state), ModBlocks.ItemCables.values());

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderItemCable::new);
		}
	}

	public final ItemCableComponent cableComponent;

	public BlockEntityItemCable(BlockEntityTypeAllocator<BlockEntityItemCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		StaticPowerTier tierObject = getTierObject();
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", getTier(), tierObject.cableItemConfiguration.itemCableMaxSpeed.get(),
				tierObject.cableItemConfiguration.itemCableFriction.get(), 1.0f / Math.max(tierObject.cableItemConfiguration.itemCableAcceleration.get(), 0.00000001f)));
	}
}
