package theking530.staticpower.blocks.crops;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.IBlockRenderLayerProvider;

/**
 * Base class for a simple single block plant.
 * 
 * @author Amine Sebastian
 *
 */
public class BaseSimplePlant extends CropsBlock implements IPlantable, IGrowable, IBlockRenderLayerProvider {
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
	 * On random block tick, attempt to grow the plant.
	 */
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);
		if (!worldIn.isAreaLoaded(pos, 1)) {
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		}
		if (worldIn.getLightSubtracted(pos, 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = getGrowthChance(this, worldIn, pos);
				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
					worldIn.setBlockState(pos, this.withAge(i + 1), 2);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
				}
			}
		}

	}

	/**
	 * Gets the bounding boxes for this crop at the provided age.
	 */
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(getAgeProperty()) > getMaxAge()) {
			StaticPower.LOGGER.error(String.format("Plant at position: %1$s was found with an invalid value for Age.", pos.toString()));
			return SHAPES[getMaxAge()];
		}
		return SHAPES[state.get(getAgeProperty())];
	}

	/**
	 * Raised when this block is activated by the player. Allows for the player to
	 * harvest by right clicking. Sets the age back down to the default once
	 * harvested.
	 */
	@Override
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
		Object[] values = getAgeProperty().getAllowedValues().toArray();
		return (int) values[values.length - 1];
	}

	/**
	 * Returns the age property used for this plant. This dictates how many stages
	 * of growth this plant has.
	 * 
	 * @return
	 */
	@Override
	public IntegerProperty getAgeProperty() {
		return BlockStateProperties.AGE_0_7;
	}

	/**
	 * Gets the current age of the block.
	 * 
	 * @param state The block state to check.
	 * @return Returns the current age.
	 */
	@Override
	protected int getAge(BlockState state) {
		return state.get(getAgeProperty());
	}

	/**
	 * Gets the block state to use with this plant at the provided age.
	 * 
	 * @param age The age of the plant.
	 * @return The blockstate at the given age.
	 */
	@Override
	public BlockState withAge(int age) {
		return this.getDefaultState().with(getAgeProperty(), Integer.valueOf(age));
	}

	/**
	 * Checks if this plant is at it's maximum age.
	 * 
	 * @param state The blockstate to check.
	 * @return
	 */
	@Override
	public boolean isMaxAge(BlockState state) {
		return state.get(getAgeProperty()) >= this.getMaxAge();
	}

	/**
	 * Checks to see if this plant is currently on valid ground.
	 * 
	 * @param state   The blockstate of the plant.
	 * @param worldIn The world the plant is in.
	 * @param pos     The position of the ground.
	 * @return True if the plant should continue to grow, false otherwise.
	 */
	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	/**
	 * Gross the plant (through an item such as bonemeal).
	 * 
	 * @param worldIn The world the plant is in.
	 * @param pos     The position of the plant.
	 * @param state   The current state of the plant.
	 */
	public void growUsingBonemeal(World worldIn, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		worldIn.setBlockState(pos, this.withAge(i), 2);
	}

	/**
	 * Gets the bonus applied from bonemeal.
	 * 
	 * @param worldIn The world the plant is in.
	 * @return The amount of stages to advance when bonemeal is used on this plant.
	 */
	@Override
	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.nextInt(worldIn.rand, 2, 5);
	}

	/**
	 * Gets the chance this block will grow.
	 * 
	 * @param blockIn The block.
	 * @param worldIn The world the plant is in.
	 * @param pos     The current position of the plant.
	 * @return The probability that this plant will grow.
	 */
	protected static float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockpos = pos.down();

		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				float f1 = 0.0F;
				BlockState blockstate = worldIn.getBlockState(blockpos.add(i, 0, j));
				if (blockstate.canSustainPlant(worldIn, blockpos.add(i, 0, j), net.minecraft.util.Direction.UP, (IPlantable) blockIn)) {
					f1 = 1.0F;
					if (blockstate.isFertile(worldIn, blockpos.add(i, 0, j))) {
						f1 = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					f1 /= 4.0F;
				}

				f += f1;
			}
		}

		BlockPos blockpos1 = pos.north();
		BlockPos blockpos2 = pos.south();
		BlockPos blockpos3 = pos.west();
		BlockPos blockpos4 = pos.east();
		boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
		boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();
		if (flag && flag1) {
			f /= 2.0F;
		} else {
			boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();
			if (flag2) {
				f /= 2.0F;
			}
		}

		return f;
	}

	/**
	 * Indicates if this plant is growing on a valid position.
	 */
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return true;
	}

	/**
	 * When colided with a ravager, break the block.
	 */
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof RavagerEntity && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, entityIn)) {
			worldIn.destroyBlock(pos, true, entityIn);
		}

		super.onEntityCollision(state, worldIn, pos, entityIn);
	}

	/**
	 * Whether this IGrowable can grow.
	 */
	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isMaxAge(state);
	}

	/**
	 * Indicates that this plant can be bonemealed.
	 */
	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	/**
	 * This method is called when an external item/block wants to grow this item.
	 */
	@Override
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		this.growUsingBonemeal(worldIn, pos, state);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(getAgeProperty());
	}

	/**
	 * Provides the array of shapes by age of the block. This is called once on
	 * block creation and cached, so should not be made to depend on runtime logic.
	 * 
	 * @return An array of {@link VoxelShape}s that indicate the sizes for each age
	 *         of the plant.
	 */
	public VoxelShape[] getShapesByAge() {
		return new VoxelShape[] { Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
				Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
				Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D) };
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.getCutout();
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) {
			return getDefaultState();
		}
		return state;
	}
}