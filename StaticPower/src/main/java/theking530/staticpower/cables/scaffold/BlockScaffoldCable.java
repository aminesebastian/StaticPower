package theking530.staticpower.cables.scaffold;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.init.ModCreativeTabs;

public class BlockScaffoldCable extends AbstractCableBlock {
	public BlockScaffoldCable() {
		super(ModCreativeTabs.CABLES, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.0f);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new CableBakedModel(existingModel, StaticPowerAdditionalModels.CABLE_SCAFFOLD);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityScaffoldCable.TYPE.create(pos, state);
	}

	@Override
	public BlockItem createItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
