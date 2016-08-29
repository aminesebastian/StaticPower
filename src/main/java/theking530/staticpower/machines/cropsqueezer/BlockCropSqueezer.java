package theking530.staticpower.machines.cropsqueezer;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.utils.EnumTextFormatting;

public class BlockCropSqueezer extends BaseMachineBlock{
	
	private static boolean keepInventory = false;
	
	public BlockCropSqueezer() {
		super("CropSqueezer");
		setHardness(3.5f);
	    setResistance(5.0f);
		setCreativeTab(StaticPower.StaticPower);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (world.isRemote) {
    		return true;
    	}else if (!player.isSneaking()) {
    		TileEntityCropSqueezer entity = (TileEntityCropSqueezer) world.getTileEntity(pos);
        	FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDCropSqueezer, world, pos.getX(), pos.getY(), pos.getZ());
    		return true;
    	}else{
    		return false;
    	}
}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCropSqueezer();
	}
}
