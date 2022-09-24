package theking530.staticpower.cables.scaffold;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;

public class BlockScaffoldCable extends AbstractCableBlock {
	public BlockScaffoldCable() {
		super(new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.0f);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelBakeEvent event) {
		BakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_SCAFFOLD_EXTENSION);
		BakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_SCAFFOLD_STRAIGHT);
		BakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_SCAFFOLD_ATTACHMENT);

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return TileEntityScaffoldCable.TYPE.create(pos, state);
	}
}
