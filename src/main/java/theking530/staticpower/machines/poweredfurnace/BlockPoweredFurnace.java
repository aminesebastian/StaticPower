package theking530.staticpower.machines.poweredfurnace;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;

public class BlockPoweredFurnace extends BaseMachineBlock{

	public BlockPoweredFurnace() {
		super("PoweredFurnace");
		setHardness(3.5f);
	    setResistance(5.0f);
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (world.isRemote) {
    		return true;
    	}else if (!player.isSneaking()) {
    		TileEntityPoweredFurnace entity = (TileEntityPoweredFurnace) world.getTileEntity(pos);
    		if (entity != null) {
    			FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDPoweredFurnace, world, pos.getX(), pos.getY(), pos.getZ());

    		}
    		return true;
    	}else{
    		return false;
    	}
	}
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityPoweredFurnace();
	}
}
