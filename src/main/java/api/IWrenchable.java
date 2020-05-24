package api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * <p>
 * Any block that requires wrench interaction with an item implementing
 * {@link IWrenchTool} must implement this interface.
 * </p>
 * 
 * @author Amine Sebastian
 *
 */
public interface IWrenchable {

	/**
	 * This method is raised when an item implementing {@link IWrenchTool} right clicks this block.
	 * @param player The player wielding the {@link IWrenchTool}.
	 * @param mode The mode of the {@link IWrenchTool}.
	 * @param wrench The {@link IWrenchTool} itemstack.
	 * @param world The world.
	 * @param pos The position of the block.
	 * @param facing The facing direction f the block.
	 * @param returnDrops True if this block should return drops, false otherwise.
	 */
	public void wrenchBlock(EntityPlayer player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos,
			EnumFacing facing, boolean returnDrops);
	/**
	 * This method is raised when an item implementing {@link IWrenchTool} sneak right clicks this block.
	 * @param player The player wielding the {@link IWrenchTool}.
	 * @param mode The mode of the {@link IWrenchTool}.
	 * @param wrench The {@link IWrenchTool} itemstack.
	 * @param world The world.
	 * @param pos The position of the block.
	 * @param facing The facing direction f the block.
	 * @param returnDrops True if this block should return drops, false otherwise.
	 */
	public void sneakWrenchBlock(EntityPlayer player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos,
			EnumFacing facing, boolean returnDrops);

	/**
	 * Determines if this block can be wrenched.
	 * @param player The player wielding the {@link IWrenchTool}.
	 * @param world The world.
	 * @param pos The position of the block.
	 * @param facing The facing direction of the block.
	 * @param sneaking True if the player is sneaking, false otherwise.
	 * @return
	 */
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean sneaking);
}
