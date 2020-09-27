package theking530.staticpower.blocks.decorative;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.blocks.StaticPowerBlock;

public class StaticPowerGlassBlock extends StaticPowerBlock {

	/**
	 * Glass block constructor.
	 * 
	 * @param name The registry name of the block sans namespace.
	 */
	public StaticPowerGlassBlock(String name) {
		super(name, Block.Properties.from(Blocks.GLASS).harvestTool(ToolType.PICKAXE).hardnessAndResistance(4.0F)
				.sound(SoundType.GLASS).notSolid());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getTranslucent();
	}

	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
}
