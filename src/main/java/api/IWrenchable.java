package api;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.machines.BaseTileEntity;

public interface IWrenchable {

	public void wrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops);
	public void sneakWrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops);
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos);
}
