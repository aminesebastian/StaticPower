package theking530.staticpower.blockentities.power.resistor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.wrench.RegularWrenchMode;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;

public class BlockResistor extends StaticPowerRotateableBlockEntityBlock {
	public static final VoxelShape X_AXIS_SHAPE = Block.box(0, 5D, 5D, 16D, 11D, 11D);
	public static final VoxelShape Z_AXIS_SHAPE = Block.box(5D, 5D, 0.0D, 11D, 11D, 16.0D);

	private final double powerLimit;

	public BlockResistor(double powerLimit) {
		super(StaticCoreTiers.BASIC);
		this.powerLimit = powerLimit;
	}

	public double getPowerLimit() {
		return powerLimit;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING).getAxis() == Axis.Z) {
			return Z_AXIS_SHAPE;
		} else {
			return X_AXIS_SHAPE;
		}
	}

	@Override
	protected boolean canBeWaterlogged() {
		return true;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (facing != Direction.UP && facing != Direction.DOWN) {
			if (facing != world.getBlockState(pos).getValue(getFacingType())) {
				world.setBlock(pos, world.getBlockState(pos).setValue(getFacingType(), facing), 1 | 2);
			} else {
				world.setBlock(pos, world.getBlockState(pos).setValue(getFacingType(), facing.getOpposite()), 1 | 2);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {

	}

	@Override
	public Component getDisplayName(ItemStack stack) {
		return Component.translatable("block.staticpower.resistor").append(" (").append(PowerTextFormatting.formatPowerRateToString(getPowerLimit()).append(")"));
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityResistor.TYPE.create(pos, state);
	}
}
