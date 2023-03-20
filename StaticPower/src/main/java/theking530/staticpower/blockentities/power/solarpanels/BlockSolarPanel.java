package theking530.staticpower.blockentities.power.solarpanels;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;

public class BlockSolarPanel extends StaticPowerRotateableBlockEntityBlock {
	public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.5D, 16.0D);

	public BlockSolarPanel(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.literal(ChatFormatting.GREEN.toString() + " • Generation ")
				.append(PowerTextFormatting.formatPowerRateToString(StaticCoreConfig.getTier(getTier()).powerConfiguration.solarPanelPowerGeneration.get())));
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (getTier() == StaticCoreTiers.BASIC) {
			return BlockEntitySolarPanel.TYPE_BASIC.create(pos, state);
		} else if (getTier() == StaticCoreTiers.ADVANCED) {
			return BlockEntitySolarPanel.TYPE_ADVANCED.create(pos, state);
		} else if (getTier() == StaticCoreTiers.STATIC) {
			return BlockEntitySolarPanel.TYPE_STATIC.create(pos, state);
		} else if (getTier() == StaticCoreTiers.ENERGIZED) {
			return BlockEntitySolarPanel.TYPE_ENERGIZED.create(pos, state);
		} else if (getTier() == StaticCoreTiers.LUMUM) {
			return BlockEntitySolarPanel.TYPE_LUMUM.create(pos, state);
		} else if (getTier() == StaticCoreTiers.CREATIVE) {
			return BlockEntitySolarPanel.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}
}
