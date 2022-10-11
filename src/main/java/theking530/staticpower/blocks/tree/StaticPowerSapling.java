package theking530.staticpower.blocks.tree;

import java.util.function.Supplier;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.interfaces.IItemBlockProvider;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;

public class StaticPowerSapling extends BushBlock implements BonemealableBlock, IItemBlockProvider, IRenderLayerProvider {
	public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	private final Supplier<AbstractTreeGrower> tree;

	public StaticPowerSapling(Supplier<AbstractTreeGrower> treeIn, Properties properties) {
		super(properties);
		this.tree = treeIn;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
		super.tick(state, worldIn, pos, rand);
		if (!worldIn.isAreaLoaded(pos, 1)) {
			return;
		}
		if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9 && rand.nextInt(7) == 0) {
			this.grow(worldIn, pos, state, rand);
		}
	}

	public void grow(ServerLevel serverWorld, BlockPos pos, BlockState state, RandomSource rand) {
		if (state.getValue(STAGE) == 0) {
			serverWorld.setBlock(pos, state.cycle(STAGE), 4);
		} else {
			if (!ForgeEventFactory.saplingGrowTree(serverWorld, rand, pos))
				return;
			this.tree.get().growTree(serverWorld, serverWorld.getChunkSource().getGenerator(), pos, state, rand);
		}
	}

	@Override
	public void performBonemeal(ServerLevel serverWorld, RandomSource rand, BlockPos pos, BlockState state) {
		this.grow(serverWorld, pos, state, rand);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		return (double) worldIn.random.nextFloat() < 0.45D;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}
}