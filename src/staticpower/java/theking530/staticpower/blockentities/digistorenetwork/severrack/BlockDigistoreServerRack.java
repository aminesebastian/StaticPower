package theking530.staticpower.blockentities.digistorenetwork.severrack;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.rendering.blocks.ServerRackModel;

public class BlockDigistoreServerRack extends StaticPowerRotateableBlockEntityBlock implements ICustomModelProvider {

	public BlockDigistoreServerRack() {
		super(Block.Properties.of(Material.METAL));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new ServerRackModel(existingModel);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityDigistoreServerRack.TYPE.create(pos, state);
	}
}
