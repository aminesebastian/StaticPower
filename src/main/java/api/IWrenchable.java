package api;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentity.BaseTileEntity;

public interface IWrenchable {

	public void wrenchBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean returnDrops);
	public void sneakWrenchBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean returnDrops);
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos, EnumFacing facing);
}
