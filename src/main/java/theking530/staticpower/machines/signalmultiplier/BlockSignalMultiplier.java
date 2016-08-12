package theking530.staticpower.machines.signalmultiplier;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;

public class BlockSignalMultiplier extends BaseMachineBlock implements ITileEntityProvider{

	private boolean isConnected = true;
	float pixel = 1F/16F;
	
	public BlockSignalMultiplier(String name) {
		super(name);
	}
	@Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		AxisAlignedBB aabb = new AxisAlignedBB(0, 0, 0, 1, pixel*3, 1);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
    }
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (world.isRemote) {
    		return true;
    	}else if (!player.isSneaking()) {
    		TileEntitySignalMultiplier entity = (TileEntitySignalMultiplier) world.getTileEntity(pos);
    		if (entity != null) {
    			FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDSignalMultiplier, world, pos.getX(), pos.getY(), pos.getZ());

    		}
    		return true;
    	}else{
    		return false;
    	}
	}
    
    public boolean canProvidePower()
    {
        return true;
    }
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntitySignalMultiplier();
	}

}
