package theking530.api.wrench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * <p>
 * Any block that requires wrench interaction from an item implementing
 * {@link IWrenchTool} must implement this interface.
 * </p>
 * 
 * @author Amine Sebastian
 *
 */
public interface IWrenchable {

	/**
	 * This method is raised when an item implementing {@link IWrenchTool} right
	 * clicks this block.
	 * 
	 * @param player      The player wielding the {@link IWrenchTool}.
	 * @param mode        The mode of the {@link IWrenchTool}.
	 * @param wrench      The {@link IWrenchTool} itemstack.
	 * @param world       The world.
	 * @param pos         The position of the block.
	 * @param facing      The facing direction f the block.
	 * @param returnDrops True if this block should return drops, false otherwise.
	 */
	public void wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops);

	/**
	 * This method is raised when an item implementing {@link IWrenchTool} sneak
	 * right clicks this block.
	 * 
	 * @param player      The player wielding the {@link IWrenchTool}.
	 * @param mode        The mode of the {@link IWrenchTool}.
	 * @param wrench      The {@link IWrenchTool} itemstack.
	 * @param world       The world.
	 * @param pos         The position of the block.
	 * @param facing      The facing direction f the block.
	 * @param returnDrops True if this block should return drops, false otherwise.
	 */
	public void sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops);

	/**
	 * Determines if this block can be wrenched.
	 * 
	 * @param player   The player wielding the {@link IWrenchTool}.
	 * @param world    The world.
	 * @param pos      The position of the block.
	 * @param facing   The facing direction of the block.
	 * @param sneaking True if the player is sneaking, false otherwise.
	 * @return
	 */
	public boolean canBeWrenched(PlayerEntity player, World world, BlockPos pos, Direction facing, boolean sneaking);
}
