package theking530.staticpower.tileentities.cables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;

public class BlockPowerCable extends StaticPowerTileEntityBlock implements ICustomModelSupplier {

	public BlockPowerCable(String name) {
		super(name, Block.Properties.create(Material.IRON).notSolid());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityPowerCable();
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
	}

	@Override
	public boolean hasModelOverride() {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(IBakedModel originalModel) {
		if (originalModel == null) {
			return null;
		}
		return new CableBakedModel(originalModel);
	}
}
