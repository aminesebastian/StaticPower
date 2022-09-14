package theking530.staticpower.blockentities.power.wireconnector;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;

public class BlockWireConnector extends StaticPowerBlockEntityBlock {
	public BlockWireConnector(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityWireConnector.TYPE_BASIC.create(pos, state);
	}
}
