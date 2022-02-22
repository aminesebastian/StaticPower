package theking530.staticpower.cables.redstone.bundled;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.CableBoundsCache;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;

public class BlockBundledRedstoneCable extends AbstractCableBlock {
	public BlockBundledRedstoneCable(String name) {
		super(name, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelBakeEvent event) {
		BakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_BUNDLED_REDSTONE_STRAIGHT);
		BakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_BUNDLED_REDSTONE_EXTENSION);
		BakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_BUNDLED_REDSTONE_ATTACHMENT);
		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return false;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return TileEntityBundledRedstoneCable.TYPE.create(pos, state);
	}
}
