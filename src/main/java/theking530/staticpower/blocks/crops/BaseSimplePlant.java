package theking530.staticpower.blocks.crops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.IBlockRenderLayerProvider;

/**
 * Base class for a simple single block plant.
 * 
 * @author Amine Sebastian
 *
 */
public class BaseSimplePlant extends CropsBlock implements IGrowable, IBlockRenderLayerProvider {
	/**
	 * The different bounding boxes for the crop at different ages.
	 */
	private final VoxelShape[] SHAPES;

	/**
	 * Simple plant constructor.
	 * 
	 * @param name The registry name for this block sans namespace.
	 */
	public BaseSimplePlant(String name) {
		super(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0f).sound(SoundType.CROP));
		setRegistryName(name);
		SHAPES = getShapesByAge();
	}

	/**
	 * Gets the bounding boxes for this crop at the provided age.
	 */
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(this.getAgeProperty()) > getMaxAge()) {
			StaticPower.LOGGER.error(String.format("Plant at position: %1$s was found with an invalid value for Age.", pos.toString()));
			return SHAPES[getMaxAge()];
		}
		return SHAPES[state.get(this.getAgeProperty())];
	}

	/**
	 * Raised when this block is activated by the player. Allows for the player to
	 * harvest by right clicking. Sets the age back down to the default once
	 * harvested.
	 */
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (isMaxAge(state)) {
			Block.spawnDrops(state, worldIn, pos);
			worldIn.setBlockState(pos, withAge(0), 2);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	/**
	 * Gets the maximum age of the plant. There should be this amount + 1 of models
	 * and states in the blockstates file.
	 */
	@Override
	public int getMaxAge() {
		return 7;
	}

	/**
	 * Provides the array of shapes by age of the block. This is called once on
	 * block creation and cached, so should not be made to depend on runtime logic.
	 * 
	 * @return An array of {@link VoxelShape}s that indicate the sizes for each age
	 *         of the plant.
	 */
	public VoxelShape[] getShapesByAge() {
		return new VoxelShape[] { Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
				Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
				Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
				Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D) };
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.getCutout();
	}
}