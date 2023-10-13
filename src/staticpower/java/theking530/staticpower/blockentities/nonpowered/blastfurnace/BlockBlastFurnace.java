package theking530.staticpower.blockentities.nonpowered.blastfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockBlockStateProperties;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockBlastFurnace extends StaticPowerRotateableBlockEntityBlock {
	public static final BooleanProperty SHOW_FACE = BooleanProperty.create("show_face");

	public BlockBlastFurnace() {
		super(StaticPowerTiers.BASIC);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(StaticPowerMachineBlock.IS_ON, false)
				.setValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK, false).setValue(SHOW_FACE, false)
				.setValue(MultiblockBlockStateProperties.IS_MASTER, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(StaticPowerMachineBlock.IS_ON);
		builder.add(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK);
		builder.add(SHOW_FACE);
		builder.add(MultiblockBlockStateProperties.IS_MASTER);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		return state.getValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK) ? HasGuiType.ALWAYS
				: HasGuiType.NEVER;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (state.getValue(MultiblockBlockStateProperties.IS_MASTER)) {
			return BlockEntityBlastFurnace.TYPE.create(pos, state);
		} else if (state.getValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
			return BlockEntityBlastFurnaceProxy.TYPE.create(pos, state);
		}
		return null;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		if (!state.getValue(SHOW_FACE)) {
			return super.getLightEmission(state, world, pos);
		}
		// Check to see if we have the IS_ON property and it is true. If so, light up.
		if (state.hasProperty(StaticPowerMachineBlock.IS_ON) && state.getValue(StaticPowerMachineBlock.IS_ON)) {
			return 15;
		}
		return super.getLightEmission(state, world, pos);
	}
}
