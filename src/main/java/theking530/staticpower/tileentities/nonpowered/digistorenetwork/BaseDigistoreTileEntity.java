package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {

	private TileEntityDigistoreManager manager;

	public BaseDigistoreTileEntity(TileEntityType<?> teType) {
		super(teType);
	}

	public void setManager(TileEntityDigistoreManager newManager) {
		manager = newManager;
	}

	public TileEntityDigistoreManager getManager() {
		if (manager != null && !manager.getNetwork().getMasterList().containsKey(getPos())) {
			manager = null;
		}
		return manager;
	}

	public boolean hasManager() {
		return getManager() != null;
	}

	public void onBroken() {

	}

	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

	}

	public void onNeighborUpdated(BlockState observerState, World world, Block oldBlock, BlockPos changedBlockPos, Block newBlock) {
	}
}
