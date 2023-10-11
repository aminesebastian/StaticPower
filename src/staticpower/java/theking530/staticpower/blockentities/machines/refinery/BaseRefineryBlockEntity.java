package theking530.staticpower.blockentities.machines.refinery;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.multiblock.IMultiBlockComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiBlockEntry;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.machines.refinery.controller.BlockEntityRefineryController;

public class BaseRefineryBlockEntity extends BlockEntityBase implements IMultiBlockComponent<BlockEntityRefineryController> {
	private MultiBlockEntry<BlockEntityRefineryController> token;
	private final ResourceLocation tier;

	public BaseRefineryBlockEntity(BlockEntityTypeAllocator<? extends BaseRefineryBlockEntity> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state);
		this.tier = tier;
	}

	@Override
	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {
		super.onNeighborChanged(currentState, neighborPos, isMoving);
		if (hasToken()) {
			getToken().remove();
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (hasToken()) {
			getToken().remove();
		}
	}

	@Override
	public ResourceLocation getTier() {
		return tier;
	}

	@Override
	public void multiBlockValidated(MultiBlockEntry<BlockEntityRefineryController> token) {
		this.token = token;
	}

	@Override
	public void multiBlockBroken() {
		token = null;
	}

	@Override
	public MultiBlockEntry<BlockEntityRefineryController> getToken() {
		return token;
	}

	public boolean hasController() {
		return hasToken();
	}

	public BlockEntityRefineryController getController() {
		if (!hasController()) {
			return null;
		}

		return token.getController();
	}

}
