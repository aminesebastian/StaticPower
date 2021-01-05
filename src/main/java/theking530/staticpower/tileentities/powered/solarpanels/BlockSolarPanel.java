package theking530.staticpower.tileentities.powered.solarpanels;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockSolarPanel extends StaticPowerTileEntityBlock {
	public static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.5D, 16.0D);
	public final ResourceLocation tierType;

	public BlockSolarPanel(String name, ResourceLocation tierType) {
		super(name);
		this.tierType = tierType;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new StringTextComponent(TextFormatting.GREEN.toString() + "• Generation ")
				.append(GuiTextUtilities.formatEnergyRateToString(StaticPowerConfig.getTier(tierType).solarPanelPowerGeneration.get())));
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (tierType == StaticPowerTiers.BASIC) {
			return TileEntitySolarPanel.TYPE_BASIC.create();
		} else if (tierType == StaticPowerTiers.ADVANCED) {
			return TileEntitySolarPanel.TYPE_ADVANCED.create();
		} else if (tierType == StaticPowerTiers.STATIC) {
			return TileEntitySolarPanel.TYPE_STATIC.create();
		} else if (tierType == StaticPowerTiers.ENERGIZED) {
			return TileEntitySolarPanel.TYPE_ENERGIZED.create();
		} else if (tierType == StaticPowerTiers.LUMUM) {
			return TileEntitySolarPanel.TYPE_LUMUM.create();
		} else if (tierType == StaticPowerTiers.CREATIVE) {
			return TileEntitySolarPanel.TYPE_CREATIVE.create();
		}
		return null;
	}
}
