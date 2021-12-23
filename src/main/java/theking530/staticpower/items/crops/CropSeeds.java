package theking530.staticpower.items.crops;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
	public InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		BlockState state = world.getBlockState(pos);
		if (context.getClickedFace() == Direction.UP && player.mayUseItemAt(pos.relative(face), face, item) && state.getBlock().canSustainPlant(state, world, pos, Direction.UP, this)
				&& world.isEmptyBlock(pos.above())) {
			world.setBlockAndUpdate(pos.above(), this.CROP_BLOCK.defaultBlockState());
			item.setCount(item.getCount() - 1);
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	/**
	 * Gets the type of this plant.
	 */
	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}

	/**
	 * Returns the block this seed with plant.
	 */
	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		return CROP_BLOCK.defaultBlockState();
	}
}