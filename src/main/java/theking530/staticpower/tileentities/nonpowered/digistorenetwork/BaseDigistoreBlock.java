package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;

public abstract class BaseDigistoreBlock extends StaticPowerTileEntityBlock {

	public BaseDigistoreBlock(String name) {
		super(name);
	}
	public boolean isOpaqueCube(BlockState state) {
		return true;
	}
	public boolean isFullCube(BlockState state) {
		return true;		
	}

    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
    	if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BaseDigistoreTileEntity) {
    		((BaseDigistoreTileEntity)world.getTileEntity(pos)).onPlaced(world, pos, state, placer, stack);
    	}
    }
    public void breakBlock(World world, BlockPos pos, BlockState state) {
    	this.shouldDropContents = false;
    	if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BaseDigistoreTileEntity) {
    		((BaseDigistoreTileEntity)world.getTileEntity(pos)).onBroken();
    	}
    }

    public void observedNeighborChange(BlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos){
		super.onNeighborChange(world, observerPos, changedBlockPos);
    	if(world.getTileEntity(observerPos) != null && world.getTileEntity(observerPos) instanceof BaseDigistoreTileEntity) {
    		((BaseDigistoreTileEntity)world.getTileEntity(observerPos)).onNeighborUpdated(observerState, world, changedBlock, changedBlockPos, world.getBlockState(changedBlockPos).getBlock());
    	}
    }
	
}
