package theking530.staticpower.machines.solarpanel;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;

public class BlockSolarPanel extends BaseMachineBlock {

	public Tier TIER;
	
	public BlockSolarPanel(String name, Tier tier) {
		super(name);
		TIER = tier;
		setCreativeTab(StaticPower.StaticPower);
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public void sneakWrenchBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {
		ArrayList<ItemStack> items = new ArrayList();
		ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
		EntityItem droppedItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), machineStack);
		world.spawnEntityInWorld(droppedItem);
		world.setBlockToAir(pos);		
	}

	@Override
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
		return true;
	}
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		switch(TIER) {
		case BASE:
			return new TileEntityBasicSolarPanel();
		case STATIC:
			return new TileEntityStaticSolarPanel();
		case ENERGIZED:
			return new TileEntityEnergizedSolarPanel();	
		case LUMUM:
			return new TileEntityLumumSolarPanel();
		case CREATIVE:
			return new TileEntityCreativeSolarPanel();
		}
		return new TileEntityBasicSolarPanel();
	}
}
