package theking530.staticpower.blockentities.nonpowered.alloyfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockAlloyFurnace extends StaticPowerRotateableBlockEntityBlock {

	public BlockAlloyFurnace() {
		super(StaticPowerTiers.ENERGIZED);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(StaticPowerMachineBlock.IS_ON, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(StaticPowerMachineBlock.IS_ON);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityAlloyFurnace.TYPE.create(pos, state);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		// Check to see if we have the IS_ON property and it is true. If so, light up.
		if (state.hasProperty(StaticPowerMachineBlock.IS_ON) && state.getValue(StaticPowerMachineBlock.IS_ON)) {
			return 15;
		}
		return super.getLightEmission(state, world, pos);
	}
}
