package theking530.staticcore.blockentity.components.multiblock.newstyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.blockentities.nonpowered.blastfurnace.BlockBlastFurnace;

public abstract class AbstractMultiblockPattern {

	public abstract MultiblockState checkWellFormed(Level level, BlockPos startPos);

	public abstract boolean isValidBlock(BlockState state);

	public abstract boolean isValidBlockForPosition(BlockState state, Direction facing, BlockPos relativePos);

	public abstract int getMaxX();

	public abstract int getMaxY();

	public abstract int getMaxZ();

	public void onWellFormedOnPlaceEvent(MultiblockState state, BlockEvent.EntityPlaceEvent event) {
		System.out.println(state);
		for (Entry<BlockPos, BlockState> entry : state.getBlocks().entrySet()) {
			if (!entry.getValue().isAir()) {
				BlockState newState = entry.getValue().setValue(BlockBlastFurnace.IS_IN_VALID_MULTIBLOCK, true);
				event.getLevel().setBlock(entry.getKey(), newState, 3);
			}
		}

		List<Vector2D> offsets = new ArrayList<>();
		offsets.add(new Vector2D(1, 0));
		offsets.add(new Vector2D(0, 1));
		offsets.add(new Vector2D(2, 1));
		offsets.add(new Vector2D(1, 2));

		BlockPos origin = state.getBlocks().firstKey();
		for (Vector2D offset : offsets) {
			BlockPos showFacePos = origin.offset(offset.getXi(), 1, offset.getYi());
			BlockState newState = event.getLevel().getBlockState(showFacePos).setValue(BlockBlastFurnace.SHOW_FACE,
					true);
			event.getLevel().setBlock(showFacePos, newState, 3);
		}
	}
}
