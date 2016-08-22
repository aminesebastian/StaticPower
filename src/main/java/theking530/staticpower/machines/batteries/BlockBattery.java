package theking530.staticpower.machines.batteries;

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
import theking530.staticpower.assists.Tier;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityEnergizedBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityLumumBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityStaticBattery;

public class BlockBattery extends BaseMachineBlock{
	
	private static boolean keepInventory = false;	
	public Tier TIER;
	
	public BlockBattery(String name, Tier tier) {
		super(name);
		setHardness(3.5f);
	    setResistance(5.0f);
	    TIER = tier;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {	
			if (world.isRemote) {
	    		return true;
	    	}else if (!player.isSneaking()) {
	    		TileEntityBattery entity = (TileEntityBattery) world.getTileEntity(pos);
	    		if (entity != null) {
	    			FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDBattery, world, pos.getX(), pos.getY(), pos.getZ());
	    		}
	    		return true;
	    	}
			return false;
	}
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		switch(TIER){
		case STATIC: return new TileEntityStaticBattery();
		case ENERGIZED: return new TileEntityEnergizedBattery();
		case LUMUM: return new TileEntityLumumBattery();
		default: return new TileEntityStaticBattery();
		}
	}
}
