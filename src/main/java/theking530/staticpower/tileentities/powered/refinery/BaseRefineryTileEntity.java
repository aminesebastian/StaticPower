package theking530.staticpower.tileentities.powered.refinery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.powered.refinery.controller.TileEntityRefineryController;

public class BaseRefineryTileEntity extends TileEntityConfigurable {
	private final ResourceLocation tier;
	private TileEntityRefineryController controller;

	public BaseRefineryTileEntity(BlockEntityTypeAllocator<? extends BaseRefineryTileEntity> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state);
		this.tier = tier;
	}

	public ResourceLocation getTier() {
		return tier;
	}

	public void setController(TileEntityRefineryController controller) {
		this.controller = controller;
	}

	public TileEntityRefineryController getController() {
		return controller;
	}

	public boolean hasController() {
		return controller != null;
	}

	@Override
	public void setRemoved() {
		// Call the super AFTER everything has been cleaned up.
		super.setRemoved();
		requestControllerRefresh();
	}

	@Override
	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {
		super.onNeighborChanged(currentState, neighborPos, isMoving);
		requestControllerRefresh();
	}

	@Override
	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		super.onNeighborReplaced(state, direction, facingState, FacingPos);
		requestControllerRefresh();
	}

	protected void requestControllerRefresh() {
		if (controller != null) {
			controller.requestMultiBlockRefresh();
		}
	}
}
