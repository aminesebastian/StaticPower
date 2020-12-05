package theking530.staticpower.tileentities.powered.pump;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockPump extends StaticPowerTileEntityBlock {
	private final ResourceLocation tier;

	public BlockPump(String name, ResourceLocation tier) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f, 5.0f).sound(SoundType.METAL).notSolid());
		this.tier = tier;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		// Create the decimal formatter.
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		// Format the pump rate and add it as a tooltip.
		String pumpRate = decimalFormat.format(tierObject.pumpRate.get() / 20.0f);
		tooltip.add(new TranslationTextComponent("gui.staticpower.pump_rate_format", TextFormatting.AQUA.toString() + pumpRate));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getCutout();
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if (tier == StaticPowerTiers.IRON) {
			return TileEntityPump.TYPE_IRON.create();
		} else if (tier == StaticPowerTiers.BASIC) {
			return TileEntityPump.TYPE_BASIC.create();
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return TileEntityPump.TYPE_ADVANCED.create();
		} else if (tier == StaticPowerTiers.STATIC) {
			return TileEntityPump.TYPE_STATIC.create();
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return TileEntityPump.TYPE_ENERGIZED.create();
		} else if (tier == StaticPowerTiers.LUMUM) {
			return TileEntityPump.TYPE_LUMUM.create();
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return TileEntityPump.TYPE_CREATIVE.create();
		}
		return null;
	}
}
