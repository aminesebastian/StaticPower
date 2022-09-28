package theking530.staticpower.blockentities.power.lamp;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityLightSocket extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityLightSocket> TYPE = new BlockEntityTypeAllocator<BlockEntityLightSocket>(
			(allocator, pos, state) -> new BlockEntityLightSocket(allocator, pos, state), ModBlocks.LightSocket);

	private boolean shouldBeOn;

	public BlockEntityLightSocket(BlockEntityTypeAllocator<BlockEntityLightSocket> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		enableFaceInteraction();
		powerStorage.setInputCurrentTypes(CurrentType.ALTERNATING, CurrentType.DIRECT);
	}

	@Override
	public void process() {
		boolean hasEnoughPower = false;
		if (powerStorage.getStoredPower() > 0.5) {
			hasEnoughPower = true;
			this.powerStorage.drainPower(0.5, false);
		} else {
			hasEnoughPower = false;
		}

		if (hasEnoughPower != shouldBeOn) {
			shouldBeOn = hasEnoughPower;
			this.addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), false);
			getLevel().getChunkSource().getLightEngine().checkBlock(getBlockPos());
		}
	}

	public boolean isLit() {
		return shouldBeOn;
	}
}
