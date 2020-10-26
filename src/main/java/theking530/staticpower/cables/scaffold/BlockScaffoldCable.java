package theking530.staticpower.cables.scaffold;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.CableBoundsCache;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;

public class BlockScaffoldCable extends AbstractCableBlock {
	public BlockScaffoldCable(String name) {
		super(name, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_SCAFFOLD_EXTENSION);
		IBakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_SCAFFOLD_STRAIGHT);
		IBakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_SCAFFOLD_ATTACHMENT);

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityScaffoldCable.TYPE.create();
	}
}
