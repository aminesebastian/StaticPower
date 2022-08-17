package theking530.staticpower.tileentities.powered.solarpanels;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockSolarPanel extends StaticPowerTileEntityBlock {
	public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.5D, 16.0D);
	public final ResourceLocation tierType;

	public BlockSolarPanel(ResourceLocation tierType) {
		this.tierType = tierType;
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
		tooltip.add(new TextComponent(ChatFormatting.GREEN.toString() + "� Generation ")
				.append(GuiTextUtilities.formatEnergyRateToString(StaticPowerConfig.getTier(tierType).solarPanelPowerGeneration.get())));
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tierType == StaticPowerTiers.BASIC) {
			return TileEntitySolarPanel.TYPE_BASIC.create(pos, state);
		} else if (tierType == StaticPowerTiers.ADVANCED) {
			return TileEntitySolarPanel.TYPE_ADVANCED.create(pos, state);
		} else if (tierType == StaticPowerTiers.STATIC) {
			return TileEntitySolarPanel.TYPE_STATIC.create(pos, state);
		} else if (tierType == StaticPowerTiers.ENERGIZED) {
			return TileEntitySolarPanel.TYPE_ENERGIZED.create(pos, state);
		} else if (tierType == StaticPowerTiers.LUMUM) {
			return TileEntitySolarPanel.TYPE_LUMUM.create(pos, state);
		} else if (tierType == StaticPowerTiers.CREATIVE) {
			return TileEntitySolarPanel.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}
}
