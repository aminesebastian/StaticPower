package theking530.staticpower.blocks.crops;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import theking530.staticcore.block.IBlockLootTableProvider;
import theking530.staticcore.block.IRenderLayerProvider;
import theking530.staticpower.blocks.StaticPowerFarmland;

/**
 * Base class for a simple single block plant.
 * 
 * @author Amine Sebastian
 *
 */
public class BaseSimplePlant extends CropBlock implements IPlantable, IBlockLootTableProvider, BonemealableBlock, IRenderLayerProvider {
	public static final Logger LOGGER = LogManager.getLogger(BaseSimplePlant.class);

	/**
	 * The different bounding boxes for the crop at different ages.
	 */
	private final VoxelShape[] SHAPES;
	private final Supplier<Item> seedSupplier;

	/**
	 * Simple plant constructor.
	 * 
	 * @param name The registry name for this block sans namespace.
	 */
	public BaseSimplePlant(Supplier<Item> seedSupplier) {
		super(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0.0f).sound(SoundType.CROP));
		SHAPES = getShapesByAge();
		this.seedSupplier = seedSupplier;
	}

	/**
	 * On random block tick, attempt to grow the plant.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
		super.tick(state, worldIn, pos, rand);
		if (!worldIn.isAreaLoaded(pos, 1)) {
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		}
		if (worldIn.getRawBrightness(pos, 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = getGrowthChance(this, worldIn, pos);
				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
					worldIn.setBlock(pos, this.getStateForAge(i + 1), 2);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
				}
			}
		}

	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		if (seedSupplier != null) {
			return new ItemStack(seedSupplier.get(), 1);
		}

		return super.getCloneItemStack(state, target, world, pos, player);
	}

	/**
	 * Gets the bounding boxes for this crop at the provided age.
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(getAgeProperty()) > getMaxAge()) {
			LOGGER.error(String.format("Plant at position: %1$s was found with an invalid value for Age.", pos.toString()));
			return SHAPES[getMaxAge()];
		}
		return SHAPES[state.getValue(getAgeProperty())];
	}

	/**
	 * Raised when this block is activated by the player. Allows for the player to
	 * harvest by right clicking. Sets the age back down to the default once
	 * harvested.
	 */
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (isMaxAge(state)) {
			Block.dropResources(state, worldIn, pos);
			worldIn.setBlock(pos, getStateForAge(0), 2);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Gets the maximum age of the plant. There should be this amount + 1 of models
	 * and states in the blockstates file.
	 */
	@Override
	public int getMaxAge() {
		Object[] values = getAgeProperty().getPossibleValues().toArray();
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
		return BlockStateProperties.AGE_7;
	}

	/**
	 * Gets the current age of the block.
	 * 
	 * @param state The block state to check.
	 * @return Returns the current age.
	 */
	@Override
	protected int getAge(BlockState state) {
		return state.getValue(getAgeProperty());
	}

	/**
	 * Gets the block state to use with this plant at the provided age.
	 * 
	 * @param age The age of the plant.
	 * @return The blockstate at the given age.
	 */
	@Override
	public BlockState getStateForAge(int age) {
		return this.defaultBlockState().setValue(getAgeProperty(), Integer.valueOf(age));
	}

	/**
	 * Checks if this plant is at it's maximum age.
	 * 
	 * @param state The blockstate to check.
	 * @return
	 */
	@Override
	public boolean isMaxAge(BlockState state) {
		return state.getValue(getAgeProperty()) >= this.getMaxAge();
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
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.is(Blocks.FARMLAND) || state.getBlock() instanceof StaticPowerFarmland;
	}

	/**
	 * Gross the plant (through an item such as bonemeal).
	 * 
	 * @param worldIn The world the plant is in.
	 * @param pos     The position of the plant.
	 * @param state   The current state of the plant.
	 */
	public void growUsingBonemeal(Level worldIn, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		worldIn.setBlock(pos, this.getStateForAge(i), 2);
	}

	/**
	 * Gets the bonus applied from bonemeal.
	 * 
	 * @param worldIn The world the plant is in.
	 * @return The amount of stages to advance when bonemeal is used on this plant.
	 */
	@Override
	protected int getBonemealAgeIncrease(Level worldIn) {
		return Mth.nextInt(worldIn.random, 2, 5);
	}

	/**
	 * Gets the chance this block will grow.
	 * 
	 * @param blockIn The block.
	 * @param worldIn The world the plant is in.
	 * @param pos     The current position of the plant.
	 * @return The probability that this plant will grow.
	 */
	protected static float getGrowthChance(Block blockIn, BlockGetter worldIn, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockpos = pos.below();

		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				float f1 = 0.0F;
				BlockState blockstate = worldIn.getBlockState(blockpos.offset(i, 0, j));
				if (blockstate.canSustainPlant(worldIn, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, (IPlantable) blockIn)) {
					f1 = 1.0F;
					if (blockstate.isFertile(worldIn, blockpos.offset(i, 0, j))) {
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
			boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();
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
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return (worldIn.getRawBrightness(pos, 0) >= 8 || worldIn.canSeeSky(pos)) && super.canSurvive(state, worldIn, pos);
	}

	/**
	 * When colided with a ravager, break the block.
	 */
	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof Ravager && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, entityIn)) {
			worldIn.destroyBlock(pos, true, entityIn);
		}

		super.entityInside(state, worldIn, pos, entityIn);
	}

	/**
	 * Whether this IGrowable can grow.
	 */
	@Override
	public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isMaxAge(state);
	}

	/**
	 * Indicates that this plant can be bonemealed.
	 */
	@Override
	public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		return true;
	}

	/**
	 * This method is called when an external item/block wants to grow this item.
	 */
	@Override
	public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		this.growUsingBonemeal(worldIn, pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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
		return new VoxelShape[] { Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
				Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
				Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D) };
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) {
			return defaultBlockState();
		}
		return state;
	}

	@Override
	public BlockDropType getBlockDropType() {
		return BlockDropType.CUSTOM;
	}
}