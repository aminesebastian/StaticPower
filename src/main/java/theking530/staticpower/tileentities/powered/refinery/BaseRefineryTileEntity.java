package theking530.staticpower.tileentities.powered.refinery;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.cables.refinery.RefineryCableComponent;
import theking530.staticpower.tileentities.TileEntityConfigurable;

public class BaseRefineryTileEntity extends TileEntityConfigurable {
	public final RefineryCableComponent refineryCableComponent;
	private final ResourceLocation tier;

	public BaseRefineryTileEntity(BlockEntityTypeAllocator<? extends BaseRefineryTileEntity> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state);
		this.tier = tier;
		registerComponent(refineryCableComponent = new RefineryCableComponent("RefineryCableComponent"));
	}

	public ResourceLocation getTier() {
		return tier;
	}
}
