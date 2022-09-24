package theking530.staticpower.blocks.decorative;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.StaticPowerBlock;

public class StaticPowerGlassBlock extends StaticPowerBlock {

	/**
	 * Glass block constructor.
	 * 
	 * @param name The registry name of the block sans namespace.
	 */
	public StaticPowerGlassBlock() {
		super(Block.Properties.copy(Blocks.GLASS).strength(4.0F).sound(SoundType.GLASS).noOcclusion());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.translucent();
	}

	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}
}
