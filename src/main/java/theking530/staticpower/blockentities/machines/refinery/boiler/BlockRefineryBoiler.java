package theking530.staticpower.blockentities.machines.refinery.boiler;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

public class BlockRefineryBoiler extends StaticPowerBlock {
	public BlockRefineryBoiler() {
		super(Material.METAL);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(StaticPowerMachineBlock.IS_ON, false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(StaticPowerMachineBlock.IS_ON);
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
