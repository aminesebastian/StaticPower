package theking530.staticpower.items.crops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import theking530.staticpower.items.StaticPowerItem;

/**
 * Base class for any static power items that are responsible for planting a
 * crop.
 * 
 * @author Amine Sebastian
 *
 */
public class CropSeeds extends StaticPowerItem implements IPlantable {
	/**
	 * The block this seed is responsible for planting.
	 */
	private final Block CROP_BLOCK;

	/**
	 * Creates a crop seed.
	 * 
	 * @param name      The registry name of the seed without namespace.
	 * @param blockCrop The block this crop should plant.
	 */
	public CropSeeds(String name, Block blockCrop) {
		super(name);
		CROP_BLOCK = blockCrop;
	}

	/**
	 * Attempts to plant the item on the block it was used on.
	 */
	@Override
	public ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		BlockState state = world.getBlockState(pos);
		if (context.getFace() == Direction.UP && player.canPlayerEdit(pos.offset(face), face, item) && state.getBlock().canSustainPlant(state, world, pos, Direction.UP, this)
				&& world.isAirBlock(pos.up())) {
			world.setBlockState(pos.up(), this.CROP_BLOCK.getDefaultState());
			item.setCount(item.getCount() - 1);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}

	/**
	 * Gets the type of this plant.
	 */
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.Crop;
	}

	/**
	 * Returns the block this seed with plant.
	 */
	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return CROP_BLOCK.getDefaultState();
	}
}